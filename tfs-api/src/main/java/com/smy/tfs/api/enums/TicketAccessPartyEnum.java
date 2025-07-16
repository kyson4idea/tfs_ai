package com.smy.tfs.api.enums;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 接入入口
 */
@Getter
@AllArgsConstructor
public enum TicketAccessPartyEnum implements Serializable {
    JSSDK("JSSDK","JSSDK接入"),
    API("API","API接入"),
    PC("PC","PC接入"),
    QW("QW","企微工作台接入"),
    ;

    @EnumValue
    @JsonValue
    private String code;
    private String msg;

    public static TicketAccessPartyEnum getEnumByCode(String code) {
        for (TicketAccessPartyEnum lt : values()) {
            if (lt.code.equals(code)) {
                return lt;
            }
        }
        throw new RuntimeException("TicketAccessPartyEnum [" + code + "] 找不到枚举值");
    }
}
