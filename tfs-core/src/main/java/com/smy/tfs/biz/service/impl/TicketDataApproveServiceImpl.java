package com.smy.tfs.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.smy.framework.core.util.SequenceUtil;
import com.smy.tfs.api.dbo.*;
import com.smy.tfs.api.dto.*;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.AccountInfoDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.dynamic.BatchDto;
import com.smy.tfs.api.dto.dynamic.TicketBatchDto;
import com.smy.tfs.api.dto.dynamic.TicketFormItemStdDto;
import com.smy.tfs.api.dto.out.TicketActionDto;
import com.smy.tfs.api.enums.*;
import com.smy.tfs.api.service.*;
import com.smy.tfs.biz.bo.DubboServiceConfig;
import com.smy.tfs.biz.bo.TicketFlowNodeApproveFinishBO;
import com.smy.tfs.biz.config.TfSJumpUrlProperties;
import com.smy.tfs.biz.mapper.TicketDataMapper;
import com.smy.tfs.biz.mapper.TicketFlowEventDataMapper;
import com.smy.tfs.biz.service.INotificationBizService;
import com.smy.tfs.biz.service.NotificationService;
import com.smy.tfs.biz.service.TicketDataApproveService;
import com.smy.tfs.biz.service.TicketFlowNodeApproveDetailService;
import com.smy.tfs.common.exception.ServiceException;
import com.smy.tfs.common.utils.SecurityUtils;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import com.smy.tfs.common.utils.http.HttpUtils;
import com.smy.tfs.framework.config.DynamicDubboConsumer;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.apidocs.annotations.ApiDoc;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.smy.tfs.api.enums.ExecutorTypeEnum.*;

/**
 * @author z01140
 * @Package: com.smy.tfs.biz.service.impl
 * @Description: 工单审批服务（审批通过，审批驳回，撤回）
 * @CreateDate 2024/4/25 12:23
 * @UpdateDate 2024/4/25 12:23
 */
@Slf4j
@Service
public class TicketDataApproveServiceImpl implements TicketDataApproveService {

    @Resource
    ITicketFlowNodeDataService flowNodeDataService;
    @Resource
    ITicketFlowNodeActionDataService ticketFlowNodeActionDataService;
    @Resource
    ITicketFlowEventDataService flowEventDataService;
    @Resource
    TicketFlowEventDataMapper ticketFlowEventDataMapper;
    @Resource
    TransactionTemplate transactionTemplate;
    @Resource
    TicketFlowNodeApproveDetailService ticketFlowNodeApproveDetailService;
    @Resource
    TicketDataMapper ticketDataMapper;
    @Resource
    DynamicDubboConsumer dynamicDubboConsumer;
    @Resource
    ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    ITicketDataService ticketDataService;
    @Resource
    ITicketTemplateService ticketTemplateService;
    @Resource
    ITicketAccountMappingService ticketAccountMappingService;
    @Resource
    ITicketFlowNodeExecutorDataService executorDataService;
    @Resource
    NotificationService notificationService;
    @Resource
    ITicketAccountService ticketAccountService;
    @Resource
    TfSJumpUrlProperties tfSJumpUrlProperties;
    @Resource
    INotificationBizService notificationBizService;
    @Resource
    ITicketAppService ticketAppService;
    @Resource
    ITicketFormItemDataService ticketFormItemDataService;

    /**
     * @param actionDto
     * @param userType
     * @param userId
     * @param userName
     * @return
     */
    @Override
    @ApiDoc(value = "执行动作（审批/修改工单）", description = "执行动作（审批/修改工单）")
    public Response<String> act(TicketActionDto actionDto, String userType, String userId, String userName) {
        if (actionDto == null) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "操作参数为空");
        }
        TicketFlowNodeActionData ticketFlowNodeActionData = ticketFlowNodeActionDataService.lambdaQuery()
                .isNull(TicketFlowNodeActionData::getDeleteTime)
                .eq(TicketFlowNodeActionData::getTicketDataId, actionDto.getTicketID())
                .eq(TicketFlowNodeActionData::getTicketFlowNodeDataId, actionDto.getActNodeId())
                .eq(TicketFlowNodeActionData::getActionName, actionDto.getActName()).oneOpt().orElse(null);
        if (ticketFlowNodeActionData == null) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("对应工单ID:%s 节点ID:%s 动作名称:%s 不存在", actionDto.getTicketID(), actionDto.getActNodeId(), actionDto.getActName()));
        }
        if (com.smy.tfs.common.utils.StringUtils.isEmpty(ticketFlowNodeActionData.getActionValue())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("actionValue:%s 参数格式不正确", ticketFlowNodeActionData.getActionValue()));
        }
        com.alibaba.fastjson2.JSONObject jsonValue = null;
        try {
            jsonValue = com.alibaba.fastjson2.JSONObject.parseObject(ticketFlowNodeActionData.getActionValue());
        } catch (Exception e) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("actionValue:%s json解析失败：%s", ticketFlowNodeActionData.getActionValue(), e.getMessage()));
        }
        if (ActionTypeEnum.APPROVE_PASS.getCode().equals(ticketFlowNodeActionData.getActionType())) {
            TicketFlowNodeApproveDto approveDto = new TicketFlowNodeApproveDto();
            approveDto.setTicketID(actionDto.getTicketID());
            approveDto.setDealType(jsonValue.getString("approve"));
            approveDto.setDealDescription(actionDto.getActDescription());
            approveDto.setDealOpinion(actionDto.getActOpinion());
            approveDto.setDealNodeId(actionDto.getActNodeId());
            approveDto.setDealMode(actionDto.getDealMode());
            return approve(approveDto, userType, userId, userName);
        }
        if (ActionTypeEnum.UPDATE_TICKET.getCode().equals(ticketFlowNodeActionData.getActionType())) {
            TicketFormUpdateDto updateDto = new TicketFormUpdateDto();
            updateDto.setTicketDataId(actionDto.getTicketID());
            updateDto.setDealDescription(actionDto.getActDescription());
            updateDto.setDealOpinion(actionDto.getActOpinion());
            updateDto.setUpdateMode(actionDto.getDealMode());
            com.alibaba.fastjson2.JSONArray jsonActValues = jsonValue.getJSONArray("setValue");
            if (CollectionUtils.isEmpty(jsonActValues)) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("setValue:%s actValue参数格式不正确3", ticketFlowNodeActionData.getActionValue()));
            }
            List<TicketFormItemStdDto> formItems = new ArrayList<>();
            for (int i = 0; i < jsonActValues.size(); i++) {
                TicketFormItemStdDto itemStdDto = new TicketFormItemStdDto();
                com.alibaba.fastjson2.JSONObject jsonObject = jsonActValues.getJSONObject(i);
                itemStdDto.setTemplateId(jsonObject.getString("templateId"));
                itemStdDto.setValue(jsonObject.getString("value"));
                itemStdDto.setDisplayValue(jsonObject.getString("displayValue"));
                itemStdDto.setDisplayAble(jsonObject.getString("displayAble"));
                itemStdDto.setRenderAble(jsonObject.getString("renderAble"));
                itemStdDto.setType(jsonObject.getString("type"));
                formItems.add(itemStdDto);
            }
            updateDto.setFormItems(formItems);
            return ticketDataService.updateTicketFormData(updateDto, userType, userId, userName);
        }
        return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("操作类型不正确:%s", ticketFlowNodeActionData.getActionType()));
    }

    @Override
    @ApiDoc(value = "审批（通过/驳回）", description = "审批（通过/驳回）")
    public Response approve(String ticketID, String dealType, String dealOpinion, String dealUserType, String dealUserId, String dealUserName, String dealNodeId) {
        TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByAccountIdAndType(dealUserId, dealUserType);
        if (ticketAccountMapping == null || StrUtil.isBlank(ticketAccountMapping.getSameOriginId())) {
            return Response.error(BizResponseEnums.UNKNOW_EXCEPTION_CODE, "未找到有效用户信息");
        }

        TicketFlowNodeApproveDto approveDto = new TicketFlowNodeApproveDto();
        approveDto.setTicketID(ticketID);
        approveDto.setDealType(dealType);
        approveDto.setDealOpinion(dealOpinion);
        approveDto.setDealNodeId(dealNodeId);
        return this.approve(approveDto, null, ticketAccountMapping.getSameOriginId(), dealUserType, dealUserId, dealUserName);
    }

    public Response<TicketDataDto> selectTicketData(ReqParam reqParam) {
        return ticketDataService.selectFullTicketDataById(reqParam);
    }

    @Override
    @ApiDoc(value = "审批（通过/驳回）", description = "审批（通过/驳回）")
    public Response approve(TicketFlowNodeApproveDto approveDto, String userType, String userId, String userName) {
        TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByAccountIdAndType(userId, userType);
        if (ticketAccountMapping == null || StrUtil.isBlank(ticketAccountMapping.getSameOriginId())) {
            return Response.error(BizResponseEnums.UNKNOW_EXCEPTION_CODE, "未找到有效用户信息");
        }
        return this.approve(approveDto, null, ticketAccountMapping.getSameOriginId(), userType, userId, userName);
    }

    @Override
    public Response<TicketBatchDto> batchApprove(BatchTicketFlowNodeApproveDto batchTicketFlowNodeApproveDto, String userType, String userId, String userName) {
        if (CollectionUtils.isEmpty(batchTicketFlowNodeApproveDto.getTicketDataIdList())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单Id列表为空");
        }
        TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByAccountIdAndType(userId, userType);
        if (ticketAccountMapping == null || StrUtil.isBlank(ticketAccountMapping.getSameOriginId())) {
            return Response.error(BizResponseEnums.UNKNOW_EXCEPTION_CODE, "未找到有效用户信息");
        }
        List<String> ticketIdList = batchTicketFlowNodeApproveDto.getTicketDataIdList();
        List<TicketData> ticketDataList = ticketDataService.lambdaQuery()
                .select(TicketData::getId, TicketData::getCurrentNodeId)
                .in(TicketData::getId, ticketIdList)
                .isNull(TicketData::getDeleteTime)
                .list();
        Map<String, String> currentNodeIdMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(ticketDataList)) {
            currentNodeIdMap = ticketDataList.stream()
                    .collect(Collectors.toMap(TicketData::getId, TicketData::getCurrentNodeId));
        }
        List<TicketFlowNodeApproveDto> approveDtoList = new ArrayList<>();
        for (String ticketID : ticketIdList) {
            TicketFlowNodeApproveDto approveDto = new TicketFlowNodeApproveDto();
            approveDto.setTicketID(ticketID);
            approveDto.setDealType(batchTicketFlowNodeApproveDto.getDealType());
            approveDto.setDealOpinion(batchTicketFlowNodeApproveDto.getDealOpinion());
            approveDto.setDealDescription(batchTicketFlowNodeApproveDto.getDealDescription());
            approveDto.setDealMode(batchTicketFlowNodeApproveDto.getDealMode());
            approveDto.setDealNodeId(currentNodeIdMap.get(ticketID));
            approveDtoList.add(approveDto);
        }
        TicketBatchDto ticketBatchDto = new TicketBatchDto();
        List<BatchDto> batchDtoList = new ArrayList<>();
        for (TicketFlowNodeApproveDto approveDto : approveDtoList) {
            String dealType = approveDto.getDealType();
            String ticketID = approveDto.getTicketID();
            //如果动作重载了，则不允许该工单通过和驳回，跳过
            if (Arrays.asList("PASS", "REJECT").contains(dealType)) {
                List<TicketFlowNodeActionData> ticketFlowNodeActionDataList = ticketFlowNodeActionDataService.lambdaQuery().eq(TicketFlowNodeActionData::getTicketDataId, ticketID)
                        .isNull(TicketFlowNodeActionData::getDeleteTime)
                        .list();
                if (CollectionUtils.isNotEmpty(ticketFlowNodeActionDataList)) {
                    TicketFlowNodeActionData ticketFlowNodeActionData = ticketFlowNodeActionDataList.stream()
                            .filter(it -> Arrays.asList(ActionTypeEnum.APPROVE_PASS, ActionTypeEnum.APPROVE_REJECT).contains(it.getActionType()))
                            .findFirst().orElse(null);
                    if (Objects.isNull(ticketFlowNodeActionData)) {
                        log.error(String.format("工单(id:%s)处理(%s)异常：动作已重载,不允许单通过和驳回", approveDto.getTicketID(), dealType));
                        BatchDto batchDto = new BatchDto();
                        batchDto.setId(approveDto.getTicketID());
                        batchDto.setErroMsg(String.format("工单（id:%s）处理失败", ticketID));
                        batchDtoList.add(batchDto);
                        continue;
                    }
                }
            }
            Response approveResponse = this.approve(approveDto, null, ticketAccountMapping.getSameOriginId(), userType, userId, userName);
            if (!approveResponse.isSuccess()) {
                String dealTypeMsg;
                switch (dealType) {
                    case "PASS":
                        dealTypeMsg = "审批通过";
                        break;
                    case "REJECT":
                        dealTypeMsg = "审批拒绝";
                        break;
                    case "FINISH":
                        dealTypeMsg = "关单";
                        break;
                    default:
                        dealTypeMsg = "未知";
                        break;
                }
                log.error(String.format("工单(id:%s)%s异常：%s", approveDto.getTicketID(), dealTypeMsg, approveResponse.getMsg()));
                String errorMsg = String.format("工单(id:%s)处理失败", approveDto.getTicketID());
                BatchDto batchDto = new BatchDto();
                batchDto.setId(approveDto.getTicketID());
                batchDto.setErroMsg(errorMsg);
                batchDtoList.add(batchDto);
            }
        }
        ticketBatchDto.setFailedList(batchDtoList);
        return Response.success(ticketBatchDto);
    }

    public Response approve(TicketFlowNodeApproveDto approveDto, AddTicketFlowNewNodeDto addNodeParam,
                            String sameOriginId, String userType, String userId, String userName) {
        String ticketID = approveDto.getTicketID();
        String dealType = approveDto.getDealType();
        String dealDescription = approveDto.getDealDescription();
        String dealOpinion = approveDto.getDealOpinion();
        AccountInfo dealUser = new AccountInfo(sameOriginId, userType, userId, userName);
        String mode = approveDto.getMode();
        if (StringUtils.isEmpty(mode) && StringUtils.isNotEmpty(approveDto.getDealMode())) {
            mode = approveDto.getDealMode();
        }
        //参数校验
        if (StringUtils.isAnyBlank(ticketID, dealType, userType, userId)) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "参数缺失");
        }

        //数据查询, 工单数据
        Response<TicketDataDto> ticketDataDtoResponse = ticketDataService.selectFullTicketDataById(new ReqParam(ticketID));
        if (!ticketDataDtoResponse.isSuccess()) {
            return ticketDataDtoResponse;
        }
        var ticketDataDto = ticketDataDtoResponse.getData();

        /*业务逻辑*/
        if (Arrays.asList("KF-SCS", "smy-kefu-20240528").contains(ticketDataDto.getAppId()) && "提交主管审批".equals(ticketDataDto.getCurrentNodeName())) {
            TicketFormUpdateDto updateDto = new TicketFormUpdateDto();
            updateDto.setTicketDataId(ticketDataDto.getId());
            updateDto.setDealOpinion(dealOpinion);
            updateDto.setUpdateMode("DYNAMIC");
            updateDto.setMode("DYNAMIC");
            switch (dealType) {
                case "PASS":
                    updateDto.setFormItems(Arrays.asList(new TicketFormItemStdDto("ticket_action", "审批通过")));
                    updateDto.setDealDescription("审批通过");
                    break;
                case "REJECT":
                    updateDto.setFormItems(Arrays.asList(new TicketFormItemStdDto("ticket_action", "审批拒绝")));
                    updateDto.setDealDescription("审批拒绝");
                    break;
                default:
                    return new Response(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "提交主管审批节点 处理类型异常:" + dealType);
            }
            return ticketDataService.updateTicketFormData(updateDto, userType, userId, userName);
        }

        //数据处理 工单流程数据
        TicketFlowDataDto flowData = ticketDataDto.getTicketFlowDataDto();
        if (flowData == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID：{%s} 不存在流程数据", ticketDataDto.getId()));
        }

        //工单流程节点数据
        List<TicketFlowNodeDataDto> flowNodeListData = flowData.getTicketFlowNodeDataDtoList();
        if (flowNodeListData == null || flowNodeListData.size() == 0) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID：{%s} 不存在流程节点数据", ticketDataDto.getId()));
        }


        String currentFlowNodeDataId = ticketDataDto.getCurrentNodeId();
        //前置幂等判断
        if (StringUtils.isNotBlank(approveDto.getDealNodeId()) && !Objects.equals(currentFlowNodeDataId, approveDto.getDealNodeId())) {
            String cardButtonText = "异常节点";
            List<TicketFlowNodeApproveDetail> approveDetailList = ticketFlowNodeApproveDetailService.lambdaQuery()
                    .isNull(TicketFlowNodeApproveDetail::getDeleteTime)
                    .eq(TicketFlowNodeApproveDetail::getTicketDataId, ticketDataDto.getId())
                    .eq(TicketFlowNodeApproveDetail::getTicketFlowNodeDataId, approveDto.getDealNodeId())
                    .eq(TicketFlowNodeApproveDetail::getDealUserId, dealUser.getAccountId())
                    .eq(TicketFlowNodeApproveDetail::getDealUserType, dealUser.getAccountType())
                    .orderByDesc(TicketFlowNodeApproveDetail::getUpdateTime)
                    .list();
            TicketFlowNodeApproveDetail approveDetail = null;
            if (CollectionUtils.isNotEmpty(approveDetailList)) {
                approveDetail = approveDetailList.get(0);
            }
            if (null != approveDetail && null != approveDetail.getDealType()) {
                switch (approveDetail.getDealType()) {
                    case PASS:
                        cardButtonText = "审批同意";
                        break;
                    case REJECT:
                        cardButtonText = "审批拒绝";
                        break;
                    case DISPATCH:
                        cardButtonText = "派单完成";
                        break;
                    case WITHDRAW:
                        cardButtonText = "申请人已撤回";
                        break;
                }
            }
            for (TicketFlowNodeDataDto flowNodeListDatum : flowNodeListData) {
                if (Objects.equals(flowNodeListDatum.getId(), approveDto.getDealNodeId())) {
                    this.disable(ticketDataDto, flowNodeListDatum, dealUser, cardButtonText);
                    break;
                }
            }
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID：%s 流程节点不匹配。传入节点=%s,当前审批节点=%s", ticketDataDto.getId(), approveDto.getDealNodeId(), currentFlowNodeDataId));
        }


        if (!TicketDataStatusEnum.APPLYING.equals(ticketDataDto.getTicketStatus())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID：{%s} 状态不在审批中", ticketDataDto.getId()));
        }
        TicketFlowNodeDataDto currentFlowNodeData = flowNodeListData.stream().filter(item -> Objects.equals(item.getId(), currentFlowNodeDataId)).findFirst().orElse(null);
        if (currentFlowNodeData == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID：{%s} 不存在当前审批的流程节点数据", ticketDataDto.getId()));
        }

        //权限验证，不能越权审批
        if (currentFlowNodeData.getAuditedType() == AuditedType.BY_USER) {
            if (StringUtils.isBlank(ticketDataDto.getCurrentDealUsers())) {
                for (TicketFlowNodeDataDto flowNodeListDatum : flowNodeListData) {
                    if (Objects.equals(flowNodeListDatum.getId(), approveDto.getDealNodeId())) {
                        this.disable(ticketDataDto, flowNodeListDatum, dealUser, "异常节点");
                        break;
                    }
                }
                return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID：{%s} 当前审批节点ID:{%s},审批人{%s}不能审批", ticketDataDto.getId(), currentFlowNodeDataId, dealUser));
            }
            List<AccountInfo> accountInfoList = AccountInfo.ToAccountInfoList(ticketDataDto.getCurrentDealUsers());
            if (Arrays.asList(ApproveDealTypeEnum.APPLY.getCode(), ApproveDealTypeEnum.PASS.getCode(), ApproveDealTypeEnum.REJECT.getCode()).contains(dealType)
                    && !this.inList(dealUser, accountInfoList)) {
                for (TicketFlowNodeDataDto flowNodeListDatum : flowNodeListData) {
                    if (Objects.equals(flowNodeListDatum.getId(), approveDto.getDealNodeId())) {
                        this.disable(ticketDataDto, flowNodeListDatum, dealUser, "异常节点");
                        break;
                    }
                }
                if ("DYNAMIC".equals(mode) || Arrays.asList("KF-SCS", "smy-kefu-20240528").contains(ticketDataDto.getAppId())) {

                } else {
                    return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID：{%s} 当前审批节点ID:{%s},审批人{%s}不能审批", ticketDataDto.getId(), currentFlowNodeDataId, dealUser));
                }
            }
        }

        TicketFlowNodeDataDto addNodeInfo = null;
        ApproveDealTypeEnum approveDealTypeEnum = ApproveDealTypeEnum.getByCode(dealType);
        //如果是加签，则需要生成新的节点
        if (addNodeParam != null) {
            addNodeInfo = this.genNode(addNodeParam, "NEXT", currentFlowNodeData);
        }
        //拒绝
        switch (approveDealTypeEnum) {
            case PASS:
            case APPLY:
                Response passResult = this.doApprovePass(ticketDataDto, currentFlowNodeData, approveDealTypeEnum, mode, dealDescription, dealUser.getAccountType(), dealUser.getAccountId(), dealUser.getAccountName(), dealOpinion, addNodeInfo);
                if (passResult.getEnum() != BizResponseEnums.SUCCESS) {
                    return new Response<>(ticketDataDto, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID:%s 审批通过失败,%s", ticketDataDto.getId(), passResult.getMsg()));
                }
                break;
            case REJECT:
                //拒绝(驳回)操作，直接结束
                Response rejectResult = this.doApproveReject(ticketDataDto, currentFlowNodeData, approveDealTypeEnum, dealDescription, dealUser.getAccountType(), dealUser.getAccountId(), dealUser.getAccountName(), dealOpinion);
                if (rejectResult.getEnum() != BizResponseEnums.SUCCESS) {
                    return new Response<>(ticketDataDto, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID:%s 审批拒绝失败,%s", ticketDataDto.getId(), rejectResult.getMsg()));
                }
                break;
            case FINISH:
                //直接结束
                Response finishResult = this.doApproveFinish(ticketDataDto, currentFlowNodeData, approveDealTypeEnum, dealDescription, dealUser.getAccountType(), dealUser.getAccountId(), dealUser.getAccountName(), dealOpinion);
                if (finishResult.getEnum() != BizResponseEnums.SUCCESS) {
                    return new Response<>(ticketDataDto, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID:%s 审批结束失败,%s", ticketDataDto.getId(), finishResult.getMsg()));
                }
                break;
            default:
                return new Response<>(ticketDataDto, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID：{%s} 审批类型不匹配", ticketDataDto.getId()));
        }
        //创建工单时，已经将流程节点计算出来并存储到数据库了。单节点链式结构，示例：node1->node2->node3
        return new Response<>(null, BizResponseEnums.SUCCESS, String.format("工单ID：{%s},节点ID：{%s} 审批完成", ticketID, currentFlowNodeDataId));
    }

    @ApiDoc(value = "审批通过", description = "审批通过")
    @Override
    public Response<String> doApprovePass(
            TicketDataDto ticketDataDto,
            TicketFlowNodeDataDto currentFlowNodeData,
            ApproveDealTypeEnum dealType,
            String dealMode,
            String dealDescription,
            String dealUserType, String dealUserId, String dealUserName, String dealOpinion,
            TicketFlowNodeDataDto addNodeInfo) {

        TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByAccountIdAndType(dealUserId, dealUserType);
        if (ticketAccountMapping == null || StrUtil.isBlank(ticketAccountMapping.getSameOriginId())) {
            return Response.error(BizResponseEnums.UNKNOW_EXCEPTION_CODE, "未找到有效用户信息");
        }
        AccountInfo dealUserInfo = new AccountInfo(ticketAccountMapping.getSameOriginId(), dealUserType, dealUserId, dealUserName);
        JSONObject optionJO = new JSONObject();
        if (StringUtils.isBlank(dealOpinion)) {
            optionJO.put("commentStrInfo", "审批通过");
        } else {
            if (dealOpinion.startsWith("{") && dealOpinion.endsWith("}")) {
                optionJO = JSONObject.parseObject(dealOpinion);
            } else {
                optionJO.put("commentStrInfo", dealOpinion);
            }
        }
        String ticketDataId = ticketDataDto.getId();
        String userDeptName = null;
        String templateId = ticketDataDto.getTemplateId();
        if (StringUtils.isNotEmpty(templateId)) {
            TicketTemplate ticketTemplate = this.ticketTemplateService.lambdaQuery().isNull(TicketTemplate::getDeleteTime).eq(TicketTemplate::getId, templateId).oneOpt().orElse(null);
            if (ticketTemplate != null && YESNOEnum.YES.equals(ticketTemplate.getShowDeptNameFlag())) {
                if (StringUtils.isEmpty(userDeptName) && StringUtils.isNotEmpty(ticketAccountMapping.getDeptName())) {
                    userDeptName = ticketAccountMapping.getDeptName();
                }
                if (StringUtils.isEmpty(userDeptName) && StringUtils.isNotEmpty(ticketAccountMapping.getDdUserDeptName())) {
                    userDeptName = ticketAccountMapping.getDdUserDeptName();
                }
            }
        }

        //新增节点执行结果
        TicketFlowNodeApproveDetail approveDetail = new TicketFlowNodeApproveDetail();
        approveDetail.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_APPROVE_DETAIL));
        approveDetail.setTicketDataId(ticketDataDto.getId());
        approveDetail.setTicketFlowNodeDataId(currentFlowNodeData.getId());
        approveDetail.setDealUserId(dealUserId);
        approveDetail.setDealUserType(dealUserType);
        approveDetail.setDealUserName(StringUtils.isEmpty(userDeptName) ? dealUserName : String.format("%s[%s]", dealUserName, userDeptName));
        approveDetail.setDealType(dealType);
        approveDetail.setDealTypeDescription(dealDescription);
        approveDetail.setDealOpinion(optionJO.toJSONString());

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("node_name", currentFlowNodeData.getNodeName());
        paramsMap.put("detail_id", approveDetail.getId());
        paramsMap.put("detail_user_id", approveDetail.getDealUserId());
        paramsMap.put("detail_user_name", dealUserName);
        paramsMap.put("detail_user_type", approveDetail.getDealUserType());
        paramsMap.put("detail_opinion", approveDetail.getDealOpinion());
        paramsMap.put("detail_type", approveDetail.getDealType().getCode());
        if (StringUtils.isNotEmpty(approveDetail.getDealTypeDescription())) {
            paramsMap.put("detail_type_des", approveDetail.getDealTypeDescription());
        } else {
            paramsMap.put("detail_type_des", approveDetail.getDealType().getDesc());
        }

        //首先执行当前审批节点的执行前动作，如果执行失败，则终止流程。
        Response<String> beforeResult = this.executeEvent(currentFlowNodeData.getTicketDataId(), currentFlowNodeData.getId(), ExecuteStepEnum.BEFORE.getCode(), paramsMap, dealUserInfo, dealType);
        if (!beforeResult.getCode().equals(BizResponseEnums.SUCCESS.getCode())) {
            return beforeResult;
        }
        Response<String> beforePassResult = this.executeEvent(currentFlowNodeData.getTicketDataId(), currentFlowNodeData.getId(), ExecuteStepEnum.BEFORE_PASS.getCode(), paramsMap, dealUserInfo, dealType);
        if (!beforePassResult.getCode().equals(BizResponseEnums.SUCCESS.getCode())) {
            return beforePassResult;
        }

        TicketData ticketDataUpdate = new TicketData();
        ticketDataUpdate.setId(ticketDataDto.getId());
        ticketDataUpdate.setUpdateTime(ticketDataDto.getUpdateTime());
        ticketDataUpdate.setCurrentDoneUsers(this.assembleThisDoneUser(ticketDataDto.getCurrentDoneUsers(), dealUserInfo));
        ticketDataUpdate.setCurrentCcUsers(this.dedupeUsers(AccountInfo.ToAccountInfoList(ticketDataDto.getCurrentCcUsers()), currentFlowNodeData.getAgreeCCUserList()));

        TicketFlowNodeDataDto currentNodeUpdate = new TicketFlowNodeDataDto();
        currentNodeUpdate.setId(currentFlowNodeData.getId());

        //计算下一个节点信息
        TicketFlowNodeApproveFinishBO nodeApproveFinishBO = this.transferNextNode(ticketDataDto, addNodeInfo, currentNodeUpdate);
        TicketFlowNodeDataDto nextNode = nodeApproveFinishBO.getNextNode();
        boolean goNext = true;
        //如果是或签：一人审批完成即流转至下一个节点
        if (Objects.equals(currentFlowNodeData.getAuditedMethod(), AuditedMethodEnum.OR) || "DYNAMIC".equals(dealMode)) {
            currentNodeUpdate.setNodeStatus(NodeStatusEnum.APPROVE_PASS);
            goNext = true;
            //没找到下一个流转节点,表示审批结束
            if (nextNode == null) {
                ticketDataUpdate.setTicketStatus(TicketDataStatusEnum.APPLY_END);
                ticketDataUpdate.setCurrentNodeId("-1");
                ticketDataUpdate.setCurrentNodeName("");
                ticketDataUpdate.setCurrentDealUsers("");
                ticketDataUpdate.setTicketFinishTime(new Date());
            } else {
                ticketDataUpdate.setTicketStatus(TicketDataStatusEnum.APPLYING);
                ticketDataUpdate.setCurrentNodeId(nextNode.getId());
                ticketDataUpdate.setCurrentNodeName(nextNode.getNodeName());
                ticketDataUpdate.setCurrentDealUsers(JSONUtil.toJsonStr(nextNode.getDealUserList()));

                //外部审批人更新
                TicketFlowNodeDataDto oldNextNode = nodeApproveFinishBO.getOldNextNode();
                if (oldNextNode != null && null == addNodeInfo) {
                    List<TicketFlowNodeExecutorDataDto> oldTicketFlowNodeExecutorDataDtoList = oldNextNode.getExcutorList();
                    String id = ticketDataDto.getId();
                    List<TicketFormItemData> ticketFormItemDataList = ticketFormItemDataService.lambdaQuery()
                            .eq(TicketFormItemData::getId, id)
                            .isNull(TicketFormItemData::getDeleteTime)
                            .list();
                    AccountInfo applyUser = AccountInfo.ToAccountInfo(ticketDataDto.getApplyUser());
                    if (CollectionUtils.isNotEmpty(oldTicketFlowNodeExecutorDataDtoList)) {
                        for (TicketFlowNodeExecutorDataDto ticketFlowNodeExecutorDataDto : oldTicketFlowNodeExecutorDataDtoList) {
                            Response response = setExecutorListStrIfExternalApprover(ticketFlowNodeExecutorDataDto, id, ticketDataDto.getInterfaceKey(), ticketFormItemDataList, oldNextNode.getNodeName(), applyUser);
                            if (!response.isSuccess()) {
                                return response;
                            }
                        }
                    }
                    //重新更新下一个节点的值
                    nodeApproveFinishBO.setNextNode(oldNextNode);
                    nextNode = oldNextNode;
                    ticketDataUpdate.setCurrentDealUsers(JSONUtil.toJsonStr(nextNode.getDealUserList()));
                }
            }
        } else {
            //如果是会签：每条节点执行人记录里面的执行人列表，都至少有一个人审批，则表示会签完成。
            for (TicketFlowNodeExecutorDataDto executorDataDto : currentFlowNodeData.getExcutorList()) {
                //此处只能验证审批类型的枚举
                if (executorDataDto.getExecutorType() == APPLY_MEMBER_LIST || executorDataDto.getExecutorType() == APPLY_GROUP
                        || executorDataDto.getExecutorType() == APPLY_LEADER || executorDataDto.getExecutorType() == APPLY_SELF
                        || executorDataDto.getExecutorType() == APPLY_POINT || executorDataDto.getExecutorType() == APPLY_DEPT_MANAGERS
                        || executorDataDto.getExecutorType() == APPLY_DEPT_POINT || executorDataDto.getExecutorType() == APPLY_EXTERNAL_APPROVER) {
                    //如果是本次审批人的executor，则跳过
                    List<AccountInfo> executorList = AccountInfo.ToAccountInfoList(executorDataDto.getExecutorList());
                    if (executorList.stream().anyMatch(item -> Objects.equals(item.getSameOriginId(), ticketAccountMapping.getSameOriginId()))) {
                        continue;
                    }
                    //验证不是本次的 executor 是否审批通过
                    boolean executePass = this.executePass(executorDataDto);
                    //有一个 executor 未审批通过，则会签没有结束
                    if (!executePass) {
                        goNext = false;
                        break;
                    }
                }
            }
            if (goNext) {
                currentNodeUpdate.setNodeStatus(NodeStatusEnum.APPROVE_PASS);
                //没找到下一个流转节点,表示审批结束
                if (nextNode == null) {
                    ticketDataUpdate.setTicketStatus(TicketDataStatusEnum.APPLY_END);
                    ticketDataUpdate.setCurrentNodeId("-1");
                    ticketDataUpdate.setCurrentNodeName("");
                    ticketDataUpdate.setCurrentDealUsers("");
                    ticketDataUpdate.setTicketFinishTime(new Date());
                } else {
                    ticketDataUpdate.setTicketStatus(TicketDataStatusEnum.APPLYING);
                    ticketDataUpdate.setCurrentNodeId(nextNode.getId());
                    ticketDataUpdate.setCurrentNodeName(nextNode.getNodeName());
                    ticketDataUpdate.setCurrentDealUsers(JSONUtil.toJsonStr(nextNode.getDealUserList()));

                    //外部审批人更新
                    TicketFlowNodeDataDto oldNextNode = nodeApproveFinishBO.getOldNextNode();
                    if (oldNextNode != null && null == addNodeInfo) {
                        List<TicketFlowNodeExecutorDataDto> oldTicketFlowNodeExecutorDataDtoList = oldNextNode.getExcutorList();
                        String id = ticketDataDto.getId();
                        List<TicketFormItemData> ticketFormItemDataList = ticketFormItemDataService.lambdaQuery()
                                .eq(TicketFormItemData::getId, id)
                                .isNull(TicketFormItemData::getDeleteTime)
                                .list();
                        AccountInfo applyUser = AccountInfo.ToAccountInfo(ticketDataDto.getApplyUser());
                        if (CollectionUtils.isNotEmpty(oldTicketFlowNodeExecutorDataDtoList)) {
                            for (TicketFlowNodeExecutorDataDto ticketFlowNodeExecutorDataDto : oldTicketFlowNodeExecutorDataDtoList) {
                                Response response = setExecutorListStrIfExternalApprover(ticketFlowNodeExecutorDataDto, id, ticketDataDto.getInterfaceKey(), ticketFormItemDataList, oldNextNode.getNodeName(), applyUser);
                                if (!response.isSuccess()) {
                                    return response;
                                }
                            }
                        }
                        //重新更新下一个节点的值
                        nodeApproveFinishBO.setNextNode(oldNextNode);
                        nextNode = oldNextNode;
                        ticketDataUpdate.setCurrentDealUsers(JSONUtil.toJsonStr(nextNode.getDealUserList()));
                    }
                }
            } else {
                currentNodeUpdate.setNodeStatus(NodeStatusEnum.APPROVING);

                ticketDataUpdate.setTicketStatus(TicketDataStatusEnum.APPLYING);
                ticketDataUpdate.setCurrentNodeId(ticketDataDto.getCurrentNodeId());
                ticketDataUpdate.setCurrentNodeName(ticketDataDto.getCurrentNodeName());
                //当前处理人移除 TODO： 同一个executor的人也要移除
                List<AccountInfo> allUser = AccountInfo.ToAccountInfoList(ticketDataDto.getCurrentDealUsers());
                AccountInfo currentUser = allUser.stream().filter(item -> Objects.equals(item.getAccountType(), dealUserType) && Objects.equals(item.getAccountId(), dealUserId)).findFirst().orElse(null);
                if (currentUser != null) {
                    allUser.remove(currentUser);
                }
                ticketDataUpdate.setCurrentDealUsers(JSONUtil.toJsonStr(allUser));
            }
        }
        //更新数据库数据
        Response<String> result = this.doPersist(dealUserInfo, ticketDataUpdate, nodeApproveFinishBO, approveDetail);
        if (!result.getEnum().equals(BizResponseEnums.SUCCESS)) {
            return result;
        }
        //通过DB重新整合工单数据，为后续动作做准备
        Response<TicketDataDto> ticketDataDtoResponse = ticketDataService.selectFullTicketDataById(new ReqParam(ticketDataDto.getId()));
        if (!ticketDataDtoResponse.isSuccess()) {
            return Response.error(BizResponseEnums.QUERY_ERROR, String.format("工单数据重新整合失败，ID:s%", ticketDataDto.getId()));
        }
        ticketDataDto = ticketDataDtoResponse.getData();

        if (Objects.equals(ticketDataUpdate.getTicketStatus(), TicketDataStatusEnum.APPLY_END)) {
            if (Objects.equals(ticketDataDto.getTicketMsgArriveType(), TicketMsgArriveTypeEnum.WECOM)) {
                List<TicketFlowNodeExecutorData> hasSelfExecutor = executorDataService.lambdaQuery()
                        .isNull(TicketFlowNodeExecutorData::getDeleteTime)
                        .eq(TicketFlowNodeExecutorData::getTicketDataId, ticketDataDto.getId())
                        .eq(TicketFlowNodeExecutorData::getTicketFlowNodeDataId, currentFlowNodeData.getId())
                        .in(TicketFlowNodeExecutorData::getExecutorType, Arrays.asList(ExecutorTypeEnum.CA_SELF, ExecutorTypeEnum.CE_SELF)).list();
                if (CollectionUtils.isNotEmpty(hasSelfExecutor)) {
                    //审批结束，发送消息
                    String title = "你发起的{{ticket_name}}已审批通过"
                            .replace("{{ticket_name}}", ticketDataDto.getTicketName());
                    List<AccountInfo> dealUserList = new ArrayList<>();
                    dealUserList.add(AccountInfo.ToAccountInfo(ticketDataDto.getApplyUser()));
                    var sendRes = notificationBizService.SendNotifyCard(
                            title,
                            ticketDataDto,
                            currentFlowNodeData.getId(),
                            dealUserInfo,
                            dealType.SEND,
                            dealUserList,
                            true);
                    if (sendRes.getEnum() != BizResponseEnums.SUCCESS) {
                        log.error("审批通过，发送审批完成消息失败，错误信息：{}", sendRes.getMsg());
                    }
                }
            }
        }
        //持久化完成，并且节点审批通过
        if (goNext) {
            //动作执行
            this.executeEvent(currentFlowNodeData.getTicketDataId(), currentFlowNodeData.getId(), ExecuteStepEnum.DONE_PASS.getCode(), paramsMap, dealUserInfo, dealType);
            this.executeEvent(currentFlowNodeData.getTicketDataId(), currentFlowNodeData.getId(), ExecuteStepEnum.FINISH.getCode(), paramsMap, dealUserInfo, dealType);
            //如果是加签,则执行加签后事件。
            if (addNodeInfo != null) {
                this.executeEvent(currentFlowNodeData.getTicketDataId(), currentFlowNodeData.getId(), ExecuteStepEnum.DONE_ADD_NODE.getCode(), null, dealUserInfo, dealType);
            }
            //消息触达(企微)
            if (ticketDataDto.getTicketMsgArriveType() == TicketMsgArriveTypeEnum.WECOM) {
                //抄送当前节点抄送人
                //this.doApproveFinishCC(dealUserInfo,  JSONUtil.toJsonStr(currentFlowNodeData.getAgreeCCUserList()), ticketDataUpdate.getTicketStatus(), ticketDataDto);
                String title = "【抄送】{{apply_user}}提交了{{ticket_name}}"
                        .replace("{{apply_user}}", ticketDataDto.getApplyUserName())
                        .replace("{{ticket_name}}", ticketDataDto.getTicketName()
                        );
                if (ticketDataUpdate.getTicketStatus() == TicketDataStatusEnum.APPLY_END) {
                    title = title + "，已审批通过";
                }
                var sendRes = notificationBizService.SendNotifyCard(
                        title,
                        ticketDataDto,
                        currentFlowNodeData.getId(),
                        dealUserInfo,
                        dealType.SEND,
                        currentFlowNodeData.getAgreeCCUserList(),
                        true
                );
                if (sendRes.getEnum() != BizResponseEnums.SUCCESS) {
                    log.error("审批通过，抄送当前节点抄送人失败，错误信息：{}", sendRes.getMsg());
                }
                //节点已审批通过，所有人卡片disable
                this.disable(ticketDataDto, currentFlowNodeData, null, "审批通过");
            }
            //如果流转到下一个节点，且节点存在
            if (nextNode != null) {
                //消息触达(企微)
                if (ticketDataDto.getTicketMsgArriveType() == TicketMsgArriveTypeEnum.WECOM) {
                    //通知下一个节点审批人
                    String title = "{{ticket_tag}}{{apply_user}}提交的{{ticket_name}}，待你处理"
                            .replace("{{ticket_tag}}", addNodeInfo != null ? "【加签】" : "")
                            .replace("{{apply_user}}", ticketDataDto.getApplyUserName())
                            .replace("{{ticket_name}}", ticketDataDto.getTicketName());
                    log.info("流转到下一个节点的工单详情:{},节点信息：{}", ticketDataDto, null == ticketDataDto.getTicketFlowDataDto() ? null : JSONObject.toJSONString(ticketDataDto.getTicketFlowDataDto().getTicketFlowNodeDataDtoList()));
                    var urgeRes = notificationBizService.SendDealCard(
                            title,
                            ticketDataDto,
                            dealUserInfo,
                            dealType.SEND,
                            nextNode.getDealUserList(),
                            false,
                            null
                    );
                    if (urgeRes.getEnum() != BizResponseEnums.SUCCESS) {
                        log.error("审批通过，通知下一个节点审批人失败，错误信息：{}", urgeRes.getMsg());
                    }
                }
                //如果下一个节点是自动审批/自动拒绝类型，触发下一个节点的自动审批。
                this.autoApprove(ticketDataDto.getId(), nextNode.getAuditedType(), nextNode.getId());
            }
        } else {
            //审批卡片状态修改
            this.disable(ticketDataDto, currentFlowNodeData, dealUserInfo, "审批通过");
        }
        return new Response<>(null, BizResponseEnums.SUCCESS, "审批通过");
    }

    /**
     * @param accountInfo
     * @param type
     * @param codeJson    {{"userId1-accountType-cardType":["code1","code2"]},{"userId2-accountType-cardType":["code1","code2"]}}
     * @return
     */
    private JSONArray getDisableCode(AccountInfoDto accountInfo, String type, JSONObject codeJson) {
        if (codeJson != null && codeJson.size() > 0) {
            //["code1","code2"]
            return codeJson.getJSONArray(String.format("%s-%s-%s", accountInfo.getAccountId(), accountInfo.getAccountType(), type));
        } else {
            return null;
        }
    }


    public void disable(TicketDataDto ticketDataDto, TicketFlowNodeDataDto currentFlowNodeData, AccountInfo dealUser, String buttonText) {
        if (ticketDataDto == null) {
            log.warn("工单不存在, 跳过.");
            return;
        }
        if (currentFlowNodeData == null) {
            log.warn("节点不存在, 跳过.");
            return;
        }
        if (StringUtils.isBlank(currentFlowNodeData.getNodeWxDealCardCode())) {
            log.warn("节点{}企业微信卡片码为空, 跳过.", currentFlowNodeData.getId());
            return;
        }
        if (ticketDataDto.getTicketMsgArriveType() != TicketMsgArriveTypeEnum.WECOM) {
            log.warn("工单[{}]通知类型不是企业微信, 不能发送抄送事件", ticketDataDto.getId());
            return;
        }
        JSONObject cardCodeJson = new JSONObject();
        try {
            cardCodeJson.putAll(JSONObject.parseObject(currentFlowNodeData.getNodeWxDealCardCode()));
        } catch (Exception e) {
            log.warn("WxDealCardCode={}格式不对, 跳过", currentFlowNodeData.getNodeWxDealCardCode(), e);
            return;
        }

        List<AccountInfoDto> accountInfoListDto = new ArrayList<>();
        //如果指定用户，则处理指定用户的卡片
        if (dealUser != null) {
            TicketRemoteAccountDto accountDto = ticketAccountService.getTicketRemoteAccountByIdAndType(dealUser.getAccountId(), dealUser.getAccountType());
            if (accountDto != null && accountDto.getQywxId() != null) {
                AccountInfoDto temp = new AccountInfoDto();
                temp.setAccountType(accountDto.getUserType());
                temp.setQywxId(accountDto.getQywxId());
                temp.setAccountId(accountDto.getUserId());
                accountInfoListDto.add(temp);
            }
        } else {
            //如果没有指定用户，则处理所有当前处理人的卡片
            accountInfoListDto.addAll(this.getCcQywxIdList(currentFlowNodeData.getDealUserList()));
        }

        if (CollectionUtils.isEmpty(accountInfoListDto)) {
            log.warn("节点 {} disable通知列表为空，跳过.", currentFlowNodeData.getId());
            return;
        }

        threadPoolTaskExecutor.execute(() -> {
            for (AccountInfoDto accountInfo : accountInfoListDto) {
                if (StringUtils.isNotBlank(accountInfo.getQywxId())) {
                    //["code1","code2"]
                    JSONArray codeAry = this.getDisableCode(accountInfo, TicketMsgArriveTypeEnum.WECOM.getCode(), cardCodeJson);
                    if (codeAry == null) {
                        log.warn("节点{}用户{}-{}企业微信卡片码为空", currentFlowNodeData.getId(), accountInfo.getAccountId(), accountInfo.getAccountType());
                    } else {
                        for (int i = 0; i < codeAry.size(); i++) {
                            String code;
                            if (StringUtils.isNotBlank((code = codeAry.getString(i)))) {
                                //审批卡片状态修改
                                notificationService.disableCardButton(code, buttonText, Collections.singletonList(accountInfo.getQywxId()));
                            }
                        }
                    }
                } else {
                    log.warn("用户{}企业微信id为空，跳过.", accountInfo.getAccountId());
                }
            }
        });
    }

    private TicketFlowNodeDataDto getNextFlowNode(TicketDataDto ticketDataDto, String currentNodeId) {
        TicketFlowDataDto flowData = ticketDataDto.getTicketFlowDataDto();
        for (TicketFlowNodeDataDto nextFlowNodeData : flowData.getTicketFlowNodeDataDtoList()) {
            if (currentNodeId.equals(nextFlowNodeData.getPreNodeId())) {
                return nextFlowNodeData;
            }
        }
        return null;
    }

    @ApiDoc(value = "审批结束", description = "审批结束")
    @Override
    public Response<String> doApproveReject(TicketDataDto ticketDataDto, TicketFlowNodeDataDto
            currentFlowNodeData, ApproveDealTypeEnum approveDealTypeEnum, String dealDescription, String dealUserType, String dealUserId, String
                                                    dealUserName, String dealOpinion) {
        TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByAccountIdAndType(dealUserId, dealUserType);
        if (ticketAccountMapping == null || StrUtil.isBlank(ticketAccountMapping.getSameOriginId())) {
            return Response.error(BizResponseEnums.UNKNOW_EXCEPTION_CODE, "未找到有效用户信息");
        }
        AccountInfo dealUserInfo = new AccountInfo(ticketAccountMapping.getSameOriginId(), dealUserType, dealUserId, dealUserName);
        JSONObject optionJO = new JSONObject();
        if (StringUtils.isBlank(dealOpinion)) {
            optionJO.put("commentStrInfo", "审批拒绝");
        } else {
            if (dealOpinion.startsWith("{") && dealOpinion.endsWith("}")) {
                optionJO = JSONObject.parseObject(dealOpinion);
            } else {
                optionJO.put("commentStrInfo", dealOpinion);
            }
        }

        //新增节点执行结果
        TicketFlowNodeApproveDetail approveDetail = new TicketFlowNodeApproveDetail();
        approveDetail.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_APPROVE_DETAIL));
        approveDetail.setTicketDataId(ticketDataDto.getId());
        approveDetail.setTicketFlowNodeDataId(currentFlowNodeData.getId());
        approveDetail.setDealUserType(dealUserType);
        approveDetail.setDealUserId(dealUserId);
        approveDetail.setDealUserName(dealUserName);
        approveDetail.setDealType(approveDealTypeEnum);
        approveDetail.setDealTypeDescription(dealDescription);
        approveDetail.setDealOpinion(optionJO.toJSONString());

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("detail_id", approveDetail.getId());
        paramsMap.put("detail_user_id", approveDetail.getDealUserId());
        paramsMap.put("detail_user_name", approveDetail.getDealUserName());
        paramsMap.put("detail_user_type", approveDetail.getDealUserType());
        paramsMap.put("detail_opinion", approveDetail.getDealOpinion());
        paramsMap.put("detail_type", approveDetail.getDealType().getCode());
        paramsMap.put("detail_type_des", approveDetail.getDealType().getDesc());

        //首先执行当前审批节点的执行前动作，如果执行失败，则终止流程。
        Response<String> beforeResult = this.executeEvent(currentFlowNodeData.getTicketDataId(), currentFlowNodeData.getId(), ExecuteStepEnum.BEFORE.getCode(), paramsMap, dealUserInfo, approveDealTypeEnum);
        if (!beforeResult.getCode().equals(BizResponseEnums.SUCCESS.getCode())) {
            return beforeResult;
        }
        Response<String> beforeRejectResult = this.executeEvent(currentFlowNodeData.getTicketDataId(), currentFlowNodeData.getId(), ExecuteStepEnum.BEFORE_REJECT.getCode(), paramsMap, dealUserInfo, approveDealTypeEnum);
        if (!beforeRejectResult.getCode().equals(BizResponseEnums.SUCCESS.getCode())) {
            return beforeRejectResult;
        }

        TicketData ticketDataUpdate = new TicketData();
        ticketDataUpdate.setId(ticketDataDto.getId());
        ticketDataUpdate.setUpdateTime(ticketDataDto.getUpdateTime());
        ticketDataUpdate.setTicketStatus(TicketDataStatusEnum.REJECT);
        ticketDataUpdate.setCurrentNodeId("-1");
        ticketDataUpdate.setCurrentNodeName("");
        ticketDataUpdate.setTicketFinishTime(new Date());
        ticketDataUpdate.setCurrentDoneUsers(this.assembleThisDoneUser(ticketDataDto.getCurrentDoneUsers(), dealUserInfo));
        ticketDataUpdate.setCurrentCcUsers(this.dedupeUsers(AccountInfo.ToAccountInfoList(ticketDataDto.getCurrentCcUsers()), currentFlowNodeData.getRejectCCUserList()));
        ticketDataUpdate.setCurrentDealUsers("");

        TicketFlowNodeDataDto currentNodeUpdate = new TicketFlowNodeDataDto();
        currentNodeUpdate.setId(currentFlowNodeData.getId());
        currentNodeUpdate.setNodeStatus(NodeStatusEnum.APPROVE_REJECT);

        TicketFlowNodeApproveFinishBO nodeApproveFinishBO = new TicketFlowNodeApproveFinishBO();
        nodeApproveFinishBO.setCurrentNode(currentNodeUpdate);

        Response result = this.doPersist(dealUserInfo, ticketDataUpdate, nodeApproveFinishBO, approveDetail);
        if (result.getEnum() != BizResponseEnums.SUCCESS) {
            return result;
        }

        //动作执行
        this.executeEvent(currentFlowNodeData.getTicketDataId(), currentFlowNodeData.getId(), ExecuteStepEnum.DONE_REJECT.getCode(), paramsMap, dealUserInfo, approveDealTypeEnum);
        this.executeEvent(currentFlowNodeData.getTicketDataId(), currentFlowNodeData.getId(), ExecuteStepEnum.FINISH.getCode(), paramsMap, dealUserInfo, approveDealTypeEnum);

        //结束节点动作执行
        if (ticketDataDto.getTicketFlowDataDto() != null && CollectionUtils.isNotEmpty(ticketDataDto.getTicketFlowDataDto().getTicketFlowNodeDataDtoList())) {
            var endNode = ticketDataDto.getTicketFlowDataDto().getTicketFlowNodeDataDtoList().stream().filter(node -> "结束".equals(node.getNodeName())).findFirst().orElse(null);
            if (endNode != null) {
                var nullNode = ticketDataDto.getTicketFlowDataDto().getTicketFlowNodeDataDtoList().stream().filter(node -> Objects.equals(node.getPreNodeId(), endNode.getId())).findFirst().orElse(null);
                if (nullNode == null) {
                    this.executeEvent(currentFlowNodeData.getTicketDataId(), endNode.getId(), ExecuteStepEnum.BEFORE.getCode(), paramsMap, dealUserInfo, approveDealTypeEnum);
                    this.executeEvent(currentFlowNodeData.getTicketDataId(), endNode.getId(), ExecuteStepEnum.BEFORE_REJECT.getCode(), paramsMap, dealUserInfo, approveDealTypeEnum);
                    this.executeEvent(currentFlowNodeData.getTicketDataId(), endNode.getId(), ExecuteStepEnum.DONE_REJECT.getCode(), paramsMap, dealUserInfo, approveDealTypeEnum);
                    this.executeEvent(currentFlowNodeData.getTicketDataId(), endNode.getId(), ExecuteStepEnum.FINISH.getCode(), paramsMap, dealUserInfo, approveDealTypeEnum);
                }
            }
        }

        if (Objects.equals(ticketDataUpdate.getTicketStatus(), TicketDataStatusEnum.REJECT)) {
            if (Objects.equals(ticketDataDto.getTicketMsgArriveType(), TicketMsgArriveTypeEnum.WECOM)) {
                //审批结束，发送消息
                List<TicketFlowNodeExecutorData> hasSelfExecutor = executorDataService.lambdaQuery()
                        .isNull(TicketFlowNodeExecutorData::getDeleteTime)
                        .eq(TicketFlowNodeExecutorData::getTicketDataId, ticketDataDto.getId())
                        .eq(TicketFlowNodeExecutorData::getTicketFlowNodeDataId, currentFlowNodeData.getId())
                        .in(TicketFlowNodeExecutorData::getExecutorType, Arrays.asList(ExecutorTypeEnum.CE_SELF)).list();
                if (CollectionUtils.isNotEmpty(hasSelfExecutor)) {
                    String title = "你发起的{{ticket_name}}已审批驳回"
                            .replace("{{ticket_name}}", ticketDataDto.getTicketName());
                    //将dealUserInfo 转换为数组
                    List<AccountInfo> dealUserList = new ArrayList<>();
                    dealUserList.add(AccountInfo.ToAccountInfo(ticketDataDto.getApplyUser()));
                    notificationBizService.SendNotifyCard(
                            title,
                            ticketDataDto,
                            currentFlowNodeData.getId(),
                            dealUserInfo,
                            ApproveDealTypeEnum.SEND,
                            dealUserList,
                            true);
                }
            }
        }

        //消息触达
        if (ticketDataDto.getTicketMsgArriveType() == TicketMsgArriveTypeEnum.WECOM) {
            //审批卡片状态修改
            this.disable(ticketDataDto, currentFlowNodeData, null, "审批拒绝");
            //消息抄送
            //this.doApproveFinishCC(dealUserInfo,  JSONUtil.toJsonStr(currentFlowNodeData.getRejectCCUserList()), ticketDataUpdate.getTicketStatus(), ticketDataDto);
            String title = "【抄送】{{apply_user}}提交了{{ticket_name}}，已审批驳回"
                    .replace("{{apply_user}}", ticketDataDto.getApplyUserName())
                    .replace("{{ticket_name}}", ticketDataDto.getTicketName()
                    );
            notificationBizService.SendNotifyCard(
                    title,
                    ticketDataDto,
                    currentFlowNodeData.getId(),
                    dealUserInfo,
                    ApproveDealTypeEnum.SEND,
                    currentFlowNodeData.getRejectCCUserList(),
                    true
            );
        }
        return new Response<>(null, BizResponseEnums.SUCCESS, "审批拒绝");
    }

    public Response<String> doApproveFinish(TicketDataDto ticketDataDto, TicketFlowNodeDataDto
            currentFlowNodeData, ApproveDealTypeEnum approveDealTypeEnum, String dealDescription, String dealUserType, String dealUserId, String
                                                    dealUserName, String dealOpinion) {
        TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByAccountIdAndType(dealUserId, dealUserType);
        if (ticketAccountMapping == null || StrUtil.isBlank(ticketAccountMapping.getSameOriginId())) {
            return Response.error(BizResponseEnums.UNKNOW_EXCEPTION_CODE, "未找到有效用户信息");
        }
        AccountInfo dealUserInfo = new AccountInfo(ticketAccountMapping.getSameOriginId(), dealUserType, dealUserId, dealUserName);
        JSONObject optionJO = new JSONObject();
        if (StringUtils.isBlank(dealOpinion)) {
            optionJO.put("commentStrInfo", "审批结束");
        } else {
            if (dealOpinion.startsWith("{") && dealOpinion.endsWith("}")) {
                optionJO = JSONObject.parseObject(dealOpinion);
            } else {
                optionJO.put("commentStrInfo", dealOpinion);
            }
        }
        //新增节点执行结果
        TicketFlowNodeApproveDetail approveDetail = new TicketFlowNodeApproveDetail();
        approveDetail.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_APPROVE_DETAIL));
        approveDetail.setTicketDataId(ticketDataDto.getId());
        approveDetail.setTicketFlowNodeDataId(currentFlowNodeData.getId());
        approveDetail.setDealUserType(dealUserType);
        approveDetail.setDealUserId(dealUserId);
        approveDetail.setDealUserName(dealUserName);
        approveDetail.setDealType(approveDealTypeEnum);
        approveDetail.setDealTypeDescription(dealDescription);
        approveDetail.setDealOpinion(optionJO.toJSONString());

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("detail_id", approveDetail.getId());
        paramsMap.put("detail_user_id", approveDetail.getDealUserId());
        paramsMap.put("detail_user_name", approveDetail.getDealUserName());
        paramsMap.put("detail_user_type", approveDetail.getDealUserType());
        paramsMap.put("detail_opinion", approveDetail.getDealOpinion());
        paramsMap.put("detail_type", approveDetail.getDealType().getCode());
        paramsMap.put("detail_type_des", approveDetail.getDealType().getDesc());

        //首先执行当前审批节点的执行前动作，如果执行失败，则终止流程。
        Response<String> beforeResult = this.executeEvent(currentFlowNodeData.getTicketDataId(), currentFlowNodeData.getId(), ExecuteStepEnum.BEFORE.getCode(), paramsMap, dealUserInfo, approveDealTypeEnum);
        if (!beforeResult.getCode().equals(BizResponseEnums.SUCCESS.getCode())) {
            return beforeResult;
        }

        TicketData ticketDataUpdate = new TicketData();
        ticketDataUpdate.setId(ticketDataDto.getId());
        ticketDataUpdate.setUpdateTime(ticketDataDto.getUpdateTime());
        ticketDataUpdate.setTicketStatus(TicketDataStatusEnum.APPLY_END);
        ticketDataUpdate.setCurrentNodeId("-1");
        ticketDataUpdate.setCurrentNodeName("");
        ticketDataUpdate.setTicketFinishTime(new Date());
        ticketDataUpdate.setCurrentDoneUsers(this.assembleThisDoneUser(ticketDataDto.getCurrentDoneUsers(), dealUserInfo));
        ticketDataUpdate.setCurrentCcUsers(this.dedupeUsers(AccountInfo.ToAccountInfoList(ticketDataDto.getCurrentCcUsers()), currentFlowNodeData.getRejectCCUserList()));
        ticketDataUpdate.setCurrentDealUsers("");


        TicketFlowNodeDataDto currentNodeUpdate = new TicketFlowNodeDataDto();
        currentNodeUpdate.setId(currentFlowNodeData.getId());
        currentNodeUpdate.setNodeStatus(NodeStatusEnum.APPROVE_END);

        TicketFlowNodeApproveFinishBO nodeApproveFinishBO = new TicketFlowNodeApproveFinishBO();
        nodeApproveFinishBO.setCurrentNode(currentNodeUpdate);

        Response result = this.doPersist(dealUserInfo, ticketDataUpdate, nodeApproveFinishBO, approveDetail);
        if (result.getEnum() != BizResponseEnums.SUCCESS) {
            return result;
        }

        //动作执行
        this.executeEvent(currentFlowNodeData.getTicketDataId(), currentFlowNodeData.getId(), ExecuteStepEnum.FINISH.getCode(), paramsMap, dealUserInfo, approveDealTypeEnum);

        //结束节点动作执行
        if (ticketDataDto.getTicketFlowDataDto() != null && CollectionUtils.isNotEmpty(ticketDataDto.getTicketFlowDataDto().getTicketFlowNodeDataDtoList())) {
            var endNode = ticketDataDto.getTicketFlowDataDto().getTicketFlowNodeDataDtoList().stream().filter(node -> "结束".equals(node.getNodeName())).findFirst().orElse(null);
            if (endNode != null) {
                var nullNode = ticketDataDto.getTicketFlowDataDto().getTicketFlowNodeDataDtoList().stream().filter(node -> Objects.equals(node.getPreNodeId(), endNode.getId())).findFirst().orElse(null);
                if (nullNode == null) {
                    this.executeEvent(currentFlowNodeData.getTicketDataId(), endNode.getId(), ExecuteStepEnum.BEFORE.getCode(), paramsMap, dealUserInfo, approveDealTypeEnum);
                    this.executeEvent(currentFlowNodeData.getTicketDataId(), endNode.getId(), ExecuteStepEnum.FINISH.getCode(), paramsMap, dealUserInfo, approveDealTypeEnum);
                }
            }
        }

        //消息触达
        if (ticketDataDto.getTicketMsgArriveType() == TicketMsgArriveTypeEnum.WECOM) {
            //审批卡片状态修改
            this.disable(ticketDataDto, currentFlowNodeData, null, "审批完成");
            //消息抄送
            //this.doApproveFinishCC(dealUserInfo,  JSONUtil.toJsonStr(currentFlowNodeData.getRejectCCUserList()), ticketDataUpdate.getTicketStatus(), ticketDataDto);
            String title = "【抄送】{{apply_user}}提交了{{ticket_name}}已结单"
                    .replace("{{apply_user}}", ticketDataDto.getApplyUserName())
                    .replace("{{ticket_name}}", ticketDataDto.getTicketName()
                    );
            notificationBizService.SendNotifyCard(
                    title,
                    ticketDataDto,
                    currentFlowNodeData.getId(),
                    dealUserInfo,
                    ApproveDealTypeEnum.SEND,
                    currentFlowNodeData.getRejectCCUserList(),
                    true
            );
        }

        if (Objects.equals(ticketDataUpdate.getTicketStatus(), TicketDataStatusEnum.APPLY_END)) {
            if (Objects.equals(ticketDataDto.getTicketMsgArriveType(), TicketMsgArriveTypeEnum.WECOM)) {
                //审批结束，发送消息
                //this.sendApproveFinishMsg(ticketDataDto, dealUserInfo, false);
                String title = "你发起的{{ticket_name}}已审批结束"
                        .replace("{{ticket_name}}", ticketDataDto.getTicketName());
                //将dealUserInfo 转换为数组
                List<AccountInfo> dealUserList = new ArrayList<>();
                dealUserList.add(AccountInfo.ToAccountInfo(ticketDataDto.getApplyUser()));
                notificationBizService.SendNotifyCard(
                        title,
                        ticketDataDto,
                        currentFlowNodeData.getId(),
                        dealUserInfo,
                        ApproveDealTypeEnum.SEND,
                        dealUserList,
                        false);
            }
        }
        return new Response<>(null, BizResponseEnums.SUCCESS, "已审批结束");
    }

    private String assembleThisDoneUser(String currentDoneUsers, AccountInfo thisUser) {
        //更新当前处理人
        if (thisUser == null) {
            return currentDoneUsers;
        }
        List<AccountInfo> accountInfoList = new ArrayList<>();
        if (StringUtils.isBlank(currentDoneUsers)) {
            accountInfoList.add(thisUser);
        } else {
            accountInfoList = AccountInfo.ToAccountInfoList(currentDoneUsers);
            if (accountInfoList.stream().filter(item -> Objects.equals(item.getAccountType(), thisUser.getAccountType()) && Objects.equals(item.getAccountId(), thisUser.getAccountId())).count() == 0) {
                accountInfoList.add(thisUser);
            }
        }
        return JSONUtil.toJsonStr(accountInfoList);
    }

    private String dedupeUsers(List<AccountInfo> oldList, List<AccountInfo> newList) {
        //更新当前处理人
        if (oldList == null) {
            oldList = new ArrayList<>();
        }
        if (newList == null) {
            newList = new ArrayList<>();
        }
        Map<String, AccountInfo> map = new HashMap<>();
        for (AccountInfo accountInfo : oldList) {
            map.putIfAbsent(accountInfo.getAccountType() + "-" + accountInfo.getAccountId(), accountInfo);
        }
        for (AccountInfo accountInfo : newList) {
            map.putIfAbsent(accountInfo.getAccountType() + "-" + accountInfo.getAccountId(), accountInfo);
        }
        return JSONUtil.toJsonStr(new ArrayList<>(map.values()));
    }

    /**
     * 判断 accountInfo 是否在 accountInfoList 中
     *
     * @param accountInfo
     * @param accountInfoList
     * @return
     */
    public boolean inList(AccountInfo accountInfo, List<AccountInfo> accountInfoList) {
        if (CollectionUtils.isEmpty(accountInfoList)) {
            return false;
        }
        for (AccountInfo info : accountInfoList) {
            if (Objects.equals(accountInfo.getSameOriginId(), info.getSameOriginId())) {
                return true;
            } else if (Objects.equals(accountInfo.getAccountType(), info.getAccountType()) && Objects.equals(accountInfo.getAccountId(), info.getAccountId())) {
                return true;
            }
        }
        return false;
    }


    /**
     * 执行动作
     *
     * @param ticketDataId
     * @param flowNodeDataId
     * @param executeStep
     */
    @Override
    public Response<String> executeEvent(String ticketDataId, String flowNodeDataId, String executeStep, HashMap<String, String> params, AccountInfo accountInfo, ApproveDealTypeEnum approveDealTypeEnum) {
        var ticketDataOpt = ticketDataService.lambdaQuery().isNull(TicketData::getDeleteTime).eq(TicketData::getId, ticketDataId).oneOpt();
        if (!ticketDataOpt.isPresent()) {
            return new Response<>(null, BizResponseEnums.SUCCESS, "单据不存在");
        }
        TicketData ticketData = ticketDataOpt.get();
        List<TicketFlowEventData> flowEventDataList = flowEventDataService.lambdaQuery()
                .eq(TicketFlowEventData::getTicketDataId, ticketDataId)
                .eq(TicketFlowEventData::getTicketFlowNodeDataId, flowNodeDataId)
                .eq(executeStep != null, TicketFlowEventData::getExecuteStep, executeStep)
                .isNull(TicketFlowEventData::getDeleteTime)
                .list();
        return executeEventList(ticketDataId, ticketData.getInterfaceKey(), flowEventDataList, params, accountInfo, approveDealTypeEnum);
    }

    /**
     * 执行事件
     *
     * @param ticketDataId
     * @param flowEventDataList
     * @return
     */
    @Override
    public Response<String> executeEventList(String ticketDataId, String ticketInterfaceKey, List<TicketFlowEventData> flowEventDataList, HashMap<String, String> params, AccountInfo accountInfo, ApproveDealTypeEnum approveDealTypeEnum) {
        //如果没有配置执行动作，直接结束
        if (CollectionUtils.isEmpty(flowEventDataList)) {
            return new Response<>(null, BizResponseEnums.SUCCESS, "没有配置执行动作");
        }
        Response<String> resp = new Response<>("", BizResponseEnums.SUCCESS, "执行动作成功");
        for (TicketFlowEventData ticketFlowEventData : flowEventDataList) {
            var executeRes = executeEventCore(ticketDataId, ticketInterfaceKey, ticketFlowEventData, params, accountInfo, approveDealTypeEnum);
            if (!executeRes.getEnum().equals(BizResponseEnums.SUCCESS)) {
                //事件执行失败，且是手动推送，创建群（如果已经有群，则不创建），发送通知。
                String pushConfig = ticketFlowEventData.getPushConfig();
                if (ObjectHelper.isEmpty(pushConfig) || pushConfig.contains(PushTypeEnum.MANUAL_PUSH.getCode())) {
                    //發送群消息 需要控制一个节点只发送一次失败消息和成功消息。
                    ticketAppService.createQWGroupAndSendMsgByNode(ticketFlowEventData.getTicketFlowNodeDataId(), CallBackMsgStatusEnum.EXCEPTION_MSG_SENG);
                }
                log.error("动作执行失败：{}", executeRes);
                return new Response<>(null, BizResponseEnums.SYSTEM_ERROR, executeRes.getMsg());
            }
        }

        return resp;
    }


    public Response<TicketFlowEventData> executeEventCore(String ticketDataId, String interfaceKey, TicketFlowEventData flowEventData, HashMap<String, String> params, AccountInfo accountInfo, ApproveDealTypeEnum approveDealTypeEnum) {
        if (flowEventData == null) {
            return new Response<>(null, BizResponseEnums.SUCCESS, "没有配置执行动作");
        }
        boolean onceStep = Arrays.asList(ExecuteStepEnum.BEFORE_COMMENT, ExecuteStepEnum.BEFORE_UPDATE, ExecuteStepEnum.BEFORE_DISPATCH).contains(flowEventData.getExecuteStep());
        if (onceStep || Arrays.asList(EventStatusEnum.INIT, EventStatusEnum.WAIT_EXECUTE, EventStatusEnum.EXECUTE_FAILURE_MIDDLE).contains(flowEventData.getEventStatus())) {
            TicketFlowEventData updateData = new TicketFlowEventData();
            updateData.setId(flowEventData.getId());
            updateData.setTicketDataId(flowEventData.getTicketDataId());
            updateData.setTicketFlowNodeDataId(flowEventData.getTicketFlowNodeDataId());
            if (onceStep || StringUtils.isEmpty(flowEventData.getEventTranData())) {
                String eventTag = flowEventData.getEventTag();
                if (StringUtils.isNotEmpty(eventTag)) {
                    if (eventTag.startsWith("{{") && eventTag.endsWith("}}") && params != null) {
                        flowEventData.setEventTranData(JSONObject.toJSONString(params));
                    } else {
                        flowEventData.setEventTranData(eventTag);
                    }
                    updateData.setEventTranData(flowEventData.getEventTranData());
                }
            }
            String tranDataStr = flowEventData.getEventTranData();
            String sign = DigestUtils.md5DigestAsHex((interfaceKey == null ? "" : interfaceKey + ticketDataId).getBytes());
            String saveTag = flowEventData.getEventTag();
            if (StringUtils.isNotEmpty(saveTag) && saveTag.startsWith("{{") && saveTag.endsWith("}}")) {
                saveTag = "调用";
            }
            switch (flowEventData.getEventType()) {
                case DUBBO_SERVICE:
                    var dubboServiceConfigRes = DubboServiceConfig.parseStrToDubboConfig(flowEventData.getEventConfig());
                    if (dubboServiceConfigRes.getEnum() != BizResponseEnums.SUCCESS) {
                        log.error("dubbo服务配置解析失败,配置内容：{}", flowEventData.getEventConfig());
                        updateData.setEventStatus(EventStatusEnum.EXECUTE_FAILURE);
                        updateEventData(updateData, "动作解析失败:" + dubboServiceConfigRes.getMsg(), params, accountInfo, approveDealTypeEnum);
                        return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "dubbo服务配置解析失败");
                    }
                    var dubboServiceConfig = dubboServiceConfigRes.getData();
                    try {
                        Object invokeResult = dynamicDubboConsumer.invokeDubboService(dubboServiceConfig.getInterfaceName(), dubboServiceConfig.getMethodName(), new Object[]{sign, tranDataStr, ticketDataId}, dubboServiceConfig.getVersion(), dubboServiceConfig.getGroup());
                        if (invokeResult instanceof String) {
                            String invokerStr = JSONUtil.toJsonStr(invokeResult);
                            if (invokerStr.contains("code:0") || invokerStr.contains("\"code\":0") || invokerStr.contains("\"code\":\"0\"")) {
                                updateData.setEventStatus(EventStatusEnum.EXECUTE_SUCCESS_FINAL);
                                updateEventData(updateData, saveTag + "成功", params, accountInfo, approveDealTypeEnum);
                                return new Response<>(null, BizResponseEnums.SUCCESS, "执行动作成功");
                            } else {
                                updateData.setEventStatus(EventStatusEnum.EXECUTE_FAILURE_MIDDLE);
                                String errMsg = invokerStr.length() > 100 ? invokerStr.substring(0, 100) : invokerStr;
                                updateEventData(updateData, saveTag + (StringUtils.isEmpty(errMsg) ? "失败" : "失败（原因：" + errMsg + "）"), params, accountInfo, approveDealTypeEnum);
                                log.error("dubbo服务调用失败1,接口名：{}, 方法名：{}, Tag:{}, 返回结果：{}", dubboServiceConfig.getInterfaceName(), dubboServiceConfig.getMethodName(), tranDataStr, invokerStr);
                                return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "dubbo服务调用失败1");
                            }
                        } else {
                            String dealOpinion = null;
                            JSONObject invokeJsonObject = JSONObject.parseObject(JSONUtil.toJsonStr(invokeResult));
                            String msg = invokeJsonObject.getString("msg");
                            if (StringUtils.isNotEmpty(msg)) {
                                dealOpinion = msg;
                            }
                            if (invokeJsonObject.containsKey("code") && "0".equals(invokeJsonObject.getString("code")) || "200".equals(invokeJsonObject.getString("code")) || 0 == invokeJsonObject.getIntValue("code") || 200 == invokeJsonObject.getIntValue("code")) {
                                updateData.setEventStatus(EventStatusEnum.EXECUTE_SUCCESS_FINAL);
                                if (StringUtils.isEmpty(dealOpinion)) {
                                    dealOpinion = saveTag + "成功";
                                }
                                updateEventData(updateData, dealOpinion, params, accountInfo, approveDealTypeEnum);
                                return new Response<>(null, BizResponseEnums.SUCCESS, "执行动作成功");
                            } else {
                                updateData.setEventStatus(EventStatusEnum.EXECUTE_FAILURE_MIDDLE);
                                if (StringUtils.isEmpty(dealOpinion)) {
                                    dealOpinion = saveTag + "失败";
                                }
                                updateEventData(updateData, dealOpinion, params, accountInfo, approveDealTypeEnum);
                                log.error("dubbo服务调用失败2,接口名：{}, 方法名：{}, Tag:{}, 返回结果：{} dealOpinion:{}", dubboServiceConfig.getInterfaceName(), dubboServiceConfig.getMethodName(), tranDataStr, JSONUtil.toJsonStr(invokeResult), dealOpinion);
                                return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, dealOpinion);
                            }
                        }
                    } catch (Exception ex) {
                        log.error("dubbo服务调用异常,配置内容：{} 异常信息：{}", flowEventData.getEventConfig(), ex != null ? ex.getMessage() : "");
                        updateData.setEventStatus(EventStatusEnum.EXECUTE_FAILURE_MIDDLE);
                        String errMsg = "";
                        if (ex != null && ex.getMessage() != null) {
                            errMsg = ex.getMessage().length() > 100 ? ex.getMessage().substring(0, 100) : ex.getMessage();
                        }
                        updateEventData(updateData, saveTag + "异常:" + errMsg, params, accountInfo, approveDealTypeEnum);
                        return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "dubbo服务调用异常");
                    }
                case HTTP_SERVICE:
                    String httpParam = String.format("sign=%s&ticketEventTag=%s&ticketDataId=%s", sign, flowEventData.getEventTag(), ticketDataId);
                    JSONObject jsonHttpObject = null;
                    try {
                        String result = HttpUtils.sendPost(flowEventData.getEventConfig(), httpParam);
                        jsonHttpObject = JSONObject.parseObject(result);
                    } catch (Exception ex) {
                        log.error("http服务调用失败,配置内容：{} {}", flowEventData.getEventConfig(), httpParam);
                        updateData.setEventStatus(EventStatusEnum.EXECUTE_FAILURE_MIDDLE);
                        updateEventData(updateData, saveTag + "异常", params, accountInfo, approveDealTypeEnum);
                        return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "http服务调用失败");
                    }
                    if (jsonHttpObject == null || (jsonHttpObject.getInteger("code") != 0 && jsonHttpObject.getInteger("code") != 200)) {
                        updateData.setEventStatus(EventStatusEnum.EXECUTE_FAILURE_MIDDLE);
                        String errMsg = jsonHttpObject == null ? null : jsonHttpObject.getString("msg");
                        updateEventData(updateData, saveTag + (StringUtils.isEmpty(errMsg) ? "失败" : "失败（原因：" + errMsg + "）"), params, accountInfo, approveDealTypeEnum);
                        return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "http服务调用失败");
                    }
                    updateData.setEventStatus(EventStatusEnum.EXECUTE_SUCCESS_FINAL);
                    updateEventData(updateData, saveTag + "成功", params, accountInfo, approveDealTypeEnum);
                    return new Response<>(null, BizResponseEnums.SUCCESS, "执行动作成功");
                case HTTPS_SERVICE:
                    //默认使用post
                    String httpsParam = String.format("sign=%s&ticketEventTag=%s&ticketDataId=%s", sign, flowEventData.getEventTag(), ticketDataId);
                    JSONObject urlParams = new JSONObject();
                    urlParams.put("sign", sign);
                    urlParams.put("ticketEventTag", flowEventData.getEventTag());
                    urlParams.put("ticketDataId", ticketDataId);
                    JSONObject httpsJsonObject = null;
                    try {
                        //String result = HttpUtils.sendSSLPostJSON(flowEventData.getEventConfig(), httpsParam, urlParams.toJSONString());
                        String result = HttpUtils.sendSSLPostJSON(flowEventData.getEventConfig(), "", urlParams.toJSONString());
                        httpsJsonObject = JSONObject.parseObject(result);
                    } catch (Exception ex) {
                        log.error("https服务调用失败,配置内容：{} {}", flowEventData.getEventConfig(), urlParams.toJSONString());
                        updateData.setEventStatus(EventStatusEnum.EXECUTE_FAILURE_MIDDLE);
                        updateEventData(updateData, saveTag + "异常", params, accountInfo, approveDealTypeEnum);
                        return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "https服务调用失败");
                    }
                    if (httpsJsonObject == null || (httpsJsonObject.getInteger("code") != 0 && httpsJsonObject.getInteger("code") != 200)) {
                        updateData.setEventStatus(EventStatusEnum.EXECUTE_FAILURE_MIDDLE);
                        String errMsg = httpsJsonObject == null ? null : httpsJsonObject.getString("msg");
                        updateEventData(updateData, saveTag + (StringUtils.isEmpty(errMsg) ? "失败" : "失败（原因：" + errMsg + "）"), params, accountInfo, approveDealTypeEnum);
                        return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "https服务调用" + (StringUtils.isEmpty(errMsg) ? "失败" : "失败（原因：" + errMsg + "）"));
                    }
                    updateData.setEventStatus(EventStatusEnum.EXECUTE_SUCCESS_FINAL);
                    updateEventData(updateData, saveTag + "成功", params, accountInfo, approveDealTypeEnum);
                    return new Response<>(null, BizResponseEnums.SUCCESS, "执行动作成功");
                default:
                    log.warn("执行事件配置异常,工单={},节点={},事件={}, 事件类型={}", flowEventData.getTicketDataId(), flowEventData.getTicketFlowNodeDataId(), flowEventData.getId(), flowEventData.getEventType());
                    updateData.setEventStatus(EventStatusEnum.EXECUTE_FAILURE_MIDDLE);
                    updateEventData(updateData, saveTag + "异常", params, accountInfo, approveDealTypeEnum);
                    return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "执行事件类型配置异常");
            }
        }
        return new Response<>(null, BizResponseEnums.SUCCESS, "执行动作成功");
    }

    /**
     * @param ticketFormDataDto
     * @param userType
     * @param userId
     * @param userName
     * @return
     */
    @Override
    public Response updateTicket(TicketFormUpdateDto ticketFormDataDto, String userType, String userId, String userName) {
        return ticketDataService.updateTicketFormData(ticketFormDataDto, userType, userId, userName);
    }

    private void updateEventData(TicketFlowEventData ticketFlowEventData, String dealOpinion, HashMap<String, String> params, AccountInfo accountInfo, ApproveDealTypeEnum approveDealTypeEnum) {
        if (ticketFlowEventData == null) {
            return;
        }
        Date now = new Date();
        EventStatusEnum eventStatusEnum = ticketFlowEventData.getEventStatus();
        String ticketFlowEventDataId = ticketFlowEventData.getId();
        LambdaUpdateChainWrapper<TicketFlowEventData> lambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(ticketFlowEventDataMapper);
        lambdaUpdateChainWrapper.eq(TicketFlowEventData::getId, ticketFlowEventDataId)
                .isNull(TicketFlowEventData::getDeleteTime)
                .set(TicketFlowEventData::getEventStatus, eventStatusEnum)
                .set(TicketFlowEventData::getUpdateTime, now)
                .set(TicketFlowEventData::getExecuteTime, now)
                .set(TicketFlowEventData::getEventTranData, ticketFlowEventData.getEventTranData())
                .set(TicketFlowEventData::getUpdateBy, "tfs_system");
        if (Objects.nonNull(approveDealTypeEnum)) {
            lambdaUpdateChainWrapper.set(TicketFlowEventData::getApproveDealType, approveDealTypeEnum);
        }
        var updateRes = lambdaUpdateChainWrapper.update();
        if (updateRes == false) {
            log.error("回调事件数据保存失败:{}", ticketFlowEventData);
        }
        String dealUserId = "tfs_system";
        String dealUserType = "ldap";
        String dealUserName = "tfs_system";
        if (Objects.nonNull(accountInfo)
                && StringUtils.isNotEmpty(accountInfo.getAccountId())
                && StringUtils.isNotEmpty(accountInfo.getAccountType())
                && StringUtils.isNotEmpty(accountInfo.getAccountName())
        ) {
            dealUserId = accountInfo.getAccountId();
            dealUserType = accountInfo.getAccountType();
            dealUserName = accountInfo.getAccountName();
        }
        //新增节点执行结果
        TicketFlowNodeApproveDetail approveDetail = new TicketFlowNodeApproveDetail();
        approveDetail.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_APPROVE_DETAIL));
        approveDetail.setTicketDataId(ticketFlowEventData.getTicketDataId());
        approveDetail.setTicketFlowNodeDataId(ticketFlowEventData.getTicketFlowNodeDataId());
        approveDetail.setDealUserId(dealUserId);
        approveDetail.setDealUserType(dealUserType);
        approveDetail.setDealUserName(dealUserName);
        DealTypeCallbackEnum dealTypeCallback = DealTypeCallbackEnum.ACTION_FAILED;
        if (eventStatusEnum == EventStatusEnum.EXECUTE_SUCCESS_FINAL) {
            dealTypeCallback = DealTypeCallbackEnum.ACTION_SUCCESS;
        }
        approveDetail.setDealTypeCallback(dealTypeCallback);
        approveDetail.setDealType(ApproveDealTypeEnum.ACTION);
        approveDetail.setDealTypeDescription(ApproveDealTypeEnum.ACTION.getDesc());
        approveDetail.setTicketFlowEventDataId(ticketFlowEventDataId);
        approveDetail.setDealOpinion(dealOpinion);
        approveDetail.setCreateTime(now);
        approveDetail.setCreateBy("tfs_system");
        approveDetail.setUpdateTime(now);
        approveDetail.setUpdateBy("tfs_system");
        if (params != null) {
            if (params.containsKey("detail_type_des") && StringUtils.isNotEmpty(params.get("detail_type_des"))) {
                approveDetail.setDealTypeDescription(params.get("detail_type_des"));
            }
        }
        Boolean saveRes = ticketFlowNodeApproveDetailService.save(approveDetail);
        if (!saveRes) {
            log.error("操作记录保存失败:{}", approveDetail);
        }

    }

    /**
     * 如果节点是自动审批节点，则自动触发审批流程
     *
     * @param nextNode      下一个节点
     * @param ticketDataDto 工单信息
     */
//    public void autoApprove(TicketFlowNodeDataDto nextNode, TicketDataDto ticketDataDto) {
//        threadPoolTaskExecutor.submit(() -> {
//            try {
//                switch (nextNode.getAuditedType()) {
//                    case AUTO_PASS:
//                        ticketDataService.dealTicketDataById(ticketDataDto.getId(), ApproveDealTypeEnum.PASS.getCode(), nextNode.getAuditedType().getMsg(), "ldap", "tfs_system", "tfs_system", ticketDataDto.getCurrentNodeId());
//                        break;
//                    case AUTO_REJECT:
//                        ticketDataService.dealTicketDataById(ticketDataDto.getId(), ApproveDealTypeEnum.REJECT.getCode(), nextNode.getAuditedType().getMsg(), "ldap", "tfs_system", "tfs_system", ticketDataDto.getCurrentNodeId());
//                        break;
//                    default:
//                        break;
//                }
//            } catch (Exception e) {
//                log.error("工单[{}]自动审批节点[{}]自动审批异常。", ticketDataDto.getId(), nextNode.getId(), e);
//            }
//        });
//    }
    public void autoApprove(String ticketDataId, AuditedType auditedType, String currentNodeId) {
        threadPoolTaskExecutor.submit(() -> {
            try {
                switch (auditedType) {
                    case AUTO_PASS:
                        ticketDataService.dealTicketDataById(ticketDataId, ApproveDealTypeEnum.PASS.getCode(), auditedType.getMsg(), "ldap", "tfs_system", "tfs_system", currentNodeId);
                        break;
                    case AUTO_REJECT:
                        ticketDataService.dealTicketDataById(ticketDataId, ApproveDealTypeEnum.REJECT.getCode(), auditedType.getMsg(), "ldap", "tfs_system", "tfs_system", currentNodeId);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                log.error("工单[{}]自动审批节点[{}]自动审批异常。", ticketDataId, currentNodeId, e);
            }
        });
    }

    private Response<String> doPersist(
            AccountInfo dealUser,
            TicketData ticketDataUpdate,
            TicketFlowNodeApproveFinishBO nodeApproveFinishBO,
            TicketFlowNodeApproveDetail approveDetail) {
        String dealUserStr = JSONUtil.toJsonStr(dealUser);

        boolean exeRes = transactionTemplate.execute((transactionStatus) -> {
            boolean isUpdate = false;
            // 工单信息
            isUpdate = ticketDataService.lambdaUpdate()
                    .eq(TicketData::getId, ticketDataUpdate.getId())
                    .eq(TicketData::getUpdateTime, ticketDataUpdate.getUpdateTime())
                    .isNull(TicketData::getDeleteTime)
                    .isNull(TicketData::getTicketFinishTime)
                    .set(TicketData::getTicketStatus, ticketDataUpdate.getTicketStatus())
                    .set(TicketData::getCurrentNodeId, ticketDataUpdate.getCurrentNodeId())
                    .set(TicketData::getCurrentNodeName, ticketDataUpdate.getCurrentNodeName())
                    .set(TicketData::getCurrentDealUsers, ticketDataUpdate.getCurrentDealUsers())
                    .set(TicketData::getCurrentDoneUsers, ticketDataUpdate.getCurrentDoneUsers())
                    .set(TicketData::getCurrentCcUsers, ticketDataUpdate.getCurrentCcUsers())
                    .set(TicketData::getTicketFinishTime, ticketDataUpdate.getTicketFinishTime())
                    .set(TicketData::getUpdateTime, new Date())
                    .set(TicketData::getUpdateBy, dealUserStr)
                    .update();
            if (!isUpdate) {
                log.error("工单审批持久化数据失败-更新工单信息失败");
                return false;
            }

            //更新节点信息
            TicketFlowNodeDataDto currentNodeInfo = nodeApproveFinishBO.getCurrentNode();
            TicketFlowNodeDataDto addNodeInfo = nodeApproveFinishBO.getAddNode();
            TicketFlowNodeDataDto oldNextInfo = nodeApproveFinishBO.getOldNextNode();
            isUpdate = flowNodeDataService.lambdaUpdate()
                    .eq(TicketFlowNodeData::getId, currentNodeInfo.getId())
                    .isNull(TicketFlowNodeData::getDeleteTime)
                    .set(TicketFlowNodeData::getNodeStatus, currentNodeInfo.getNodeStatus())
                    .set(TicketFlowNodeData::getUpdateTime, new Date())
                    .set(TicketFlowNodeData::getUpdateBy, dealUserStr)
                    .update();
            if (!isUpdate) {
                log.error("工单审批持久化数据失败-更新当前节点信息失败");
                return false;
            }
            switch (currentNodeInfo.getNodeStatus()) {
                case APPROVING:
                    //是否加签
                    if (addNodeInfo != null) {
                        Date date = new Date();
                        TicketFlowNodeData addNodeData = new TicketFlowNodeData();
                        addNodeData.setNodeStatus(addNodeInfo.getNodeStatus());
                        addNodeData.setId(addNodeInfo.getId());
                        addNodeData.setNodeName(addNodeInfo.getNodeName());
                        addNodeData.setPreNodeId(addNodeInfo.getPreNodeId());
                        addNodeData.setTemplateId("-1");
                        addNodeData.setTicketDataId(addNodeInfo.getTicketDataId());
                        addNodeData.setTicketFlowDataId(addNodeInfo.getTicketFlowDataId());
                        addNodeData.setAuditedMethod(addNodeInfo.getAuditedMethod());
                        addNodeData.setAuditedType(addNodeInfo.getAuditedType());
                        addNodeData.setNodeStatus(NodeStatusEnum.APPROVE_INIT);
                        addNodeData.setNodeOrder(addNodeInfo.getNodeOrder());
                        addNodeData.setCreateBy(dealUserStr);
                        addNodeData.setUpdateBy(dealUserStr);
                        addNodeData.setCreateTime(date);
                        addNodeData.setUpdateTime(date);
                        isUpdate = flowNodeDataService.save(addNodeData);
                        if (!isUpdate) {
                            log.error("工单审批持久化数据失败-加签时，添加节点信息失败");
                            return false;
                        }
                        List<TicketFlowNodeExecutorDataDto> excutorList = addNodeInfo.getExcutorList();
                        if (CollectionUtils.isNotEmpty(excutorList)) {
                            List<TicketFlowNodeExecutorData> addList = new ArrayList<>();
                            for (TicketFlowNodeExecutorDataDto executorDataDto : excutorList) {
                                TicketFlowNodeExecutorData addInfo = new TicketFlowNodeExecutorData();
                                addInfo.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_EXECUTOR_DATA));
                                addInfo.setTicketFlowNodeDataId(addNodeData.getId());
                                addInfo.setTemplateId(executorDataDto.getTemplateId());
                                addInfo.setTicketDataId(executorDataDto.getTicketDataId());
                                addInfo.setExecutorType(executorDataDto.getExecutorType());
                                addInfo.setExecutorValue(executorDataDto.getExecutorValue());
                                addInfo.setExecutorList(executorDataDto.getExecutorList());
                                addInfo.setExecutorDoneList(executorDataDto.getExecutorDoneList());
                                addInfo.setCreateBy(dealUserStr);
                                addInfo.setUpdateBy(dealUserStr);
                                addInfo.setCreateTime(date);
                                addInfo.setUpdateTime(date);
                                addList.add(addInfo);
                            }
                            isUpdate = executorDataService.saveBatch(addList);
                            if (!isUpdate) {
                                log.error("工单审批持久化数据失败-加签时：添加执行组信息失败");
                                return false;
                            }
                        }
                    }
                    if (oldNextInfo != null) {
                        isUpdate = flowNodeDataService.lambdaUpdate()
                                .eq(TicketFlowNodeData::getId, oldNextInfo.getId())
                                .isNull(TicketFlowNodeData::getDeleteTime)
                                .set(TicketFlowNodeData::getPreNodeId, addNodeInfo == null ? oldNextInfo.getPreNodeId() : addNodeInfo.getId())
                                .set(TicketFlowNodeData::getNodeStatus, NodeStatusEnum.APPROVE_INIT)
                                .set(TicketFlowNodeData::getUpdateTime, new Date())
                                .set(TicketFlowNodeData::getUpdateBy, dealUserStr)
                                .update();
                        if (!isUpdate) {
                            log.error("工单审批持久化数据失败-更新下一个节点信息失败");
                            return false;
                        }
                    }
                    break;
                case APPROVE_PASS:
                    //是否加签
                    if (addNodeInfo != null) {
                        Date date = new Date();
                        TicketFlowNodeData addNodeData = new TicketFlowNodeData();
                        addNodeData.setNodeStatus(NodeStatusEnum.APPROVING);
                        addNodeData.setId(addNodeInfo.getId());
                        addNodeData.setNodeName(addNodeInfo.getNodeName());
                        addNodeData.setPreNodeId(addNodeInfo.getPreNodeId());
                        addNodeData.setTemplateId("-1");
                        addNodeData.setTicketDataId(addNodeInfo.getTicketDataId());
                        addNodeData.setTicketFlowDataId(addNodeInfo.getTicketFlowDataId());
                        addNodeData.setAuditedMethod(addNodeInfo.getAuditedMethod());
                        addNodeData.setAuditedType(addNodeInfo.getAuditedType());
                        addNodeData.setNodeOrder(addNodeInfo.getNodeOrder());
                        addNodeData.setCreateBy(dealUserStr);
                        addNodeData.setUpdateBy(dealUserStr);
                        addNodeData.setCreateTime(date);
                        addNodeData.setUpdateTime(date);
                        isUpdate = flowNodeDataService.save(addNodeData);
                        if (!isUpdate) {
                            log.error("工单审批持久化数据失败-加签时，添加节点信息失败");
                            return false;
                        }
                        List<TicketFlowNodeExecutorDataDto> excutorList = addNodeInfo.getExcutorList();
                        if (CollectionUtils.isNotEmpty(excutorList)) {
                            List<TicketFlowNodeExecutorData> addList = new ArrayList<>();
                            for (TicketFlowNodeExecutorDataDto executorDataDto : excutorList) {
                                TicketFlowNodeExecutorData addInfo = new TicketFlowNodeExecutorData();
                                addInfo.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_EXECUTOR_DATA));
                                addInfo.setTicketFlowNodeDataId(addNodeData.getId());
                                addInfo.setTemplateId(executorDataDto.getTemplateId());
                                addInfo.setTicketDataId(executorDataDto.getTicketDataId());
                                addInfo.setExecutorType(executorDataDto.getExecutorType());
                                addInfo.setExecutorValue(executorDataDto.getExecutorValue());
                                addInfo.setExecutorList(executorDataDto.getExecutorList());
                                addInfo.setExecutorDoneList(executorDataDto.getExecutorDoneList());
                                addInfo.setCreateBy(dealUserStr);
                                addInfo.setUpdateBy(dealUserStr);
                                addInfo.setCreateTime(date);
                                addInfo.setUpdateTime(date);
                                addList.add(addInfo);
                            }
                            isUpdate = executorDataService.saveBatch(addList);
                            if (!isUpdate) {
                                log.error("工单审批持久化数据失败-加签时：添加执行组信息失败");
                                return false;
                            }
                        }
                    }
                    if (oldNextInfo != null) {
                        NodeStatusEnum oldNextStatus = NodeStatusEnum.APPROVING;
                        if (null != addNodeInfo) {
                            oldNextStatus = NodeStatusEnum.APPROVE_INIT;
                        }
                        isUpdate = flowNodeDataService.lambdaUpdate()
                                .eq(TicketFlowNodeData::getId, oldNextInfo.getId())
                                .isNull(TicketFlowNodeData::getDeleteTime)
                                .set(TicketFlowNodeData::getPreNodeId, addNodeInfo == null ? oldNextInfo.getPreNodeId() : addNodeInfo.getId())
                                .set(TicketFlowNodeData::getNodeStatus, oldNextStatus)
                                .set(TicketFlowNodeData::getUpdateTime, new Date())
                                .set(TicketFlowNodeData::getUpdateBy, dealUserStr)
                                .update();
                        if (!isUpdate) {
                            log.error("工单审批持久化数据失败-更新下一个节点信息失败");
                            return false;
                        }
                        //更新节点审批人
                        if (NodeStatusEnum.APPROVING == oldNextStatus && CollectionUtils.isNotEmpty(oldNextInfo.getExcutorList())) {
                            List<TicketFlowNodeExecutorDataDto> excutorList = oldNextInfo.getExcutorList();
                            List<TicketFlowNodeExecutorData> ticketFlowNodeExecutorDataList = excutorList.stream()
                                    .filter(it -> ExecutorTypeEnum.APPLY_EXTERNAL_APPROVER == it.getExecutorType()
                                            || ExecutorTypeEnum.CA_EXTERNAL_APPROVER == it.getExecutorType()
                                            || ExecutorTypeEnum.CE_EXTERNAL_APPROVER == it.getExecutorType()
                                    )
                                    .map(it -> it.toTicketFlowNodeExecutorData())
                                    .collect(Collectors.toList());
                            if (CollectionUtils.isNotEmpty(ticketFlowNodeExecutorDataList)) {
                                isUpdate = executorDataService.updateBatchById(ticketFlowNodeExecutorDataList);
                                if (!isUpdate) {
                                    log.error("工单审批持久化数据失败-更新外部人员失败：{}", JSONObject.toJSONString(ticketFlowNodeExecutorDataList));
                                    return false;
                                }
                            }
                        }
                    }
                    break;
                default:
                    break;
            }

            //审批明细
            if (approveDetail != null) {
                var now = new Date();
                if (StringUtils.isEmpty(approveDetail.getId())) {
                    approveDetail.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_APPROVE_DETAIL));
                }
                approveDetail.setCreateBy(dealUserStr);
                approveDetail.setUpdateBy(dealUserStr);
                approveDetail.setCreateTime(now);
                approveDetail.setUpdateTime(now);
                isUpdate = ticketFlowNodeApproveDetailService.save(approveDetail);
                if (!isUpdate) {
                    log.error("工单审批持久化数据失败-插入审批明细信息失败");
                    return false;
                }
            }
            return true;
        });
        if (!exeRes) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "审批数据保存异常");
        }
        return new Response<>(null, BizResponseEnums.SUCCESS, "审批数据保存成功");
    }

    private Response setExecutorListStrIfExternalApprover(TicketFlowNodeExecutorDataDto ticketFlowNodeExecutorDataDto, String ticketDataId, String interfaceKey, List<TicketFormItemData> ticketFormItemDataList, String nodeName, AccountInfo applyUser) {
        String executorListStr = StringUtils.EMPTY;
        ExecutorTypeEnum executorType = ticketFlowNodeExecutorDataDto.getExecutorType();
        String executorValue = ticketFlowNodeExecutorDataDto.getExecutorValue();
        switch (executorType) {
            case APPLY_EXTERNAL_APPROVER:
            case CA_EXTERNAL_APPROVER:
            case CE_EXTERNAL_APPROVER:
                if (Objects.isNull(executorValue)) {
                    log.error("工单({})外部审批人({})，信息为空", ticketDataId, executorValue);
                    executorListStr = ticketFlowNodeExecutorDataDto.getDefaultExecutorValue(applyUser, ticketAccountService, nodeName);
                    if (StringUtils.isNotEmpty(executorListStr)) {
                        ticketFlowNodeExecutorDataDto.setExecutorList(executorListStr);
                    }
                    return Response.success();
                }
                JSONArray jsonArray = JSON.parseArray(executorValue);
                if (Objects.isNull(jsonArray) || jsonArray.size() < 1) {
                    log.error("工单({})外部审批人({})，信息异常", ticketDataId, executorValue);
                    executorListStr = ticketFlowNodeExecutorDataDto.getDefaultExecutorValue(applyUser, ticketAccountService, nodeName);
                    if (StringUtils.isNotEmpty(executorListStr)) {
                        ticketFlowNodeExecutorDataDto.setExecutorList(executorListStr);
                    }
                    return Response.success();
                }
                JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                if (StringUtils.isEmpty(jsonObject.getString("externalUrl")) || StringUtils.isEmpty(jsonObject.getString("externalTag"))) {
                    log.error("工单({})外部审批人({})，信息异常", ticketDataId, executorValue);
                    executorListStr = ticketFlowNodeExecutorDataDto.getDefaultExecutorValue(applyUser, ticketAccountService, nodeName);
                    if (StringUtils.isNotEmpty(executorListStr)) {
                        ticketFlowNodeExecutorDataDto.setExecutorList(executorListStr);
                    }
                    return Response.success();
                }
                String externalUrl = jsonObject.getString("externalUrl");
                String externalTag = jsonObject.getString("externalTag");
                JSONObject ticketFormItemDataJSONObj = new JSONObject();
                if (CollectionUtils.isNotEmpty(ticketFormItemDataList)) {
                    ticketFormItemDataList.stream().forEach(it -> {
                        ticketFormItemDataJSONObj.put(it.getItemLabel(), it.getItemValue());
                    });
                }
                String ticketFormItemData = JSON.toJSONString(ticketFormItemDataJSONObj);
                String sign = DigestUtils.md5DigestAsHex((interfaceKey == null ? "" : interfaceKey + ticketDataId).getBytes());
                var dubboServiceConfigRes = DubboServiceConfig.parseStrToDubboConfig(externalUrl);
                if (!dubboServiceConfigRes.isSuccess()) {
                    log.error("工单({})查外部审批人dubbo服务配置解析失败,配置内容：{}", ticketDataId, executorValue);
                    executorListStr = ticketFlowNodeExecutorDataDto.getDefaultExecutorValue(applyUser, ticketAccountService, nodeName);
                    if (StringUtils.isNotEmpty(executorListStr)) {
                        ticketFlowNodeExecutorDataDto.setExecutorList(executorListStr);
                    }
                    return Response.success();
                }
                var dubboServiceConfig = dubboServiceConfigRes.getData();
                List<AccountInfo> dataList;
                try {
                    Object invokeResult = dynamicDubboConsumer.invokeDubboService(dubboServiceConfig.getInterfaceName(), dubboServiceConfig.getMethodName(), new Object[]{sign, ticketDataId, externalTag, ticketFormItemData}, dubboServiceConfig.getVersion(), dubboServiceConfig.getGroup());
                    if (invokeResult instanceof String) {
                        String invokerStr = JSONUtil.toJsonStr(invokeResult);
                        if (invokerStr.contains("code:0") || invokerStr.contains("\"code\":0") || invokerStr.contains("\"code\":\"0\"")) {
                            Response<List<AccountInfo>> response = JSONObject.parseObject(invokerStr, new TypeReference<Response<List<AccountInfo>>>() {
                            });
                            dataList = response.getData();
                            if (CollectionUtils.isNotEmpty(dataList)) {
                                dataList.stream().forEach(it -> {
                                    String accountType = it.getAccountType();
                                    String accountId = it.getAccountId();
                                    if (StringUtils.isNotEmpty(accountId) && StringUtils.isNotEmpty(accountType)) {
                                        TicketRemoteAccountDto accountDto = ticketAccountService.getTicketRemoteAccountByIdAndType(accountId, accountType);
                                        if (null == accountDto) {
                                            String errorMsg = String.format("指定审批人：%s工单系统不存在", JSONObject.toJSONString(accountDto));
                                            log.error(errorMsg);
                                            throw new ServiceException(errorMsg);
                                        }
                                        it.setAccountName(accountDto.getUserName());
                                        it.setSameOriginId(accountDto.getSameOriginId());
                                    }
                                });
                                executorListStr = AccountInfo.ToAccountInfoListStr(dataList);
                            }
                        } else {
                            log.error("查外部审批人dubbo服务调用失败,接口名：{}, 方法名：{}, 返回结果：{}", dubboServiceConfig.getInterfaceName(), dubboServiceConfig.getMethodName(), invokerStr);
                            throw new ServiceException("查外部审批人dubbo服务调用失败");
                        }
                    } else {
                        JSONObject invokeJsonObject = JSONObject.parseObject(JSONUtil.toJsonStr(invokeResult));
                        if (invokeJsonObject.containsKey("code") && ("0".equals(invokeJsonObject.getString("code")) || "200".equals(invokeJsonObject.getString("code")) || 0 == invokeJsonObject.getIntValue("code") || 200 == invokeJsonObject.getIntValue("code"))) {
                            String invokerStr = JSONObject.toJSONString(invokeJsonObject);
                            Response<List<AccountInfo>> response = JSONObject.parseObject(invokerStr, new TypeReference<Response<List<AccountInfo>>>() {
                            });
                            dataList = response.getData();
                            if (CollectionUtils.isNotEmpty(dataList)) {
                                dataList.stream().forEach(it -> {
                                    String accountType = it.getAccountType();
                                    String accountId = it.getAccountId();
                                    if (StringUtils.isEmpty(accountId) && StringUtils.isEmpty(accountType)) {
                                        String errorMsg = String.format("外部审批人(accountId：%s，accountType：%s)信息不合法", accountId, accountType);
                                        log.error(errorMsg);
                                        throw new ServiceException(errorMsg);
                                    }
                                    TicketRemoteAccountDto accountDto = ticketAccountService.getTicketRemoteAccountByIdAndType(accountId, accountType);
                                    if (null == accountDto) {
                                        String errorMsg = String.format("指定审批人：%s工单系统不存在", JSONObject.toJSONString(accountDto));
                                        log.error(errorMsg);
                                        throw new ServiceException(errorMsg);
                                    }
                                    it.setAccountName(accountDto.getUserName());
                                    it.setSameOriginId(accountDto.getSameOriginId());
                                });
                                executorListStr = AccountInfo.ToAccountInfoListStr(dataList);
                            }
                        } else {
                            log.error("查外部审批人dubbo服务调用失败,接口名：{}, 方法名：{}, 返回结果：{}", dubboServiceConfig.getInterfaceName(), dubboServiceConfig.getMethodName(), JSONUtil.toJsonStr(invokeResult));
                            throw new ServiceException("查外部审批人dubbo服务调用失败");
                        }
                    }
                } catch (Exception ex) {
                    log.error("查外部审批人dubbo服务调用异常,配置内容：{} 异常信息：{}", executorValue, ex != null ? ex : "");
                } finally {
                    if (StringUtils.isEmpty(executorListStr)) {
                        //自己兜底
                        ticketAccountService.notifyQwMsg(
                                String.format("未查到外部审批人(%s)，请及时处理！", dubboServiceConfig),
                                Arrays.asList("songbing", "owen", "yinshasha")
                        );
                        executorListStr = ticketFlowNodeExecutorDataDto.getDefaultExecutorValue(applyUser, ticketAccountService, nodeName);
                    }
                    if (StringUtils.isNotEmpty(executorListStr)) {
                        ticketFlowNodeExecutorDataDto.setExecutorList(executorListStr);
                    }
                }
        }
        return Response.success();
    }

    /**
     * 流转到下一个节点
     */
    private TicketFlowNodeApproveFinishBO transferNextNode(
            TicketDataDto ticketDataDto,
            TicketFlowNodeDataDto addNodeInfo,
            TicketFlowNodeDataDto currentNodeInfo
    ) {
        TicketFlowNodeApproveFinishBO nodeApproveFinishBO = new TicketFlowNodeApproveFinishBO();
        nodeApproveFinishBO.setAddNode(addNodeInfo);
        nodeApproveFinishBO.setCurrentNode(currentNodeInfo);

        //找出原有的下一个节点
        List<TicketFlowNodeDataDto> ticketFlowNodeDataDtoList = ticketDataDto.getTicketFlowDataDto().getTicketFlowNodeDataDtoList();
        TicketFlowNodeDataDto oldNextNode = ticketFlowNodeDataDtoList.stream().filter(item -> Objects.equals(item.getPreNodeId(), ticketDataDto.getCurrentNodeId())).findFirst().orElse(null);
        nodeApproveFinishBO.setOldNextNode(oldNextNode);
        nodeApproveFinishBO.setNextNode(addNodeInfo == null ? oldNextNode : addNodeInfo);
        return nodeApproveFinishBO;
    }


    /**
     * 根据流转节点，设置工单更新信息
     *
     * @param ticketDataUpdate
     * @param transferNode
     */
    private void setTransferNodeInfoToTicketData(TicketData ticketDataUpdate, TicketFlowNodeDataDto transferNode) {
        if (transferNode == null) {
            ticketDataUpdate.setCurrentNodeId("-1");
            ticketDataUpdate.setCurrentNodeName("");
            ticketDataUpdate.setTicketFinishTime(new Date());
            ticketDataUpdate.setCurrentDealUsers("");
        } else {
            //更新 currentNodeId
            ticketDataUpdate.setCurrentNodeId(transferNode.getId());
            //更新 currentNodeName
            ticketDataUpdate.setCurrentNodeName(transferNode.getNodeName());
            //更新 currentDealUsers
            ticketDataUpdate.setCurrentDealUsers(JSONUtil.toJsonStr(transferNode.getDealUserList()));
        }
    }


    /**
     * 审批通过， 审批拒绝抄送
     * com.smy.tfs.biz.service.NotificationService#notifyQw
     * com.smy.tfs.biz.service.NotificationService#notifyQwCard
     *
     * @param ccUsers 已计算出的抄送人
     */
    private void doApproveFinishCC(AccountInfo dealUser, String ccUsers, TicketDataStatusEnum
            ticketStatus, TicketDataDto ticketDataDto) {
        String dealUserStr = JSONUtil.toJsonStr(dealUser);
        List<AccountInfoDto> accountInfoList = getCcQywxIdList(ccUsers);
        if (CollectionUtils.isEmpty(accountInfoList)) {
            log.warn("未找到用户[{}]相关的企业微信ID列表", ccUsers);
            return;
        }
        TicketFlowNodeDataDto currentFlowNodeData = ticketDataDto.getTicketFlowDataDto().getTicketFlowNodeDataDtoList().stream().filter(item -> Objects.equals(item.getId(), ticketDataDto.getCurrentNodeId())).findFirst().orElse(null);
        NotificationService.QwCardMsg qwcardMsg = new NotificationService.QwCardMsg();
        String titleTemplate = "【抄送】{{apply_user}}提交了{{ticket_name}}";
        titleTemplate = titleTemplate
                .replace("{{apply_user}}", ticketDataDto.getApplyUserName())
                .replace("{{ticket_name}}", ticketDataDto.getTicketName()
                );
        if (ticketStatus == TicketDataStatusEnum.APPLY_END) {
            titleTemplate = titleTemplate + "，已审批通过";
        } else if (ticketStatus == TicketDataStatusEnum.REJECT) {
            titleTemplate = titleTemplate + "，已审批驳回";
        }
        qwcardMsg.setTitle(titleTemplate);

        String description = "申请时间：{{apply_time}}   业务：{{app_name}}";
        qwcardMsg.setDescription(description
                .replace("{{apply_time}}", DateUtil.formatDateTime(ticketDataDto.getCreateTime()))
                .replace("{{app_name}}", ticketDataDto.getAppName())
        );

        Map<String, String> linkMap = new HashMap<>();
        String jumpUrl = tfSJumpUrlProperties.getTicketDetailUrl() + ticketDataDto.getId();
        linkMap.put("工单详情", jumpUrl);
        qwcardMsg.setLinkKeyMap(linkMap);

        List<String> userIdList = accountInfoList.stream().map(AccountInfoDto::getQywxId).distinct().collect(Collectors.toList());
        qwcardMsg.setUserIdList(userIdList);
        qwcardMsg.setJumpUrl(jumpUrl);

        //TODO 复杂处理
        List<NotificationService.KvContent> kvContentList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(ticketDataDto.getTicketFormDataDto().getTicketFormItemDataDtoList())) {
            int maxSum = 0;
            int itemCount = ticketDataDto.getTicketFormDataDto().getTicketFormItemDataDtoList().size();
            for (TicketFormItemDataDto ticketFormItemDataDto : ticketDataDto.getTicketFormDataDto().getTicketFormItemDataDtoList()) {
                //TODO:Switch类型
                maxSum = maxSum + 1;
                if (itemCount > 6 && maxSum == 6) {
                    kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.LINK, ticketFormItemDataDto.getItemLabel(), "查看更多..", jumpUrl, "", ""));
                    break;
                } else {
                    kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.TEXT, ticketFormItemDataDto.getItemLabel(), ticketFormItemDataDto.getItemValue(), "", "", ""));
                }
            }
        }
        qwcardMsg.setKvContentList(kvContentList);
        NotificationService.CardNotifyRet cardNotifyRet = notificationService.notifyQwCard(qwcardMsg);

        var now = new Date();
        for (AccountInfoDto info : accountInfoList) {
            TicketFlowNodeApproveDetail approveDetail = new TicketFlowNodeApproveDetail();
            approveDetail.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_APPROVE_DETAIL));
            approveDetail.setTicketDataId(ticketDataDto.getId());
            approveDetail.setTicketFlowNodeDataId(currentFlowNodeData.getId());
            approveDetail.setDealUserId(info.getAccountId());
            approveDetail.setDealUserType(info.getAccountType());
            approveDetail.setDealUserName(info.getAccountName());
            approveDetail.setDealType(ApproveDealTypeEnum.SEND);
            approveDetail.setDealOpinion("");
            approveDetail.setCreateBy(dealUserStr);
            approveDetail.setUpdateBy(dealUserStr);
            approveDetail.setCreateTime(now);
            approveDetail.setUpdateTime(now);
            var ccRes = ticketFlowNodeApproveDetailService.save(approveDetail);
            if (ccRes == false) {
                log.error(String.format("抄送卡片异常：%v", qwcardMsg));
            }
        }
    }

    private String calcCurrentCcUsers(TicketDataDto ticketDataDto, List<AccountInfoDto> accountInfoList) {
        List<AccountInfo> allList = AccountInfo.ToAccountInfoList(ticketDataDto.getCurrentCcUsers());
        if (CollectionUtils.isNotEmpty(accountInfoList)) {
            for (var item : accountInfoList) {
                allList.add(item.ToAccountInfo());
            }
        }
        return JSONUtil.toJsonStr(AccountInfo.Distinct(allList));
    }

    private void doApproveRejectCC(String ccUsers, TicketDataDto ticketDataDto) {
        List<AccountInfoDto> accountInfoList = getCcQywxIdList(ccUsers);
        if (CollectionUtils.isEmpty(accountInfoList)) {
            log.warn("未找到用户[{}]相关的企业微信ID列表", ccUsers);
            return;
        }

        TicketFlowNodeDataDto currentFlowNodeData = ticketDataDto.getTicketFlowDataDto().getTicketFlowNodeDataDtoList().stream().filter(item -> Objects.equals(item.getId(), ticketDataDto.getCurrentNodeId())).findFirst().orElse(null);
        NotificationService.QwCardMsg qwcardMsg = new NotificationService.QwCardMsg();
        String titleTemplate = "工单： {{ticket_name}} 节点：{{node_name}} 审批驳回 ";
        qwcardMsg.setTitle(titleTemplate.replace("{{ticket_name}}", ticketDataDto.getTicketName()).replace("{{node_name}}", currentFlowNodeData.getNodeName()));

        String description = "申请人：{{apply_user}} 申请时间：{{apply_time}}";
        qwcardMsg.setDescription(description.replace("{{apply_user}}", ticketDataDto.getApplyUserName()).replace("{{apply_time}}", DateUtil.formatDateTime(ticketDataDto.getCreateTime())));

        String jumpUrl = tfSJumpUrlProperties.getTicketDetailUrl() + ticketDataDto.getId();
        Map<String, String> linkMap = new HashMap<>();
        linkMap.put("工单详情", jumpUrl);
        qwcardMsg.setLinkKeyMap(linkMap);

        List<String> userIdList = accountInfoList.stream().map(AccountInfoDto::getQywxId).distinct().collect(Collectors.toList());

        qwcardMsg.setUserIdList(userIdList);
        qwcardMsg.setJumpUrl(jumpUrl);

        //TODO 复杂处理
        List<NotificationService.KvContent> kvContentList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(ticketDataDto.getTicketFormDataDto().getTicketFormItemDataDtoList())) {
            int maxSum = 0;
            int itemCount = ticketDataDto.getTicketFormDataDto().getTicketFormItemDataDtoList().size();
            for (TicketFormItemDataDto ticketFormItemDataDto : ticketDataDto.getTicketFormDataDto().getTicketFormItemDataDtoList()) {
                //TODO:Switch类型
                maxSum = maxSum + 1;
                if (itemCount > 6 && maxSum == 6) {
                    kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.LINK, ticketFormItemDataDto.getItemLabel(), "查看更多..", jumpUrl, "", ""));
                    break;
                } else {
                    switch (ticketFormItemDataDto.getItemType()) {
                        case INPUTNUMBER:
                            kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.TEXT, ticketFormItemDataDto.getItemLabel(), ticketFormItemDataDto.getItemValue(), "", "", ""));
                            break;
                        case SELECTMULTIPLE:
                            kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.TEXT, ticketFormItemDataDto.getItemLabel(), ticketFormItemDataDto.getItemValue(), "", "", ""));
                            break;
                        case TIMESPAN:
                            kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.TEXT, ticketFormItemDataDto.getItemLabel(), ticketFormItemDataDto.getItemValue(), "", "", ""));
                            break;
                        case PICTURE:
                            kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.TEXT, ticketFormItemDataDto.getItemLabel(), ticketFormItemDataDto.getItemValue(), "", "", ""));
                            break;
                        case FILE:
                            kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.TEXT, ticketFormItemDataDto.getItemLabel(), ticketFormItemDataDto.getItemValue(), "", "", ""));
                            break;
                        case GROUP:
                            kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.TEXT, ticketFormItemDataDto.getItemLabel(), ticketFormItemDataDto.getItemValue(), "", "", ""));
                            break;
                        default:
                            kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.TEXT, ticketFormItemDataDto.getItemLabel(), ticketFormItemDataDto.getItemValue(), "", "", ""));
                            break;
                    }
                }
            }
        }
        qwcardMsg.setKvContentList(kvContentList);
        NotificationService.CardNotifyRet cardNotifyRet = notificationService.notifyQwCard(qwcardMsg);

        ticketDataService.lambdaUpdate()
                .eq(TicketData::getId, ticketDataDto.getId())
                .set(TicketData::getCurrentCcUsers, this.calcCurrentCcUsers(ticketDataDto, accountInfoList))
                .set(TicketData::getUpdateTime, new Date())
                .update();
    }


    /**
     * 流转到节点，通知相关审批人
     * com.smy.tfs.biz.service.NotificationService#notifyQw
     * com.smy.tfs.biz.service.NotificationService#notifyQwCard
     *
     * @param notifyUsers
     */
    public Response<String> doApproveNotify(List<AccountInfo> notifyUsers, TicketDataDto ticketDataDto, TicketFlowNodeDataDto flowNodeDataDto) {

        List<AccountInfoDto> accountInfoList = getCcQywxIdList(notifyUsers);

        NotificationService.QwCardMsg qwcardMsg = new NotificationService.QwCardMsg();
        String titleTemplate = "{{apply_user}}提交的{{ticket_name}}，待你处理";
        qwcardMsg.setTitle(titleTemplate
                .replace("{{apply_user}}", ticketDataDto.getApplyUserName())
                .replace("{{ticket_name}}", ticketDataDto.getTicketName())
        );

        String desciptionTemplate = "申请时间：{{apply_time}}    业务：{{app_name}}";
        qwcardMsg.setDescription(desciptionTemplate
                .replace("{{apply_time}}", DateUtil.formatDateTime(ticketDataDto.getCreateTime()))
                .replace("{{app_name}}", ticketDataDto.getAppName()));

        String jumpUrl = tfSJumpUrlProperties.getTicketDetailUrl() + ticketDataDto.getId();
        qwcardMsg.setJumpUrl(jumpUrl);

        //TODO 复杂处理
        List<NotificationService.KvContent> kvContentList = new ArrayList<>();
        List<TicketFormItemDataDto> ticketFormItemDataDtoList = ticketDataDto.getTicketFormDataDto().getTicketFormItemDataDtoList();
        if (CollectionUtils.isNotEmpty(ticketFormItemDataDtoList)) {
            int maxSum = 0;
            int itemCount = ticketDataDto.getTicketFormDataDto().getTicketFormItemDataDtoList().size();
            for (TicketFormItemDataDto ticketFormItemDataDto : ticketDataDto.getTicketFormDataDto().getTicketFormItemDataDtoList()) {
                //TODO:Switch类型
                maxSum = maxSum + 1;
                if (itemCount > 6 && maxSum == 6) {
                    kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.LINK, ticketFormItemDataDto.getItemLabel(), "查看更多..", jumpUrl, "", ""));
                    break;
                } else {
                    kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.TEXT, ticketFormItemDataDto.getItemLabel(), ticketFormItemDataDto.getItemValue(), "", "", ""));
                }
            }
        }
        qwcardMsg.setKvContentList(kvContentList);

        log.info("doApproveNotify 需要通知的人，{}", JSONUtil.toJsonStr(accountInfoList));
        for (AccountInfoDto accountInfo : accountInfoList) {
            qwcardMsg.setUserIdList(Arrays.asList(accountInfo.getQywxId()));
            Map<String, String> buttonMap = new LinkedHashMap<>();
            String rejectKey = String.format("%s-%s-%s-%s-%s",
                    "approveCardButtonCallBack",
                    flowNodeDataDto.getTicketDataId(),
                    ApproveDealTypeEnum.REJECT.getCode(),
                    String.format("%s_%s_%s", accountInfo.getAccountType(), accountInfo.getAccountId(), accountInfo.getAccountName()),
                    flowNodeDataDto.getId()
            );
            buttonMap.put(rejectKey, "驳回");
            String passKey = String.format("%s-%s-%s-%s-%s",
                    "approveCardButtonCallBack",
                    flowNodeDataDto.getTicketDataId(),
                    ApproveDealTypeEnum.PASS.getCode(),
                    String.format("%s_%s_%s", accountInfo.getAccountType(), accountInfo.getAccountId(), accountInfo.getAccountName()),
                    flowNodeDataDto.getId()
            );
            buttonMap.put(passKey, "通过");
            qwcardMsg.setButtonKeyMap(buttonMap);
            NotificationService.CardNotifyRet cardNotifyRet = notificationService.notifyQwCard(qwcardMsg);
            TicketFlowNodeData ticketFlowNodeData = flowNodeDataService.lambdaQuery()
                    .eq(TicketFlowNodeData::getId, flowNodeDataDto.getId())
                    .one();
            Map<String, List> wxDealCardCode = new HashMap<>();
            if (StringUtils.isNotBlank(ticketFlowNodeData.getNodeWxDealCardCode())) {
                wxDealCardCode.putAll(JSONObject.parseObject(ticketFlowNodeData.getNodeWxDealCardCode(), Map.class));
            }

            String key = String.format("%s-%s-%s", accountInfo.getAccountId(), accountInfo.getAccountType(), TicketMsgArriveTypeEnum.WECOM.getCode());
            if (!wxDealCardCode.containsKey(key)) {
                wxDealCardCode.put(key, new ArrayList());
            }
            wxDealCardCode.get(key).add(cardNotifyRet.getResponseCode());

            var msgSaveRes = flowNodeDataService.lambdaUpdate()
                    .eq(TicketFlowNodeData::getId, flowNodeDataDto.getId()).
                    isNull(TicketFlowNodeData::getDeleteTime)
                    .set(TicketFlowNodeData::getNodeWxDealCardCode, JSONUtil.toJsonStr(wxDealCardCode))
                    .update();
            if (!msgSaveRes) {
                log.error(String.format("节点卡片信息存储失败，工单ID：%s 流程节点ID：%s", flowNodeDataDto.getTicketDataId(), flowNodeDataDto.getId()));
            }
        }
        return new Response<>(null, BizResponseEnums.SUCCESS, "审批通知成功");
    }

    private List<AccountInfoDto> getCcQywxIdList(String ccUsers) {
        if (ccUsers == null) {
            return new ArrayList<>();
        }
        List<AccountInfo> accountInfoList = AccountInfo.ToAccountInfoList(ccUsers);
        List<AccountInfoDto> accountInfoListDto = new ArrayList<>();
        for (AccountInfo notifyUser : accountInfoList) {
            TicketRemoteAccountDto ticketRemoteAccountDto = ticketAccountService.getTicketRemoteAccountByIdAndType(notifyUser.getAccountId(), notifyUser.getAccountType());
            if (ticketRemoteAccountDto != null && StringUtils.isNotBlank(ticketRemoteAccountDto.getQywxId())) {
                accountInfoListDto.add(new AccountInfoDto(notifyUser.getSameOriginId(), notifyUser.getAccountType(), notifyUser.getAccountId(), notifyUser.getAccountName(), ticketRemoteAccountDto.getQywxId()));
            } else {
                log.info(String.format("Type:[%s] ID:[%s]转换企业ID失败", notifyUser.getAccountType(), notifyUser.getAccountId()));
            }
        }
        return accountInfoListDto;
    }

    private List<AccountInfoDto> getCcQywxIdList(List<AccountInfo> accountInfoList) {
        if (accountInfoList == null) {
            accountInfoList = new ArrayList<>();
        }
        List<AccountInfoDto> accountInfoListDto = new ArrayList<>();
        for (AccountInfo notifyUser : accountInfoList) {
            TicketRemoteAccountDto ticketRemoteAccountDto = ticketAccountService.getTicketRemoteAccountByIdAndType(notifyUser.getAccountId(), notifyUser.getAccountType());
            if (ticketRemoteAccountDto != null && StringUtils.isNotBlank(ticketRemoteAccountDto.getQywxId())) {
                accountInfoListDto.add(new AccountInfoDto(notifyUser.getSameOriginId(), notifyUser.getAccountType(), notifyUser.getAccountId(), notifyUser.getAccountName(), ticketRemoteAccountDto.getQywxId()));
            } else {
                log.info(String.format("Type:[%s] ID:[%s]转换企业ID失败", notifyUser.getAccountType(), notifyUser.getAccountId()));
            }
        }
        return accountInfoListDto;
    }

    /**
     * 计算，叠加抄送人
     *
     * @param ccUserList [{"accountId":"admin","accountName":"admin","accountType":"ldap"}]
     * @param applyUser  {"accountId":"admin","accountName":"admin","accountType":"ldap"}
     * @return
     */
    private String calcCcUsers(List<AccountInfo> ccUserList, String applyUser) {
        //抄送人计算
        List<AccountInfo> accountInfoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(ccUserList)) {
            accountInfoList.addAll(ccUserList);
        }
        if (StringUtils.isNotBlank(applyUser)) {
            try {
                accountInfoList.add(BeanUtil.toBean(applyUser, AccountInfo.class));
            } catch (Exception e) {
                log.error("", e);
            }
        }
        return JSONUtil.toJsonStr(accountInfoList);
    }


    /**
     * 判断当前 executor是否存在有人审批通过的记录
     *
     * @param executorDataDto
     * @return
     */
    private boolean executePass(TicketFlowNodeExecutorDataDto executorDataDto) {
        if (StringUtils.isEmpty(executorDataDto.getExecutorList())) {
            return false;
        }
        List<AccountInfo> userList = AccountInfo.ToAccountInfoList(executorDataDto.getExecutorList());
        for (var user : userList) {
            TicketFlowNodeApproveDetailQuery detailQuery = new TicketFlowNodeApproveDetailQuery();
            detailQuery.setTicketDataId(executorDataDto.getTicketDataId());
            detailQuery.setTicketFlowNodeDataId(executorDataDto.getTicketFlowNodeDataId());
            if (SecurityUtils.isTfs()) {
                detailQuery.setSameOriginId(user.getSameOriginId());
            } else {
                detailQuery.setDealUserId(user.getAccountId());
                detailQuery.setDealUserType(user.getAccountType());
            }

            detailQuery.setDealType(ApproveDealTypeEnum.PASS.getCode());
            Integer count = ticketFlowNodeApproveDetailService.countByQuery(detailQuery);
            if (count > 0) {
                return true;
            }
        }
        return false;
    }

    private boolean updateById(TicketData ticketDataUpdate, String dealUserId) {
        if (ticketDataUpdate == null) {
            return false;
        }
        Date date = new Date();
        ticketDataUpdate.setUpdateTime(date);
        ticketDataUpdate.setUpdateBy(dealUserId);
        return ticketDataMapper.updateById(ticketDataUpdate) == 1;
    }

    @ApiDoc(value = "加签", description = "加签")
    @Override
    public Response addTicketFlowNodeData(AddTicketFlowNodeDto addTicketFlowNodeDto, AccountInfo accountInfo) {
        if (addTicketFlowNodeDto == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "参数为空");
        }
        if (addTicketFlowNodeDto.getAddNodeDataDto() == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "添加节点信息为空");
        }
        if ("BEFORE".equals(addTicketFlowNodeDto.getAddNodeType())) {

        } else {
            if (addTicketFlowNodeDto.getApproveDto() == null) {
                return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "节点审批信息为空");
            }
        }
        if (addTicketFlowNodeDto.getAddNodeDataDto().getAuditedMethod() == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "添加节点审批方式为空");
        }
        if (CollectionUtils.isEmpty(addTicketFlowNodeDto.getAddNodeDataDto().getExcutorList())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "添加节点执行组列表为空");
        }
        addTicketFlowNodeDto.getApproveDto().setDealType(ApproveDealTypeEnum.PASS.getCode());
        if (StringUtils.isEmpty(addTicketFlowNodeDto.getApproveDto().getDealOpinion())) {
            if ("BEFORE".equals(addTicketFlowNodeDto.getAddNodeType())){
                addTicketFlowNodeDto.getApproveDto().setDealOpinion("前置加签");
            }else{
                addTicketFlowNodeDto.getApproveDto().setDealOpinion(ApproveDealTypeEnum.PASS.getDesc());
            }
        }
        Response response = null;
        if ("BEFORE".equals(addTicketFlowNodeDto.getAddNodeType())) {
            response = this.addPreNode(
                    addTicketFlowNodeDto,
                    accountInfo
            );
        } else {
            response = this.approve(
                    addTicketFlowNodeDto.getApproveDto(),
                    addTicketFlowNodeDto.getAddNodeDataDto(),
                    accountInfo.getSameOriginId(),
                    accountInfo.getAccountType(),
                    accountInfo.getAccountId(),
                    accountInfo.getAccountName()
            );
        }
        if (response == null || !response.isSuccess()) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "加签失败" + (response == null ? "" : "," + response.getMsg()));
        } else {
            return new Response<>(null, BizResponseEnums.SUCCESS, "加签成功");
        }
    }

    /**
     * 加上签
     * 1.撤销卡片
     * 2.添加记录
     * 3.更新状态(data+node)
     * 4.添加节点
     * 5.发送通知
     *
     * @param nodeDto
     * @return
     */
    public Response<String> addPreNode(AddTicketFlowNodeDto nodeDto, AccountInfo accountInfo) {
        //参数校验
        Response<String> addPreNodeParamsCheck = TicketDataApproveServiceInner.addPreNodeParamsCheck(nodeDto, accountInfo);
        if (!addPreNodeParamsCheck.isSuccess()) {
            return addPreNodeParamsCheck;
        }
        final Date now = new Date();
        //参数校验函数
        String dealUserStr = accountInfo.ToJsonString();
        TicketFlowNodeApproveDto approveDto = nodeDto.getApproveDto();
        AddTicketFlowNewNodeDto addNodeDto = nodeDto.getAddNodeDataDto();
        String ticketID = approveDto.getTicketID();
        String mode = nodeDto.getMode();
        //数据查询
        Response<TicketDataDto> ticketDataDtoResponse = ticketDataService.selectFullTicketDataById(new ReqParam(ticketID));
        if (!ticketDataDtoResponse.isSuccess()) {
            return new Response<>(null, BizResponseEnums.QUERY_ERROR, "工单数据重新查询失败");
        }
        //数据库工单数据
        TicketDataDto dbTicketData = ticketDataDtoResponse.getData();
        TicketFlowDataDto dbFlowData = null;
        List<TicketFlowNodeDataDto> dbFlowNodeListData = null;
        String dbCurrentNodeID = dbTicketData.getCurrentNodeId();
        TicketFlowNodeDataDto dbCurrentNodeData = null;

        //数据赋值
        if (StringUtils.isEmpty(dbTicketData.getCurrentNodeId())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单当前节点为空，无法加签");
        }
        dbFlowData = dbTicketData.getTicketFlowDataDto();
        if (dbFlowData == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID：{%s} 不存在流程数据", dbTicketData.getId()));
        }
        dbFlowNodeListData = dbFlowData.getTicketFlowNodeDataDtoList();
        if (CollectionUtils.isEmpty(dbFlowNodeListData)) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID：{%s} 不存在流程节点数据", dbTicketData.getId()));
        }
        dbCurrentNodeData = dbFlowNodeListData.stream().filter(item -> Objects.equals(item.getId(), dbCurrentNodeID)).findFirst().orElse(null);
        if (dbCurrentNodeData == null) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "当前节点不存在");
        }
        //前置幂等判断
        if (StringUtils.isNotBlank(approveDto.getDealNodeId()) && !Objects.equals(dbCurrentNodeID, approveDto.getDealNodeId())) {
            String cardButtonText = "异常节点";
            List<TicketFlowNodeApproveDetail> approveDetailList = ticketFlowNodeApproveDetailService.lambdaQuery()
                    .isNull(TicketFlowNodeApproveDetail::getDeleteTime)
                    .eq(TicketFlowNodeApproveDetail::getTicketDataId, dbTicketData.getId())
                    .eq(TicketFlowNodeApproveDetail::getTicketFlowNodeDataId, approveDto.getDealNodeId())
                    .eq(TicketFlowNodeApproveDetail::getDealUserId, accountInfo.getAccountId())
                    .eq(TicketFlowNodeApproveDetail::getDealUserType, accountInfo.getAccountType()).list();
            if (CollectionUtils.isNotEmpty(approveDetailList)) {
                TicketFlowNodeApproveDetail approveDetail = approveDetailList.get(0);
                switch (approveDetail.getDealType()) {
                    case PASS:
                        cardButtonText = "审批同意";
                        break;
                    case REJECT:
                        cardButtonText = "审批拒绝";
                        break;
                    case DISPATCH:
                        cardButtonText = "派单完成";
                        break;
                    case WITHDRAW:
                        cardButtonText = "申请人已撤回";
                        break;
                }
            }
            for (TicketFlowNodeDataDto flowNodeListDatum : dbFlowNodeListData) {
                if (Objects.equals(flowNodeListDatum.getId(), approveDto.getDealNodeId())) {
                    this.disable(dbTicketData, flowNodeListDatum, accountInfo, cardButtonText);
                    break;
                }
            }
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID：%s 流程节点不匹配。传入节点=%s,当前审批节点=%s", dbTicketData.getId(), approveDto.getDealNodeId(), dbCurrentNodeID));
        }

        //1.撤销卡片
        this.disable(dbTicketData, dbCurrentNodeData, accountInfo, "加签完成");
        TicketFlowNodeDataDto newNode = genNode(nodeDto.getAddNodeDataDto(), "BEFORE", dbCurrentNodeData);

        List<TicketFlowNodeActionData> actionDataList = new ArrayList<>();
        if ("STRICT".equals(mode)) {
            TicketFlowNodeActionData approvePassAction = new TicketFlowNodeActionData();
            approvePassAction.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_ACTION_DATA));
            approvePassAction.setTemplateId("-1");
            approvePassAction.setTicketDataId(dbTicketData.getId());
            approvePassAction.setTicketFlowNodeDataId(newNode.getId());
            approvePassAction.setActionValue("{}");
            approvePassAction.setCreateBy(dealUserStr);
            approvePassAction.setUpdateBy(dealUserStr);
            approvePassAction.setCreateTime(now);
            approvePassAction.setUpdateTime(now);
            TicketFlowNodeActionData approveRejectAction = new TicketFlowNodeActionData();
            approveRejectAction.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_ACTION_DATA));
            approveRejectAction.setTemplateId("-1");
            approveRejectAction.setTicketDataId(dbTicketData.getId());
            approveRejectAction.setTicketFlowNodeDataId(newNode.getId());
            approveRejectAction.setActionValue("{}");
            approveRejectAction.setCreateBy(dealUserStr);
            approveRejectAction.setUpdateBy(dealUserStr);
            approveRejectAction.setCreateTime(now);
            approveRejectAction.setUpdateTime(now);
            TicketFlowNodeActionData approveCommentAction = new TicketFlowNodeActionData();
            approveCommentAction.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_ACTION_DATA));
            approveCommentAction.setTemplateId("-1");
            approveCommentAction.setTicketDataId(dbTicketData.getId());
            approveCommentAction.setTicketFlowNodeDataId(newNode.getId());
            approveCommentAction.setActionValue("{}");
            approveCommentAction.setCreateBy(dealUserStr);
            approveCommentAction.setUpdateBy(dealUserStr);
            approveCommentAction.setCreateTime(now);
            approveCommentAction.setUpdateTime(now);

            approvePassAction.setActionName("通过");
            approvePassAction.setActionType(ActionTypeEnum.APPROVE_PASS);
            actionDataList.add(approvePassAction);

            approveRejectAction.setActionName("拒绝");
            approveRejectAction.setActionType(ActionTypeEnum.APPROVE_REJECT);
            actionDataList.add(approveRejectAction);

            approveCommentAction.setActionName("评论");
            approveCommentAction.setActionType(ActionTypeEnum.COMMENT);
            actionDataList.add(approveCommentAction);
        }

        //2.更新数据
        TicketFlowNodeDataDto finalDbCurrentNodeData = dbCurrentNodeData;
        transactionTemplate.executeWithoutResult((transactionStatus) -> {
            boolean isUpdate = false;
            isUpdate = ticketDataService.lambdaUpdate()
                    .eq(TicketData::getId, dbTicketData.getId())
                    .eq(TicketData::getUpdateTime, dbTicketData.getUpdateTime())
                    .isNull(TicketData::getDeleteTime)
                    .set(TicketData::getCurrentNodeId, newNode.getId())
                    .set(TicketData::getCurrentNodeName, newNode.getNodeName())
                    .set(TicketData::getCurrentDealUsers, AccountInfo.ToAccountInfoListStr(newNode.getDealUserList()))
                    .set(TicketData::getUpdateTime, now)
                    .set(TicketData::getUpdateBy, dealUserStr)
                    .update();
            if (!isUpdate) {
                throw new RuntimeException("工单审批持久化数据失败-更新工单信息失败");
            }
            isUpdate = flowNodeDataService.lambdaUpdate()
                    .eq(TicketFlowNodeData::getId, finalDbCurrentNodeData.getId())
                    .isNull(TicketFlowNodeData::getDeleteTime)
                    .set(TicketFlowNodeData::getPreNodeId, newNode.getId())
                    .set(TicketFlowNodeData::getNodeStatus, NodeStatusEnum.APPROVE_INIT)
                    .set(TicketFlowNodeData::getUpdateTime, now)
                    .set(TicketFlowNodeData::getUpdateBy, dealUserStr)
                    .update();
            if (!isUpdate) {
                throw new RuntimeException("工单审批持久化数据失败-更新当前节点信息失败");
            }
            Date date = new Date();
            TicketFlowNodeData addNodeData = new TicketFlowNodeData();
            addNodeData.setId(newNode.getId());
            addNodeData.setNodeName(newNode.getNodeName());
            addNodeData.setPreNodeId(newNode.getPreNodeId());
            addNodeData.setTemplateId("-1");
            addNodeData.setTicketDataId(newNode.getTicketDataId());
            addNodeData.setTicketFlowDataId(newNode.getTicketFlowDataId());
            addNodeData.setAuditedMethod(newNode.getAuditedMethod());
            addNodeData.setAuditedType(newNode.getAuditedType());
            addNodeData.setNodeOrder(newNode.getNodeOrder());
            addNodeData.setCreateBy(dealUserStr);
            addNodeData.setUpdateBy(dealUserStr);
            addNodeData.setNodeStatus(NodeStatusEnum.APPROVING);
            addNodeData.setCreateTime(now);
            addNodeData.setUpdateTime(now);
            isUpdate = flowNodeDataService.save(addNodeData);
            if (!isUpdate) {
                throw new RuntimeException("工单审批持久化数据失败-加签时，添加节点信息失败");
            }
            List<TicketFlowNodeExecutorDataDto> excutorList = newNode.getExcutorList();
            if (CollectionUtils.isNotEmpty(excutorList)) {
                List<TicketFlowNodeExecutorData> addList = new ArrayList<>();
                for (TicketFlowNodeExecutorDataDto executorDataDto : excutorList) {
                    TicketFlowNodeExecutorData addInfo = new TicketFlowNodeExecutorData();
                    addInfo.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_EXECUTOR_DATA));
                    addInfo.setTicketFlowNodeDataId(addNodeData.getId());
                    addInfo.setTemplateId(executorDataDto.getTemplateId());
                    addInfo.setTicketDataId(executorDataDto.getTicketDataId());
                    addInfo.setExecutorType(executorDataDto.getExecutorType());
                    addInfo.setExecutorValue(executorDataDto.getExecutorValue());
                    addInfo.setExecutorList(executorDataDto.getExecutorList());
                    addInfo.setExecutorDoneList(executorDataDto.getExecutorDoneList());
                    addInfo.setCreateBy(dealUserStr);
                    addInfo.setUpdateBy(dealUserStr);
                    addInfo.setCreateTime(now);
                    addInfo.setUpdateTime(now);
                    addList.add(addInfo);
                }
                isUpdate = executorDataService.saveBatch(addList);
                if (!isUpdate) {
                    throw new RuntimeException("工单审批持久化数据失败-加签时：添加执行组信息失败");
                }
            }

            if (CollectionUtils.isNotEmpty(actionDataList)) {
                isUpdate = ticketFlowNodeActionDataService.saveBatch(actionDataList);
                if (!isUpdate) {
                    throw new RuntimeException("工单审批持久化数据失败-加签时：添加执行动作信息失败");
                }
            }

            TicketFlowNodeApproveDetail approveDetail = new TicketFlowNodeApproveDetail();
            approveDetail.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_APPROVE_DETAIL));
            approveDetail.setTicketDataId(dbTicketData.getId());
            approveDetail.setTicketFlowNodeDataId(newNode.getId());
            approveDetail.setDealUserId(accountInfo.getAccountId());
            approveDetail.setDealUserType(accountInfo.getAccountType());
            approveDetail.setDealUserName(accountInfo.getAccountName());
            approveDetail.setDealTypeDescription(StringUtils.isNotEmpty(approveDto.getDealDescription()) ? approveDto.getDealDescription() : "加签");
            approveDetail.setDealOpinion(approveDto.getDealOpinion());
            approveDetail.setCreateBy(dealUserStr);
            approveDetail.setUpdateBy(dealUserStr);
            approveDetail.setDealType(ApproveDealTypeEnum.SEND);
            approveDetail.setCreateTime(now);
            approveDetail.setUpdateTime(now);
            isUpdate = ticketFlowNodeApproveDetailService.save(approveDetail);
            if (!isUpdate) {
                throw new RuntimeException("工单审批持久化数据失败-插入审批明细信息失败");
            }
        });

        //通过DB重新整合工单数据，为后续动作做准备
        Response<TicketDataDto> newTicketDataDtoResponse = ticketDataService.selectFullTicketDataById(new ReqParam(dbTicketData.getId()));
        if (!newTicketDataDtoResponse.isSuccess()) {
            return Response.error(BizResponseEnums.QUERY_ERROR, String.format("工单数据重新整合失败，ID:s%", dbTicketData.getId()));
        }
        var newTicketDataDto = newTicketDataDtoResponse.getData();

        //3.发送通知
        if (newTicketDataDto.getTicketMsgArriveType() == TicketMsgArriveTypeEnum.WECOM) {
            //通知下一个节点审批人
            String title = "【加签】{{apply_user}}提交的{{ticket_name}}，待你处理"
                    .replace("{{apply_user}}", newTicketDataDto.getApplyUserName())
                    .replace("{{ticket_name}}", newTicketDataDto.getTicketName());
            var urgeRes = notificationBizService.SendDealCard(
                    title,
                    newTicketDataDto,
                    accountInfo,
                    ApproveDealTypeEnum.SEND,
                    newNode.getDealUserList(),
                    false,
                    null
            );
            if (urgeRes.getEnum() != BizResponseEnums.SUCCESS) {
                log.error("加签发送企微卡片失败，错误信息：{}", urgeRes.getMsg());
            }
        }
        return new Response<>(null, BizResponseEnums.SUCCESS, "加签成功");
    }

    /**
     * @param addNode     新增节点
     * @param approveNode 审批节点
     * @return
     */
    private TicketFlowNodeDataDto genNode(AddTicketFlowNewNodeDto addNode, String addType, TicketFlowNodeDataDto approveNode) {
        Date now = new Date();
        TicketFlowNodeDataDto ticketFlowNodeData = new TicketFlowNodeDataDto();
        String addTicketFlowNodeDataId = SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_DATA);
        ticketFlowNodeData.setId(addTicketFlowNodeDataId);
        if (StringUtils.isNotBlank(addNode.getNodeName())) {
            ticketFlowNodeData.setNodeName(addNode.getNodeName());
        } else {
            ticketFlowNodeData.setNodeName("加签节点");
        }
        ticketFlowNodeData.setTemplateId("-1");//动态数据 templateId为-1
        ticketFlowNodeData.setTicketDataId(approveNode.getTicketDataId());
        ticketFlowNodeData.setTicketFlowDataId(approveNode.getTicketFlowDataId());
        ticketFlowNodeData.setAuditedMethod(addNode.getAuditedMethod());
        ticketFlowNodeData.setAuditedType(AuditedType.BY_USER);
        ticketFlowNodeData.setNodeStatus(NodeStatusEnum.APPROVE_INIT);
        ticketFlowNodeData.setNodeOrder(approveNode.getNodeOrder());
        if ("BEFORE".equals(addType)) {
            ticketFlowNodeData.setPreNodeId(approveNode.getPreNodeId());
        } else {
            ticketFlowNodeData.setPreNodeId(approveNode.getId());//动态数据
            List<TicketFlowEventData> ticketFlowEventDataList = flowEventDataService.lambdaQuery()
                    .eq(TicketFlowEventData::getTicketDataId, approveNode.getTicketDataId())
                    .eq(TicketFlowEventData::getTicketFlowNodeDataId, approveNode.getId())
                    .eq(TicketFlowEventData::getExecuteStep, ExecuteStepEnum.DONE_ADD_NODE)
                    .list();
            //复制并保存当前节点的执行事件
            if (CollectionUtils.isNotEmpty(ticketFlowEventDataList)) {
                ticketFlowEventDataList.forEach(it -> {
                    it.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_EVENT_DATA));
                    it.setTicketFlowNodeDataId(addTicketFlowNodeDataId);
                    it.setEventStatus(EventStatusEnum.INIT);
                    it.setExecuteTime(null);
                    it.setCreateTime(now);
                    it.setUpdateTime(now);
                    //把重试次数去掉，再放入新对象字段
                    if (StringUtils.isNotEmpty(it.getPushConfig()) && it.getPushConfig().contains("attempt")) {
                        String pushConfigStr = it.getPushConfig();
                        JSONObject pushConfigJson = JSONObject.parseObject(pushConfigStr);
                        pushConfigJson.remove("attempt");
                        it.setPushConfig(JSONObject.toJSONString(pushConfigJson));
                    }
                });
                try {
                    if (!flowEventDataService.saveBatch(ticketFlowEventDataList)) {
                        log.error("加签后，更新原节点的回调失败");
                    }
                } catch (Exception e) {
                    log.error("加签后，复制原节点的回调异常：{}", e);
                    throw e;
                }
            }
            List<TicketFlowNodeActionData> ticketFlowNodeActionDataList = ticketFlowNodeActionDataService.lambdaQuery()
                    .eq(TicketFlowNodeActionData::getTicketDataId, approveNode.getTicketDataId())
                    .eq(TicketFlowNodeActionData::getTicketFlowNodeDataId, approveNode.getId())
                    .isNull(TicketFlowNodeActionData::getDeleteTime)
                    .list();
            if (CollectionUtils.isNotEmpty(ticketFlowNodeActionDataList)) {
                ticketFlowNodeActionDataList.forEach(item -> {
                    item.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_ACTION_DATA));
                    item.setTicketFlowNodeDataId(addTicketFlowNodeDataId);
                    item.setCreateTime(now);
                    item.setUpdateTime(now);
                });
                try {
                    if (!ticketFlowNodeActionDataService.saveBatch(ticketFlowNodeActionDataList)) {
                        log.error("加签后，更新原节点的动作失败");
                    }
                } catch (Exception e) {
                    log.error("加签后，复制原节点的动作异常：{}", e);
                    throw e;
                }
            }
        }
        //填充TicketFlowNodeExecutorData
        List<TicketFlowNodeExecutorDataDto> excutorList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(addNode.getExcutorList())) {
            //处理人
            List<AccountInfo> addExcutorList = addNode.getExcutorList();
            for (AccountInfo addEx : addExcutorList) {
                List<AccountInfo> thisExcutorList = new ArrayList<>();
                thisExcutorList.add(addEx);
                String accountListStr = AccountInfo.ToAccountInfoListStr(thisExcutorList);
                TicketFlowNodeExecutorDataDto ticketFlowNodeExecutorData = new TicketFlowNodeExecutorDataDto();
                ticketFlowNodeExecutorData.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_EXECUTOR_DATA));
                ticketFlowNodeExecutorData.setTemplateId("-1");
                ticketFlowNodeExecutorData.setTicketDataId(ticketFlowNodeData.getTicketDataId());
                ticketFlowNodeExecutorData.setTicketFlowNodeDataId(ticketFlowNodeData.getId());
                ticketFlowNodeExecutorData.setExecutorType(APPLY_MEMBER_LIST);
                ticketFlowNodeExecutorData.setExecutorValue(accountListStr);
                ticketFlowNodeExecutorData.setExecutorList(accountListStr);
                ticketFlowNodeExecutorData.setExecutorDoneList("");
                excutorList.add(ticketFlowNodeExecutorData);
            }
        }
        if (CollectionUtils.isNotEmpty(addNode.getCcList())) {
            //审批通过抄送
            TicketFlowNodeExecutorDataDto caTicketFlowNodeExecutorData = new TicketFlowNodeExecutorDataDto();
            caTicketFlowNodeExecutorData.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_EXECUTOR_DATA));
            caTicketFlowNodeExecutorData.setTemplateId("-1");
            caTicketFlowNodeExecutorData.setTicketDataId(ticketFlowNodeData.getTicketDataId());
            caTicketFlowNodeExecutorData.setTicketFlowNodeDataId(ticketFlowNodeData.getId());
            caTicketFlowNodeExecutorData.setExecutorType(CE_MEMBER_LIST);
            String accountListStr = AccountInfo.ToAccountInfoListStr(addNode.getCcList());
            caTicketFlowNodeExecutorData.setExecutorValue(accountListStr);
            caTicketFlowNodeExecutorData.setExecutorList(accountListStr);
            caTicketFlowNodeExecutorData.setExecutorDoneList("");
            excutorList.add(caTicketFlowNodeExecutorData);
        }
        ticketFlowNodeData.setExcutorList(excutorList);
        return ticketFlowNodeData;
    }

    public static String cutStr(String input, int lenght) {
        if (input == null) {
            return "";
        }
        if (input.length() > lenght) {
            return input.substring(0, lenght) + "...";
        }
        return input;
    }
}
