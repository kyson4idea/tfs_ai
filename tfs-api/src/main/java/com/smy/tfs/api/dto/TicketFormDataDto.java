package com.smy.tfs.api.dto;

import com.smy.tfs.api.dbo.TicketFormData;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 工单单数据对象 ticket_form_data
 *
 * @author zzd
 * @date 2024-04-11
 */
@Getter @Setter
public class TicketFormDataDto  implements Serializable{
    private static final long serialVersionUID = -4653670737735105765L;
    /** * $column.columnComment */
    private String id;

    /**     * $column.columnComment     */
    private String ticketDataId;

    /**     * 模版ID     */
    private String templateId;

    private List<TicketFormItemDataDto> ticketFormItemDataDtoList;

    public TicketFormDataDto() {
    }
    public TicketFormDataDto(TicketFormData ticketFormData) {
        this.id = ticketFormData.getId();
        this.ticketDataId = ticketFormData.getTicketDataId();
        this.templateId = ticketFormData.getTemplateId();
    }

}
