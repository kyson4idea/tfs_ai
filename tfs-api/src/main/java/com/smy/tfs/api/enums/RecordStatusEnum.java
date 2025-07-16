package com.smy.tfs.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public enum RecordStatusEnum implements Serializable {

    /**
     *
     */
    NORMAL("0", "正常"),

    /**
     * 已删除
     */
    DELETED("1","已删除"),

    /**
     *成功
     */
    SUCCESS("success", "成功"),

    /**
     * 失败
     */
    ERROR("error","失败"),


    ;

    private String code;
    private String desc;
}
