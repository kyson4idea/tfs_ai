package com.smy.tfs.api.enums;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/*   * 草稿中     * 审批中     * 审批结束 */
@Getter
@AllArgsConstructor
public enum TicketDataStatusEnum implements Serializable {
    INIT("INIT","初始化"),
    DRAFT("DRAFT","草稿中"),
    APPLYING("APPLYING","审批中"),
    APPLY_END("APPLY_END","审批结束"),
    REJECT("REJECT","审批驳回"),
    WITHDRAW("WITHDRAW","撤回"),
    BACK("BACK","退回"),
    ;

    @EnumValue
    @JsonValue
    private String code;
    private String msg;

    public static TicketDataStatusEnum getEnumByCode(String code) {
        for (TicketDataStatusEnum lt : values()) {
            if (lt.code.equals(code)) {
                return lt;
            }
        }
        throw new RuntimeException("TicketDataStatusEnum [" + code + "] 找不到枚举值");
    }
}
