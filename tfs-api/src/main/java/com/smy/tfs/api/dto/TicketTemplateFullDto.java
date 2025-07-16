package com.smy.tfs.api.dto;

import com.smy.tfs.api.dbo.*;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TicketTemplateFullDto implements Serializable {

    private static final long serialVersionUID = 7155810038374681723L;
    /**
     * 工单模版
     */
    private TicketTemplate ticketTemplate;

    private TicketFormTemplate ticketFormTemplate;

    private List<TicketFormItemTemplate> ticketFormItemTemplateList;

    private List<TicketFormItemIdColMapping> ticketFormItemIdColMappingList;

    private TicketFlowTemplate ticketFlowTemplate;

    private List<TicketFlowNodeTemplate> ticketFlowNodeTemplateList;

    private List<TicketFlowEventTemplate> ticketFlowEventTemplateList;

    private List<TicketFlowNodeRuleTemplate> ticketFlowNodeRuleTemplateList;

    private List<TicketFlowNodeExecutorTemplate> ticketFlowNodeExecutorTemplateList;

    private List<TicketFlowNodeActionTemplate> ticketFlowNodeActionTemplateList;

    private TicketSlaTemplate ticketSlaTemplate;

    private List<TicketSlaConfigTemplate> ticketSlaConfigTemplateList;
}
