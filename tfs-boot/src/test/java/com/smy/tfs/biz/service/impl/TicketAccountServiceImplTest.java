package com.smy.tfs.biz.service.impl;

import com.smy.tfs.SpringTestCase;
import com.smy.tfs.api.constants.TfsBaseConstant;
import com.smy.tfs.api.dto.TicketAccountDto;
import com.smy.tfs.api.dto.TicketRemoteAccountDto;
import com.smy.tfs.api.service.ITicketAccountService;
import com.smy.tfs.common.utils.SecurityUtils;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

public class TicketAccountServiceImplTest extends SpringTestCase {

    @Resource
    private ITicketAccountService ticketAccountService;

    @Test
    public void syncTicketRemoteAccountTest(){
        SecurityUtils.wrapContext(TfsBaseConstant.defaultOriginId, TfsBaseConstant.defaultUserId, TfsBaseConstant.defaultUserName,
                TfsBaseConstant.defaultUserType, TfsBaseConstant.defaultAppId, () -> {
                    ticketAccountService.syncTicketRemoteAccount();
                });
    }

    @Test
    public void getLeaderByTypeAndIdTest(){
        TicketRemoteAccountDto leader = ticketAccountService.getLeaderByTypeAndId("ldap", "z00740");
        System.out.println(leader);
    }

    @Test
    public void insertTicketAccountTest() {
        TicketAccountDto ticketAccountDto = new TicketAccountDto();
        ticketAccountDto.setTicketAccountName("xxx");
        ticketAccountDto.setTicketAccountType("xxx");
        ticketAccountDto.setTicketAccountValue("xxx");
        ticketAccountDto.setTicketAccountDescription("xxx");
        ticketAccountService.insertTicketAccount(ticketAccountDto);
    }

    @Test
    public void selectTicketAccountByIdTest() {
        TicketAccountDto ticketAccountDto = ticketAccountService.selectTicketAccountById("1781227593073274881");
        System.out.println(ticketAccountDto);
    }

    @Test
    public void ticketAccountCacheTest(){
        List<TicketRemoteAccountDto>  accountDtoList= ticketAccountService.getTicketRemoteAccountListByType("oms");
        System.out.println(accountDtoList);

        TicketRemoteAccountDto ticketRemoteAccountDto=  ticketAccountService.getTicketRemoteAccountByIdAndType("oms", "itadmin");
        System.out.println(ticketRemoteAccountDto);

        TicketRemoteAccountDto ticketRemoteAccountDto1=  ticketAccountService.getTicketRemoteAccountByIdAndType("owen","smy");
        System.out.println(ticketRemoteAccountDto1);


        TicketRemoteAccountDto ticketRemoteAccountDto2=  ticketAccountService.getTicketRemoteAccountByIdAndType("o02157","oms");
        System.out.println(ticketRemoteAccountDto2);
    }

}