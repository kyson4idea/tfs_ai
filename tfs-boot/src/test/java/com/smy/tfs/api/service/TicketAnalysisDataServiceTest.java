package com.smy.tfs.api.service;

import com.smy.tfs.api.enums.TicketAnalysisDataTypeEnum;
import com.smy.tfs.common.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.time.LocalDate;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TicketAnalysisDataServiceTest {
    @Autowired
    ITicketAnalysisDataService ticketAnalysisDataService;

    @Test
    public void testSaveWeekTicketData() {
        LocalDate currentDate = LocalDate.now();
        LocalDate[] previousWeekRange = DateUtils.getPreviousWeekRange(currentDate);
        Timestamp startTimestamp = DateUtils.getDateBeforeStart(previousWeekRange[0], 0);
        Timestamp endTimestamp = DateUtils.getDateBeforeEnd(previousWeekRange[1], 0);
        ticketAnalysisDataService.saveTimeRangeTicketData(startTimestamp, endTimestamp, TicketAnalysisDataTypeEnum.WEEK.getCode(), false);
    }

    @Test
    public void testSaveDayTicketData() {
        LocalDate currentDate = LocalDate.now();
        Timestamp startTimestamp = DateUtils.getDateBeforeStart(currentDate, 1);
        Timestamp endTimestamp = DateUtils.getDateBeforeEnd(currentDate, 1);
        ticketAnalysisDataService.saveTimeRangeTicketData(startTimestamp, endTimestamp, TicketAnalysisDataTypeEnum.DAY.getCode(), false);
    }

    @Test
    public void testSendPrevPeriodTicketSummary() {
//        ticketAnalysisDataService.sendPrevPeriodTicketSummary(LocalDate.now(), null, TicketAnalysisDataTypeEnum.DAY.getCode());
        ticketAnalysisDataService.sendPrevPeriodTicketSummary(LocalDate.now(), null, TicketAnalysisDataTypeEnum.WEEK.getCode());
    }

}
