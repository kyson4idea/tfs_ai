package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 工单流程模版表
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Getter
@Setter
@TableName("ticket_flow_template")
public class TicketFlowTemplate extends TfsBaseEntity implements Serializable {


    private static final long serialVersionUID = 744033967838752347L;
    private String id;

    /**
     * 工单模版id
     */
    private String ticketTemplateId;

    /**
     * 开始时抄送
     * <p>
     * [
     * user_type:””,//用户类型，用户组 上一级，指定账户体系
     * user_id:””，//用户ID
     * ]
     */
    private String startCc;

    /**
     * 结束时抄送
     */
    private String endCc;



}
