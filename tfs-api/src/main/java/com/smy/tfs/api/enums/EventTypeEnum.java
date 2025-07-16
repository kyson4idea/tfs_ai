package com.smy.tfs.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author z01140
 * @Package: com.smy.tfs.api.enums
 * @Description:
 * @CreateDate 2024/4/22 17:34
 * @UpdateDate 2024/4/22 17:34
 */
@Getter
@AllArgsConstructor
public enum EventTypeEnum implements Serializable {

    HTTP_SERVICE("HTTP_SERVICE", "http"),
    HTTPS_SERVICE("HTTPS_SERVICE", "https"),
    DUBBO_SERVICE("DUBBO_SERVICE", "dubbo"),
    ;

    private String code;
    private String desc;

    public static EventTypeEnum getEnumByCode(String code) {
        for (EventTypeEnum lt : values()) {
            if (lt.code.equals(code)) {
                return lt;
            }
        }
        throw new RuntimeException("ExecuteTypeEnum [" + code + "] 找不到枚举值");
    }
}
