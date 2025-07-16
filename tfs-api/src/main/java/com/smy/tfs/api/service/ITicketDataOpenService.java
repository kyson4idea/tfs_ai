package com.smy.tfs.api.service;

import com.smy.tfs.api.dto.*;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.query.RemoteTableDataInfo;
import com.smy.tfs.common.core.page.TableDataInfo;


/**
 * <p>
 * 工单数据表 服务类
 * </p>
 *
 * @author yss
 * @since 2024-11-21
 */
public interface ITicketDataOpenService {
    //获取工单申请编号
    public Response<RemoteTableDataInfo> pageQueryTicketList(PageQueryTicketDataReqDto pageQueryTicketDataReqDto, String userType, String userId, String userName);

    public Response<TicketFlowNodeApproveDetailDto> getTicketFlowNodeApproveDetailDto(TicketFlowNodeApproveDetailDto ticketFlowNodeApproveDetailDto, String userType, String userId, String userName);

    //根据通用字段查询工单列表
    public Response<BusiTicketDataResponseDto> queryBusiTicketDataList(BusiTicketDataRequestDto commonFieldsRequestDto);

}
