package com.smy.tfs.api.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.smy.tfs.api.enums.TicketAnalysisDataTypeEnum;
import com.smy.tfs.common.utils.DateUtils;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
public class TicketAnalysisDataDto implements Serializable{
    private static final long serialVersionUID = -7397541459040674928L;
    private String id;
    private String appId;
    private String top3CreateBy;
    private String lastTop3CreateBy;
    private Integer createByCount = 0;
    private Integer lastCreateByCount = 0;
    private Integer executorCount = 0;
    private Integer lastExecutorCount = 0;
    private Integer applyCount = 0;
    private Integer lastApplyCount = 0;
    private Integer withdrawCount = 0;
    private Integer lastWithdrawCount = 0;
    private Integer rejectCount = 0;
    private Integer lastRejectCount = 0;
    private Integer doneCount = 0;
    private Integer lastDoneCount = 0;
    private Integer applyingCount = 0;
    private Integer lastApplyingCount = 0;
    private Integer ticketAvgEfficiency;
    private Integer lastTicketAvgEfficiency;
    private String category;
    private Date startDate;
    private Date endDate;

    // ticket_app 表
    private String appName;
    private String appAdminUsers;

    @Data
    public static class TicketAnalysisQuery {
        private Timestamp startTimestamp;
        private Timestamp endTimestamp;
        private String appId;
        private List<String> appIds;
        private Integer topX;
        private String category;

        public static TicketAnalysisQuery copyAsPrevPeriod (TicketAnalysisQuery original) {
            TicketAnalysisQuery copy = new TicketAnalysisQuery();
            if (TicketAnalysisDataTypeEnum.WEEK.getCode().equals(original.getCategory())) {
                LocalDate[] prevWeekRange = DateUtils.getPreviousWeekRange(original.getStartTimestamp().toLocalDateTime().toLocalDate());
                copy.startTimestamp = DateUtils.getDateBeforeStart(prevWeekRange[0], 0);
                copy.endTimestamp = DateUtils.getDateBeforeEnd(prevWeekRange[1], 0);
            } else {
                LocalDate prevDay = original.getStartTimestamp().toLocalDateTime().toLocalDate().minusDays(1);
                copy.startTimestamp = DateUtils.getDateBeforeStart(prevDay, 0);
                copy.endTimestamp = DateUtils.getDateBeforeEnd(prevDay, 0);
            }
            copy.appId = original.getAppId();
            copy.topX = original.getTopX();
            copy.category = original.getCategory();

            return copy;
        }
    }

    @Data
    public static class TicketBaseStatistic {
        /**
         * 应用ID
         */
        private String appId;
        private int applyCount;
        private int applyingCount;
        private int withdrawCount;
        private int rejectCount;
    }

    @Data
    public static class TicketAvgEfficiency {
        /**
         * 应用ID
         */
        private String appId;


        /**
         * 工单状态数量
         */
        private Double ticketAvgEfficiency;
    }

    @Data
    public static class TicketTopXCreateBy {
        /**
         * 应用ID
         */
        private String appId;


        /**
         * 提单用户
         */
        private String createBy;

        /**
         * 用户提单数量
         */
        private String totalCount;
    }

    @Data
    public static class TicketStatusCount {
        /**
         * 应用ID
         */
        private String appId;

        /**
         * 工单状态
         * 初始化、草稿中、审批中、审批结束、审批驳回、撤回
         */
        private String ticketStatus;

        /**
         * 工单状态数量
         */
        private int statusCount;
    }

    @Data
    public static class TicketExecutorCount {
        /**
         * 应用ID
         */
        private String appId;
        /**
         * 审批人求和数量
         */
        private Integer executorCount;
    }

    @Data
    public static class CalcTicketAnalysisData implements Serializable {
        @NotNull(message = "同步日期不能为空")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate syncDate;

        @NotBlank(message = "类别（week/day）不能为空")
        @NotNull(message = "类别（week/day）不能为空")
        private String category;

        private Boolean updateStrategy = false;
    }
}
