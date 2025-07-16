package com.smy.tfs.quartz.task;

import com.smy.tfs.api.service.ITicketFlowEventDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component("ticketCallBackRetryTask")
public class TicketCallBackRetryTask {
    @Resource
    private ITicketFlowEventDataService ticketFlowEventDataService;
    /**
     * 调度任务触发 自动重试执行动作
     */
    public void callBackRetry() {
        log.info("自动重试执行动作跑批开始...");
        ticketFlowEventDataService.callBackRetry();
        log.info("自动重试执行动作跑批结束...");
    }
}
