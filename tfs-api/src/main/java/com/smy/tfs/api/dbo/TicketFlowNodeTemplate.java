package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smy.tfs.api.enums.AuditedMethodEnum;
import com.smy.tfs.api.enums.AuditedType;
import com.smy.tfs.api.enums.ExecutorTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 工单流程节点模版表
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Getter
@Setter
@TableName("ticket_flow_node_template")
public class TicketFlowNodeTemplate extends TfsBaseEntity implements Serializable {


    private static final long serialVersionUID = -2805181352463153153L;
    private String id;

    private String nodeName;

    //上节点ID 1,2,3
    private String preNodeId;

    private String ticketTemplateId;

    private String ticketFlowTemplateId;

    //节点规则类型：静态规则static 动态规则dynamic
    //private String flowNodeRuleType;

    /**
     * 审批方式
     * 会签
     * 或签
     */
    private AuditedMethodEnum auditedMethod;

    /**
     * 审批类型
     * 人工审核&自动审核&自动拒绝
     */
    private AuditedType auditedType;

    /**
     * 节点顺序
     */
    private Integer nodeOrder;

    /**
     * 当前节点可修改的字段
     */
    private String modifyFieldList;


}
