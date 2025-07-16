package com.smy.tfs.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum BusiQueryUserDealTypeEnum implements Serializable {
    ALL_ALL("ALL_ALL","全部工单_全部"),
    MY_DEPT_APPLY_WAITING_DISPATCH("MY_DEPT_APPLY_WAITING_DISPATCH","待分配工单数据"),
    MY_APPLY_ALL("MY_APPLY_ALL","我发起的工单_全部"),
    MY_DEAL_WAITING_HANDLE("MY_DEAL_WAITING_HANDLE", "我处理的工单_待处理"),
    MY_DEAL_FINISH("MY_DEAL_FINISH","我处理的工单_已完结"),
    MY_APPLY_WITHDRAW("MY_APPLY_WITHDRAW","我发起的工单_已撤销"),
    BACK_MY("BACK_MY","退回到我的工单"),
    ALL_APPLYING("ALL_APPLYING","全部工单_处理中"),
    ;

    private String code;
    private String msg;

    public static BusiQueryUserDealTypeEnum getEnumByCode(String code) {
        for (BusiQueryUserDealTypeEnum userDealTypeEnum : values()) {
            if (userDealTypeEnum.code.equals(code)) {
                return userDealTypeEnum;
            }
        }
        throw new RuntimeException("ExecuteTypeEnum [" + code + "] 找不到枚举值");
    }
}
