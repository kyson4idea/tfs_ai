package com.smy.tfs.api.dto.dynamic;

import lombok.Data;
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
@Data
public class TicketFormDataDynamicDto implements Serializable {

    private static final long serialVersionUID = 8243557873065580934L;
    private List<TicketFormItemDataDynamicDto> ticketFormItemDataDtoList;
}
