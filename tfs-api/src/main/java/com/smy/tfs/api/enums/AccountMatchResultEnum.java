package com.smy.tfs.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountMatchResultEnum {

    INIT("INIT", "初始化"),
    SUCCESS("SUCCESS", "匹配成功"),
    FAIL("FAIL", "匹配失败"),
    NO_NEED_UPDATE("NO_NEED_UPDATE", "无需更新"),
    ;

    @EnumValue
    @JsonValue
    private String code;
    private String desc;
}
