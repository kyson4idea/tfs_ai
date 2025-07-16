package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 应用维度分析数据
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Getter
@Setter
@TableName("ticket_analysis_data")
public class TicketAnalysisData extends TfsBaseEntity implements Serializable {


    private static final long serialVersionUID = 7908888025480490880L;
    /**
     * ID
     */
    private String id;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * Top3 提单人
     */
    private String top3CreateBy;

    /**
     * Top3 上个周期（周/日）提单人
     */
    private String lastTop3CreateBy;

    /**
     * 提单人数
     */
    private Integer createByCount;

    /**
     * 上个周期（周/日）提单人数
     */
    private Integer lastCreateByCount;

    /**
     * 上个周期（周/日）审批人数
     */
    private Integer executorCount;

    /**
     * 上个周期（周/日）审批人数
     */
    private Integer lastExecutorCount;

    /**
     * 工单申请量
     */
    private Integer applyCount;

    /**
     * 上个周期（周/日）工单申请量
     */
    private Integer lastApplyCount;

    /**
     * 撤回工单量
     */
    private Integer withdrawCount;

    /**
     * 上个周期（周/日）撤回工单量
     */
    private Integer lastWithdrawCount;

    /**
     * 审批拒绝工单量
     */
    private Integer rejectCount;

    /**
     * 上个周期（周/日）审批拒绝工单量
     */
    private Integer lastRejectCount;

    /**
     * 工单完成量
     */
    private Integer doneCount;

    /**
     * 上个周期（周/日）工单完成量
     */
    private Integer lastDoneCount;

    /**
     * 审批中工单量
     */
    private Integer applyingCount;

    /**
     * 上个周期（周/日）审批中工单量
     */
    private Integer lastApplyingCount;

    /**
     * 工单平均审批时效（s）
     */
    private Integer ticketAvgEfficiency;

    /**
     * 上个周期（周/日）工单平均审批时效（s）
     */
    private Integer lastTicketAvgEfficiency;

    /**
     * 类别 day / week
     */
    private String category;

    /**
     * 统计开始日期 2024-03-23 00:00:00
     */
    private Date startDate;

    /**
     * 统计结束日期 2024-03-23 23:59:59
     */
    private Date endDate;
}
