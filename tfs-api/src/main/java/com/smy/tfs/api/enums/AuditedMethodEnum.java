package com.smy.tfs.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author z01140
 * @Package: com.smy.tfs.api.enums
 * @Description: 节点审批方式
 * @CreateDate 2024/4/20 11:26
 * @UpdateDate 2024/4/20 11:26
 */
@Getter
@AllArgsConstructor
public enum AuditedMethodEnum implements Serializable {

    AND("AND", "会签"),
    OR("OR", "或签"),
    ;

    private String code;
    private String desc;

    public static AuditedMethodEnum getEnumByCode(String code) {
        for (AuditedMethodEnum lt : values()) {
            if (lt.code.equals(code)) {
                return lt;
            }
        }
        throw new RuntimeException("ExecuteTypeEnum [" + code + "] 找不到枚举值");
    }
}
