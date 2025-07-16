package com.smy.tfs.api.enums;

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
public enum EventStatusEnum implements Serializable {
    INIT("INIT", "初始化"),
    WAIT_EXECUTE("WAIT_EXECUTE","待执行"),
    EXECUTE_SUCCESS_FINAL("EXECUTE_SUCCESS_FINAL","成功"),
    EXECUTE_FAILURE_MIDDLE("EXECUTE_FAILURE_MIDDLE","失败"),
    EXECUTE_FAILURE("EXECUTE_FAILURE","失败"),
    ;

    private String code;
    private String desc;
}
