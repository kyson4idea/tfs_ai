package com.smy.tfs.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum CategoryStatusEnum implements Serializable {
    OPEN("OPEN", "启用"),
    STOP("STOP","停用"),
    ;

    private String code;
    private String desc;
}
