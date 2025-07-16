package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smy.tfs.api.enums.ApproveDealTypeEnum;
import com.smy.tfs.api.enums.DealTypeCallbackEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 工单流程节点审批明细
 * </p>
 *
 * @author 01140
 * @since 2024-04-20
 */
@Data
@TableName("ticket_flow_node_approve_detail")
public class TicketFlowNodeApproveDetail extends TfsBaseEntity implements Serializable{


    private static final long serialVersionUID = -7848046705377451074L;
    @TableId("id")
    private String id;

    /**
     * 工单ID
     */
    @TableField("ticket_data_id")
    private String ticketDataId;

    /**
     * 流程节点ID
     */
    @TableField("ticket_flow_node_data_id")
    private String ticketFlowNodeDataId;

    @TableField("deal_user_type")
    private String dealUserType;
    /**
     * 处理人ID
     */
    @TableField("deal_user_id")
    private String dealUserId;

    @TableField("deal_user_name")
    private String dealUserName;

    /**
     * 处理意见
     */
    @TableField("deal_opinion")
    private String dealOpinion;

    /**
     * 处理类型/审批结果（审批通过，审批驳回，审批驳回至上一节点）
     */

    @TableField("deal_type")
    private ApproveDealTypeEnum dealType;

    /**
     * 处理类型描述，可能业务有自己的描述，不叫通过驳回
     */

    @TableField("deal_type_description")
    private String dealTypeDescription;

    /**
     * 处理类型/审批结果（审批通过，审批驳回，审批驳回至上一节点）
     */

    @TableField("deal_type_callback")
    private DealTypeCallbackEnum dealTypeCallback;

    /**
     * 执行动作数据id
     */
    @TableField("ticket_flow_event_data_id")
    private String ticketFlowEventDataId;

}
