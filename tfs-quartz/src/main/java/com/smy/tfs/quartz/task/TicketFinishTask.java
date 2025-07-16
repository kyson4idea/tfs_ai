package com.smy.tfs.quartz.task;

import com.alibaba.excel.util.StringUtils;
import com.smy.tfs.api.dto.BatchFinishTicketsDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.service.ITicketDataActService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 定时任务批量关单
 */
@Slf4j
@Component("ticketFinishTask")
public class TicketFinishTask {

    @Resource
    private ITicketDataActService ticketDataActService;

    /**
     * 关闭俩小时内的工单数据
     */
    public void finishTicketSync(){
        log.info("定时任务：开始批量关单");
        BatchFinishTicketsDto batchFinishTicketsDto = new BatchFinishTicketsDto();
        String userType = "ldap";
        String userId = "tfs_system";
        String userName = "tfs_system";
        batchFinishTicketsDto.setDealOpinion("系统关单");
        Response response = ticketDataActService.batchFinishTickets(batchFinishTicketsDto, userType, userId, userName);
        if (!response.isSuccess()) {
            log.error("定时任务：批量关单异常：{}", response.getMsg());
        }
        log.info("定时任务：结束批量关单");
    }


}
