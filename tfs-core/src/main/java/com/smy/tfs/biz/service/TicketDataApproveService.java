package com.smy.tfs.biz.service;

import com.smy.tfs.api.dbo.TicketFlowEventData;
import com.smy.tfs.api.dto.*;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.dynamic.BatchDto;
import com.smy.tfs.api.dto.dynamic.TicketBatchDto;
import com.smy.tfs.api.dto.out.TicketActionDto;
import com.smy.tfs.api.enums.ApproveDealTypeEnum;
import com.smy.tfs.api.enums.AuditedType;

import java.util.HashMap;
import java.util.List;

/**
 * @author z01140
 * @Package: com.smy.tfs.biz.service
 * @Description:
 * @CreateDate 2024/4/25 12:21
 * @UpdateDate 2024/4/25 12:21
 */
public interface TicketDataApproveService {

    Response<String> act(TicketActionDto actionDto, String userType, String userId, String userName);

    /**
     * 工单审批
     *
     * @param ticketID
     * @param dealType
     * @param dealOpinion
     * @param dealUserType
     * @param dealUserId
     * @param dealUserName
     * @param dealNodeId
     * @return
     */
    Response approve(String ticketID, String dealType, String dealOpinion,
                     String dealUserType, String dealUserId, String dealUserName, String dealNodeId);

    /**
     * 工单审批
     *
     * @param approveDto
     * @return
     */
    Response approve(TicketFlowNodeApproveDto approveDto, String userType, String userId, String userName);

    /**
     * 工单批量审批
     * @param batchTicketFlowNodeApproveDto
     * @param userType
     * @param userId
     * @param userName
     * @return
     */
    Response<TicketBatchDto> batchApprove(BatchTicketFlowNodeApproveDto batchTicketFlowNodeApproveDto, String userType, String userId, String userName);

    Response<String> doApprovePass(TicketDataDto ticketDataDto, TicketFlowNodeDataDto currentFlowNodeData,
                                   ApproveDealTypeEnum dealType, String dealMode, String dealDescription, String dealUserType,
                                   String dealUserId, String dealUserName, String dealOpinion,
                                   TicketFlowNodeDataDto addNodeInfo);

    Response<String> doApproveReject(TicketDataDto ticketDataDto, TicketFlowNodeDataDto currentFlowNodeData,
                                     ApproveDealTypeEnum approveDealTypeEnum, String dealDescription, String dealUserType,
                                     String dealUserId, String dealUserName, String dealOpinion);

    Response<String> doApproveFinish(TicketDataDto ticketDataDto, TicketFlowNodeDataDto currentFlowNodeData,
                                     ApproveDealTypeEnum dealType, String dealDescription, String dealUserType,
                                     String dealUserId, String dealUserName, String dealOpinion);

    Response<String> doApproveNotify(List<AccountInfo> notifyUsers, TicketDataDto ticketDataDto, TicketFlowNodeDataDto flowNodeDataDto);


    /**
     * 加签 = 审批当前节点 + 增加下一个节点
     *
     * @param addTicketFlowNodeDto
     * @return
     */
    Response addTicketFlowNodeData(AddTicketFlowNodeDto addTicketFlowNodeDto, AccountInfo accountInfo);


    /**
     * 加上签
     *
     * @param addNodeDto
     * @return
     */
    Response<String> addPreNode(AddTicketFlowNodeDto addNodeDto, AccountInfo accountInfo);


    Response<String> executeEvent(String ticketDataId, String flowNodeDataId, String executeStep, HashMap<String, String> params, AccountInfo accountInfo, ApproveDealTypeEnum approveDealTypeEnum);

    /**
     * 执行事件
     *
     * @param ticketDataId
     * @param flowEventDataList
     * @return
     */
    Response<String> executeEventList(String ticketDataId, String ticketInterfaceKey, List<TicketFlowEventData> flowEventDataList, HashMap<String, String> params, AccountInfo accountInfo, ApproveDealTypeEnum approveDealTypeEnum);

    boolean inList(AccountInfo accountInfo, List<AccountInfo> accountInfoList);

    void disable(TicketDataDto ticketDataDto, TicketFlowNodeDataDto currentFlowNodeData, AccountInfo dealUser, String buttonText);

    /**
     * 如果节点是自动审批节点，则自动触发审批流程
     *
     * @param ticketDataId  工单号
     * @param auditedType   审批类型
     * @param currentNodeId 当前节点ID
     */
    void autoApprove(String ticketDataId, AuditedType auditedType, String currentNodeId);

    /**
     * 执行事件
     *
     * @param ticketDataId
     * @param interfaceKey
     * @param flowEventData
     * @return
     */
    Response<TicketFlowEventData> executeEventCore(String ticketDataId, String interfaceKey, TicketFlowEventData flowEventData, HashMap<String, String> params, AccountInfo accountInfo, ApproveDealTypeEnum approveDealTypeEnum);

    Response updateTicket(TicketFormUpdateDto ticketFormDataDto, String userType, String userId, String userName);

    Response<TicketDataDto> selectTicketData(ReqParam reqParam);
}
