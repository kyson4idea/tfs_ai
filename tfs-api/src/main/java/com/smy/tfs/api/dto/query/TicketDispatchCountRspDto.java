package com.smy.tfs.api.dto.query;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TicketDispatchCountRspDto implements Serializable {
    private static final long serialVersionUID = 6943427197542900321L;

    // 待分配的工单数
    private List<TicketDispatchCountDto> ticketDispatchCountDtoList;



}
