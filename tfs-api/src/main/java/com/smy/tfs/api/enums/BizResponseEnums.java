package com.smy.tfs.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author yss
 * @Description:
 * @CreateDate 2024/4/20 11:31
 * @UpdateDate 2024/4/20 11:31
 */
@Getter
@AllArgsConstructor
public enum BizResponseEnums implements Serializable {

    CHECK_PARAMS_EXCEPTION("1", "参数校验异常"),
    SUCCESS("200","成功"),
    IDEMPOTENT("99","幂等"),
    UNKNOW_EXCEPTION_CODE("-1","未知错误"),
    SAVE_ERROR("2","数据保存异常"),
    UPDATE_ERROR("3","数据更新异常"),
    SYSTEM_ERROR("5","系统异常"),
    QUERY_ERROR("6","查询异常"),
    DES_ERROR("7","加解密异常"),
    DEL_ERROR("8","删除数据异常"),
    BATCH_ERROR("9","批量异常"),
    CONVERT_ERROR("10","转换异常"),
    ;

    private String code;
    private String msg;

    public static BizResponseEnums getEnumByCode(String code) {
        for (BizResponseEnums lt : values()) {
            if (lt.code.equals(code)) {
                return lt;
            }
        }
        throw new RuntimeException("BizResponseEnums [" + code + "] 找不到枚举值");
    }
}
