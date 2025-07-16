package com.smy.tfs.biz.service.wrapper;

import com.alibaba.fastjson2.JSONObject;
import com.smy.tfs.api.constants.TfsBaseConstant;
import com.smy.tfs.api.dbo.TicketFormItemValues;
import com.smy.tfs.api.dto.*;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.dynamic.BatchDto;
import com.smy.tfs.api.dto.dynamic.TicketBatchDto;
import com.smy.tfs.api.dto.dynamic.TicketDataDynamicDto;
import com.smy.tfs.api.dto.dynamic.TicketDataStdDto;
import com.smy.tfs.api.dto.out.TicketActionDto;
import com.smy.tfs.api.dto.query.*;
import com.smy.tfs.api.enums.ApproveDealTypeEnum;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.service.*;
import com.smy.tfs.biz.service.TicketDataApproveService;
import com.smy.tfs.common.utils.SecurityUtils;
import com.smy.tfs.openapi.service.ITicketDataServiceWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.apidocs.annotations.ApiDoc;
import org.apache.dubbo.apidocs.annotations.ApiModule;
import org.apache.dubbo.apidocs.annotations.RequestParam;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 工单数据表 服务实现类
 * </p>
 *
 * @author yss
 * @since 2024-05-29
 */
@Slf4j
@Component("ticketDataServiceWrapper")
@org.apache.dubbo.config.annotation.Service
@ApiModule(value = "工单对外数据服务", apiInterface = ITicketDataServiceWrapper.class)
public class TicketDataServiceWrapper implements ITicketDataServiceWrapper {

    @Resource
    private ITicketDataService ticketDataService;

    @Resource
    private TicketDataApproveService ticketDataApproveService;

    @Resource
    private ITicketAccountService ticketAccountService;

    @Resource
    private ITicketDataOpenService ticketDataOpenService;

    @Resource
    private ITicketDataQueryService ticketDataQueryService;

    @Resource
    private ITicketDataActService ticketDataActService;

    @Override
    @ApiDoc(value = "获取工单申请编号", description = "获取工单申请编号")
    public Response<String> getTicketApplyId(
            @RequestParam(value = "业务标识", example = " ", description = "业务标识")
            String appid
    )
    {

        return ticketDataService.getTicketApplyId(appid);
    }

    @Override
    @ApiDoc(value = "创建工单", description = "创建工单")
    public Response<String> createTicket(
            @RequestParam(value = "工单内容", example = " ", description = "工单内容")
            TicketDataStdDto ticketDataStdDto,
            @RequestParam(value = "用户类型", example = " ", description = "用户类型")
            String userType,
            @RequestParam(value = "用户ID", example = " ", description = "用户ID")
            String userId,
            @RequestParam(value = "用户名称", example = " ", description = "用户名称")
            String userName
    )
    {

        try {
            return ticketDataService.createTicket(ticketDataStdDto, userType, userId, userName);
        } catch (Exception e) {
            log.error("创建工单失败,方法入参 ticketDataStdDto:{}, userType:{},  userId:{}, userName:{} 原因：{}", JSONObject.toJSONString(ticketDataStdDto), userType, userId, userName, e);
            return Response.error(BizResponseEnums.SYSTEM_ERROR, String.format("创建工单异常，原因：%s,请联系统一工单管理员", e.getMessage()));
        }
    }

    @Override
    @ApiDoc(value = "修改工单", description = "修改工单")
    public Response<String> updateTicket(TicketFormUpdateDto updateDto, String userType, String userId, String userName){

        return ticketDataService.updateTicketFormData(updateDto, userType, userId, userName);
    }

    @Override
    @ApiDoc(value = "创建动态工单", description = "创建动态工单")
    public Response<String> createTicketDynamic(TicketDataDynamicDto dynamicDto, String userType, String userId, String userName){

        return ticketDataService.createTicketDynamic(dynamicDto, userType, userId, userName);
    }

    /**
     * @param ticketID    工单ID
     * @param dealType    审批类型
     * @param dealOpinion 审批意见
     * @param dealNodeId  审批节点ID
     * @param userType    审批人类型
     * @param userId      审批人ID
     * @param userName    审批人名称
     * @return
     */
    @ApiDoc(value = "审批（通过/驳回）", description = "审批（通过/驳回）")
    @Override
    public Response<String> approve(
            @RequestParam(value = "工单ID", example = " ", description = "工单ID")
            String ticketID,
            @RequestParam(value = "处理类型", example = " ", description = "处理类型")
            String dealType,
            @RequestParam(value = "处理意见", example = " ", description = "处理意见")
            String dealOpinion,
            @RequestParam(value = "处理节点ID", example = " ", description = "处理节点ID")
            String dealNodeId,
            @RequestParam(value = "用户类型", example = " ", description = "用户类型")
            String userType,
            @RequestParam(value = "用户ID", example = " ", description = "用户ID")
            String userId,
            @RequestParam(value = "用户名称", example = " ", description = "用户名称")
            String userName
    )
    {

        return ticketDataApproveService.approve(ticketID, dealType, dealOpinion, userType, userId, userName, dealNodeId);
    }


    public Response<String> approve(TicketFlowNodeApproveDto approveDto, String userType, String userId, String userName){

        return ticketDataApproveService.approve(approveDto, userType, userId, userName);
    }

    /**
     * @param actionDto
     * @param userType
     * @param userId
     * @param userName
     * @return
     */
    @Override
    public Response<String> act(TicketActionDto actionDto, String userType, String userId, String userName){

        return ticketDataApproveService.act(actionDto, userType, userId, userName);
    }

    @Override
    public Response<TicketBatchDto> batchApprove(BatchTicketFlowNodeApproveDto batchTicketFlowNodeApproveDto, String userType, String userId, String userName){

        return ticketDataApproveService.batchApprove(batchTicketFlowNodeApproveDto, userType, userId, userName);
    }

    //评论
    public Response<String> comment(
            @RequestParam(value = "评论内容", example = " ", description = "评论内容")
            AddTicketFlowNodeCommentDto commentDto,
            @RequestParam(value = "用户类型", example = " ", description = "用户类型")
            String userType,
            @RequestParam(value = "用户ID", example = " ", description = "用户ID")
            String userId,
            @RequestParam(value = "用户名称", example = " ", description = "用户名称")
            String userName
    )
    {

        return ticketDataService.comment(commentDto, new AccountInfo("", userType, userId, userName));
    }

    public Response<String> addNode(AddTicketFlowNodeDto addNodeDto, String userType, String userId, String userName){

        return ticketDataService.addNodeData(addNodeDto, new AccountInfo("", userType, userId, userName));
    }

    //退回
    public Response<String> gotoNode(String ticketDataId, String currentNodeId, String gotoNodeId, String gotoNodeReason, String userType, String userId, String userName){

        return ticketDataService.gotoFlowNode(ticketDataId, currentNodeId, gotoNodeId, gotoNodeReason, new AccountInfo("", userType, userId, userName));
    }

    public Response<List<TicketFormItemValues>> advancedQuery(AdvancedQueryDto advancedQueryDto, String userType, String userId, String userName){

        return ticketDataService.advancedSelectTicketDataList(advancedQueryDto);
    }


    //通过工单号查询工单所有数据
    @Override
    @ApiDoc(value = "通过工单号查询工单所有数据", description = "通过工单号查询工单所有数据")
    public Response<TicketDataDto> selectFullTicketDataById(
            @RequestParam(value = "工单号", example = " ", description = "工单号")
            String ticketId
    )
    {

        return ticketDataService.selectFullTicketDataById(new ReqParam(ticketId));
    }

    public Response<TicketDataDto> selectTicket(ReqParam reqParam){

        return ticketDataService.selectFullTicketDataById(reqParam);
    }

    //通过业务号，获取审批中的工单
    public Response<TicketDataDto> selectTicketByBusiKey(String busiKey){

        return ticketDataService.selectFullTicketDataById(new ReqParam(busiKey, "BusiKey"));
    }

    @Override
    @ApiDoc(value = "通过工单号列表撤回工单", description = "通过工单号列表撤回工单")
    public Response<String> withdrawTicketByIdList(
            @RequestParam(value = "工单Id列表", example = " ", description = "工单Id列表")
            List<String> idList,
            @RequestParam(value = "用户类型", example = " ", description = "用户类型")
            String userType,
            @RequestParam(value = "用户ID", example = " ", description = "用户ID")
            String userId,
            @RequestParam(value = "用户名称", example = " ", description = "用户名称")
            String userName
    )
    {

        return ticketDataService.withdrawTicketByIdList(idList, userType, userId, userName);
    }

    /**
     * @param accountType
     * @return
     */
    @Override
    public Response<String> syncAccountConfig(String accountType){

        SecurityUtils.wrapContext(TfsBaseConstant.defaultOriginId, TfsBaseConstant.defaultUserId, TfsBaseConstant.defaultUserName,
                TfsBaseConstant.defaultUserType, TfsBaseConstant.defaultAppId, () -> {
                    ticketAccountService.doSyncTicketAccountConfig(accountType);
                });
        return Response.success();
    }

    @Override
    public Response<String> syncTicketAccountGroup(String appID, String groupName, String groupDesc, List<String> accountIdList){

        return ticketAccountService.syncTicketAccountGroup(appID, groupName, groupDesc, accountIdList);
    }

    @Override
    public Response<String> urgeTicketByIdList(ApproveDealTypeEnum dealTypeEnum, List<String> ticketIdList, String userType, String userId, String userName, String dealOpinion){

        return ticketDataService.urgeTicketByIdList(dealTypeEnum, ticketIdList, userType, userId, userName, dealOpinion);
    }

    @Override
    public Response<RemoteTableDataInfo> pageQueryTicketList(PageQueryTicketDataReqDto pageQueryTicketDto, String userType, String userId, String userName){

        return ticketDataOpenService.pageQueryTicketList(pageQueryTicketDto, userType, userId, userName);
    }

    @Override
    public Response<String> dispatchTicket(TicketDispatchDto ticketDispatchDto, String userType, String userId, String userName){

        return ticketDataService.dispatchTicket(ticketDispatchDto, userType, userId, userName);
    }

    @Override
    public Response<TicketBatchDto> batchDispatchTicket(BatchTicketDispatchDto batchTicketDispatchDto, String userType, String userId, String userName){

        return ticketDataService.batchDispatchTicket(batchTicketDispatchDto, userType, userId, userName);
    }

    @Override
    public Response<TicketFlowNodeApproveDetailDto> getTicketFlowNodeApproveDetailDto(TicketFlowNodeApproveDetailDto ticketFlowNodeApproveDetailDto, String userType, String userId, String userName){

        return ticketDataOpenService.getTicketFlowNodeApproveDetailDto(ticketFlowNodeApproveDetailDto, userType, userId, userName);
    }

    @Override
    public Response<BusiTicketDataResponseDto> queryBusiTicketDataList(BusiTicketDataRequestDto busiTicketDataRequestDto){

        return ticketDataOpenService.queryBusiTicketDataList(busiTicketDataRequestDto);
    }

    @Override
    public Response<String> createQWGroupByIdList(List<String> ticketIdList, List<AccountInfo> accountInfoList){

        return ticketDataService.createQWGroupByIdList(ticketIdList, accountInfoList);
    }

    @Override
    public RemoteTableDataInfo<BusiQueryRspDto> busiQuery(BusiQueryReqDto busiQueryReqDto, String userType, String userId, String userName){

        return ticketDataQueryService.busiQuery(busiQueryReqDto, userType, userId, userName);
    }

    @Override
    public Response<ProcessStageCountRespDto> processStageQueryCount(ProcessStageCountReqDto processStageCountReqDto, String userType, String userId, String userName){

        return ticketDataQueryService.processStageQueryCount(processStageCountReqDto, userType, userId, userName);
    }

    @Override
    public Response<SubmitStageCountRespDto> submitStageQueryCount(SubmitStageCountReqDto submitStageCountReqDto, String userType, String userId, String userName){

        return ticketDataQueryService.submitStageQueryCount(submitStageCountReqDto, userType, userId, userName);
    }

    @Override
    public Response<TopRankingRespDto> getTopRanking(TopRankingReqDto topRankingReqDto, String userType, String userId, String userName){

        return ticketDataQueryService.getTopRanking(topRankingReqDto, userType, userId, userName);
    }

    @Override
    public Response<TicketDispatchCountRspDto> getTicketDispatchCountList(TicketDispatchCountReqDto ticketDispatchCountReqDto, String userType, String userId, String userName){

        return ticketDataQueryService.getTicketDispatchCountList(ticketDispatchCountReqDto, userType, userId, userName);
    }

    @Override
    public Response<Long> busiQueryCount(BusiQueryReqDto busiQueryReqDto, String userType, String userId, String userName){

        return ticketDataQueryService.busiQueryCount(busiQueryReqDto, userType, userId, userName);
    }

    @Override
    public Response<List<TicketDataStatusDto>> selectTicketStatusByBusinessKey(String businessKey, String ticketStatus, Integer limit){

        return ticketDataService.selectTicketStatusByBusinessKey(businessKey, ticketStatus, limit);
    }

    @Override
    public Response<List<TicketDataDto>> selectTicketByBusinessKey(String businessKey, String templateIdOrCode, Integer limit){

        return ticketDataService.selectTicketByBusinessKey(businessKey, templateIdOrCode, limit);
    }

    @Override
    public RemoteTableDataInfo<BusiCommonESQueryRspDto> busiCommonESQuery(BusiCommonESQueryReqDto busiCommonESQueryReqDto) {
        return ticketDataQueryService.busiCommonESQuery(busiCommonESQueryReqDto);
    }

    @Override
    public Response<List<BatchDto>> batchCreateTicket(List<TicketDataStdDto> ticketDataStdDtoList, String userType, String userId, String userName){

        return ticketDataActService.batchCreateTicket(ticketDataStdDtoList, userType, userId, userName);
    }

    @Override
    public Response<List<String>> getTicketApplyIdList(String appid, Long n){

        return ticketDataActService.getTicketApplyIdList(appid, n);
    }

}