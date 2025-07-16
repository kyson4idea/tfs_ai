package com.smy.tfs.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum TicketAnalysisDataTypeEnum implements Serializable {
    WEEK("WEEK", "周"),
    DAY("DAY","天"),
    ;

    private String code;
    private String desc;
}
