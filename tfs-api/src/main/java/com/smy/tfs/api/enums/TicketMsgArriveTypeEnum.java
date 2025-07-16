package com.smy.tfs.api.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 消息触达方式
 * @author
 */
@AllArgsConstructor
@Getter
public enum TicketMsgArriveTypeEnum implements Serializable {
    //抄送/审批 环节会发送企微消息
    WECOM("WECOM","企微"),
    DINGTALK("DINGTALK","钉钉"),
    NULL("NULL","不启用"),
            ;

    @EnumValue
    @JsonValue
    private String code;
    private String msg;

    public static TicketMsgArriveTypeEnum getEnumByCode(String code) {
        for (TicketMsgArriveTypeEnum lt : values()) {
            if (lt.code.equals(code)) {
                return lt;
            }
        }
        throw new RuntimeException("TicketMsgArriveTypeEnum [" + code + "] 找不到枚举值");
    }

}
