package com.smy.tfs.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author yss
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
public enum PushTypeEnum implements Serializable {
    AUTO_PUSH("AUTO_PUSH", "自动推送"),
    MANUAL_PUSH("MANUAL_PUSH","手动推送"),
    ;

    private String code;
    private String desc;
}
