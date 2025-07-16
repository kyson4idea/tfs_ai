package com.smy.tfs.api.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 是否系统自带用户
 *
 * @author
 */
@AllArgsConstructor
@Getter
public enum IsSystemAccountEnum implements Serializable {
    TRUE(Boolean.TRUE,"TRUE", "是"),
    FALSE(Boolean.FALSE,"FALSE", "否"),
    ;
    private Boolean booleanCode;

    @EnumValue
    @JsonValue
    private String code;
    private String msg;

    public static IsSystemAccountEnum getEnumByCode(String code) {
        for (IsSystemAccountEnum lt : values()) {
            if (lt.code.equals(code)) {
                return lt;
            }
        }
        throw new RuntimeException("ExecuteTypeEnum [" + code + "] 找不到枚举值");
    }
}
