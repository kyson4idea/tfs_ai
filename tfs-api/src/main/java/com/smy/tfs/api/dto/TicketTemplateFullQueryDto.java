package com.smy.tfs.api.dto;

import com.smy.tfs.api.dbo.*;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class TicketTemplateFullQueryDto implements Serializable {

    private static final long serialVersionUID = 3499685518015574293L;
    /**
     * 工单模版
     */
    private TicketTemplate ticketTemplate;

    private TicketFormTemplate ticketFormTemplate;

    private List<TicketFormItemTemplate> ticketFormItemTemplateList;

    private List<TicketFormItemIdColMapping> ticketFormItemIdColMappingList;

    private Map<String,String> idColMappingMap;

    private TicketFlowTemplate ticketFlowTemplate;

    private List<TicketFlowNodeTemplate> ticketFlowNodeTemplateList;

    private List<TicketFlowEventTemplate> ticketFlowEventTemplateList;

    private List<TicketFlowNodeRuleTemplate> ticketFlowNodeRuleTemplateList;

    private List<TicketFlowNodeActionTemplate> ticketFlowNodeActionTemplateList;

    private List<TicketFlowNodeExecutorTemplate> ticketFlowNodeExecutorTemplateList;

    private TicketSlaTemplate ticketSlaTemplate;

    private List<TicketSlaConfigTemplate> ticketSlaConfigTemplateList;

    //待删除的ID
    private String delTicketTemplateId;

    private String delTicketFormTemplateId;

    private List<String> delTicketFormItemTemplateIdList;

    private List<String> delTicketFormItemIdColMappingIdList;

    private String delTicketFlowTemplateId;

    private List<String> delTicketFlowNodeTemplateIdList;

    private List<String> delTicketFlowEventTemplateIdList;

    private List<String> delTicketFlowNodeRuleTemplateIdList;

    private List<String> delTicketFlowNodeExecutorTemplateIdList;

    private List<String> delTicketFlowNodeActionTemplateIdList;

    private String delTicketSlaTemplateId;

    private List<String> delTicketSlaConfigTemplateIdList;

    private Integer version;

    private String applyUser;



}
