package com.smy.tfs.api.dto;

import com.smy.tfs.api.dbo.TicketFlowNodeRuleTemplate;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 工单流程节点规则模版对象 ticket_flow_node_rule_template
 *
 * @author zzd
 * @date 2024-04-11
 */
@Data
public class TicketFlowNodeRuleTemplateDto implements Serializable {

    private static final long serialVersionUID = 5965206484508995317L;
    /**
     * ID
     */
    private String id;

    /**
     * 流程节点模板ID
     */
    private String ticketFlowNodeTemplateId;
    /**
     * 规则内容（内部且外部或）
     * [{FieldID,CompareType,CompareValue}]
     */
    private String ruleInfoList;

    /**
     * 删除时间
     */
    private Date deleteTime;

    public TicketFlowNodeRuleTemplateDto() {

    }

    public TicketFlowNodeRuleTemplateDto(TicketFlowNodeRuleTemplate ticketFlowNodeRuleTemplate) {
        this.id = ticketFlowNodeRuleTemplate.getId();
        this.ticketFlowNodeTemplateId = ticketFlowNodeRuleTemplate.getTicketFlowNodeTemplateId();
        this.ruleInfoList = ticketFlowNodeRuleTemplate.getRuleInfoList();
    }

}
