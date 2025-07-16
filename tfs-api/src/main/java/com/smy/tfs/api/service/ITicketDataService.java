package com.smy.tfs.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smy.tfs.api.dbo.TicketData;
import com.smy.tfs.api.dbo.TicketFormItemValues;
import com.smy.tfs.api.dto.*;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.dynamic.ConvertDataParamDto;
import com.smy.tfs.api.dto.dynamic.TicketBatchDto;
import com.smy.tfs.api.dto.dynamic.TicketDataDynamicDto;
import com.smy.tfs.api.dto.dynamic.TicketDataStdDto;
import com.smy.tfs.api.enums.ApproveDealTypeEnum;

import java.util.HashMap;
import java.util.List;


/**
 * <p>
 * 工单数据表 服务类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
public interface ITicketDataService extends IService<TicketData> {

    //*****************工单 begin*****************//
    //获取工单申请编号
    public Response<String> getTicketApplyId(String appid);

    //获取转换数据
    public Response<HashMap<String, String>> getConvertData(ConvertDataParamDto convertDataParamDto, String userType, String userId, String userName);

    //创建工单数据
    public Response<String> createTicket(TicketDataStdDto ticketDataStdDto, String userType, String userId, String userName);

    //计算审批流程节点
    public Response<String> countFlowNode(TicketDataStdDto ticketDataStdDto);

    public Response<String> gotoFlowNode(String ticketDataId, String currentNodeId, String flowNodeId, String gotoNodeReason, AccountInfo accountInfo);

    //创建动态工单数据
    public Response<String> createTicketDynamic(TicketDataDynamicDto dynamicDto, String userType, String userId, String userName);

    //通过工单号查询工单所有数据
    public Response<TicketDataDto> selectFullTicketDataById(ReqParam reqParam);

    //通过银行卡查询工单状态
    Response<List<TicketDataStatusDto>> selectTicketStatusByBusinessKey(String businessKey, String ticketStatus, Integer limit);

    //通过业务号和 templateCode 查询工单
    Response<List<TicketDataDto>> selectTicketByBusinessKey(String businessKey, String templateIdOrCode, Integer limit);

    //通过工单号查询工单本身数据
    public TicketData selectTicketDataById(String ticketDataId);

    //通过工单号列表查询工单本身数据
    public List<TicketData> selectTicketDataById(List<String> ticketDataIdList);

    //分页查询工单数据
    public Page<TicketDataDto> selectTicketDataPage(TicketDataDto ticketDataDto, int pageIndex, int pageSize);

    //查询工单列表
    Response<List<TicketDataListResponseDto>> selectTicketDataList(TicketDataListRequestDto ticketDataListRequestDto);

    //查询工单列表
    Response<List<TicketDataListResponseDto>> selectTicketDataList(TicketDataListRequestDto ticketDataListRequestDto, Integer pageNum, Integer pageSize);

    //查询工单列表
    Response<List<TicketFormItemValues>> advancedSelectTicketDataList(AdvancedQueryDto advancedQueryDto);

    //根据条件查询工单总数
    Response<Integer> advancedSelectTicketDataCount(AdvancedQueryDto advancedQueryDto);

    //查询工单数量
    Integer selectTicketDataCount(TicketDataListRequestDto ticketDataListRequestDto);

    //审批（通过/驳回）
    public Response<TicketDataDto> dealTicketDataById(String ticketID, String dealType, String dealOpinion, String dealUserType, String dealUserId, String dealUserName, String dealNodeId);

    //评论
    public TicketDataDto argumentTicketDataById(TicketDataDto ticketDataDto, String dealUserType, String dealUserId, String argumentContent);

    //加签
    public Response addNodeData(AddTicketFlowNodeDto addTicketFlowNodeDto, AccountInfo accountInfo);

    //撤回
    public Response<String> withdrawTicketByIdList(List<String> idList, String userType, String userId, String userName);

    //催办
    public Response<String> urgeTicketByIdList(ApproveDealTypeEnum dealTypeEnum, List<String> ticketIdList, String userType, String userId, String userName, String dealOpinion);

    //企微建群
    public Response<String> createQWGroupByIdList(List<String> ticketIdList, List<AccountInfo> accountInfoList);

    public Response<String> joinQWGroup(String ticketId, String userType, String userId);

    public Response<String> comment(AddTicketFlowNodeCommentDto commentDto, AccountInfo accountInfo);

    //接企微群消息（入口）
    public TicketDataDto receiveQWGroupMsg(TicketDataDto ticketDataDto, String currentFlowNodeDataId, String dealUserType, String dealUserId, String msgContent);

    //*****************工单 end*****************//

    //更新表单数据
    public Response updateTicketFormData(TicketFormUpdateDto ticketFormDataDto, String userType, String userId, String userName);

    //检查当前操作人是否有权限
    boolean authCheck(String userId, String appId, String dealUsers);

    //工单派单
    Response<String> dispatchTicket(TicketDispatchDto ticketDispatchDto, String userType, String userId, String userName);

    //批量派单
    Response<TicketBatchDto> batchDispatchTicket(BatchTicketDispatchDto batchTicketDispatchDto, String userType, String userId, String userName);

    Response<List<TicketDataListResponseDto>> advancedQueryPostHandle(List<TicketFormItemValues> ticketFormItemValuesList);

    void ticketDataTagsSet();

}
