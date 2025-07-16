package com.smy.tfs.biz.service.impl;

import com.smy.tfs.SpringTestCase;
import com.smy.tfs.api.dto.TicketRemoteAccountDto;
import com.smy.tfs.api.service.ITicketExportUserService;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

public class ITicketExportUserServiceImplTest extends SpringTestCase {

    @Resource
    private ITicketExportUserService ticketExportUserService;

    @Test
    public void exportLdapUserList(){
        List<TicketRemoteAccountDto> result = ticketExportUserService.exportLdapUserList();
        System.out.println(result);
    }
}