package com.smy.tfs.openapi.service;

import com.smy.tfs.api.dto.TicketFormItemDataDto;
import com.smy.tfs.api.dto.base.Response;


/**
 * <p>
 * 工单数据表 服务类
 * </p>
 *
 * @author yss
 * @since 2024-05-28
 */
public interface ITicketFormItemServiceWrapper {


    //通过业务号，获取审批中的工单
    public Response<TicketFormItemDataDto> selectFormItemsByTicketId(String ticketId);




}
