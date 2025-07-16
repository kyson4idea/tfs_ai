package com.smy.tfs.api.dto.out;

import lombok.Data;

@Data
public class TicketActionDto {
    private static final long serialVersionUID = 3469295454101730880L;

    /**
     * 工单ID(必填)
     */
    private String ticketID;

    /**
     * 动作类型(必填)
     * APPROVE("BEFORE", "审批类型"),
     * UPDATE("UPDATE", "修改类型"),
     */
    private String actType;

    /**
     * 动作类型(必填) 模版上配置的名称
     */
    private String actName;

    /**
     * 处理类型描述
     */
    private String actDescription;

    /**
     * 处理意见(必填)
     */
    private String actOpinion;

    /**
     * 审批节点(必填)
     */
    private String actNodeId;

    /**
     * 审批模式(选填)
     * 为空:默认模式，根据工单审批配置进行审批
     * DYNAMIC:动态模式，不校验审批人，且以节点维度审批
     */
    private String dealMode;
}
