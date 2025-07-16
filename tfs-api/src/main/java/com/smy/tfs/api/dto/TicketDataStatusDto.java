package com.smy.tfs.api.dto;

import com.smy.tfs.api.enums.TicketDataStatusEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class TicketDataStatusDto implements Serializable {

    private static final long serialVersionUID = 8218417709618523690L;

    /**
     * 工单ID
     */
    private String id;

    /**
     * 业务号，保障只有一个审批中的工单
     */
    private String ticketBusinessKey;

    private TicketDataStatusEnum ticketStatus;

}
