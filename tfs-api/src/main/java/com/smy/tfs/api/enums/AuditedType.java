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
public enum AuditedType implements Serializable {
    BY_USER("BY_USER","人工审核"),
    AUTO_PASS("AUTO_PASS","自动通过"),
    AUTO_REJECT("AUTO_REJECT","自动拒绝")
            ;
    private String code;
    private String msg;

    public static AuditedType getEnumByCode(String code) {
        for (AuditedType lt : values()) {
            if (lt.code.equals(code)) {
                return lt;
            }
        }
        throw new RuntimeException("ExecuteTypeEnum [" + code + "] 找不到枚举值");
    }
}
