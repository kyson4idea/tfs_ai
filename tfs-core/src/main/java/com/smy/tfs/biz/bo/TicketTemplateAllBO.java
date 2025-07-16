package com.smy.tfs.biz.bo;

import com.smy.tfs.api.dbo.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TicketTemplateAllBO {
    private TicketApp ticketApp;
    private TicketTemplate ticketTemplate;

    private TicketFlowTemplate  ticketFlowTemplate;
    private List<TicketFlowNodeTemplate> ticketFlowNodeTemplateList;
    private List<TicketFlowNodeActionTemplate> ticketFlowNodeActionTemplateList;
    private List<TicketFlowNodeExecutorTemplate> ticketFlowNodeExecutorTemplateList;
    private List<TicketFlowEventTemplate> ticketFlowEventTemplateList;
    private List<TicketFlowNodeRuleTemplate> ticketFlowNodeRuleTemplateList;

    private TicketFormTemplate ticketFormTemplate;
    private List<TicketFormItemTemplate> ticketFormItemTemplateList;
}
