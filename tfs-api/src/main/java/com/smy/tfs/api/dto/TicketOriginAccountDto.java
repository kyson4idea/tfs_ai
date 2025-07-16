package com.smy.tfs.api.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Data
@Slf4j
public class TicketOriginAccountDto implements Serializable {
    private String id;

    private String searchWord;

    private String matchResult;
}
