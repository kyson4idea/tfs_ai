package com.smy.tfs.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;


@Getter
@AllArgsConstructor
public enum NcsTicketTypeEnum implements Serializable {
    TICKET("TICKET", "工单"),
    CHANNEL_TICKET("CHANNEL_TICKET", "渠道工单"),
    COMPLAINT_TICKET("COMPLAINT_TICKET", "投诉工单"),
    ;
    private final String code;
    private final String desc;


    public static NcsTicketTypeEnum getEnumByCode(String code) {
        for (NcsTicketTypeEnum ntt : values()) {
            if (ntt.code.equals(code)) {
                return ntt;
            }
        }
        return TICKET;
    }
}
