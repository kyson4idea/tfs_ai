package com.smy.tfs.api.dto;

import com.smy.tfs.api.dbo.TicketFlowNodeApproveDetail;
import com.smy.tfs.api.dbo.TicketFlowNodeData;
import com.smy.tfs.api.dbo.TicketFlowNodeExecutorData;
import lombok.Data;

import java.util.List;

@Data
public class TicketFlowDataGroupDto {
    private List<TicketFlowNodeData> ticketFlowNodeDataList;
    private List<TicketFlowNodeExecutorData> ticketFlowNodeExecutorDataList;
    private List<TicketFlowNodeApproveDetail> ticketFlowNodeApproveDetailList;
}
