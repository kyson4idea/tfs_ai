package com.smy.tfs.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Sets;
import com.smy.tfs.api.dto.*;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.ticket_sla_service.NewTag;
import com.smy.tfs.api.enums.ApproveDealTypeEnum;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostloanServiceImpl implements IPostloanService {

    private static final String ACTION_TYPE = "动作类型";

    private static final String CASE_REVIEW_FAILED = "结案审核失败";

    private static final String POST_LOAN = "贷后处理";

    private static final String CUSTOMER_SERVICE_POST_LOAN = "客服贷后业务";

    @Resource
    ITicketDataService ticketDataService;

    @Resource
    ITicketDataActService ticketDataActService;

    @Override
    public Response<String> addTagsForPostLoanTicketsCallback(String sign, String ticketEventTag, String ticketDataId) {

        // 贷后打标 贷后处理 审批前
        if (StrUtil.isBlank(ticketDataId) || StrUtil.isBlank(ticketEventTag)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单id && EventTag不能为空");
        }

        try {

            JSONObject callBackJson = JSONObject.parseObject(ticketEventTag);
            if (callBackJson.containsKey("NodeName") && StrUtil.isNotBlank(callBackJson.getString("NodeName"))) {
                String nodeValue = callBackJson.getString("NodeName");

                Response<TicketDataDto> ticketDataResponse = ticketDataService.selectFullTicketDataById(new ReqParam(ticketDataId));
                if (!ticketDataResponse.getCode().equals("200") || ObjectUtil.isEmpty(ticketDataResponse.getData())) {
                    return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION,
                            StrUtil.format("查询工单失败或工单不存在 id: {}", ticketDataId));
                }

                TicketDataDto ticketDataDto = ticketDataResponse.getData();
                TicketFlowDataDto ticketFlowDataDto = ticketDataDto.getTicketFlowDataDto();

                if (ObjectUtil.isEmpty(ticketFlowDataDto) || CollUtil.isEmpty(ticketFlowDataDto.getTicketFlowNodeDataDtoList())) {
                    return Response.success("当前工单流程节点为空，无需处理");
                }

                Optional<TicketFlowNodeDataDto> flowNodeDataDto = ticketFlowDataDto.getTicketFlowNodeDataDtoList().stream()
                        .filter(node -> nodeValue.equals(node.getNodeName()))
                        .findFirst();

                if (flowNodeDataDto.isPresent()) {
                    // 创建NewTag对象
                    NewTag newTag = new NewTag();
                    newTag.setTagValue(POST_LOAN);
                    newTag.setTagUniqueValue(POST_LOAN + "_" + System.currentTimeMillis());
                    newTag.setTagTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

                    List<NewTag> newTagsToAdd = Collections.singletonList(newTag);
                    Response resp = ticketDataActService.addNewTags(ticketDataId, newTagsToAdd, "ldap", "tfs_system", "tfs_system");

                    if (resp != null && resp.getCode().equals("200")) {
                        log.info("工单 {} 成功添加 {} 标签", ticketDataId, POST_LOAN);
                    } else {
                        log.warn("工单 {} 添加标签失败: {}", ticketDataId, resp != null ? resp.getMsg() : "未知错误");
                    }
                }
            }
        } catch (Exception e) {
            log.error("处理工单标签回调异常, ticketDataId: {}, ticketEventTag: {}", ticketDataId, ticketEventTag, e);
            return Response.error(BizResponseEnums.SYSTEM_ERROR, "处理异常");
        }
        return Response.success();
    }

    @Override
    public Response<String> addTagsForCustomerServiceTicketsCallback(String sign, String ticketEventTag, String ticketDataId) {

        // 进入贷后 审批前
        if (StrUtil.isBlank(ticketDataId)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单id && EventTag不能为空");
        }

        try {

            Response<TicketDataDto> ticketDataResponse = ticketDataService.selectFullTicketDataById(new ReqParam(ticketDataId));
            if (!ticketDataResponse.getCode().equals("200") || ObjectUtil.isEmpty(ticketDataResponse.getData())) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION,
                        StrUtil.format("查询工单失败或工单不存在 id: {}", ticketDataId));
            }

            TicketDataDto ticketDataDto = ticketDataResponse.getData();
            TicketFormDataDto ticketFormDataDto = ticketDataDto.getTicketFormDataDto();

            if (ObjectUtil.isEmpty(ticketFormDataDto) || CollUtil.isEmpty(ticketFormDataDto.getTicketFormItemDataDtoList())) {
                return Response.success("当前工单表单数据为空，无需处理");
            }

            Set<String> targetFeedbackChannels = Sets.newHashSet("内部-12378专线");
            Set<String> targetTicketTypes = Sets.newHashSet(
                    "贷后普诉-逾期协商", "贷后普诉-操作类",
                    "贷后资深-操作类", "贷后资深-催收投诉",
                    "贷后资深-逾期协商", "贷后普诉-催收投诉"
            );

            String feedbackChannel = ticketFormDataDto.getTicketFormItemDataDtoList().stream()
                    .filter(item -> "反馈渠道".equals(item.getItemLabel()))
                    .map(TicketFormItemDataDto::getItemValue)
                    .filter(StrUtil::isNotBlank)
                    .findFirst().orElse("");

            String ticketType = ticketFormDataDto.getTicketFormItemDataDtoList().stream()
                    .filter(item -> "工单类型".equals(item.getItemLabel()))
                    .map(TicketFormItemDataDto::getItemValue)
                    .filter(StrUtil::isNotBlank)
                    .findFirst().orElse("");

            if (targetFeedbackChannels.contains(feedbackChannel) || targetTicketTypes.contains(ticketType)) {
                // 创建NewTag对象
                NewTag newTag = new NewTag();
                newTag.setTagValue(CUSTOMER_SERVICE_POST_LOAN);
                newTag.setTagUniqueValue(null);
                newTag.setTagTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

                List<NewTag> newTagsToAdd = Collections.singletonList(newTag);
                Response resp = ticketDataActService.addNewTags(ticketDataId, newTagsToAdd, "ldap", "tfs_system", "tfs_system");

                if (resp != null && resp.getCode().equals("200")) {
                    log.info("工单 {} 成功添加 {} 标签，反馈渠道: {}, 工单类型: {}", ticketDataId, CUSTOMER_SERVICE_POST_LOAN, feedbackChannel, ticketType);
                } else {
                    log.error("工单 {} 添加标签失败: {}", ticketDataId, resp.toString());
                }
            }

        } catch (Exception e) {
            log.error("处理客服工单标签回调异常, ticketDataId: {}, ticketEventTag: {}", ticketDataId, ticketEventTag, e);
            return Response.error(BizResponseEnums.SYSTEM_ERROR, "处理客服工单标签回调异常");
        }

        return Response.success();
    }


    @Override
    public Response<List<AccountInfo>> autoAssignToRecentHandlerCallback(String sign, String ticketEventTag, String ticketDataId) {

        if (StrUtil.isBlank(ticketDataId) || StrUtil.isBlank(ticketEventTag)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单id && EventTag不能为空");
        }
        try {

            Response<TicketDataDto> ticketDataResponse = ticketDataService.selectFullTicketDataById(new ReqParam(ticketDataId));
            if (!ticketDataResponse.getCode().equals("200") || ObjectUtil.isEmpty(ticketDataResponse.getData())) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION,
                        StrUtil.format("查询工单失败或工单不存在 id: {}", ticketDataId));
            }

            TicketDataDto ticketDataDto = ticketDataResponse.getData();
            TicketFormDataDto ticketFormDataDto = ticketDataDto.getTicketFormDataDto();

            if (ObjectUtil.isEmpty(ticketFormDataDto) || CollUtil.isEmpty(ticketFormDataDto.getTicketFormItemDataDtoList())) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "当前工单表单数据为空");
            }

            boolean hasFailedAction = ticketFormDataDto.getTicketFormItemDataDtoList().stream()
                    .anyMatch(item -> ACTION_TYPE.equals(item.getItemLabel()) && CASE_REVIEW_FAILED.equals(item.getItemValue()));

            if (!hasFailedAction) {
                log.info("工单动作类型不是结案审核失败 id: {}", ticketDataId);
                return Response.success("工单动作类型不是结案审核失败，无需处理");
            }

            TicketFlowDataDto ticketFlowDataDto = ticketDataDto.getTicketFlowDataDto();
            if (ObjectUtil.isEmpty(ticketFlowDataDto) || CollUtil.isEmpty(ticketFlowDataDto.getTicketFlowNodeDataDtoList())) {
                return Response.success("工单流程数据为空，无需处理");
            }

            TicketFlowNodeApproveDetailDto recentHandler = findRecentHandler(ticketFlowDataDto.getTicketFlowNodeDataDtoList());
            if (ObjectUtil.isEmpty(recentHandler) || StrUtil.isBlank(recentHandler.getDealUserId())) {
                log.info("工单 {} 未找到符合条件的最近处理人", ticketDataId);
                return Response.success("未找到符合条件的最近处理人");
            }
            AccountInfo accountInfo = new AccountInfo();
            accountInfo.setAccountId(recentHandler.getDealUserId());
            accountInfo.setAccountName(recentHandler.getDealUserName());
            accountInfo.setAccountType(recentHandler.getDealUserType());

            List<AccountInfo> accountInfos = Collections.singletonList(accountInfo);
            return Response.success(accountInfos);

        } catch (Exception e) {
            log.error("autoAssignToRecentHandlerCallback 回调异常, ticketDataId: {}, ticketEventTag: {}", ticketDataId, ticketEventTag, e);
            return Response.error(BizResponseEnums.SYSTEM_ERROR, "处理异常");
        }

    }

    /**
     * 找出最近审批人
     *
     * @param nodeList
     * @return
     */
    private TicketFlowNodeApproveDetailDto findRecentHandler(List<TicketFlowNodeDataDto> nodeList) {
        // 合并所有节点的审批详情
        List<TicketFlowNodeApproveDetailDto> allApproveDetails = nodeList.stream()
                .filter(node -> CollUtil.isNotEmpty(node.getApproveDetailList()))
                .flatMap(node -> node.getApproveDetailList().stream())
                .filter(detail -> detail != null && detail.getUpdateTime() != null)
                .collect(Collectors.toList());

        if (CollUtil.isEmpty(allApproveDetails)) {
            return new TicketFlowNodeApproveDetailDto();
        }

        // 按updateTime倒序排序，找到最新的符合条件的记录
        Optional<TicketFlowNodeApproveDetailDto> recentHandler = allApproveDetails.stream()
                .filter(detail -> ApproveDealTypeEnum.PASS.equals(detail.getDealType())
                        && isValidUserType(detail.getDealUserType())
                        && isValidLdapId(detail.getDealUserId()))
                .sorted((d1, d2) -> d2.getUpdateTime().compareTo(d1.getUpdateTime()))
                .findFirst();

        return recentHandler.orElse(new TicketFlowNodeApproveDetailDto());
    }

    private boolean isValidLdapId(String userId) {
        if (StrUtil.isBlank(userId)) {
            return false;
        }
        // 校验是否包含字母和数字
        return userId.matches("^[a-zA-Z]+\\d+$");
    }

    private boolean isValidUserType(String userType) {
        if (StrUtil.isBlank(userType)) {
            return false;
        }
        String lowerUserType = userType.toLowerCase();
        //ncs 催收
        return "ldap".equals(lowerUserType) || "ncs".equals(lowerUserType);
    }

    // 测试
    @Override
    public Response<String> addTagsForPostLoanTicketsCallback(TicketDataDto ticketData) {

        TicketDataDto ticketDataDto = ticketData;
        TicketFlowDataDto ticketFlowDataDto = ticketDataDto.getTicketFlowDataDto();

        if (ObjectUtil.isEmpty(ticketFlowDataDto) || CollUtil.isEmpty(ticketFlowDataDto.getTicketFlowNodeDataDtoList())) {
            return Response.success("当前工单流程节点为空，无需处理");
        }

        Optional<TicketFlowNodeDataDto> flowNodeDataDto = ticketFlowDataDto.getTicketFlowNodeDataDtoList().stream()
                .filter(node -> "贷后处理".equals(node.getNodeName()))
                .findFirst();

        if (flowNodeDataDto.isPresent()) {
            // 创建NewTag对象
            NewTag newTag = new NewTag();
            newTag.setTagValue(POST_LOAN);
            newTag.setTagUniqueValue(null);
            newTag.setTagTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

            List<NewTag> newTagsToAdd = Collections.singletonList(newTag);
            Response resp = ticketDataActService.addNewTags(ticketData.getId(), newTagsToAdd, "ldap", "tfs_system", "tfs_system");

            return resp;
        }
        return null;
    }

    @Override
    public Response<String> addTagsForCustomerServiceTicketsCallback(TicketDataDto ticketData) {
        TicketDataDto ticketDataDto = ticketData;
        TicketFormDataDto ticketFormDataDto = ticketDataDto.getTicketFormDataDto();

        if (ObjectUtil.isEmpty(ticketFormDataDto) || CollUtil.isEmpty(ticketFormDataDto.getTicketFormItemDataDtoList())) {
            return Response.success("当前工单表单数据为空，无需处理");
        }

        Set<String> targetFeedbackChannels = Sets.newHashSet("内部-12378专线");
        Set<String> targetTicketTypes = Sets.newHashSet(
                "贷后普诉-逾期协商", "贷后普诉-操作类",
                "贷后资深-操作类", "贷后资深-催收投诉",
                "贷后资深-逾期协商", "贷后普诉-催收投诉"
        );

        String feedbackChannel = ticketFormDataDto.getTicketFormItemDataDtoList().stream()
                .filter(item -> "反馈渠道".equals(item.getItemLabel()))
                .map(TicketFormItemDataDto::getItemValue)
                .filter(StrUtil::isNotBlank)
                .findFirst().orElse("");

        String ticketType = ticketFormDataDto.getTicketFormItemDataDtoList().stream()
                .filter(item -> "工单类型".equals(item.getItemLabel()))
                .map(TicketFormItemDataDto::getItemValue)
                .filter(StrUtil::isNotBlank)
                .findFirst().orElse("");

        if (targetFeedbackChannels.contains(feedbackChannel) || targetTicketTypes.contains(ticketType)) {
            // 创建NewTag对象
            NewTag newTag = new NewTag();
            newTag.setTagValue(CUSTOMER_SERVICE_POST_LOAN);
            newTag.setTagUniqueValue(null);
            newTag.setTagTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

            List<NewTag> newTagsToAdd = Collections.singletonList(newTag);

        }
        return null;
    }

    @Override
    public Response<List<AccountInfo>> autoAssignToRecentHandlerCallback(TicketDataDto ticketData){
        TicketDataDto ticketDataDto = ticketData;
        TicketFormDataDto ticketFormDataDto = ticketDataDto.getTicketFormDataDto();

        if (ObjectUtil.isEmpty(ticketFormDataDto) || CollUtil.isEmpty(ticketFormDataDto.getTicketFormItemDataDtoList())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "当前工单表单数据为空");
        }

        boolean hasFailedAction = ticketFormDataDto.getTicketFormItemDataDtoList().stream()
                .anyMatch(item -> ACTION_TYPE.equals(item.getItemLabel()) && CASE_REVIEW_FAILED.equals(item.getItemValue()));

        if (!hasFailedAction) {

            return Response.success("工单动作类型不是结案审核失败，无需处理");
        }

        TicketFlowDataDto ticketFlowDataDto = ticketDataDto.getTicketFlowDataDto();
        if (ObjectUtil.isEmpty(ticketFlowDataDto) || CollUtil.isEmpty(ticketFlowDataDto.getTicketFlowNodeDataDtoList())) {
            return Response.success("工单流程数据为空，无需处理");
        }

        TicketFlowNodeApproveDetailDto recentHandler = findRecentHandler(ticketFlowDataDto.getTicketFlowNodeDataDtoList());
        if (ObjectUtil.isEmpty(recentHandler) || StrUtil.isBlank(recentHandler.getDealUserId())) {

            return Response.success("未找到符合条件的最近处理人");
        }
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountId(recentHandler.getDealUserId());
        accountInfo.setAccountName(recentHandler.getDealUserName());
        accountInfo.setAccountType(recentHandler.getDealUserType());

        List<AccountInfo> accountInfos = Collections.singletonList(accountInfo);
        return Response.success(accountInfos);
    }
}
