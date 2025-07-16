package com.smy.tfs.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author z01140
 * @Package: com.smy.tfs.api.enums
 * @Description:
 *     /**
 *      * 0:初始化
 *      * 1:待执行
 *      * 2:执行中，异步待确认
 *      * 3:执行失败，待执行
 *      * 20:执行成功（终态）
 *      * 30:执行失败（终态）
 * @CreateDate 2024/4/19 11:07
 * @UpdateDate 2024/4/19 11:07
 */
@Getter
@AllArgsConstructor
public enum YESNOEnum implements Serializable {
    YES("YES", "是"),
    NO("NO","否");

    @EnumValue
    @JsonValue
    private String code;
    private String desc;

    public static YESNOEnum getEnumByCode(String code) {
        for (YESNOEnum lt : values()) {
            if (lt.code.equals(code)) {
                return lt;
            }
        }
        throw new RuntimeException("YESNOEnum [" + code + "] 找不到枚举值");
    }
}
