package com.smy.tfs.api.enums;

/*
 * 执行者类型
 */

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;


@AllArgsConstructor
@Getter
public enum ExecutorTypeEnum implements Serializable {

    APPLY_MEMBER_LIST("APPLY_MEMBER_LIST","指定成员数组"),
    APPLY_GROUP("APPLY_GROUP","用户组"),
    APPLY_LEADER("APPLY_LEADER","申请人上级"),
    APPLY_DEPT_MANAGERS("APPLY_DEPT_MANAGER","申请人部门负责人"),
    APPLY_SELF("APPLY_SELF","申请人本人"),
    APPLY_POINT("APPLY_POINT","申请人指定人"),
    APPLY_DEPT_POINT("APPLY_DEPT_POINT","指定部门"),
    APPLY_EXTERNAL_APPROVER("APPLY_EXTERNAL_APPROVER","外部审批人"),

    CA_LEADER("CA_LEADER","同意时抄送人-上级"),
    CA_DEPT_MANAGERS("CA_DEPT_MANAGER","同意时抄送人-部门负责人"),
    CA_GROUP("CA_GROUP","同意时抄送人-用户组"),
    CA_MEMBER_LIST("CA_MEMBER_LIST","同意时抄送人-指定成员数组"),
    CA_SELF("CA_SELF","同意时抄送人-提交本人"),
    CA_EXTERNAL_APPROVER("CA_EXTERNAL_APPROVER","同意时抄送人-外部审批人"),

    CE_LEADER("CE_LEADER","结束时抄送人-上级"),
    CE_DEPT_MANAGERS("CE_DEPT_MANAGER","结束时抄送人-部门负责人"),
    CE_GROUP("CE_GROUP","结束时抄送人-用户组"),
    CE_MEMBER_LIST("CE_MEMBER_LIST","结束时抄送人-指定成员数组"),
    CE_SELF("CE_SELF","结束时抄送人-提交本人"),
    CE_EXTERNAL_APPROVER("CE_EXTERNAL_APPROVER","结束时抄送人-外部审批人"),

    ;
    private String code;
    private String msg;

    public static ExecutorTypeEnum getEnumByCode(String code) {
        for (ExecutorTypeEnum lt : values()) {
            if (lt.code.equals(code)) {
                return lt;
            }
        }
        throw new RuntimeException("excutorTypeEnum [" + code + "] 找不到枚举值");
    }
}
