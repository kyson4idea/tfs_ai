package com.smy.tfs.api.dto.ticket_sla_service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smy.tfs.api.dbo.TicketSlaConfigTemplate;
import com.smy.tfs.api.enums.TicketSlaTemplateStatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 工单/工单流程节点SLA模版配置Dto
 * </p>
 *
 * @author yss
 * @since 2025-03-13
 */
@Data
@NoArgsConstructor
public class TicketSlaConfigTemplateDto implements Serializable {
    private static final long serialVersionUID = -3661636841150062510L;

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
     *  提醒规则：[{"remindType":"before/current/after","timeValue":30m","ticketMsgArriveType":"WECOM","executorType":"APPLY_MEMBER_LIST","executorValue":"[{"accountType":"kefu","accountId":"7025","accountName":"陈红","sameOriginId":"10581"},{"accountType":"kefu","accountId":"6688","accountName":"廖海龙","sameOriginId":"10373"},{"accountType":"kefu","accountId":"4001","accountName":"陈圆圆","sameOriginId":"10580"}]"}]
      */
    private String remindConfig;

    private TicketSlaTemplateStatusEnum status;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    public TicketSlaConfigTemplate toTicketSlaConfigTemplate(TicketSlaConfigTemplateDto ticketSlaConfigTemplateDto){
        TicketSlaConfigTemplate ticketSlaConfigTemplate = new TicketSlaConfigTemplate();
        ticketSlaConfigTemplate.setId(ticketSlaConfigTemplateDto.getId());
        ticketSlaConfigTemplate.setConfigType(ticketSlaConfigTemplateDto.getConfigType());
        ticketSlaConfigTemplate.setConfigTypeContent(ticketSlaConfigTemplateDto.getConfigTypeContent());
        ticketSlaConfigTemplate.setRemindConfig(ticketSlaConfigTemplateDto.getRemindConfig());
        ticketSlaConfigTemplate.setStartEndTime(ticketSlaConfigTemplateDto.getStartEndTime());
        ticketSlaConfigTemplate.setTimeout(ticketSlaConfigTemplateDto.getTimeout());
        ticketSlaConfigTemplate.setStatus(ticketSlaConfigTemplateDto.getStatus());
        ticketSlaConfigTemplate.setCreateTime(ticketSlaConfigTemplateDto.getCreateTime());
        return ticketSlaConfigTemplate;
    }

    public TicketSlaConfigTemplateDto (TicketSlaConfigTemplate ticketSlaConfigTemplate) {
        this.id = ticketSlaConfigTemplate.getId();
        this.ticketTemplateId = ticketSlaConfigTemplate.getTicketTemplateId();
        this.ticketSlaTemplateId = ticketSlaConfigTemplate.getTicketSlaTemplateId();
        this.configType = ticketSlaConfigTemplate.getConfigType();
        this.configTypeContent = ticketSlaConfigTemplate.getConfigTypeContent();
        this.remindConfig = ticketSlaConfigTemplate.getRemindConfig();
        this.startEndTime = ticketSlaConfigTemplate.getStartEndTime();
        this.timeout = ticketSlaConfigTemplate.getTimeout();
        this.status = ticketSlaConfigTemplate.getStatus();
        this.createBy = ticketSlaConfigTemplate.getCreateBy();
        this.updateBy = ticketSlaConfigTemplate.getUpdateBy();
        this.createTime = ticketSlaConfigTemplate.getCreateTime();
        this.updateTime = ticketSlaConfigTemplate.getUpdateTime();

    }



}