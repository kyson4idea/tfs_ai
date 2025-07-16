package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smy.tfs.api.enums.AuditedMethodEnum;
import com.smy.tfs.api.enums.AuditedType;
import com.smy.tfs.api.enums.CallBackMsgStatusEnum;
import com.smy.tfs.api.enums.NodeStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 工单流程节点数据表
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Getter
@Setter
@TableName("ticket_flow_node_data")
public class TicketFlowNodeData extends TfsBaseEntity implements Serializable {


    private static final long serialVersionUID = 5958998385356380505L;
    private String id;

    private String nodeName;

    /**
     * 上节点ID, first表示开始节点
     */
    private String preNodeId;

    /**
     * 模版ID
     */
    private String templateId;

    /**
     * 工单数据ID
     */
    private String ticketDataId;

    /**
     * 流程数据ID
     */
    private String ticketFlowDataId;

    /**
     * audited_method  audited_type
     * <p>
     * approve_method_type   execute_type
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
     * 流程节点状态
     * APPROVE_INIT 审批初始化
     * APPROVE_PASS 审批通过
     * APPROVE_REJECT 审批拒绝
     * APPROVING 审批中
     *
     * @see com.smy.tfs.api.enums.NodeStatusEnum
     */
    private NodeStatusEnum nodeStatus;


    /**
     * 节点顺序
     */
    private int nodeOrder;

    /**
     * 企微审批卡片ID
     */
    private String nodeWxDealCardCode;

    private String nodeWxDealCardMessageId;

    /**
     * 当前节点可修改的字段
     */
    private String modifyFieldList;

    /**
     * 推送消息狀态
     */
    private CallBackMsgStatusEnum callBackMsgStatus;

    public TicketFlowNodeData() {
    }

    public TicketFlowNodeData(TicketFlowNodeTemplate ticketFlowNodeTemplate, String id, String preNodeId, String ticketDataId, String ticketFlowDataId) {
        this.id = id;
        this.nodeName = ticketFlowNodeTemplate.getNodeName();
        this.preNodeId = preNodeId;
        this.templateId = ticketFlowNodeTemplate.getId();
        this.ticketDataId = ticketDataId;
        this.ticketFlowDataId = ticketFlowDataId;
        this.auditedMethod = ticketFlowNodeTemplate.getAuditedMethod();
        this.auditedType = ticketFlowNodeTemplate.getAuditedType();
        this.nodeStatus = NodeStatusEnum.APPROVE_INIT;
        this.nodeOrder = ticketFlowNodeTemplate.getNodeOrder();
        this.modifyFieldList = ticketFlowNodeTemplate.getModifyFieldList();
        this.setCreateBy("system");
        this.setUpdateBy("system");
        this.setCreateTime(new Date());
        this.setUpdateTime(new Date());
    }
}
