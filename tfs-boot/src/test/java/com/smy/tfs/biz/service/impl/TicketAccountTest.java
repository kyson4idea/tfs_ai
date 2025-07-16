package com.smy.tfs.biz.service.impl;

import com.smy.tfs.SpringTestCase;
import com.smy.tfs.api.constants.TfsBaseConstant;
import com.smy.tfs.api.dbo.TicketAccountMapping;
import com.smy.tfs.api.dto.TicketAccountDto;
import com.smy.tfs.api.dto.TicketRemoteAccountDto;
import com.smy.tfs.api.service.ITicketAccountMappingService;
import com.smy.tfs.api.service.ITicketAccountService;
import com.smy.tfs.common.utils.SecurityUtils;
import com.smy.tfs.quartz.task.TicketAccountTask;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

public class TicketAccountTest extends SpringTestCase {

    @Resource
    private ITicketAccountMappingService ticketAccountMappingService;
    @Resource
    private ITicketAccountService ticketAccountService;
    @Resource
    private TicketAccountTask ticketAccountTask;

    @Test
    public void testTicketAccountMapping() {
//        TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByAccountIdAndType("alert_callback", "ldap");
//        System.out.println(ticketAccountMapping);
        TicketRemoteAccountDto applyUser = ticketAccountService.getTicketRemoteAccountByIdAndType("songbing", "ldap");
        ticketAccountService.notifyQwMsg(
                String.format("未找到用户【userType:%s userId:%s】上级，请及时处理！", applyUser.getUserId(), applyUser.getUserName()),
                Arrays.asList("songbing", "zhengzhuang")
        );
    }

    @Test
    public void testTicketAccount() {
        String jsonParam = "[{\"accountType\":\"kefu\",\"qwCorporate\":\"hjsd\"}]";
        ticketAccountTask.syncAccountMappingQywxId(jsonParam);
    }

}