package com.smy.tfs.api.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smy.tfs.api.dbo.TicketAnalysisData;
import com.smy.tfs.api.dto.TicketAnalysisDataDto;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 应用维度分析数据 服务类
 */
public interface ITicketAnalysisDataService extends IService<TicketAnalysisData> {
    /**
     * @param appId 应用ID
     */
    Map<String, Integer> getOverview(String appId);

    /**
     * 按应用分组获取时间范围内基础的统计数据（提单人数量、申请工单数量、已完成数量）
     */

    List<TicketAnalysisDataDto.TicketBaseStatistic> getTimeRangeTicketBaseStatistics(TicketAnalysisDataDto.TicketAnalysisQuery ticketAnalysisQuery);

    /**
     * 按应用分组获取时间范围内工单完成数量
     */
    List<TicketAnalysisDataDto.TicketStatusCount> getTimeRangeTicketDoneCount(TicketAnalysisDataDto.TicketAnalysisQuery ticketAnalysisQuery);

    /**
     * 按应用分组获取时间范围内工单的平均审批时效
     */
    List<TicketAnalysisDataDto.TicketAvgEfficiency> getTimeRangeTicketAvgEfficiency(TicketAnalysisDataDto.TicketAnalysisQuery ticketAnalysisQuery);

    /**
     * 按应用分组获取时间范围内审批人数量
     */
    List<TicketAnalysisDataDto.TicketExecutorCount> getTimeRangeTicketExecutorCount(TicketAnalysisDataDto.TicketAnalysisQuery ticketAnalysisQuery);

    /**
     * 按应用分组获取时间范围内工单的TopX的提单人
     */
    List<TicketAnalysisDataDto.TicketTopXCreateBy> getTimeRangeTicketTopXCreateBy(TicketAnalysisDataDto.TicketAnalysisQuery ticketAnalysisQuery);

    /**
     * 按应用分组获取时间范围内前一个周期的数据
     */
    List<TicketAnalysisDataDto> getPrevPeriodTicketAnalysisData(TicketAnalysisDataDto.TicketAnalysisQuery ticketAnalysisQuery);

    /**
     * 按应用分组获取时间范围内工单的统计数据
     */
    Map<String, TicketAnalysisDataDto> getTimeRangeTicketAnalysisData(TicketAnalysisDataDto.TicketAnalysisQuery ticketAnalysisQuery);

    Map<String, TicketAnalysisDataDto> getRealTimeTicketAnalysisData();


    /**
     * 保存时间范围内的工单数据
     * @param startTimestamp 开始时间
     * @param endTimestamp 结束时间
     * @param category 时间周期（周/天）
     * @param updateStrategy 是否更新
     */
    List<TicketAnalysisData> saveTimeRangeTicketData(Timestamp startTimestamp, Timestamp endTimestamp, String category, boolean updateStrategy);


    /**
     * 构建卡片内容
     * @param ticketAnalysisDataDto
     * @param qwUsers 企微用户ID列表
     * @return
     */
    JSONObject buildQwCardContent(TicketAnalysisDataDto ticketAnalysisDataDto, List<String> qwUsers);

    /**
     * 发送上个周期工单总结报告
     * @param currentLocalDate 当前的日期
     * @param appIds 应用IDs
     * @param category 周期（周/天）
     */
    void sendPrevPeriodTicketSummary(LocalDate currentLocalDate, List<String> appIds, String category);

    void sendBizTicketAlertSummary(String[] ticketTemplateIds, String[] bizRuleNameArr, Timestamp curDeadLineTimestamp, Timestamp hisStartTimestamp, String[] qwUsers);
}
