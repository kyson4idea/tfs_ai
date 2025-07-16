package com.smy.tfs.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smy.tfs.api.dbo.TicketAnalysisData;
import com.smy.tfs.api.dto.TicketAnalysisDataDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TicketAnalysisDataMapper extends BaseMapper<TicketAnalysisData> {
    /**
     * 查询工单总数
     *
     * @param appId 应用ID
     * @return 工单总数量
     */
    Integer countTickets(@Param("appId") String appId);

    /**
     * 查询提单用户总数
     *
     * @param appId 应用ID
     * @return 提单用户数量
     */
    Integer countCreateUsers(@Param("appId") String appId);

    /**
     * 查询工单模版数量
     * @param appId 应用ID
     * @return 工单模版总数量
     */
    Integer countTemplates(@Param("appId") String appId);

    /**
     * 查询提单用户信息
     * @param appId 应用ID
     * @return 用户名称集合
     */
    List<String> getCreateUsers(@Param("appId") String appId);

    /**
     * 应用分组查询指定日期当天提单人数、工单申请数量、工单审批完成量
     */
    List<TicketAnalysisDataDto.TicketBaseStatistic> getTimeRangeTicketBaseStatistics(@Param("ticketAnalysisQuery") TicketAnalysisDataDto.TicketAnalysisQuery ticketAnalysisQuery);

    /**
     * 应用分组查询指定日期内工单处于完成状态下的数量
     */
    List<TicketAnalysisDataDto.TicketStatusCount> getTimeRangeTicketDoneCount(@Param("ticketAnalysisQuery") TicketAnalysisDataDto.TicketAnalysisQuery ticketAnalysisQuery);

    /**
     * 应用分组查询指定日期内完成工单的平均处理时长
     */
    List<TicketAnalysisDataDto.TicketAvgEfficiency> getTimeRangeTicketAvgEfficiency(@Param("ticketAnalysisQuery") TicketAnalysisDataDto.TicketAnalysisQuery ticketAnalysisQuery);

    /**
     * 应用分组查询指定日期内审批人数量
     */
    List<TicketAnalysisDataDto.TicketExecutorCount> getTimeRangeTicketExecutorCount(@Param("ticketAnalysisQuery") TicketAnalysisDataDto.TicketAnalysisQuery ticketAnalysisQuery);


    /**
     * 应用分组查询指定日期内TopX的用户
     */
    List<TicketAnalysisDataDto.TicketTopXCreateBy> getTimeRangeTicketTopXCreateBy(@Param("ticketAnalysisQuery") TicketAnalysisDataDto.TicketAnalysisQuery ticketAnalysisQuery);

    /**
     * 应用的工单数据信息
     */
    List<TicketAnalysisDataDto> getAppTicketAnalysisData(@Param("ticketAnalysisQuery") TicketAnalysisDataDto.TicketAnalysisQuery ticketAnalysisQuery);
}
