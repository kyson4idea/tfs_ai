package com.smy.tfs.api.dto.ticket_act_service;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DelTicketsParams implements Serializable {
    private static final long serialVersionUID = 3369275654301730880L;

    private List<String> delTicketIds;
}
