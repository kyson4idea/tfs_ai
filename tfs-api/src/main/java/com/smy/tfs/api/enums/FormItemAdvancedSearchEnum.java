package com.smy.tfs.api.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 高级搜索
 * @author
 */
@AllArgsConstructor
@Getter
public enum FormItemAdvancedSearchEnum implements Serializable {
    TRUE(Boolean.TRUE,"TRUE","支持"),
    FALSE(Boolean.FALSE,"FALSE","不支持"),
            ;
    private Boolean booleanCode;

    @EnumValue
    @JsonValue
    private String code;
    private String msg;

    public static FormItemAdvancedSearchEnum getEnumByCode(String code) {
        for (FormItemAdvancedSearchEnum lt : values()) {
            if (lt.code.equals(code)) {
                return lt;
            }
        }
        throw new RuntimeException("ExecuteTypeEnum [" + code + "] 找不到枚举值");
    }
    public static FormItemAdvancedSearchEnum getEnumByBoolean(Boolean booleanCode) {
        for (FormItemAdvancedSearchEnum lt : values()) {
            if (lt.booleanCode.equals(booleanCode)) {
                return lt;
            }
        }
        throw new RuntimeException("ExecuteTypeEnum [" + booleanCode + "] 找不到枚举值");
    }
}
