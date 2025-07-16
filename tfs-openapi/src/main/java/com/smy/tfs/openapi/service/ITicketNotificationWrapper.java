package com.smy.tfs.openapi.service;

import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.ticket_notify.NotifyMsgDto;

/**
 * <p>
 * 工单通知类
 * </p>
 *
 * @author yss
 * @since 2025-05-20
 */
public interface ITicketNotificationWrapper {

    public Response notifyMsg(NotifyMsgDto notifyMsgDto);

}
