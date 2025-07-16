package com.smy.tfs.api.enums;

/*
 * 执行者类型
 */

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;


/*
input:单行文本	textarea:多行文本 inputNumber:数字inputMoney:金额select:单选selectMultiple:多选time日期 timeSpan:日期区间picture:图片file:附件phone:电话group:明细
* */
@AllArgsConstructor
@Getter
public enum FormItemTypeEnum implements Serializable {
    INPUT("INPUT", "单行文本"),
    TEXTAREA("TEXTAREA", "多行文本"),
    INPUTNUMBER("INPUTNUMBER", "数字"),
    SELECT("SELECT", "单选"),
    SELECTMULTIPLE("SELECTMULTIPLE", "多选"),
    TIME("TIME", "日期"),
    TIMESPAN("TIMESPAN", "日期区间"),
    PICTURE("PICTURE", "图片"),
    FILE("FILE", "附件"),
    TIP("TIP", "说明"),
    GROUP("GROUP", "明细"),
    PERSON("PERSON", "人员"),
    DEPT("DEPT", "部门"),
    CASCADER("CASCADER","级联组件"),
    LINK("LINK", "链接"),

    //目前值只持API传
    TABLE("TABLE", "表格"),
    TAGVIEWS("TAGVIEWS", "标签组"),
    PANEL("PANEL", "模块"),
    ;
    private String code;
    private String msg;

    public static FormItemTypeEnum getEnumByCode(String code) {
        for (FormItemTypeEnum lt : values()) {
            if (lt.code.equals(code)) {
                return lt;
            }
        }
        throw new RuntimeException("FormItemTypeEnum [" + code + "] 找不到枚举值");
    }
    public static FormItemTypeEnum getEnumByCodeOrDefault(String code, FormItemTypeEnum defaultEnum) {
        for (FormItemTypeEnum lt : values()) {
            if (lt.code.equals(code)) {
                return lt;
            }
        }
        return defaultEnum;
    }
}
