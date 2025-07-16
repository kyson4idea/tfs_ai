package com.smy.tfs.api.dto;

import com.smy.tfs.api.dbo.TfsBaseEntity;
import com.smy.tfs.api.dbo.TicketFlowNodeActionTemplate;
import com.smy.tfs.api.enums.ActionTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 工单流程节点执行人模版表
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Data
public class TicketFlowNodeActionTemplateDto extends TfsBaseEntity implements Serializable {
    private static final long serialVersionUID = -6899318924821671299L;

    private String id;

    private String ticketTemplateId;
    /**
     * 工单流程节点模版ID
     */
    private String ticketFlowNodeTemplateId;

    private String actionName;

    private ActionTypeEnum actionType;

    //{"update_ticket": [{"code": "name","value": "aaa"}]}
    private String ActionValue;

    private String actionConfig;

    public TicketFlowNodeActionTemplate toTicketFlowNodeActionTemplate(TicketFlowNodeActionTemplateDto ticketFlowNodeActionTemplateDto) {
        if (ticketFlowNodeActionTemplateDto == null) {
            return null;
        }
        TicketFlowNodeActionTemplate ticketFlowNodeActionTemplate = new TicketFlowNodeActionTemplate();
        ticketFlowNodeActionTemplate.setId(ticketFlowNodeActionTemplateDto.getId());
        ticketFlowNodeActionTemplate.setTicketTemplateId(ticketFlowNodeActionTemplateDto.getTicketTemplateId());
        ticketFlowNodeActionTemplate.setTicketFlowNodeTemplateId(ticketFlowNodeActionTemplateDto.getTicketFlowNodeTemplateId());
        ticketFlowNodeActionTemplate.setActionName(ticketFlowNodeActionTemplateDto.getActionName());
        ticketFlowNodeActionTemplate.setActionType(ticketFlowNodeActionTemplateDto.getActionType());
        ticketFlowNodeActionTemplate.setActionValue(ticketFlowNodeActionTemplateDto.getActionValue());
        ticketFlowNodeActionTemplate.setActionConfig(ticketFlowNodeActionTemplateDto.getActionConfig());
        return ticketFlowNodeActionTemplate;
    }

    public TicketFlowNodeActionTemplateDto(){

    }
    public TicketFlowNodeActionTemplateDto(TicketFlowNodeActionTemplate ticketFlowNodeActionTemplate) {
        this.id = ticketFlowNodeActionTemplate.getId();
        this.ticketFlowNodeTemplateId = ticketFlowNodeActionTemplate.getTicketFlowNodeTemplateId();
        this.actionName = ticketFlowNodeActionTemplate.getActionName();
        this.actionType = ticketFlowNodeActionTemplate.getActionType();
        this.ActionValue = ticketFlowNodeActionTemplate.getActionValue();
        this.actionConfig = ticketFlowNodeActionTemplate.getActionConfig();
    }
}
