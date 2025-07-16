package com.smy.tfs.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author z01140
 * @Package: com.smy.tfs.api.enums
 * @Description:
 * @CreateDate 2024/11/18 15:23
 * @UpdateDate 2024/11/18 15:23
 */
@Getter
@AllArgsConstructor
public enum DealTypeCallbackEnum implements Serializable {

    ACTION_SUCCESS("ACTION_SUCCESS", "执行成功"),
    ACTION_FAILED("ACTION_FAILED", "执行失败"),
    ;

    @EnumValue
    @JsonValue
    private String code;
    private String desc;

    public static DealTypeCallbackEnum getByCode(String code){
        for (DealTypeCallbackEnum e : DealTypeCallbackEnum.values()) {
            if (Objects.equals(e.getCode(), code)){
                return e;
            }
        }
        throw new RuntimeException("excutorTypeEnum [" + code + "] 找不到枚举值");
    }
}
