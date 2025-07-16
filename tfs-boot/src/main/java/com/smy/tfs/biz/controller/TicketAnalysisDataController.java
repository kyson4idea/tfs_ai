package com.smy.tfs.biz.controller;

import com.smy.tfs.api.dto.TicketAnalysisDataDto;
import com.smy.tfs.api.enums.TicketAnalysisDataTypeEnum;
import com.smy.tfs.api.service.ITicketAnalysisDataService;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.utils.DateUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDate;

@RestController
@RequestMapping("/ticketAnalysis")
public class TicketAnalysisDataController {
    @Resource
    private ITicketAnalysisDataService ticketAnalysisDataService;

    @GetMapping("/overview")
    public AjaxResult getOverview(@RequestParam(required = false) String appId) {
        TicketAnalysisDataDto.TicketAnalysisQuery ticketAnalysisQuery = new TicketAnalysisDataDto.TicketAnalysisQuery();
        return AjaxResult.success(ticketAnalysisDataService.getOverview(appId));
    }

    /**
     * 手动补偿某个周期的数据统计
     * 1: 日期2024-04-24 day 同步的是2024-04-23（前一天） 的数据
     * 2：日期2024-04-24 week 同步的是2024-04-15 ～ 2024-04-21（上一周）的数据
     * @param calcTicketAnalysisData
     */
    @PostMapping("/sync")
    public AjaxResult syncTicketAnalysisData(@Validated @RequestBody TicketAnalysisDataDto.CalcTicketAnalysisData calcTicketAnalysisData) {
        Timestamp startTimestamp;
        Timestamp endTimestamp;
        if (TicketAnalysisDataTypeEnum.DAY.getCode().equals(calcTicketAnalysisData.getCategory())) {
            startTimestamp = DateUtils.getDateBeforeStart(calcTicketAnalysisData.getSyncDate(), 1);
            endTimestamp = DateUtils.getDateBeforeEnd(calcTicketAnalysisData.getSyncDate(), 1);
        } else if (TicketAnalysisDataTypeEnum.WEEK.getCode().equals(calcTicketAnalysisData.getCategory())) {
            LocalDate[] previousWeekRange = DateUtils.getPreviousWeekRange(calcTicketAnalysisData.getSyncDate());
            startTimestamp = DateUtils.getDateBeforeStart(previousWeekRange[0], 0);
            endTimestamp = DateUtils.getDateBeforeEnd(previousWeekRange[1], 0);
        } else {
            return AjaxResult.error( String.format("非法类别%s信息，只能是week、day", calcTicketAnalysisData.getCategory()));
        }
        ticketAnalysisDataService.saveTimeRangeTicketData(startTimestamp, endTimestamp, calcTicketAnalysisData.getCategory(), calcTicketAnalysisData.getUpdateStrategy());
        return AjaxResult.success("更新成功");
    }
}
