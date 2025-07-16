package com.smy.tfs.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author z01140
 * @Package: com.smy.tfs.api.enums
 * @Description:
 *      * 流程节点状态
 *      * approveSuccess
 *      * approveReject
 *      * approving
 * @CreateDate 2024/4/22 19:11
 * @UpdateDate 2024/4/22 19:11
 */
@Getter
@AllArgsConstructor
public enum NodeStatusEnum implements Serializable {
    APPROVE_INIT("APPROVE_INIT", "审批初始化"),
    APPROVE_PASS("APPROVE_PASS", "审批通过"),
    APPROVE_END("APPROVE_END", "审批结单"),
    APPROVE_REJECT("APPROVE_REJECT", "审批拒绝"),
    APPROVING("APPROVING", "审批中"),
    WITHDRAW("WITHDRAW", "已撤回"),
    ;
    private String code;
    private String desc;
}
