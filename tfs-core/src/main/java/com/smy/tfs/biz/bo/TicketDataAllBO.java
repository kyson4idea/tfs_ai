package com.smy.tfs.biz.bo;

import com.smy.tfs.api.dbo.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class TicketDataAllBO {
    TicketData ticketData;

    TicketFlowData ticketFlowData;
    List<TicketFlowNodeData> ticketFlowNodeDataList;
    List<TicketFlowNodeExecutorData> ticketFlowNodeExecutorDataList;
    List<TicketFlowNodeActionData> ticketFlowNodeActionDataList;
    List<TicketFlowEventData> ticketFlowEventDataList;

    TicketFormData ticketFormData;
    List<TicketFormItemData> ticketFormItemDataList;
}
