package com.smy.tfs.api.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 表单项是否必填
 *
 * @author
 */
@AllArgsConstructor
@Getter
public enum FormItemRequiredEnum implements Serializable {
    TRUE(Boolean.TRUE,"TRUE", "必填"),
    FALSE(Boolean.FALSE,"FALSE", "不必填"),
    ;
    private Boolean booleanCode;

    @EnumValue
    @JsonValue
    private String code;
    private String msg;

    public static FormItemRequiredEnum getEnumByCode(String code) {
        for (FormItemRequiredEnum lt : values()) {
            if (lt.code.equals(code)) {
                return lt;
            }
        }
        throw new RuntimeException("ExecuteTypeEnum [" + code + "] 找不到枚举值");
    }

    public static FormItemRequiredEnum getEnumByBoolean(Boolean booleanCode) {
        //String thisCode = booleanCode ? "true" : "false";
        for (FormItemRequiredEnum lt : values()) {
            if (lt.booleanCode.equals(booleanCode)) {
                return lt;
            }
        }
        throw new RuntimeException("ExecuteTypeEnum [" + booleanCode + "] 找不到枚举值");
    }
}
