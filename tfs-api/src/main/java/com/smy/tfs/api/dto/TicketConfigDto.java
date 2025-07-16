package com.smy.tfs.api.dto;

import com.smy.tfs.api.dbo.TicketConfig;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter @Setter
public class TicketConfigDto implements Serializable {

    private static final long serialVersionUID = -5600530855381176172L;

    private String id;

    //优先级 ticket_flow_node_data_id>ticket_data_id>ticket_tempalte_id
    private String ticketTemplateId;

    //{"":[""]}
    private String ticketConfigStr;


    public TicketConfig ToTicketConfig() {
        TicketConfig ticketConfig = new TicketConfig();
        ticketConfig.setId(this.id);
        ticketConfig.setTicketTemplateId(this.ticketTemplateId);
        ticketConfig.setTicketConfigStr(this.ticketConfigStr);
        return ticketConfig;
    }
}
