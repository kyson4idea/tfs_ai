package com.smy.tfs.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author yss
 * @Description:
 * @CreateDate 2025/4/3 11:31
 * @UpdateDate 2025/4/3 11:31
 */
@Getter
@AllArgsConstructor
public enum ConfigTypeEnums implements Serializable {

    FLOW_NODE("FLOW_NODE", "流程节点"),
    TICKET("TICKET","工单"),
    ;

    private String code;
    private String msg;

    public static ConfigTypeEnums getEnumByCode(String code) {
        for (ConfigTypeEnums lt : values()) {
            if (lt.code.equals(code)) {
                return lt;
            }
        }
        throw new RuntimeException("BizResponseEnums [" + code + "] 找不到枚举值");
    }
}
