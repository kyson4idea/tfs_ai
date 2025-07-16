package com.smy.tfs.biz.service.impl;

import com.smy.tfs.SpringTestCase;
import com.smy.tfs.api.dto.TicketAppDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.service.ITicketAppService;
import com.smy.tfs.biz.service.TicketApproveCallBackService;
import org.junit.Test;

import javax.annotation.Resource;

public class TicketAppServiceImplTest extends SpringTestCase {

    @Resource
    private ITicketAppService ticketAppService;
    @Resource
    private TicketApproveCallBackService ticketApproveCallBackService;

    @Test
    public void createTicketAppFromTicketFormTest(){
        TicketAppDto appDto = ticketAppService.selectTicketAppFullById("zzd_test_06");
        System.out.println(appDto);
    }

    @Test
    public void sendApproveResultMqForSheetTest(){
        Response response = ticketApproveCallBackService.sendApproveResultMqForSheet("", "pass","1002411200005710019");
        System.out.println(response);
    }
}
