package com.smy.tfs.api.enums;

/*
 * 默认审批人类型
 */

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;


@AllArgsConstructor
@Getter
public enum DefaultExecutorTypeEnum implements Serializable {


    DEFAULT_SELF("DEFAULT_SELF","申请人本人"),
    DEFAULT_BIZ_MANAGER("DEFAULT_BIZ_MANAGER","业务管理员"),
    DEFAULT_MEMBER_LIST("DEFAULT_MEMBER_LIST","指定成员"),

    ;
    private String code;
    private String msg;

    public static DefaultExecutorTypeEnum getEnumByCode(String code) {
        for (DefaultExecutorTypeEnum lt : values()) {
            if (lt.code.equals(code)) {
                return lt;
            }
        }
        throw new RuntimeException("excutorTypeEnum [" + code + "] 找不到枚举值");
    }
}
