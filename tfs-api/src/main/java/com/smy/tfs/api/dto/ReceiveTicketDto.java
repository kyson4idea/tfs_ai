package com.smy.tfs.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ReceiveTicketDto implements Serializable {

    private static final long serialVersionUID = -7549081050490922119L;
    private String id;
    private List<String> idList;
}
