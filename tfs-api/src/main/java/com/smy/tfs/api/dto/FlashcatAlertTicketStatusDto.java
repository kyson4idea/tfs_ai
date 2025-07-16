package com.smy.tfs.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FlashcatAlertTicketStatusDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单ID
     */
    private String ticketId;

    /**
     * 工单名称
     */
    private String ticketName;

    /**
     * 工单状态
     */
    private String ticketStatus;

    /**
     * 当前处理人
     */
    private String curProcessor;

    /**
     * 处理完成的人
     */
    private String currentDoneUsers;

    /**
     * 工单创建时间
     */
    private String createTime;

    /**
     * 工单最后更新时间
     */
    private String lastUpdateTime;

    /**
     * 告警规则ID
     */
    private String ruleId;

    /**
     * 卡片ID
     */
    private String cardId;

    /**
     * 首次告警时间
     */
    private String firstTriggerTime;

    /**
     * 最近告警时间
     */
    private String lastTriggerTime;

    @Data
    public static class AlertRuleIdsReq implements Serializable {
        private List<String> ruleIds;
        private Boolean applyingOnly = true;
        private String startDate;
    }
}
