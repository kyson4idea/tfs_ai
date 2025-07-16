package com.smy.tfs.api.dto;

import com.smy.tfs.api.dbo.TfsBaseEntity;
import com.smy.tfs.api.dbo.TicketFlowNodeActionData;
import com.smy.tfs.api.enums.ActionTypeEnum;
import lombok.Data;

import java.io.Serializable;


@Data
public class TicketFlowNodeActionDataDto extends TfsBaseEntity implements Serializable {
    private static final long serialVersionUID = -889565911383801298L;

    private String id;

    private String templateId;

    private String ticketDataId;

    private String ticketFlowNodeDataId;

    private String actionName;

    private ActionTypeEnum actionType;

    //{approve:pass,goto:123}
    private String actionValue;

    private String actionConfig;

    public TicketFlowNodeActionDataDto() {

    }
    public TicketFlowNodeActionDataDto(TicketFlowNodeActionData ticketFlowNodeActionData) {
        this.id = ticketFlowNodeActionData.getId();
        this.templateId = ticketFlowNodeActionData.getTemplateId();
        this.ticketDataId = ticketFlowNodeActionData.getTicketDataId();
        this.ticketFlowNodeDataId = ticketFlowNodeActionData.getTicketFlowNodeDataId();
        this.actionName = ticketFlowNodeActionData.getActionName();
        this.actionType = ticketFlowNodeActionData.getActionType();
        this.actionValue = ticketFlowNodeActionData.getActionValue();
        this.actionConfig = ticketFlowNodeActionData.getActionConfig();
    }
    public TicketFlowNodeActionData toTicketFlowNodeActionData(TicketFlowNodeActionDataDto ticketFlowNodeActionDataDto) {
        if (ticketFlowNodeActionDataDto == null) {
            return null;
        }
        TicketFlowNodeActionData ticketFlowNodeActionData = new TicketFlowNodeActionData();
        ticketFlowNodeActionData.setId(ticketFlowNodeActionDataDto.getId());
        ticketFlowNodeActionData.setTemplateId(ticketFlowNodeActionDataDto.getTemplateId());
        ticketFlowNodeActionData.setTicketDataId(ticketFlowNodeActionDataDto.getTicketDataId());
        ticketFlowNodeActionData.setTicketFlowNodeDataId(ticketFlowNodeActionDataDto.getTicketFlowNodeDataId());
        ticketFlowNodeActionData.setActionName(ticketFlowNodeActionDataDto.getActionName());
        ticketFlowNodeActionData.setActionType(ticketFlowNodeActionDataDto.getActionType());
        ticketFlowNodeActionData.setActionValue(ticketFlowNodeActionDataDto.getActionValue());
        ticketFlowNodeActionData.setActionConfig(ticketFlowNodeActionDataDto.getActionConfig());
        return ticketFlowNodeActionData;
    }
}
