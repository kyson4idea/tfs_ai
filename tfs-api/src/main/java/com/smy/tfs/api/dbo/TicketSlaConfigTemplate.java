package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smy.tfs.api.enums.TicketSlaTemplateStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 工单/工单流程节点SLA模版配置表
 * </p>
 *
 * @author yss
 * @since 2025-03-13
 */
@Getter
@Setter
@TableName("ticket_sla_config_template")
public class TicketSlaConfigTemplate extends TfsBaseEntity implements Serializable {
    private static final long serialVersionUID = 4815578987767790228L;

    /**
     * $column.columnComment
     */
    private String id;

    /**
     * 工单模版ID
     */
    private String ticketTemplateId;

    /**
     * 工单sla模版ID
     */
    private String ticketSlaTemplateId;
    /**
     *  配置方式：FLOW_NODE：流程节点，TICKET:工单
     */
    private String configType;
    /**
     *  配置方式内容:如config_type是flow_node方式，则内容是{"节点id1","节点id2"}
     */
    private String configTypeContent;
    /**
     *  起止时间：[00:00:00,23:59:59]
     */
    private String startEndTime;
    /**
    *  30小时：例如30h
     */   
    private String timeout;
     /**
     *  提醒规则：
      *
      *  [{
      * 	"remindType": "before/current/after",
      * 	"timeValue": "30 m ",
      * 	"remindMsg": [{
      * 		"remindMsgType": "WECOM"
      * 	    }, {
      * 		"remindMsgType": "TAG"
      *    }, {
      * 		"remindMsgType": "INTERFACE",
      * 		"interfaceContent": ""
      *    }],
      * 	"executors": [{
      * 		"executorType ": "APPLY_MEMBER_LIST ",
      * 		"executorValue": [{
      * 			"accountType": "kefu",
      * 			"accountId": "7025",
      * 			"accountName": "陈红",
      * 			"sameOriginId": "10581"
      *        }, {
      * 			"accountType": "kefu",
      * 			"accountId": "6688",
      * 			"accountName": "廖海龙",
      * 			"sameOriginId": "10373"
      *        }, {
      * 			"accountType": "kefu",
      * 			"accountId": "4001",
      * 			"accountName": "陈圆圆",
      * 			"sameOriginId": "10580"
      *        }]
      *    }, {
      * 		"executorType ": "APPLYER "
      *    }]
      * }]
      *
      */
    private String remindConfig;

    private TicketSlaTemplateStatusEnum status;



}