package com.smy.tfs.api.dto;

import com.smy.tfs.api.dbo.TicketFlowData;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class TicketFlowDataDto implements Serializable {

    private static final long serialVersionUID = -6697946999175572021L;
    /**
     * $column.columnComment
     */
    private String id;

    /**
     * 工单ID
     */
    private String ticketDataId;

    /**
     * 流程模版ID
     */
    private String templateId;

    /**
     * 删除时间
     */
    private Date deleteTime;

    private List<TicketFlowNodeDataDto> ticketFlowNodeDataDtoList;

    public TicketFlowDataDto() {
    }

    public TicketFlowDataDto(TicketFlowData ticketFlowData) {
        this.id = ticketFlowData.getId();
        this.ticketDataId = ticketFlowData.getTicketDataId();
        this.templateId = ticketFlowData.getTemplateId();
//        this.startCc = ticketFlowData.getStartCc();
//        this.endCc = ticketFlowData.getEndCc();
    }
}
