package com.smy.tfs.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.smy.framework.base.IPage;
import com.smy.tfs.api.dbo.*;
import com.smy.tfs.api.dto.*;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.query.RemoteTableDataInfo;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.enums.FormItemTypeEnum;
import com.smy.tfs.api.enums.TicketDataStatusEnum;
import com.smy.tfs.api.enums.TicketMsgArriveTypeEnum;
import com.smy.tfs.api.service.*;
import com.smy.tfs.biz.component.AccountReturnComponent;
import com.smy.tfs.biz.mapper.TicketDataMapper;
import com.smy.tfs.biz.service.ITicketFormItemIdColMappingService;
import com.smy.tfs.biz.service.ITicketFormItemValuesService;
import com.smy.tfs.biz.service.TicketDataESService;
import com.smy.tfs.biz.service.TicketFlowNodeApproveDetailService;
import com.smy.tfs.common.constant.HttpStatus;
import com.smy.tfs.common.core.page.TableDataInfo;
import com.smy.tfs.common.utils.DateUtils;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 工单数据表 服务实现类
 * </p>
 *
 * @author yss
 * @since 2024-11-22
 */
@Slf4j
@Component("ticketDataOpenServiceImpl")
public class TicketDataOpenServiceImpl implements ITicketDataOpenService {

    @Resource
    ITicketTemplateService ticketTemplateService;
    @Resource
    ITicketFormItemIdColMappingService iTicketFormItemIdColMappingService;
    @Resource
    ITicketFormItemTemplateService iTicketFormItemTemplateService;
    @Resource
    ITicketFormItemValuesService ticketFormItemValuesService;
    @Resource
    AccountReturnComponent accountReturnComponent;
    @Resource
    TicketFlowNodeApproveDetailService ticketFlowNodeApproveDetailService;
    @Resource
    ITicketCategoryService ticketCategoryService;
    @Resource
    ITicketDataService ticketDataService;

    @Resource
    TicketDataMapper ticketDataMapper;
    @Resource
    ITicketAppService ticketAppService;
    @Resource
    private TicketDataESService ticketDataESService;

    @Value("${tfs.extend.query.open}")
    private Boolean openExtendQuery = false;

    @Value("${ncs.query.es.open}")
    private Boolean openESQuery = true;
    @Value("${es.ticket_data_info.index}")
    private String index;

    @Override
    public Response<RemoteTableDataInfo> pageQueryTicketList(PageQueryTicketDataReqDto pageQueryTicketDataReqDto, String userType, String userId, String userName) {
        RemoteTableDataInfo tableDataInfo = new RemoteTableDataInfo();
        tableDataInfo.setCode(200);
        tableDataInfo.setMsg("操作成功");
        try {
            if (Objects.isNull(pageQueryTicketDataReqDto)
                    || StringUtils.isEmpty(userType)
                    || StringUtils.isEmpty(userId)
                    || StringUtils.isEmpty(userName)) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "请检查查询条件、用户信息是否为空");
            }
            if (ObjectHelper.isEmpty(pageQueryTicketDataReqDto.getTemplateId()) && ObjectHelper.isEmpty(pageQueryTicketDataReqDto.getTemplateCode())) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单模版id为空");
            }

            String ticketTemplateId = pageQueryTicketDataReqDto.getTemplateId();
            String templateCode = pageQueryTicketDataReqDto.getTemplateCode();

            TicketTemplate ticketTemplate = null;
            if (Objects.nonNull(templateCode)) {
                var tempOpt = ticketTemplateService.lambdaQuery().isNull(TicketTemplate::getDeleteTime).eq(TicketTemplate::getTicketTemplateCode, templateCode).oneOpt();
                if (tempOpt.isPresent()) {
                    ticketTemplate = tempOpt.get();
                }
            }
            if (Objects.isNull(ticketTemplate) && StringUtils.isNotEmpty(ticketTemplateId)) {
                ticketTemplate = ticketTemplateService.lambdaQuery().isNull(TicketTemplate::getDeleteTime).eq(TicketTemplate::getId, ticketTemplateId).one();
            }
            if (Objects.isNull(ticketTemplate)) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单模板(id:%s,标识:%s) 不存在", ticketTemplateId, templateCode));
            }
            //ticketTemplateId 转换
            ticketTemplateId = ticketTemplate.getId();
            pageQueryTicketDataReqDto.setTemplateId(ticketTemplateId);
            List<TicketFormItemTemplate> ticketFormItemTemplateList = iTicketFormItemTemplateService.lambdaQuery()
                    .eq(TicketFormItemTemplate::getTicketTemplateId, ticketTemplateId)
//                    .isNull(TicketFormItemTemplate::getDeleteTime)
                    .list();
            if (CollectionUtils.isEmpty(ticketFormItemTemplateList)) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单模板(id:%s,标识:%s) 配置的表单项不存在", ticketTemplateId, templateCode));
            }
            //Map<String, String> lableIdMap:key值为表单项的label，value值为表单项的id
            Map<String, String> lableIdMap = ticketFormItemTemplateList.stream()
                    .collect(Collectors.toMap(TicketFormItemTemplate::getItemLabel, TicketFormItemTemplate::getId));
            if (ObjectHelper.isEmpty(lableIdMap)) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单模板(id:%s,标识:%s) 配置的表单项标题和id映射关系不存在", ticketTemplateId, templateCode));
            }
            List<TicketFormItemAttriDto> formItemQueryList = pageQueryTicketDataReqDto.getFormItemList();
            if (CollectionUtils.isNotEmpty(formItemQueryList)) {
                for(TicketFormItemAttriDto it : formItemQueryList){
                    String formItemLabel = it.getFormItemLabel();
                    String itemId = lableIdMap.get(formItemLabel);
                    if (StringUtils.isEmpty(itemId)) {
                        return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单模板(id:%s,标识:%s) 配置的表单项(标题：%s)不存在", ticketTemplateId, templateCode, formItemLabel));
                    }
                    it.setFormItemId(itemId);
                }
            }
            /**
             *
             * 1、生成最高版本的模板项<表单项id值,表单项id值对应的列名>映射itemIdColMap
             * 2、生成 <表单项id,表单项类型>itemIdTypeMap
             *
             */
            List<TicketFormItemIdColMapping> ticketFormItemIdColMappingList = iTicketFormItemIdColMappingService.lambdaQuery().eq(TicketFormItemIdColMapping::getTicketTemplateId, ticketTemplateId).list();
            Response<Map<String, String>> itemIdColMapResponse = TicketDataServiceInner.getItemIdColMap(ticketFormItemIdColMappingList);
            if (!itemIdColMapResponse.isSuccess()) {
                return Response.error(BizResponseEnums.getEnumByCode(itemIdColMapResponse.getCode()), itemIdColMapResponse.getMsg());
            }
            Map<String, String> itemIdColMap = itemIdColMapResponse.getData();
            Map<String, FormItemTypeEnum> itemIdTypeMap = new HashMap();
            if (ObjectHelper.isNotEmpty(ticketFormItemTemplateList)) {
                itemIdTypeMap = ticketFormItemTemplateList.stream()
                        .collect(Collectors.toMap(TicketFormItemTemplate::getId, TicketFormItemTemplate::getItemType));
            }
            /**
             * 组装List<TicketFormItemAttriDto> ticketFormItemAttriDtoList；
             * TicketFormItemAttriDto为{formItemId,formItemValue,formItemType}
             */
            Response<List<TicketFormItemAttriDto>> ticketFormItemAttriDtoListResponse = TicketDataServiceInner.getTicketFormItemAttriDtoList(pageQueryTicketDataReqDto.getFormItemList(), itemIdColMap, itemIdTypeMap);
            if (!ticketFormItemAttriDtoListResponse.isSuccess()) {
                return Response.error(BizResponseEnums.getEnumByCode(ticketFormItemAttriDtoListResponse.getCode()), ticketFormItemAttriDtoListResponse.getMsg());
            }
            List<TicketFormItemAttriDto> ticketFormItemAttriDtoList = ticketFormItemAttriDtoListResponse.getData();

            //开始分页
            String user = String.format("\"accountType\":\"%s\",\"accountId\":\"%s\",\"accountName\":\"%s\"", userType, userId, userName);
            PageInfo<TicketFormItemValues> pageInfo = ticketFormItemValuesService.pageQueryTicketFormItemValuesList(pageQueryTicketDataReqDto, user, ticketFormItemAttriDtoList);
            List<TicketFormItemValues> ticketFormItemValuesList = pageInfo.getList();
            Response<List<TicketDataQueryResult>> ticketDataQueryResultListResp = convert(ticketFormItemValuesList, lableIdMap, itemIdColMap);
            if (!ticketDataQueryResultListResp.isSuccess()) {
                return Response.error(BizResponseEnums.getEnumByCode(ticketDataQueryResultListResp.getCode()), ticketDataQueryResultListResp.getMsg());
            }
            tableDataInfo.setRows(ticketDataQueryResultListResp.getData());
            tableDataInfo.setTotal(pageInfo.getTotal());
        }catch (Exception e) {
            log.error("查询异常：{}", e);
            return Response.error(BizResponseEnums.QUERY_ERROR, e.getMessage());
        }
        return Response.success(tableDataInfo);

    }



    public Response<List<TicketDataQueryResult>> convert(List<TicketFormItemValues> ticketFormItemValuesList, Map<String, String> lableIdMap, Map<String, String> itemIdColMap) {
        if (CollUtil.isEmpty(ticketFormItemValuesList)) {
            log.info("查询结果为空");
            return Response.success(new ArrayList<>());
        }
        Map<String, String> colLabelMap = new HashMap();
        if (ObjectHelper.isNotEmpty(itemIdColMap)) {
            for (Map.Entry<String, String> entry : lableIdMap.entrySet()) {
                String label = entry.getKey();
                String id = entry.getValue();
                if (StringUtils.isEmpty(id)) {
                    return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("label(%s)找不到对应的id", label));
                }
                String col = itemIdColMap.get(id);
                if (StringUtils.isNotEmpty(col)) {
                    colLabelMap.put(col, label);
                }
            }
        }
        List<TicketDataQueryResult> ticketDataQueryResultList = new ArrayList<>();
        for (TicketFormItemValues ticketFormItemValues: ticketFormItemValuesList) {
            TicketDataQueryResult ticketDataQueryResult = new TicketDataQueryResult(ticketFormItemValues);
            ticketDataQueryResult.setApplyUser(accountReturnComponent.toAccountInfoStrForFront(ticketDataQueryResult.getApplyUser()));
            if (ticketDataQueryResult.getTicketStatus() == TicketDataStatusEnum.APPLYING
                    && ticketDataQueryResult.getTicketMsgArriveType() == TicketMsgArriveTypeEnum.WECOM) {
                ticketDataQueryResult.setShowReminderButton(Boolean.TRUE);
                ticketDataQueryResult.setShowFollowButton(StrUtil.isBlank(ticketFormItemValues.getWxChatGroupId()));
            }
            if (ObjectHelper.isNotEmpty(colLabelMap)) {
                ticketDataQueryResult.setFormItemList(getTicketFormItemAttriDtoList(ticketFormItemValues, colLabelMap));
            }
            //表单字段值转换
            ticketDataQueryResultList.add(ticketDataQueryResult);
        }
        return Response.success(ticketDataQueryResultList);
    }


    private TicketFormItemAttriDto getTicketFormItemAttriDto(String label, String value) {
        if (StringUtils.isNotEmpty(label)) {
            TicketFormItemAttriDto ticketFormItemAttriDto = new TicketFormItemAttriDto();
            ticketFormItemAttriDto.setFormItemLabel(label);
            ticketFormItemAttriDto.setFormItemValue(value);
            return ticketFormItemAttriDto;
        }
        return null;
    }


    private List<TicketFormItemAttriDto> getTicketFormItemAttriDtoList(TicketFormItemValues ticketFormItemValues, Map<String, String> colLabel) {
        List<TicketFormItemAttriDto> ticketFormItemAttriDtoList = new ArrayList<>();
        try {
            // 定义一个映射，将方法名映射到对应的键
            Map<String, String> formItemMap = new HashMap<>();
            formItemMap.put("getFormItemValue1", "form_item_value1");
            formItemMap.put("getFormItemValue2", "form_item_value2");
            formItemMap.put("getFormItemValue3", "form_item_value3");
            formItemMap.put("getFormItemValue4", "form_item_value4");
            formItemMap.put("getFormItemValue5", "form_item_value5");
            formItemMap.put("getFormItemValue6", "form_item_value6");
            formItemMap.put("getFormItemValue7", "form_item_value7");
            formItemMap.put("getFormItemValue8", "form_item_value8");
            formItemMap.put("getFormItemValue9", "form_item_value9");
            formItemMap.put("getFormItemValue10", "form_item_value10");
            formItemMap.put("getFormItemValue11", "form_item_value11");
            formItemMap.put("getFormItemValue12", "form_item_value12");
            formItemMap.put("getFormItemValue13", "form_item_value13");
            formItemMap.put("getFormItemValue14", "form_item_value14");
            formItemMap.put("getFormItemValue15", "form_item_value15");
            formItemMap.put("getFormItemValue16", "form_item_value16");
            formItemMap.put("getFormItemValue17", "form_item_value17");
            formItemMap.put("getFormItemValue18", "form_item_value18");
            formItemMap.put("getFormItemValue19", "form_item_value19");
            formItemMap.put("getFormItemValue20", "form_item_value20");
            formItemMap.put("getFormItemValue21", "form_item_value21");
            formItemMap.put("getFormItemValue22", "form_item_value22");
            formItemMap.put("getFormItemValue23", "form_item_value23");
            formItemMap.put("getFormItemValue24", "form_item_value24");
            formItemMap.put("getFormItemValue25", "form_item_value25");
            formItemMap.put("getFormItemValue26", "form_item_value26");
            formItemMap.put("getFormItemValue27", "form_item_value27");
            formItemMap.put("getFormItemValue28", "form_item_value28");
            formItemMap.put("getFormItemValue29", "form_item_value29");
            formItemMap.put("getFormItemValue30", "form_item_value30");
            // 遍历映射并执行相应的检查与添加操作
            for (Map.Entry<String, String> entry : formItemMap.entrySet()) {
                // 使用反射获取对应的方法
                Method method = ticketFormItemValues.getClass().getMethod(entry.getKey());
                // 调用方法获取值
                String value = (String) method.invoke(ticketFormItemValues);
                // 使用辅助方法添加非空项
                String col = entry.getValue();
                String label = colLabel.get(col);
                TicketFormItemAttriDto ticketFormItemAttriDto = getTicketFormItemAttriDto(label, value);
                if (Objects.nonNull(ticketFormItemAttriDto)) {
                    ticketFormItemAttriDtoList.add(ticketFormItemAttriDto);
                }
            }
        } catch (Exception e) {
            // 处理可能的异常，如方法不存在、调用失败等
            log.error("字段名和字段值映射异常", e);
        }
        return ticketFormItemAttriDtoList;
    }



    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected TableDataInfo getDataTable(List<?> list) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected TableDataInfo getErrorDataTable(int code, String msg) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(code);
        rspData.setMsg(msg);
        return rspData;
    }

    @Override
    public Response<TicketFlowNodeApproveDetailDto> getTicketFlowNodeApproveDetailDto(TicketFlowNodeApproveDetailDto ticketFlowNodeApproveDetailDto, String userType, String userId, String userName) {
        Optional<TicketFlowNodeApproveDetail> opt = ticketFlowNodeApproveDetailService.lambdaQuery()
                .eq(TicketFlowNodeApproveDetail::getId, ticketFlowNodeApproveDetailDto.getId())
                .isNull(TicketFlowNodeApproveDetail::getDeleteTime)
                .oneOpt();
        if (!opt.isPresent()) {
            return Response.error(BizResponseEnums.QUERY_ERROR, "审批节点明细不存在");
        }
        return Response.success(new TicketFlowNodeApproveDetailDto(opt.get()));
    }

    //只给催收用
    @Override
    public Response<BusiTicketDataResponseDto> queryBusiTicketDataList(BusiTicketDataRequestDto commonFieldsRequestDto) {
        if (!openESQuery) {
            log.info("******************数据库查询开始******************");
            if (!openExtendQuery) {
                return Response.success(new BusiTicketDataFieldsMappingDto());
            }
            if (Objects.isNull(commonFieldsRequestDto)
                    || StringUtils.isEmpty(commonFieldsRequestDto.getAppId())
                    || CollectionUtils.isEmpty(commonFieldsRequestDto.getTemplateIdList())
                    || StringUtils.isEmpty(commonFieldsRequestDto.getExtend1())) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "请检查入参对象、应用ID、模板Id、extend1参数是否为空");
            }
            LambdaQueryWrapper<TicketData> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            List<String> extend1List = Arrays.asList(commonFieldsRequestDto.getExtend1().split(","));
            lambdaQueryWrapper.and(LambdaQueryWrapper -> {
                for (String extend1 : extend1List) {
                    LambdaQueryWrapper.eq(TicketData::getExtend1, extend1);
                    LambdaQueryWrapper.or();
                }
            });
            lambdaQueryWrapper.isNull(TicketData::getDeleteTime);
            if (StringUtils.isNotEmpty(commonFieldsRequestDto.getAppId())) {
                lambdaQueryWrapper.eq(TicketData::getAppId, commonFieldsRequestDto.getAppId());
            }
            //根据模版标识查询模版id
            List<String> ticketTemplateIdList = commonFieldsRequestDto.getTemplateIdList();
            LambdaQueryWrapper<TicketTemplate> templateQueryWrapper = new LambdaQueryWrapper();
            templateQueryWrapper.isNull(TicketTemplate::getDeleteTime);
            templateQueryWrapper.and(LambdaQueryWrapper -> {
                LambdaQueryWrapper.in(TicketTemplate::getId, ticketTemplateIdList);
                LambdaQueryWrapper.or();
                LambdaQueryWrapper.in(TicketTemplate::getTicketTemplateCode, ticketTemplateIdList);
            });
            List<TicketTemplate> ticketTemplateList = ticketTemplateService.list(templateQueryWrapper);
            if (CollectionUtils.isEmpty(ticketTemplateList)) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("所选中的工单标识/id(%s)不存在匹配的模版", ticketTemplateIdList));
            }
            List<String> templateIdList = ticketTemplateList.stream()
                    .map(it -> it.getId())
                    .collect(Collectors.toList());
            lambdaQueryWrapper.in(TicketData::getTemplateId, templateIdList);
            if (Objects.nonNull(commonFieldsRequestDto.getCreateStartTime())) {
                lambdaQueryWrapper.ge(TicketData::getCreateTime, commonFieldsRequestDto.getCreateStartTime());
            }
            if (Objects.nonNull(commonFieldsRequestDto.getCreateEndTime())) {
                lambdaQueryWrapper.le(TicketData::getCreateTime, commonFieldsRequestDto.getCreateEndTime());
            }
            if (StringUtils.isNotEmpty(commonFieldsRequestDto.getExtend2())) {
                List<String> extend2List = Arrays.asList(commonFieldsRequestDto.getExtend2().split(","));
                lambdaQueryWrapper.and(LambdaQueryWrapper -> {
                    for (String extend2 : extend2List) {
                        LambdaQueryWrapper.eq(TicketData::getExtend2, extend2);
                        LambdaQueryWrapper.or();
                    }
                });
            }
            lambdaQueryWrapper.last("limit 100");
            lambdaQueryWrapper.select(
                    TicketData::getId,
                    TicketData::getAppId,
                    TicketData::getTemplateId,
                    TicketData::getTicketName,
                    TicketData::getTicketStatus,
                    TicketData::getApplyUser,
                    TicketData::getCreateTime,
                    TicketData::getTicketFinishTime,
                    TicketData::getTicketMsgArriveType,
                    TicketData::getTags,
                    TicketData::getCurrentDealUsers,
                    TicketData::getExtend1,
                    TicketData::getExtend2,
                    TicketData::getExtend3,
                    TicketData::getExtend4,
                    TicketData::getExtend5,
                    TicketData::getExtend6,
                    TicketData::getExtend7,
                    TicketData::getExtend8,
                    TicketData::getExtend9,
                    TicketData::getExtend10);
            List<TicketData> ticketDataList = ticketDataService.getBaseMapper().selectList(lambdaQueryWrapper);
            if (CollectionUtils.isEmpty(ticketDataList)) {
                return Response.success(new BusiTicketDataResponseDto());
            }
            List<BusiTicketDataDto> busiTicketDataDtoList = ticketDataList.stream()
                    .map(it -> new BusiTicketDataDto(it))
                    .collect(Collectors.toList());
            BusiTicketDataResponseDto busiTicketDataResponseDto = new BusiTicketDataResponseDto();
            busiTicketDataResponseDto.setBusiTicketDataDtoList(busiTicketDataDtoList);
            return Response.success(busiTicketDataResponseDto);
        }
        log.info("******************es查询开始******************");

        //走es查询
        if (Objects.isNull(commonFieldsRequestDto)
                || StringUtils.isEmpty(commonFieldsRequestDto.getAppId())
                || CollectionUtils.isEmpty(commonFieldsRequestDto.getTemplateIdList())
                || StringUtils.isEmpty(commonFieldsRequestDto.getExtend1())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "请检查入参对象、应用ID、模板Id、extend1参数是否为空");
        }
        List<String> ticketTemplateIdList = commonFieldsRequestDto.getTemplateIdList();
        LambdaQueryWrapper<TicketTemplate> templateQueryWrapper = new LambdaQueryWrapper();
        templateQueryWrapper.isNull(TicketTemplate::getDeleteTime);
        templateQueryWrapper.and(LambdaQueryWrapper -> {
            LambdaQueryWrapper.in(TicketTemplate::getId, ticketTemplateIdList);
            LambdaQueryWrapper.or();
            LambdaQueryWrapper.in(TicketTemplate::getTicketTemplateCode, ticketTemplateIdList);
        });
        List<TicketTemplate> ticketTemplateList = ticketTemplateService.list(templateQueryWrapper);
        if (CollectionUtils.isEmpty(ticketTemplateList)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("所选中的工单标识/id(%s)不存在匹配的模版", ticketTemplateIdList));
        }
        //根据查询条件构造es的BoolQueryBuilder
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //appId
        String appId = commonFieldsRequestDto.getAppId();
        BoolQueryBuilder appIdBoolQueryBuilder = QueryBuilders.boolQuery();
        appIdBoolQueryBuilder.should(QueryBuilders.termQuery("app_id.keyword", appId));
        appIdBoolQueryBuilder.should(QueryBuilders.matchQuery("beyond_apps", appId).operator(Operator.AND));
        boolQueryBuilder.filter(appIdBoolQueryBuilder);
        //delete_time IS NULL
        boolQueryBuilder.mustNot(QueryBuilders.existsQuery("delete_time"));
        //时间范围
        Date createStartTime = commonFieldsRequestDto.getCreateStartTime();
        Date createEndTime = new Date();
        String createStartTimeStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, createStartTime);
        String createEndTimeStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, createEndTime);
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("create_time")
                .from(createStartTimeStr).to(createEndTimeStr);
        boolQueryBuilder.must(rangeQuery);
        //templateIdList
        List<String> templateIdList = ticketTemplateList.stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        BoolQueryBuilder templateIdBoolQueryBuilder = QueryBuilders.boolQuery();
        templateIdBoolQueryBuilder.should(QueryBuilders.termsQuery("template_id.keyword", templateIdList));
        templateIdBoolQueryBuilder.should(QueryBuilders.termsQuery("ticket_template_code.keyword", templateIdList));
        boolQueryBuilder.filter(templateIdBoolQueryBuilder);
        //extend1
        List<String> extend1List = Arrays.asList(commonFieldsRequestDto.getExtend1().split(","));
        boolQueryBuilder.filter(QueryBuilders.termsQuery("extend1.keyword", extend1List));
        //extend2
        if (StringUtils.isNotEmpty(commonFieldsRequestDto.getExtend2())) {
            List<String> extend2List = Arrays.asList(commonFieldsRequestDto.getExtend2().split(","));
            boolQueryBuilder.filter(QueryBuilders.termsQuery("extend2.keyword", extend2List));
        }
        log.info("boolQueryBuilder:{}", boolQueryBuilder);
        IPage<BusiTicketDataDto> page = ticketDataESService.ncsESQuery(index, boolQueryBuilder, 1, 100, Boolean.FALSE);
        List<BusiTicketDataDto> busiTicketDataDtoList = Lists.newArrayList();
        if (null != page) {
            busiTicketDataDtoList = (List)page.getData();
        }
        BusiTicketDataResponseDto busiTicketDataResponseDto = new BusiTicketDataResponseDto();
        busiTicketDataResponseDto.setBusiTicketDataDtoList(busiTicketDataDtoList);
        return Response.success(busiTicketDataResponseDto);
    }
}