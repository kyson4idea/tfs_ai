package com.smy.tfs.api.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 *
 * @author
 */
@AllArgsConstructor
@Getter
public enum TicketMsgBuildTypeEnum implements Serializable {
    APPLY_CREATE("APPLY_CREATE","自动创建"),
    AUDITOR_CREATE("AUDITOR_CREATE","手动创建"),
    CREATE_NONE("CREATE_NONE","不能创建"),
            ;

    @EnumValue
    @JsonValue
    private String code;
    private String msg;

    public static TicketMsgBuildTypeEnum getEnumByCode(String code) {
        for (TicketMsgBuildTypeEnum lt : values()) {
            if (lt.code.equals(code)) {
                return lt;
            }
        }
        throw new RuntimeException("TicketMsgBuildTypeEnum [" + code + "] 找不到枚举值");
    }
}
