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
public enum BusiCompareType implements Serializable {
    CONTAIN_ANY("CONTAIN_ANY", "包含任意"),
    NOT_CONTAIN_ANY("NOT_CONTAIN_ANY", "不包含任意"),
    BELONG_TO("BELONG_TO", "属于"),
    NOT_BELONG_TO("NOT_BELONG_TO", "不属于"),
    ;
    private String code;
    private String msg;

    public static BusiCompareType getEnumByCode(String code) {
        for (BusiCompareType lt : values()) {
            if (lt.code.equals(code)) {
                return lt;
            }
        }
        throw new RuntimeException("FormItemCompareType [" + code + "] 找不到枚举值");
    }
}
