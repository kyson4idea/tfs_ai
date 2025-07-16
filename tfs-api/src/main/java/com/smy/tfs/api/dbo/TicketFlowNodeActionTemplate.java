package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smy.tfs.api.enums.ActionTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 工单流程节点执行人模版表
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Getter
@Setter
@TableName("ticket_flow_node_action_template")
public class TicketFlowNodeActionTemplate extends TfsBaseEntity implements Serializable {
    private static final long serialVersionUID = -6899318924821671206L;

    private String id;
    //工单模版ID
    private String ticketTemplateId;
    /**
     * 工单流程节点模版ID
     */
    private String ticketFlowNodeTemplateId;

    private String actionName;

    private ActionTypeEnum actionType;

    //{approve:pass,update:[{"code":"组件ID","value": "aaa"}]}
    private String actionValue;

    private String actionConfig;
}
