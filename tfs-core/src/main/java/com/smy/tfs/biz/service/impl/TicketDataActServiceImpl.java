package com.smy.tfs.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.smy.framework.core.util.SequenceUtil;
import com.smy.tfs.api.dbo.*;
import com.smy.tfs.api.dto.BatchFinishTicketsDto;
import com.smy.tfs.api.dto.BatchTicketFlowNodeApproveDto;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.dynamic.BatchDto;
import com.smy.tfs.api.dto.dynamic.TicketBatchDto;
import com.smy.tfs.api.dto.dynamic.TicketDataStdDto;
import com.smy.tfs.api.dto.ticket_act_service.DelTicketsParams;
import com.smy.tfs.api.dto.ticket_sla_service.NewTag;
import com.smy.tfs.api.enums.ApproveDealTypeEnum;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.enums.TFSTableIdCode;
import com.smy.tfs.api.enums.TicketDataStatusEnum;
import com.smy.tfs.api.service.*;
import com.smy.tfs.biz.mapper.TicketDataMapper;
import com.smy.tfs.biz.service.TicketDataApproveService;
import com.smy.tfs.biz.service.TicketFlowNodeApproveDetailService;
import com.smy.tfs.common.utils.DateUtils;
import com.smy.tfs.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.apidocs.annotations.RequestParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
@Component("ticketDataActServiceImpl")
public class TicketDataActServiceImpl implements ITicketDataActService {
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private ITicketAccountMappingService ticketAccountMappingService;
    @Resource
    TicketFlowNodeApproveDetailService ticketFlowNodeApproveDetailService;
    @Resource
    ITicketDataService ticketDataService;
    @Resource
    ITicketFlowDataService ticketFlowDataService;
    @Resource
    ITicketFlowNodeDataService ticketFlowNodeDataService;
    @Resource
    ITicketFormDataService ticketFormDataService;
    @Resource
    ITicketFormItemDataService ticketFormItemDataService;
    @Resource
    private TicketDataApproveService ticketDataApproveService;
    @Resource
    private TicketDataMapper ticketDataMapper;
    @Value("${ticket.finish.templateId}")
    private String templateId;
    @Value("${ticket.finish.applyUser}")
    private String applyUser;
    @Value("${ticket.batch.limit:20}")
    private Long ticketsBatchLimit;

    /**
     * @param params
     * @param userType
     * @param userId
     * @param userName
     * @return
     */
    @Override
    public Response<String> delTickets(DelTicketsParams params, String userType, String userId, String userName) {
        if (params == null || StringUtils.isAnyEmpty(userType, userId, userName)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "params userType userId userName参数不能为空");
        }
        if (CollectionUtils.isEmpty(params.getDelTicketIds())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单id不能为空");
        }
        TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByAccountIdAndType(userId, userType);
        if (ticketAccountMapping == null || StrUtil.isBlank(ticketAccountMapping.getSameOriginId())) {
            return Response.error(BizResponseEnums.UNKNOW_EXCEPTION_CODE, "未找到有效用户信息");
        }
        Date delNow = new Date();
        AccountInfo applyUser = new AccountInfo(ticketAccountMapping.getSameOriginId(), userType, userId, userName);
        String updateBy = applyUser.ToJsonString();
        for (String ticketDataId : params.getDelTicketIds()) {
            try {
                transactionTemplate.executeWithoutResult((transactionStatus) -> {
                    ticketDataService.lambdaUpdate()
                            .eq(TicketData::getId, ticketDataId)
                            .isNull(TicketData::getDeleteTime)
                            .set(TicketData::getUpdateBy, updateBy)
                            .set(TicketData::getUpdateTime, delNow)
                            .set(TicketData::getDeleteTime, delNow)
                            .update();
                    ticketFlowDataService.lambdaUpdate()
                            .eq(TicketFlowData::getTicketDataId, ticketDataId)
                            .isNull(TicketFlowData::getDeleteTime)
                            .set(TicketFlowData::getUpdateBy, updateBy)
                            .set(TicketFlowData::getUpdateTime, delNow)
                            .set(TicketFlowData::getDeleteTime, delNow)
                            .update();
                    ticketFlowNodeDataService.lambdaUpdate()
                            .eq(TicketFlowNodeData::getTicketDataId, ticketDataId)
                            .isNull(TicketFlowNodeData::getDeleteTime)
                            .set(TicketFlowNodeData::getUpdateBy, updateBy)
                            .set(TicketFlowNodeData::getUpdateTime, delNow)
                            .set(TicketFlowNodeData::getDeleteTime, delNow)
                            .update();
                    ticketFormDataService.lambdaUpdate()
                            .eq(TicketFormData::getTicketDataId, ticketDataId)
                            .isNull(TicketFormData::getDeleteTime)
                            .set(TicketFormData::getUpdateBy, updateBy)
                            .set(TicketFormData::getUpdateTime, delNow)
                            .set(TicketFormData::getDeleteTime, delNow)
                            .update();
                    ticketFormItemDataService.lambdaUpdate()
                            .eq(TicketFormItemData::getTicketDataId, ticketDataId)
                            .isNull(TicketFormItemData::getDeleteTime)
                            .set(TicketFormItemData::getUpdateBy, updateBy)
                            .set(TicketFormItemData::getUpdateTime, delNow)
                            .set(TicketFormItemData::getDeleteTime, delNow)
                            .update();
                    TicketFlowNodeApproveDetail ticketFlowNodeApproveDetail = new TicketFlowNodeApproveDetail();
                    ticketFlowNodeApproveDetail.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_APPROVE_DETAIL));
                    ticketFlowNodeApproveDetail.setTicketDataId(ticketDataId);
                    ticketFlowNodeApproveDetail.setTicketFlowNodeDataId("-1");
                    ticketFlowNodeApproveDetail.setDealUserType(applyUser.getAccountType());
                    ticketFlowNodeApproveDetail.setDealUserId(applyUser.getAccountId());
                    ticketFlowNodeApproveDetail.setDealUserName(applyUser.getAccountName());
                    ticketFlowNodeApproveDetail.setDealOpinion(String.format("工单删除，删除人：%s 删除时间：%s", applyUser.getAccountName(), DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", delNow)));
                    ticketFlowNodeApproveDetail.setDealType(ApproveDealTypeEnum.FINISH);
                    ticketFlowNodeApproveDetail.setDealTypeDescription("工单删除");
                    ticketFlowNodeApproveDetail.setCreateBy(updateBy);
                    ticketFlowNodeApproveDetail.setUpdateBy(updateBy);
                    ticketFlowNodeApproveDetail.setCreateTime(delNow);
                    ticketFlowNodeApproveDetail.setUpdateTime(delNow);
                    ticketFlowNodeApproveDetailService.save(ticketFlowNodeApproveDetail);
                });
            } catch (Exception e) {
                log.error("工单删除异常:", e);
            }
        }
        return new Response<>(null, BizResponseEnums.SUCCESS, "工单删除成功");
    }

    @Override
    public Response batchFinishTickets(BatchFinishTicketsDto batchFinishTicketsDto, String userType, String userId, String userName) {
        log.info("批量关单入参：{},{},{},{}", batchFinishTicketsDto, userType, userId, userName);
        String startTime = batchFinishTicketsDto.getStartTime();
        String endTime = batchFinishTicketsDto.getEndTime();
        if (StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime)) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime twoHoursAgo = now.minusHours(2);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            startTime = twoHoursAgo.format(formatter);
            endTime = now.format(formatter);
        }
        LambdaQueryWrapper<TicketData> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.ge(TicketData::getUpdateTime, startTime);
        lambdaQueryWrapper.le(TicketData::getUpdateTime, endTime);
        lambdaQueryWrapper.eq(TicketData::getTemplateId, templateId);
        lambdaQueryWrapper.like(TicketData::getApplyUser, applyUser);
        if (StringUtils.isNotEmpty(batchFinishTicketsDto.getTicketDataId())) {
            lambdaQueryWrapper.eq(TicketData::getId, batchFinishTicketsDto.getTicketDataId());
        }
        lambdaQueryWrapper.eq(TicketData::getTicketStatus, TicketDataStatusEnum.APPLYING.getCode());
        lambdaQueryWrapper.orderByDesc(TicketData::getUpdateTime).orderByDesc(TicketData::getId);
        int pageNum = 1;
        int pageSize = 100;
        List<TicketData> ticketDataList;
        do {
            PageInfo<TicketData> ticketDataPageInfo = PageHelper.<TicketData>startPage(pageNum, pageSize)
                    .doSelectPageInfo(() -> ticketDataMapper.selectList(lambdaQueryWrapper));
            if (null == ticketDataPageInfo || CollectionUtils.isEmpty(ticketDataPageInfo.getList())) {
                log.info("查询数据为空：{}", ticketDataPageInfo);
                break;
            }
            ticketDataList = ticketDataPageInfo.getList();
            int batchSize = 10; // 每批处理 10 条数据
            int ticketDataListSize = ticketDataList.size();
            for (int i = 0; i < ticketDataListSize; i += batchSize) {
                // 获取当前批次的数据
                List<TicketData> batch = ticketDataList.subList(i, Math.min(i + batchSize, ticketDataListSize));
                List<String> ticketDataIdList = batch.stream().map(it -> it.getId()).collect(Collectors.toList());
                // 处理当前批次的数据
                BatchTicketFlowNodeApproveDto batchTicketFlowNodeApproveDto = new BatchTicketFlowNodeApproveDto();
                batchTicketFlowNodeApproveDto.setTicketDataIdList(ticketDataIdList);
                batchTicketFlowNodeApproveDto.setDealOpinion(batchFinishTicketsDto.getDealOpinion());
                batchTicketFlowNodeApproveDto.setDealType(ApproveDealTypeEnum.FINISH.getCode());
                Response<TicketBatchDto> response = ticketDataApproveService.batchApprove(batchTicketFlowNodeApproveDto, userType, userId, userName);
                if (!response.isSuccess()) {
                    log.error("批量关单异常：{}", response.getMsg());
                    continue;
                }
                if (null != response.getData() && CollectionUtils.isNotEmpty(response.getData().getFailedList())) {
                    log.warn("批量关单信息：{}", response.getData().getFailedList());
                }
            }
            pageNum = pageNum + 1;
        } while (CollectionUtils.isNotEmpty(ticketDataList) && ticketDataList.size() == 100);
        return Response.success();
    }

    @Override
    public Response<List<String>> getTicketApplyIdList(String appid, Long n) {
        if (StringUtils.isEmpty(appid) || Objects.isNull(n) || n < 1) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "参数异常");
        }
        if (n > ticketsBatchLimit) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("批量生成工单id个数不能大于%s", ticketsBatchLimit));
        }
        List<String> ticketApplyIdList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Response<String> response = ticketDataService.getTicketApplyId(appid);
            if (!response.isSuccess()) {
                return Response.error(BizResponseEnums.SAVE_ERROR, String.format("批量生成工单id异常：%s", response.getMsg()));
            }
            ticketApplyIdList.add(response.getData());
        }
        return Response.success(ticketApplyIdList);
    }

    @Override
    public Response<List<BatchDto>> batchCreateTicket(
            @RequestParam(value = "工单列表", example = " ", description = "工单列表")
            List<TicketDataStdDto> ticketDataStdDtoList,
            @RequestParam(value = "用户类型", example = " ", description = "用户类型")
            String userType,
            @RequestParam(value = "用户ID", example = " ", description = "用户ID")
            String userId,
            @RequestParam(value = "用户名称", example = " ", description = "用户名称")
            String userName) {
        if (CollectionUtils.isEmpty(ticketDataStdDtoList) || StringUtils.isAnyEmpty(userType, userId, userName)) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "参数缺失");
        }
        if (ticketDataStdDtoList.size() > 20) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("批量创建工单个数不能大于%s", ticketsBatchLimit));
        }
        List<BatchDto> failedList = new ArrayList<>();
        for (TicketDataStdDto ticketDataStdDto : ticketDataStdDtoList) {
            Response<String> createTicketResponse = ticketDataService.createTicket(ticketDataStdDto, userType, userId, userName);
            if (!createTicketResponse.isSuccess()) {
                String ticketDataId = ticketDataStdDto.getApplyId();
                String errorMsg = String.format("创建工单(id:%s)异常：%s", ticketDataId, createTicketResponse.getMsg());
                log.error(errorMsg);
                BatchDto batchDto = new BatchDto();
                batchDto.setId(ticketDataId);
                batchDto.setErroMsg(errorMsg);
                failedList.add(batchDto);
            }
        }
        return Response.success(failedList);
    }

    @Override
    public Response addTags(TicketData ticketData, String userType, String userId, String userName) {
        if (StringUtils.isEmpty(ticketData.getId()) || StringUtils.isEmpty(ticketData.getTags())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单id或者tag为空");
        }
        TicketData queryTicketData = ticketDataService.selectTicketDataById(ticketData.getId());
        if (Objects.isNull(queryTicketData)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单(id：%s)不存在", ticketData.getId()));
        }
        List<String> existTagList = new ArrayList<>();
        if (StringUtils.isNotEmpty(queryTicketData.getTags())) {
            String existTagStr = queryTicketData.getTags().trim();
            if (existTagStr.startsWith("[") && existTagStr.endsWith("]")) {
                existTagList = JSONObject.parseObject(existTagStr, List.class);
            } else {
                existTagList = new ArrayList<>(Arrays.asList(existTagStr.split(",")));
            }
        }
        List<String> addTagList = JSONObject.parseObject(ticketData.getTags(), List.class);
        ;
        existTagList.addAll(addTagList);
        List<String> existTagListWithoutDuplicates = existTagList.stream()
                .distinct()
                .collect(Collectors.toList());
        existTagListWithoutDuplicates = existTagListWithoutDuplicates.stream().map(it -> "\"" + it + "\"").collect(Collectors.toList());
        TicketData newTicketData = new TicketData();
        newTicketData.setId(ticketData.getId());
        newTicketData.setTags(existTagListWithoutDuplicates.toString());
        newTicketData.setUpdateBy(queryTicketData.getUpdateBy());
        newTicketData.setUpdateTime(new Date());
        boolean updateFlag = ticketDataService.updateById(newTicketData);
        if (!updateFlag) {
            return Response.error(BizResponseEnums.SAVE_ERROR, String.format("工单(id：%s)保存异常", ticketData.getId()));
        }
        return Response.success();
    }

    @Override
    public Response delTags(TicketData ticketData, String userType, String userId, String userName) {
        if (StringUtils.isEmpty(ticketData.getId()) || StringUtils.isEmpty(ticketData.getTags())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单id或者tag为空");
        }
        TicketData queryTicketData = ticketDataService.selectTicketDataById(ticketData.getId());
        if (Objects.isNull(queryTicketData)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单(id：%s)不存在", ticketData.getId()));
        }
        List<String> existTagList = new ArrayList<>();
        if (StringUtils.isNotEmpty(queryTicketData.getTags())) {
            String existTagStr = queryTicketData.getTags().trim();
            if (existTagStr.startsWith("[") && existTagStr.endsWith("]")) {
                existTagList = JSONObject.parseObject(existTagStr, List.class);
            } else {
                existTagList = new ArrayList<>(Arrays.asList(existTagStr.split(",")));
            }
        }
        List<String> delTagList = JSONObject.parseObject(ticketData.getTags(), List.class);
        existTagList.removeAll(delTagList);

        String tagsStr = "";
        if (CollectionUtils.isNotEmpty(existTagList)) {
            List<String> existTagListWithoutDuplicates = existTagList.stream()
                    .distinct()
                    .collect(Collectors.toList());
            existTagListWithoutDuplicates = existTagListWithoutDuplicates.stream().map(it -> "\"" + it + "\"").collect(Collectors.toList());
            tagsStr = existTagListWithoutDuplicates.toString();
        }
        TicketData newTicketData = new TicketData();
        newTicketData.setId(ticketData.getId());
        newTicketData.setTags(tagsStr);
        newTicketData.setUpdateBy(queryTicketData.getUpdateBy());
        newTicketData.setUpdateTime(new Date());
        boolean updateFlag = ticketDataService.updateById(newTicketData);
        if (!updateFlag) {
            return Response.error(BizResponseEnums.SAVE_ERROR, String.format("工单(id：%s)保存异常", ticketData.getId()));
        }
        return Response.success();
    }

    @Override
    public Response addBusiTags(String sign, String tranDataStr, String ticketDataId) {
        List<String> tagList = new ArrayList<>();
        List<TicketFormItemData> ticketFormItemDataList = ticketFormItemDataService.selectTicketFormByDataId(ticketDataId);
        if (CollectionUtils.isEmpty(ticketFormItemDataList)) {
            String errorMsg = String.format("工单（%s）表单数据项为空", ticketDataId);
            log.error(errorMsg);
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, errorMsg);
        }
        for (TicketFormItemData ticketFormItemData : ticketFormItemDataList) {
            String itemLabel = ticketFormItemData.getItemLabel();
            if (itemLabel.contains("优先级")) {
                String itemValue = ticketFormItemData.getItemValue();
                if (StringUtils.isNotEmpty(itemValue) && !itemValue.contains("优先级")) {
                    tagList.add(itemValue + "优先级");
                } else if (StringUtils.isNotEmpty(itemValue) && itemValue.contains("优先级")) {
                    tagList.add(itemValue);
                }
            } else if (itemLabel.contains("来源渠道")) {
                String itemValue = ticketFormItemData.getItemValue();
                if (StringUtils.isNotEmpty(itemValue)) {
                    tagList.add(itemValue);
                }
            }
        }
        if (CollectionUtils.isEmpty(tagList)) {
            log.info(String.format("工单（id:%s）的优先级和来源渠道为空，无需添加tags ", ticketDataId));
            return Response.success();
        }
        List<String> tagListWithoutDuplicates = tagList.stream()
                .distinct()
                .collect(Collectors.toList());
        tagListWithoutDuplicates = tagListWithoutDuplicates.stream().map(it -> "\"" + it + "\"").collect(Collectors.toList());
        TicketData ticketData = new TicketData();
        ticketData.setId(ticketDataId);
        ticketData.setTags(tagListWithoutDuplicates.toString());
        String userType = "ldap";
        String userId = "tfs_system";
        String userName = "tfs_system";
        return addTags(ticketData, userType, userId, userName);
    }

    @Override
    public Response addTags(String ticketDataId, List<String> tags, String userType, String userId, String userName) {

        if (StrUtil.isBlank(ticketDataId) || CollUtil.isEmpty(tags)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "addTags：工单id 或者 tags 为空");
        }

        TicketData ticketData = ticketDataService.selectTicketDataById(ticketDataId);
        if (ObjectUtil.isEmpty(ticketData)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, StrUtil.format("addTags：工单(id：{})不存在", ticketData.getId()));
        }

        List<String> existTagList = parseTagsFromStr(ticketData.getTags());

        Set<String> mergedTagSet = new LinkedHashSet<>(existTagList);
        mergedTagSet.addAll(tags);

        TicketData updateTicketData = new TicketData();
        updateTicketData.setId(ticketDataId);
        updateTicketData.setTags(JSON.toJSONString(new ArrayList<>(mergedTagSet)));
        updateTicketData.setUpdateBy(userName);
        updateTicketData.setUpdateTime(new Date());

        boolean updateFlag = ticketDataService.updateById(updateTicketData);
        if (!updateFlag) {
            return Response.error(BizResponseEnums.SAVE_ERROR,
                    StrUtil.format("addTags: 工单(id：{})保存异常", ticketDataId));
        }

        return Response.success();
    }

    @Override
    public Response delTags(String ticketDataId, List<String> tags, String userType, String userId, String userName) {

        if (StrUtil.isBlank(ticketDataId) || CollUtil.isEmpty(tags)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "delTags：工单id或者 tags 为空");
        }

        TicketData ticketData = ticketDataService.selectTicketDataById(ticketDataId);
        if (ObjectUtil.isEmpty(ticketData)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, StrUtil.format("delTags: 工单(id：{})不存在", ticketDataId));
        }

        List<String> existTagList = parseTagsFromStr(ticketData.getTags());

        List<String> collect = existTagList.stream()
                .filter(tag -> !tags.contains(tag))
                .collect(Collectors.toList());

        TicketData updateTicketData = new TicketData();
        updateTicketData.setId(ticketDataId);
        updateTicketData.setTags(JSON.toJSONString(collect));
        updateTicketData.setUpdateBy(userName);
        updateTicketData.setUpdateTime(new Date());

        boolean updateFlag = ticketDataService.updateById(updateTicketData);
        if (!updateFlag) {
            return Response.error(BizResponseEnums.SAVE_ERROR,
                    StrUtil.format("工单(id：{}})保存异常", ticketDataId));
        }

        return Response.success();
    }

    private List<String> parseTagsFromStr(String tagsStr) {

        if (StrUtil.isBlank(tagsStr)) {
            return new ArrayList<>();
        }

        try {
            List<String> tagList = JSON.parseArray(tagsStr, String.class);
            return CollUtil.isNotEmpty(tagList) ? tagList : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public Response addNewTags(String ticketDataId, List<NewTag> newTags, String userType, String userId, String userName) {
        if (StrUtil.isBlank(ticketDataId) || CollUtil.isEmpty(newTags)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "addNewTags：工单id 或者 newTags 为空");
        }

        TicketData ticketData = ticketDataService.selectTicketDataById(ticketDataId);
        if (ObjectUtil.isEmpty(ticketData)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION,
                    StrUtil.format("addNewTags：工单(id：{})不存在", ticketDataId));
        }

        List<NewTag> existNewTagList = parseNewTagsFromStr(ticketData.getNewTags());

        // tagValue 去重
        Map<String, NewTag> mergedTagMap = existNewTagList.stream()
                .collect(Collectors.toMap(
                        tag -> StrUtil.isNotBlank(tag.getTagUniqueValue()) ? tag.getTagUniqueValue() : tag.getTagValue(),
                        tag -> tag,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new));

        // 合并
        newTags.forEach(newTag -> {
            String key = StrUtil.isNotBlank(newTag.getTagUniqueValue()) ? newTag.getTagUniqueValue() : newTag.getTagValue();
            mergedTagMap.put(key, newTag);
        });

        // 更新工单
        TicketData updateTicketData = new TicketData();
        updateTicketData.setId(ticketDataId);
        updateTicketData.setNewTags(JSON.toJSONString(new ArrayList<>(mergedTagMap.values())));
        updateTicketData.setUpdateBy(userName);
        updateTicketData.setUpdateTime(new Date());

        boolean updateFlag = ticketDataService.updateById(updateTicketData);
        if (!updateFlag) {
            return Response.error(BizResponseEnums.SAVE_ERROR,
                    StrUtil.format("addNewTags: 工单(id：{})保存异常", ticketDataId));
        }

        return Response.success();
    }

    @Override
    public Response delNewTags(String ticketDataId, List<NewTag> delValues, String userType, String userId, String userName) {

        if (StrUtil.isBlank(ticketDataId) || CollUtil.isEmpty(delValues)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "delNewTags：工单id或者 delValues 为空");
        }

        TicketData ticketData = ticketDataService.selectTicketDataById(ticketDataId);
        if (ObjectUtil.isEmpty(ticketData)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION,
                    StrUtil.format("delNewTags: 工单(id：{})不存在", ticketDataId));
        }

        // 解析已有的NewTag列表
        List<NewTag> existNewTagList = parseNewTagsFromStr(ticketData.getNewTags());

        // 构建要删除的标签的标识集合（优先使用 tagUniqueValue，为空时使用 tagValue）
        Set<String> delValueSet = delValues.stream()
                .map(delTag -> StrUtil.isNotBlank(delTag.getTagUniqueValue())
                        ? delTag.getTagUniqueValue()
                        : delTag.getTagValue())
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());

        // 过滤掉要删除的标签
        List<NewTag> remainingNewTags = existNewTagList.stream()
                .filter(existTag -> {
                    String compareValue = StrUtil.isNotBlank(existTag.getTagUniqueValue())
                            ? existTag.getTagUniqueValue()
                            : existTag.getTagValue();
                    return !delValueSet.contains(compareValue);
                })
                .collect(Collectors.toList());

        // 更新工单
        TicketData updateTicketData = new TicketData();
        updateTicketData.setId(ticketDataId);
        updateTicketData.setNewTags(JSON.toJSONString(remainingNewTags));
        updateTicketData.setUpdateBy(userName);
        updateTicketData.setUpdateTime(new Date());

        boolean updateFlag = ticketDataService.updateById(updateTicketData);
        if (!updateFlag) {
            return Response.error(BizResponseEnums.SAVE_ERROR,
                    StrUtil.format("delNewTags: 工单(id：{})保存异常", ticketDataId));
        }

        return Response.success();
    }

    private List<NewTag> parseNewTagsFromStr(String newTagsStr) {
        if (StrUtil.isBlank(newTagsStr)) {
            return new ArrayList<>();
        }
        try {
            List<NewTag> newTagList = JSON.parseArray(newTagsStr, NewTag.class);
            return CollUtil.isNotEmpty(newTagList) ? newTagList : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}