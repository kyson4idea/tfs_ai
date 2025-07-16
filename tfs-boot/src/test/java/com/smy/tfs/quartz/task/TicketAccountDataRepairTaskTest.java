package com.smy.tfs.quartz.task;

import com.smy.tfs.SpringTestCase;
import org.junit.Test;

import javax.annotation.Resource;

public class TicketAccountDataRepairTaskTest extends SpringTestCase {
    @Resource
    private TicketAccountDataRepairTask ticketAccountDataRepairTask;

    @Test
    public void repairTicketAccountDataTest(){
        ticketAccountDataRepairTask.repairTicketAccountData();
    }
}