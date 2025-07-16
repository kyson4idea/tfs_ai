package com.smy.tfs.biz.service.wrapper;

import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.ticket_notify.NotifyMsgDto;
import com.smy.tfs.biz.service.NotificationService;
import com.smy.tfs.openapi.service.ITicketDataServiceWrapper;
import com.smy.tfs.openapi.service.ITicketNotificationWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.apidocs.annotations.ApiDoc;
import org.apache.dubbo.apidocs.annotations.ApiModule;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * <p>
 * 工单通知类
 * </p>
 *
 * @author yss
 * @since 2025-05-20
 */
@Slf4j
@Component("ticketNotificationWrapper")
@org.apache.dubbo.config.annotation.Service
@ApiModule(value = "工单对外通知服务", apiInterface = ITicketNotificationWrapper.class)
public class TicketNotificationWrapper implements ITicketNotificationWrapper {

    @Resource
    private NotificationService notificationService;

    @Override
    @ApiDoc(value = "文本消息通知", description = "文本消息通知")
    public Response notifyMsg(NotifyMsgDto notifyMsgDto) {
        return notificationService.notifyMsg(notifyMsgDto);
    }
}
