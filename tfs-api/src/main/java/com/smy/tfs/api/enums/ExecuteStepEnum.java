package com.smy.tfs.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author z01140
 * @Package: com.smy.tfs.api.enums
 * @Description: 动作执行时机枚举 before:执行前 doing:执行中  done:执行后
 * @CreateDate 2024/4/19 10:51
 * @UpdateDate 2024/4/19 10:51
 */
@Getter
@AllArgsConstructor
public enum ExecuteStepEnum implements Serializable {

    BEFORE("BEFORE", "执行前"),
    BEFORE_PASS("BEFORE_PASS", "通过前执行"),
    BEFORE_REJECT("BEFORE_REJECT", "拒绝前执行"),
    BEFORE_COMMENT("BEFORE_COMMENT","评论前执行"),
    BEFORE_BACK("BEFORE_BACK","退回前执行"),
    BEFORE_UPDATE("BEFORE_UPDATE","修改前执行"),
    BEFORE_DISPATCH("BEFORE_DISPATCH","派单前执行"),
    DONE_PASS("DONE_PASS","通过后执行"),
    DONE_REJECT("DONE_REJECT","拒绝后执行"),
    DONE_UPDATE("DONE_UPDATE","修改后执行"),
    FINISH("FINISH","完成后执行"),
    DONE_ADD_NODE("DONE_ADD_NODE","加签后"),
    ;

    private String code;
    private String desc;

    public static ExecuteStepEnum getEnumByCode(String code) {
        for (ExecuteStepEnum lt : values()) {
            if (lt.code.equals(code)) {
                return lt;
            }
        }
        throw new RuntimeException("ExecuteTypeEnum [" + code + "] 找不到枚举值");
    }
}
