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
public enum FormItemCompareType implements Serializable {
    EQUAL("EQUAL", "等于"),
    NOT_EQUAL("NOT_EQUAL", "不等于"),
    GREATER("GREATER", "大于"),
    LESS("LESS", "小于"),
    GREATER_EQUAL("GREATER_EQUAL", "大于等于"),
    LESS_EQUAL("LESS_EQUAL", "小于等于"),
    CHOOSED("CHOOSED", "选中"),
    NO_CHOOSED("NO_CHOOSED", "未选中"),
    CASCADER_CHOOSED("CASCADER_CHOOSED", "级联选中"),
    NO_CASCADER_CHOOSED("NO_CASCADER_CHOOSED", "未级联选中"),
    CONTAIN_ALL("CONTAIN_ALL", "包含所有"),
    CONTAIN_ANY("CONTAIN_ANY", "包含任意"),
    INCLUDE_ALL("INCLUDE_ALL", "被包含所有"),
    INCLUDE_ANY("INCLUDE_ANY", "被包含任意"),

    //新数据
    INPUT_NOT_CONTAIN("INPUT_NOT_CONTAIN", "输入框不包含"),
    INPUT_CONTAIN("INPUT_CONTAIN", "输入框包含"),
    INPUT_CONTAIN_ANY("INPUT_CONTAIN_ANY", "输入框包含任意"),

    // 选项型：单选（SELECT）
    SELECT_CONTAIN_ANY("SELECT_CONTAIN_ANY", "包含任意"),
    SELECT_NOT_CONTAIN_ALL("SELECT_NOT_CONTAIN_ALL", "不包含所有"),

    // 选项型：级联（CASCADER）
    CASCADER_CONTAIN_ANY("CASCADER_CONTAIN_ANY", "包含任意"),
    CASCADER_NOT_CONTAIN_ALL("CASCADER_NOT_CONTAIN_ALL", "不包含所有"),

    // 选项型：多选（SELECTMULTIPLE）
    //SELECTMULTIPLE_CONTAIN_ANY("SELECTMULTIPLE_CONTAIN_ANY", "包含任意"),
    //SELECTMULTIPLE_CONTAIN_ALL("SELECTMULTIPLE_CONTAIN_ALL", "包含所有"),
    SELECTMULTIPLE_NOT_CONTAIN_ALL("SELECTMULTIPLE_NOT_CONTAIN_ALL", "不包含所有");
    ;
    private String code;
    private String msg;

    public static FormItemCompareType getEnumByCode(String code) {
        for (FormItemCompareType lt : values()) {
            if (lt.code.equals(code)) {
                return lt;
            }
        }
        throw new RuntimeException("FormItemCompareType [" + code + "] 找不到枚举值");
    }
}
