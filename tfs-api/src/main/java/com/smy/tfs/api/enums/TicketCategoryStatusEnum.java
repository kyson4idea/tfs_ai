package com.smy.tfs.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 工单分类状态
 */
@Getter
@AllArgsConstructor
public enum TicketCategoryStatusEnum implements Serializable {
    OPEN("OPEN", "启用"),
    STOP("STOP","停用"),
    DEL("DEL","删除")
    ;

    private String code;
    private String desc;
}
