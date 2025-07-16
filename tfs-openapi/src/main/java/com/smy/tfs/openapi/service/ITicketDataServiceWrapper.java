package com.smy.tfs.openapi.service;

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

import java.util.List;


/**
 * <p>
 * 工单数据表 服务类
 * </p>
 *
 * @author yss
 * @since 2024-05-28
 */
public interface ITicketDataServiceWrapper {

    public Response<String> getTicketApplyId(String appid);

    //创建动态工单
    public Response<String> createTicketDynamic(TicketDataDynamicDto dynamicDto, String userType, String userId, String userName);

    //创建工单
    public Response<String> createTicket(TicketDataStdDto ticketDataStdDto, String userType, String userId, String userName);

    //修改工单
    public Response<String> updateTicket(TicketFormUpdateDto updateDto, String userType, String userId, String userName);

    //审批（通过/驳回）
    public Response<String> approve(String ticketID, String dealType, String dealOpinion, String dealNodeId, String userType, String userId, String userName);

    //审批（通过/驳回）
    public Response<String> approve(TicketFlowNodeApproveDto approveDto, String userType, String userId, String userName);

    //执行动作
    public Response<String> act(TicketActionDto actionDto, String userType, String userId, String userName);

    //批量审批（通过/驳回）
    public Response<TicketBatchDto> batchApprove(BatchTicketFlowNodeApproveDto batchTicketFlowNodeApproveDto, String userType, String userId, String userName);

    //评论
    public Response<String> comment(AddTicketFlowNodeCommentDto commentDto, String userType, String userId, String userName);

    //加签
    public Response<String> addNode(AddTicketFlowNodeDto addNodeDto, String userType, String userId, String userName);

    //回退
    public Response<String> gotoNode(String ticketDataId, String currentNodeId, String gotoNodeId, String gotoNodeReason, String userType, String userId, String userName);

    //高级查询
    Response<List<TicketFormItemValues>> advancedQuery(AdvancedQueryDto advancedQueryDto, String userType, String userId, String userName);

    //查询工单
    public Response<TicketDataDto> selectFullTicketDataById(String ticketId);

    //查询工单
    public Response<TicketDataDto> selectTicket(ReqParam reqParam);

    //通过业务号，获取审批中的工单
    public Response<TicketDataDto> selectTicketByBusiKey(String busiKey);

    //撤回
    public Response<String> withdrawTicketByIdList(List<String> idList, String userType, String userId, String userName);

    //同步账户体系
    public Response<String> syncAccountConfig(String accountType);

    //同步用户组信息
    public Response<String> syncTicketAccountGroup(String appID, String groupName, String groupDesc, List<String> accountIdList);

    //催办
    public Response<String> urgeTicketByIdList(ApproveDealTypeEnum dealTypeEnum, List<String> ticketIdList, String userType, String userId, String userName, String dealOpinion);

    //分页查询工单列表（附带表单信息）
    public Response<RemoteTableDataInfo> pageQueryTicketList(PageQueryTicketDataReqDto pageQueryTicketDto, String userType, String userId, String userName);

    //派单
    Response<String> dispatchTicket(TicketDispatchDto ticketDispatchDto, String userType, String userId, String userName);

    //派单
    Response<TicketBatchDto> batchDispatchTicket(BatchTicketDispatchDto batchTicketDispatchDto, String userType, String userId, String userName);


    //获取审批详情
    Response<TicketFlowNodeApproveDetailDto> getTicketFlowNodeApproveDetailDto(TicketFlowNodeApproveDetailDto ticketFlowNodeApproveDetailDto, String userType, String userId, String userName);

    Response<BusiTicketDataResponseDto> queryBusiTicketDataList(BusiTicketDataRequestDto busiTicketDataRequestDto);

    //批量创建工单
    public Response<List<BatchDto>> batchCreateTicket(List<TicketDataStdDto> ticketDataStdDtoList, String userType, String userId, String userName);

    //批量申请工单id
    public Response<List<String>> getTicketApplyIdList(String appid, Long n);

    //企微建群
    Response<String> createQWGroupByIdList(List<String> ticketIdList, List<AccountInfo> accountInfoList);

    //业务查询（es）给方舟用
    RemoteTableDataInfo<BusiQueryRspDto> busiQuery(BusiQueryReqDto busiQueryReqDto, String userType, String userId, String userName);

    //查询工单处理阶段数据统计：包括 待处理、处理中、已完结
    Response<ProcessStageCountRespDto> processStageQueryCount(ProcessStageCountReqDto processStageCountReqDto, String userType, String userId, String userName);

    //工单提交阶段：包括 新建、退回
    Response<SubmitStageCountRespDto> submitStageQueryCount(SubmitStageCountReqDto submitStageCountReqDto, String userType, String userId, String userName);

    //获得工单排行榜 top 10
    Response<TopRankingRespDto> getTopRanking(TopRankingReqDto topRankingReqDto, String userType, String userId, String userName);

    //查询待分配的工单数量
    public Response<TicketDispatchCountRspDto> getTicketDispatchCountList(TicketDispatchCountReqDto ticketDispatchCountReqDto, String userType, String userId, String userName);

    //查询待处理的工单（es）
    Response<Long> busiQueryCount(BusiQueryReqDto busiQueryReqDto, String userType, String userId, String userName);

    //根据businessKey 查询 工单状态
    Response<List<TicketDataStatusDto>> selectTicketStatusByBusinessKey(String businessKey, String ticketStatus, Integer limit);

    //通过业务号和 templateCode 查询工单
    Response<List<TicketDataDto>> selectTicketByBusinessKey(String businessKey, String templateIdOrCode, Integer limit);

    //业务查询（es）
    RemoteTableDataInfo<BusiCommonESQueryRspDto> busiCommonESQuery(BusiCommonESQueryReqDto busiCommonESQueryReqDto);


}
