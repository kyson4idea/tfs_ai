package com.smy.tfs.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum UserDealTypeEnum implements Serializable {
    MY_DEAL_ALL("MY_DEAL_ALL", "我处理的工单_全部"),
    MY_DEAL_WAITING_HANDLE("MY_DEAL_WAITING_HANDLE", "我处理的工单_待处理"),
    MY_DEAL_HANDLED("MY_DEAL_HANDLED", "我处理的工单_已处理"),
    MY_DEAL_HAS_CC("MY_DEAL_HAS_CC", "我处理的工单_抄送我"),
    MY_APPLY_ALL("MY_APPLY_ALL","我发起的工单_全部"),
    MY_APPLY_APPLYING("MY_APPLY_APPLYING","我发起的工单_处理中"),
    MY_APPLY_PASS("MY_APPLY_PASS","我发起的工单_已通过"),
    MY_APPLY_REJECT("MY_APPLY_REJECT","我发起的工单_已拒绝"),
    ALL_ALL("ALL_ALL","全部工单_全部"),
    ALL_APPLYING("ALL_APPLYING","全部工单_处理中"),
    ALL_APPLYEND("ALL_APPLYEND","全部工单_已处理"),
    ;

    private String code;
    private String msg;

    public static UserDealTypeEnum getEnumByCode(String code) {
        for (UserDealTypeEnum userDealTypeEnum : values()) {
            if (userDealTypeEnum.code.equals(code)) {
                return userDealTypeEnum;
            }
        }
        throw new RuntimeException("ExecuteTypeEnum [" + code + "] 找不到枚举值");
    }
}
