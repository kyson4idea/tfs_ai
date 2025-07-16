package com.smy.tfs.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;


@Getter
@AllArgsConstructor
public enum ActionTypeEnum implements Serializable {
    BUILD_GROUP("BUILD_GROUP", "建群"),
    COMMENT("COMMENT", "评论"),
    ADD_PRE_NODE("ADD_PRE_NODE", "加上签"),
    ADD_NEXT_NODE("ADD_NEXT_NODE", "加下签"),
    APPROVE_FINISH("APPROVE_FINISH", "审批关单"),
    APPROVE_REJECT("APPROVE_REJECT", "审批拒绝"),
    APPROVE_PASS("APPROVE_PASS", "审批通过"),
    UPDATE_FORM("UPDATE_FORM", "修改表单"),
    UPDATE_TICKET("UPDATE_TICKET", "修改工单"),
    BACK_NODE("BACK_NODE", "回退节点"),
    ;
    @EnumValue
    @JsonValue
    private String code;
    private String desc;

    public static ActionTypeEnum getEnumByCode(String code) {
        for (ActionTypeEnum lt : values()) {
            if (lt.code.equals(code)) {
                return lt;
            }
        }
        throw new RuntimeException("ActionTypeEnum [" + code + "] 找不到枚举值");
    }
}
