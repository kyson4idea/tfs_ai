package com.smy.tfs.quartz.task;

import com.smy.tfs.api.enums.TicketAnalysisDataTypeEnum;
import com.smy.tfs.api.service.ITicketAnalysisDataService;
import com.smy.tfs.common.utils.DateUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;

@Component("tickAnalysisTask")
public class TicketAnalysisDataTask {

    @Resource
    private ITicketAnalysisDataService ticketAnalysisDataService;

    @Value("${report.dayAppIds:}")
    private String dayAppIds;

    @Value("${report.weekAppIds:}")
    private String weekAppIds;

    @Value("${report.bizRuleNames:}")
    private String bizRuleNames;

    @Value("${report.bizDayDeadline:}")
    private String bizDayDeadline;

    @Value("${report.bizStartDate:}")
    private String bizStartDate;

    @Value("${report.notifyQwUsers:}")
    private String notifyQwUsers;

    @Value("${flashcat.alert.ticketTemplateId:}")
    private String ticketTemplateId;

    @Value("${rh.alert.ticketTemplateId:}")
    private String rhTicketTemplateId;

    /**
     * 收集昨天的工单数据，并保存
     */
    public void collectTicketDataPrevDay() {
        LocalDate currentDate = LocalDate.now();
        Timestamp startTimestamp = DateUtils.getDateBeforeStart(currentDate, 1);
        Timestamp endTimestamp = DateUtils.getDateBeforeEnd(currentDate, 1);
        ticketAnalysisDataService.saveTimeRangeTicketData(startTimestamp, endTimestamp, TicketAnalysisDataTypeEnum.DAY.getCode(), false);
    }

    /**
     * 收集当前时间下上周的工单数据，并保存
     */
    public void collectTicketDataPrevWeek() {
        LocalDate currentDate = LocalDate.now();
        LocalDate[] previousWeekRange = DateUtils.getPreviousWeekRange(currentDate);
        Timestamp startTimestamp = DateUtils.getDateBeforeStart(previousWeekRange[0], 0);
        Timestamp endTimestamp = DateUtils.getDateBeforeEnd(previousWeekRange[1], 0);
        ticketAnalysisDataService.saveTimeRangeTicketData(startTimestamp, endTimestamp, TicketAnalysisDataTypeEnum.WEEK.getCode(), false);
    }

    /**
     * 发送上一周所有应用下各工单总结报告
     */
    public void sendWeeklyAllTicketSummary() {
        if (weekAppIds == null || weekAppIds.isEmpty()) {
            ticketAnalysisDataService.sendPrevPeriodTicketSummary(LocalDate.now(), null, TicketAnalysisDataTypeEnum.WEEK.getCode());
        } else {
            ticketAnalysisDataService.sendPrevPeriodTicketSummary(LocalDate.now(), Arrays.asList(weekAppIds.split(",")), TicketAnalysisDataTypeEnum.WEEK.getCode());
        }
    }

    /**
     * 发送上一周具体某个应用的工单总结报告
     */
    public void sendWeeklyAppTicketSummary(String appId) {
        ticketAnalysisDataService.sendPrevPeriodTicketSummary(LocalDate.now(), Collections.singletonList(appId), TicketAnalysisDataTypeEnum.WEEK.getCode());
    }

    /**
     * 发送昨天所有应用下各工单总结报告
     */
    public void sendDailyAllTicketSummary() {
        if (dayAppIds == null || dayAppIds.isEmpty()) {
            ticketAnalysisDataService.sendPrevPeriodTicketSummary(LocalDate.now(), null, TicketAnalysisDataTypeEnum.DAY.getCode());
        } else {
            ticketAnalysisDataService.sendPrevPeriodTicketSummary(LocalDate.now(), Arrays.asList(dayAppIds.split(",")), TicketAnalysisDataTypeEnum.DAY.getCode());
        }
    }

    /**
     * 发送昨天具体某个应用的工单总结报告
     */
    public void sendDailyAppTicketSummary(String appId) {
        ticketAnalysisDataService.sendPrevPeriodTicketSummary(LocalDate.now(), Collections.singletonList(appId), TicketAnalysisDataTypeEnum.DAY.getCode());
    }

    /**
     * 发送业务告警工单统计日报
     */
    public void sendDailyBizTicketAlertSummary() {
        String[] bizRuleNameArr = bizRuleNames.split(",");

        LocalTime curDayTime = LocalTime.parse(bizDayDeadline, DateTimeFormatter.ofPattern("HH:mm:ss"));
        LocalDate curDate = LocalDate.now();
        Timestamp curDeadLineTimestamp = Timestamp.valueOf(LocalDateTime.of(curDate, curDayTime));

        Timestamp hisStartTimestamp = null;
        if (NumberUtils.isDigits(bizStartDate)) {
            int days = Integer.parseInt(bizStartDate);
            if (days > -1) {
                hisStartTimestamp = DateUtils.getDateBeforeStart(curDate, days);
            }
        } else {
            try {
                LocalDate startDate = LocalDate.parse(bizStartDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                hisStartTimestamp = DateUtils.getDateBeforeStart(startDate, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String[] notifyQwUserArr = notifyQwUsers.split(",");
        ticketAnalysisDataService.sendBizTicketAlertSummary(new String[]{ticketTemplateId, rhTicketTemplateId}, bizRuleNameArr, curDeadLineTimestamp, hisStartTimestamp, notifyQwUserArr);
    }
}
