package com.smy.tfs.biz.service.impl;


import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.smy.framework.core.util.SequenceUtil;
import com.smy.tfs.api.dbo.TicketData;
import com.smy.tfs.api.dbo.TicketFormItemData;
import com.smy.tfs.api.dbo.TicketFormItemIdColMapping;
import com.smy.tfs.api.dbo.TicketFormItemValues;
import com.smy.tfs.api.dto.AdvancedQueryDto;
import com.smy.tfs.api.dto.PageQueryTicketDataReqDto;
import com.smy.tfs.api.dto.TicketDataListResponseDto;
import com.smy.tfs.api.dto.TicketFormItemAttriDto;
import com.smy.tfs.api.enums.TFSTableIdCode;
import com.smy.tfs.api.service.ITicketDataService;
import com.smy.tfs.api.service.ITicketFormItemDataService;
import com.smy.tfs.biz.mapper.TicketFormItemValuesMapper;
import com.smy.tfs.biz.service.ITicketFormItemIdColMappingService;
import com.smy.tfs.biz.service.ITicketFormItemValuesService;
import com.smy.tfs.biz.service.TicketDataESService;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * （表单项col和表单项value对应的平铺表） 服务实现类
 * </p>
 *
 * @author yss
 * @since 2024-05-10
 */
@Service
@Slf4j
public class TicketFormItemValuesServiceImpl extends ServiceImpl<TicketFormItemValuesMapper, TicketFormItemValues> implements ITicketFormItemValuesService {

    @Resource
    ITicketDataService ticketDataService;
    @Resource
    ITicketFormItemDataService ticketFormItemDataService;
    @Resource
    ITicketFormItemIdColMappingService ticketFormItemIdColMappingService;

    @Resource
    TicketFormItemValuesMapper ticketFormItemValuesMapper;
    @Resource
    private TicketDataESService ticketDataESService;
    @Value("${es.ticket_data_info.index}")
    private String index;


    @Override
    public List<TicketDataListResponseDto> selectTicketFormItemValuesList(AdvancedQueryDto advancedQueryDto) {
        return this.baseMapper.selectTicketFormItemValuesList(advancedQueryDto);
    }

    /**
     * 同步ticket_data表数据到ticket_form_item_values
     * @param startTimestamp
     * @param endTimestamp
     */
    @Override
    public void syncTimeRangeTicketData(Timestamp startTimestamp, Timestamp endTimestamp) {
        Map<String,String> ticketFormItemValuesIdMap = new HashMap<>();
        /**
         * 获取在[startTimestamp，endTimestamp]时间区间内更新的TicketData数据
         */
        List<TicketData> filterTicketDataList = getFilterTicketDataList(startTimestamp, endTimestamp, ticketFormItemValuesIdMap);
        if (ObjectHelper.isEmpty(filterTicketDataList)) {
            log.info("无新增或者变更的单，无需同步到高级查询表");
            return;
        }
        log.info("新增或者变更的单({}个):{}",filterTicketDataList.size(), filterTicketDataList.stream().map(TicketData::getId).collect(Collectors.toList()));
        for(TicketData ticketData : filterTicketDataList){
            /**
             * 赋值给新对象TicketFormItemValues的通用字段
             */
            TicketFormItemValues ticketFormItemValues = new TicketFormItemValues().toTicketFormItemValues(ticketData);
            String ticketDataId = ticketFormItemValues.getTicketDataId();
            if (ObjectHelper.isNotEmpty(ticketFormItemValuesIdMap) && ObjectHelper.isNotEmpty(ticketFormItemValuesIdMap.get(ticketDataId))) {
                ticketFormItemValues.setId(ticketFormItemValuesIdMap.get(ticketDataId));
            } else {
                ticketFormItemValues.setId(SequenceUtil.getId(TFSTableIdCode.TICKET_FORM_ITEM_VALUES));
            }
            /**
             * 组装TicketFormItemValues的不定个数字段{formItemValue1...formItemValue30}数据
             */
            String ticketTemplateId = ticketFormItemValues.getTemplateId();
            Integer version = ticketFormItemValues.getVersion();
            //TODO 引入本地内存
            //同步formItemValue
            List<TicketFormItemIdColMapping> ticketFormItemIdColMappingList = ticketFormItemIdColMappingService.lambdaQuery()
                    .eq(TicketFormItemIdColMapping::getTicketTemplateId,ticketTemplateId)
                    .eq(TicketFormItemIdColMapping::getVersion,version)
                    .isNull(TicketFormItemIdColMapping::getDeleteTime).list();
            if (ObjectHelper.isEmpty(ticketFormItemIdColMappingList)) {
                log.info("无高级查询映射配置，不需同步，工单Id:{}", ticketDataId);
                continue;
            }
            //组装itemIdColMap
            Map<String,String> itemIdColMap = new HashMap<>();
            for (TicketFormItemIdColMapping t:ticketFormItemIdColMappingList) {
                itemIdColMap.put(t.getFormItemId(), t.getFormItemValueCol());
            }
            log.info("工单Id:{}高级查询映射配置，itemIdColMap：{}", ticketDataId, itemIdColMap);
            Set<String> itemIdSet = itemIdColMap.keySet();
            //itemIdValMap为<表单项id,表单项项值>
            List<TicketFormItemData> ticketFormItemDataList = ticketFormItemDataService.lambdaQuery()
                    .eq(TicketFormItemData::getTicketDataId,ticketDataId)
                    .in(TicketFormItemData::getTemplateId,itemIdSet)
                    .isNull(TicketFormItemData::getDeleteTime).list();
            if (ObjectHelper.isEmpty(ticketFormItemDataList)) {
                String formatError = String.format("查询出来的支持高级搜索的表单项数据为空(ticketDataId:%s,ticketTemplateId:%s):",ticketDataId,ticketTemplateId);
                log.warn(formatError);
                continue;
            }
            Map<String, String> itemIdValMap = new HashMap();
            ticketFormItemDataList.stream().forEach(it->itemIdValMap.put(it.getTemplateId(),it.getItemValue()));
            if (ObjectHelper.isEmpty(itemIdValMap)) {
                String formatError = String.format("查询出来的表单项id和value映射为空(ticketDataId:%s,ticketTemplateId:%s):",ticketDataId,ticketTemplateId);
                log.warn(formatError);
                continue;
            }
            log.info("工单Id:{}表单项id和value映射，itemIdColMap：{}", ticketDataId, itemIdValMap);
            //遍历itemIdValMap，将值赋值给ticketFormItemValues对应的字段。
            try {
                for (String key : itemIdColMap.keySet()){
                    //将itemIdColMap.get(key)要变成首字母小写的驼峰形式
                    String fieldKey = StringUtils.toLowerCamelCase(itemIdColMap.get(key));
                    Class ticketFormItemValuesClass = ticketFormItemValues.getClass();
                    Field field = ticketFormItemValuesClass.getDeclaredField(fieldKey);
                    field.setAccessible(Boolean.TRUE);
                    //字符串长度超过数据库限制，则不保存
                    String value = itemIdValMap.get(key);
                    if (!isStringLengthValid(value,245)) {
                        String formatError = String.format("(ticketDataId:%s,列id:%s,列code:%s,列值:%s)超过数据库最大限制245", ticketDataId, key, fieldKey, value);
                        log.warn(formatError);
                        value = substringByByte(value,245);
                    }
                    field.set(ticketFormItemValues, value);
                }
            } catch (Exception e) {
                String formatError = String.format("设置formItemValue异常(ticketDataId:%s,ticketTemplateId:%s,fieldKey:%s,val:%s):",ticketDataId,ticketTemplateId,itemIdColMap,itemIdValMap);
                log.error(formatError, e);
                continue;
            }
            /**
             * 保存List<TicketFormItemValues> ticketFormItemValuesList数据
             */
            try {
                if (ObjectHelper.isNotEmpty(ticketFormItemValues) && !saveOrUpdate(ticketFormItemValues)) {
                    String formatError = String.format("同步保存ticketFormItemValues异常：%s", JSONObject.toJSONString(ticketFormItemValues));
                    log.error(formatError);
                }
            } catch (Exception e) {
                String formatError = String.format("保存ticketFormItemValues数据异常(ticketDataId:%s):",ticketDataId);
                log.error(formatError, e);
            }
            /**
             *  高级查询字段同步到es
             */
            try {
                log.info("工单id:{}高级查询字段开始同步到es", ticketDataId);
                JSONObject ticketFormItemDataJsonObj = new JSONObject();
                ticketFormItemDataJsonObj.put("id", ticketDataId);
                for (TicketFormItemData ticketFormItemData : ticketFormItemDataList) {
                    ticketFormItemDataJsonObj.put(ticketFormItemData.getItemLabel(), ticketFormItemData.getItemValue());
                }
                //更新
                ticketDataESService.update(index, ticketDataId, ticketFormItemDataJsonObj.toJSONString());
                log.info("工单id:{}高级查询字段结束同步到es", ticketDataId);
            } catch (Exception e) {
                log.error("工单id:{}高级查询字段同步到es,异常:{}", ticketDataId, e);
            }
        }
    }

    public boolean isStringLengthValid(String input, int maxDatabaseLength) {
        if (StringUtils.isEmpty(input)) {
            return true;
        }
        // 获取字符串的字节长度
        int byteLength = input.getBytes(StandardCharsets.UTF_8).length;
        // 判断字节长度是否超过数据库字段的最大长度
        return byteLength < maxDatabaseLength;
    }

    public String substringByByte(String str, int byteLength) {
        if (str == null || byteLength <= 0) {
            return "";
        }
        try {
            byte[] bytes = str.getBytes(StandardCharsets.UTF_8); // 根据编码调整，中文常用GBK
            if (byteLength >= bytes.length) {
                return str;
            }
            // 处理截断位置可能位于多字节字符中间的情况
            if (bytes[byteLength - 1] > 0) { // 单字节字符
                return new String(bytes, 0, byteLength, StandardCharsets.UTF_8);
            } else {
                // 往前检查是否完整字符
                int count = 0;
                for (int i = 0; i < byteLength; i++) {
                    if (bytes[i] < 0) {
                        count++;
                    }
                }
                // GBK编码中汉字占2字节，且第一个字节为负数
                if (count % 2 != 0) {
                    byteLength--; // 避免截断半个汉字
                }
                return new String(bytes, 0, byteLength, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            log.error("根据字节截取字符串异常：", e);
            return str.substring(0, 80);
        }
    }

    /**
     * 获取在[startTimestamp，endTimestamp]时间区间内更新的TicketData数据
     * @param startTimestamp
     * @param endTimestamp
     * @param ticketFormItemValuesIdMap
     * @return
     */
    private List<TicketData> getFilterTicketDataList(Timestamp startTimestamp, Timestamp endTimestamp, Map<String,String> ticketFormItemValuesIdMap){
        List<TicketData> ticketDataList = ticketDataService.lambdaQuery().between(TicketData::getUpdateTime,startTimestamp,endTimestamp)
                .ne(TicketData::getTemplateId,"-1").isNull(TicketData::getDeleteTime).list();
        if (ObjectHelper.isEmpty(ticketDataList))
            return null;

        List<String> ticketDataIdList = ticketDataList.stream().map(it->it.getId()).collect(Collectors.toList());
        List<String> excludeTicketDataIdList = new ArrayList<>();

        List<TicketData> filterTicketDataList = ticketDataList;
        List<TicketFormItemValues> existTicketFormItemValuesList = this.lambdaQuery()
                .in(TicketFormItemValues::getTicketDataId,ticketDataIdList).list();
        if (ObjectHelper.isNotEmpty(existTicketFormItemValuesList)) {
            existTicketFormItemValuesList.stream().forEach(ticketFormItemValues->{
                String ticketDataId = ticketFormItemValues.getTicketDataId();
                Date ticketFormItemValuesUpdateTime = ticketFormItemValues.getUpdateTime();
                String ticketFormItemValuesTags = ticketFormItemValues.getTags();
                ticketDataList.stream().forEach(ticketData->{
                    String id = ticketData.getId();
                    Date ticketDataUpdateTime = ticketData.getUpdateTime();
                    String tags = ticketData.getTags();
                    if (ticketDataId.equals(id) && ticketFormItemValuesUpdateTime.compareTo(ticketDataUpdateTime) == 0
                            && (ObjectHelper.isEmpty(tags) || tags.equals(ticketFormItemValuesTags))) {
                        excludeTicketDataIdList.add(ticketDataId);
                    }
                    if (ticketDataId.equals(id) && (ticketFormItemValuesUpdateTime.compareTo(ticketDataUpdateTime) != 0
                            || (ObjectHelper.isNotEmpty(tags) && !tags.equals(ticketFormItemValuesTags)))){
                        //数据库中已存在，还未更新。
                        ticketFormItemValuesIdMap.put(ticketDataId,ticketFormItemValues.getId());
                    }
                });
            });
            filterTicketDataList = ticketDataList.stream()
                    .filter(ticketData -> !excludeTicketDataIdList.contains(ticketData.getId()))
                    .collect(Collectors.toList());
        }
        return filterTicketDataList;

    }

    /**
     *
     * @param advancedQueryDto
     * @param ticketFormItemAttriDtoList
     * @return
     */
//    @DataSource(DataSourceType.SR) //TODO sr版本升级，有复杂函数不支持，先屏蔽。
    @Override
    public List<TicketFormItemValues> queryTicketDataListResponseDtoList(AdvancedQueryDto advancedQueryDto, List<TicketFormItemAttriDto> ticketFormItemAttriDtoList) {
        LambdaQueryWrapper<TicketFormItemValues> lambdaQueryWrapper = TicketDataServiceInner.getTicketFormItemValuesQueryWrapper(advancedQueryDto, ticketFormItemAttriDtoList);
        List<TicketFormItemValues> ticketFormItemValuesList = ticketFormItemValuesMapper.selectList(lambdaQueryWrapper);
        return ticketFormItemValuesList;
    }

    @Override
    public PageInfo<TicketFormItemValues> pageQueryTicketFormItemValuesList(PageQueryTicketDataReqDto pageQueryTicketDataReqDto, String user, List<TicketFormItemAttriDto> ticketFormItemAttriDtoList) {
        LambdaQueryWrapper<TicketFormItemValues> lambdaQueryWrapper = TicketDataServiceInner.pageQueryWrapper(pageQueryTicketDataReqDto, user, ticketFormItemAttriDtoList);
        return PageHelper.<TicketFormItemValues>startPage(pageQueryTicketDataReqDto.getPageNum(), pageQueryTicketDataReqDto.getPageSize())
                .doSelectPageInfo(() -> ticketFormItemValuesMapper.selectList(lambdaQueryWrapper));
    }

    @Override
    public void syncTicketData(String ticketDataId) {
        TicketData ticketData = ticketDataService.lambdaQuery()
                .eq(TicketData::getId, ticketDataId).one();
        /**
         * 赋值给新对象TicketFormItemValues的通用字段
         */
        TicketFormItemValues ticketFormItemValues = new TicketFormItemValues().toTicketFormItemValues(ticketData);
        ticketFormItemValues.setId(SequenceUtil.getId(TFSTableIdCode.TICKET_FORM_ITEM_VALUES));
        /**
         * 组装TicketFormItemValues的不定个数字段{formItemValue1...formItemValue30}数据
         */
        String ticketTemplateId = ticketFormItemValues.getTemplateId();
        Integer version = ticketFormItemValues.getVersion();
        //同步formItemValue
        List<TicketFormItemIdColMapping> ticketFormItemIdColMappingList = ticketFormItemIdColMappingService.lambdaQuery()
                .eq(TicketFormItemIdColMapping::getTicketTemplateId,ticketTemplateId)
                .eq(TicketFormItemIdColMapping::getVersion,version)
                .isNull(TicketFormItemIdColMapping::getDeleteTime).list();
        if (ObjectHelper.isEmpty(ticketFormItemIdColMappingList)) {
            String formatError = String.format("查询出来的支持高级搜索的表单项id和列名为空(ticketTemplateId:%s):",ticketTemplateId);
            log.error(formatError);
            return;
        }
        log.info("支持高级搜索的表单项id和列名:{}", ticketFormItemIdColMappingList);
        //组装itemIdColMap
        Map<String,String> itemIdColMap = new HashMap<>();
        for (TicketFormItemIdColMapping t:ticketFormItemIdColMappingList) {
            itemIdColMap.put(t.getFormItemId(), t.getFormItemValueCol());
        }
        Set<String> itemIdSet = itemIdColMap.keySet();
        //itemIdValMap为<表单项id,表单项项值>
        List<TicketFormItemData> ticketFormItemDataList = ticketFormItemDataService.lambdaQuery()
                .eq(TicketFormItemData::getTicketDataId,ticketDataId)
                .in(TicketFormItemData::getTemplateId,itemIdSet)
                .isNull(TicketFormItemData::getDeleteTime).list();
        if (ObjectHelper.isEmpty(ticketFormItemDataList)) {
            String formatError = String.format("查询出来的支持高级搜索的表单项数据为空(ticketDataId:%s,ticketTemplateId:%s):", ticketDataId, ticketTemplateId);
            log.error(formatError);
            throw new RuntimeException(formatError);
        }
        Map<String, String> itemIdValMap = new HashMap();
        if (ObjectHelper.isNotEmpty(ticketFormItemDataList)) {
            ticketFormItemDataList.stream().forEach(it->itemIdValMap.put(it.getTemplateId(),it.getItemValue()));
        }
        if (ObjectHelper.isEmpty(itemIdValMap)) {
            String formatError = String.format("查询出来的表单项id和value映射为空(ticketDataId:%s,ticketTemplateId:%s):",ticketDataId,ticketTemplateId);
            log.error(formatError);
            throw new RuntimeException(formatError);
        }
        log.info("支持高级搜索的表单项id和value值:{}", itemIdValMap);
        //遍历itemIdValMap，将值赋值给ticketFormItemValues对应的字段。
        for (String key : itemIdColMap.keySet()){
            try {
                //将itemIdColMap.get(key)要变成首字母小写的驼峰形式
                String fieldKey = StringUtils.toLowerCamelCase(itemIdColMap.get(key));
                Class ticketFormItemValuesClass = ticketFormItemValues.getClass();
                Field field = ticketFormItemValuesClass.getDeclaredField(fieldKey);
                field.setAccessible(Boolean.TRUE);
                field.set(ticketFormItemValues,itemIdValMap.get(key));
            } catch (Exception e) {
                String formatError = String.format("设置formItemValue异常(ticketDataId:%s,ticketTemplateId:%s,fieldKey:%s,val:%s):",ticketDataId,ticketTemplateId,itemIdColMap.get(key),itemIdValMap.get(key));
                log.error(formatError);
                throw new RuntimeException(formatError);
            }
        }
        log.info("同步保存ticketFormItemValues:{}", JSONObject.toJSONString(ticketFormItemValues));
        /**
         * 保存ticketFormItemValues数据
         */
        if (ObjectHelper.isNotEmpty(ticketFormItemValues) && !save(ticketFormItemValues)) {
            String formatError = String.format("同步保存ticketFormItemValues异常", ticketFormItemValues);
            log.error("同步保存ticketFormItemValues异常:{}", ticketFormItemValues);
            throw new RuntimeException(formatError);
        };

    }

}
