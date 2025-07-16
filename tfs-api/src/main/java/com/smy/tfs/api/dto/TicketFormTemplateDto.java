package com.smy.tfs.api.dto;

import com.smy.tfs.api.dbo.TicketFormTemplate;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 工单单模版对象 ticket_form_template
 *
 * @author zzd
 * @date 2024-04-11
 */
@Data
public class TicketFormTemplateDto implements Serializable {

    private static final long serialVersionUID = -1737080427925976540L;
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


    private List<TicketFormItemTemplateDto> ticketFormItemTemplateDtoList;

    public TicketFormTemplateDto () {}

    public TicketFormTemplateDto (TicketFormTemplate ticketFormTemplate) {
         this.id = ticketFormTemplate.getId();
         this.ticketTemplateId = ticketFormTemplate.getTicketTemplateId();

    }

}
