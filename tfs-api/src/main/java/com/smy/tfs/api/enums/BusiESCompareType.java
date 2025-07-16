package com.smy.tfs.api.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 表单项比较类型
 *
 * @author
 */
@AllArgsConstructor
@Getter
public enum BusiESCompareType implements Serializable {
    EQ("EQ", "等于"),
    LIKE("LIKE", "匹配"),
    ;
    private String code;
    private String msg;

    public static BusiESCompareType getEnumByCode(String code) {
        for (BusiESCompareType lt : values()) {
            if (lt.code.equals(code)) {
                return lt;
            }
        }
        throw new RuntimeException("BusiESCompareType [" + code + "] 找不到枚举值");
    }
}
