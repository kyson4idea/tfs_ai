package com.smy.tfs.biz.service.impl;

import com.smy.tfs.SpringTestCase;
import org.junit.Test;

import javax.annotation.Resource;

public class TicketCreateAppServiceImplTest extends SpringTestCase {
    @Resource
    private TicketCreateAppServiceImpl ticketCreateAppService;

    @Test
    public void createTicketAppFromTicketFormTest() {
        String ticketDataId = "1002405150002840001";
        ticketCreateAppService.createTicketAppFromTicketForm("", "", ticketDataId);
    }

}