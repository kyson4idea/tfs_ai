package com.smy.tfs.api.enums;

/*
 * 执行者类型
 */

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 部门层级枚举
 */

@AllArgsConstructor
@Getter
public enum DeptLevelEnum implements Serializable {

    DEPT("DEPT","所属部门"),
    ONE_LEVEL_DEPT("ONE_LEVEL_DEPT","所属部门+1级"),
    TWO_LEVEL_DEPT("TWO_LEVEL_DEPT","所属部门+2级"),
    THREE_LEVEL_DEPT("THREE_LEVEL_DEPT","所属部门+3级"),


    ;
    private String code;
    private String msg;

    public static DeptLevelEnum getEnumByCode(String code) {
        for (DeptLevelEnum lt : values()) {
            if (lt.code.equals(code)) {
                return lt;
            }
        }
        throw new RuntimeException("deptLevelEnum [" + code + "] 找不到枚举值");
    }
}
