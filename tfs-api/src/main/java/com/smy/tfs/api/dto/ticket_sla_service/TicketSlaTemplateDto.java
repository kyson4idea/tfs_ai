package com.smy.tfs.api.dto.ticket_sla_service;

import com.smy.tfs.api.dbo.TicketSlaTemplate;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 工单sla模版对象Dto ticket_sla_template
 *
 * @author zzd
 * @date 2024-04-11
 */
@Data
public class TicketSlaTemplateDto implements Serializable {

    private static final long serialVersionUID = -1022987157722980917L;
    /**
     * $column.columnComment
     */
    private String id;

    /**
     * $column.columnComment
     */
    private String ticketTemplateId;

    /**
     * $column.columnComment
     */
    private String deleteTime;


    private List<TicketSlaConfigTemplateDto> ticketSlaConfigTemplateDtoList;

    public TicketSlaTemplateDto() {}

    public TicketSlaTemplateDto(TicketSlaTemplate ticketSlaTemplate) {
         this.id = ticketSlaTemplate.getId();
         this.ticketTemplateId = ticketSlaTemplate.getTicketTemplateId();

    }

    public TicketSlaTemplate toTicketSlaTemplate (TicketSlaTemplateDto ticketSlaTemplateDto) {
        TicketSlaTemplate ticketSlaTemplate = new TicketSlaTemplate();
        ticketSlaTemplate.setId(ticketSlaTemplateDto.getId());
        ticketSlaTemplate.setTicketTemplateId(ticketSlaTemplateDto.getTicketTemplateId());
        return ticketSlaTemplate;
    }



}
