package com.smy.tfs.quartz.task;

import com.smy.tfs.api.service.ITicketDataService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 给ticket_data表的字段tags赋值
 */
@Component("ticketDataTagTask")
public class TicketDataTagTask {

    @Resource
    private ITicketDataService ticketDataService;

    public void ticketDataTagsSet(){
        //设置ticket_data表tags的值
        ticketDataService.ticketDataTagsSet();
    }


}
