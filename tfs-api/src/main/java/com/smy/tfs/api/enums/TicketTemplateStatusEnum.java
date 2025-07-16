package com.smy.tfs.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.compress.utils.Lists;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Getter
public enum TicketTemplateStatusEnum implements Serializable {

    /**
     *初始化
     */
    INIT("INIT","初始化", Arrays.asList("ENABLE", "PAUSE", "CANCEL")),
    /**
     *启用中
     */
    ENABLE("ENABLE", "启用中", Arrays.asList("PAUSE", "CANCEL")),
    /**
     *暂停
     */
    PAUSE("PAUSE", "暂停", Arrays.asList("ENABLE", "CANCEL")),
    /**
     * 作废
     */
    CANCEL("CANCEL","作废", Lists.newArrayList());

    @EnumValue
    @JsonValue
    private String code;

    private String desc;

    private List<String> canChangeStatus;

    public static TicketTemplateStatusEnum getEnumByCode(String code) {
        for (TicketTemplateStatusEnum ttse : values()) {
            if (ttse.code.equals(code)) {
                return ttse;
            }
        }
        throw new RuntimeException("ExecuteTypeEnum [" + code + "] 找不到枚举值");
    }

}
