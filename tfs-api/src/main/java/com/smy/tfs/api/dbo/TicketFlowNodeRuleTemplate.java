package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 工单流程节点规则模版表
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Getter
@Setter
@TableName("ticket_flow_node_rule_template")
public class TicketFlowNodeRuleTemplate extends TfsBaseEntity implements Serializable {


    private static final long serialVersionUID = -944856110850433359L;
    /**
     * ID
     */
    private String id;

    /**
     * 流程节点模板ID
     */
    private String ticketFlowNodeTemplateId;

    //工单模版ID
    private String ticketTemplateId;

    /**
     * 规则内容（内部且外部或）
     * [[{CompareId:"",CompareType:"",CompareValue:""}]]
     */
    private String ruleInfoList;
}
