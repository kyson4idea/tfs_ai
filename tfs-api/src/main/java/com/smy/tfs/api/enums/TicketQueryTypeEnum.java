package com.smy.tfs.api.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 人工审核&自动审核&自动拒绝
 * @author
 */
@AllArgsConstructor
@Getter
public enum TicketQueryTypeEnum implements Serializable {
    CREATED_BY_ME("CREATED_BY_ME","我创建的"),
    HANDLED_BY_ME("CREATED_BY_ME","我处理的"),
            ;
    private String code;
    private String msg;

    public static TicketQueryTypeEnum getEnumByCode(String code) {
        for (TicketQueryTypeEnum lt : values()) {
            if (lt.code.equals(code)) {
                return lt;
            }
        }
        throw new RuntimeException("ExecuteTypeEnum [" + code + "] 找不到枚举值");
    }
}
