package com.smy.tfs.biz.service.impl;

import com.smy.tfs.SpringTestCase;
import com.smy.tfs.api.service.ITicketOriginAccountService;
import org.junit.Test;

import javax.annotation.Resource;

public class TicketOriginAccountServiceImplTest extends SpringTestCase {
    @Resource
    private ITicketOriginAccountService ticketOriginAccountService;

    @Test
    public void batchMatchOriginAccountInfoTest(){
        ticketOriginAccountService.batchMatchOriginAccountInfo();
    }

}