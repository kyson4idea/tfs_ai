package com.smy.tfs.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 工单sla模版状态
 */
@Getter
@AllArgsConstructor
public enum TicketSlaTemplateStatusEnum implements Serializable {
    OPEN("OPEN", "启用"),
    STOP("STOP","禁用")
    ;

    private String code;
    private String desc;
}
