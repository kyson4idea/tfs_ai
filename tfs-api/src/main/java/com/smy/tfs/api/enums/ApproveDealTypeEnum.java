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
 * @CreateDate 2024/4/20 15:23
 * @UpdateDate 2024/4/20 15:23
 */
@Getter
@AllArgsConstructor
public enum ApproveDealTypeEnum implements Serializable {

    APPLY("APPLY", "提交工单"),
    PASS("PASS", "审批通过"),
    REJECT("REJECT", "驳回工单"),
    WITHDRAW("WITHDRAW", "撤回工单"),
    REJECT_PRE("REJECT_PRE", "驳回到上一个节点"),
    COMMENT("COMMENT", "评论"),
    OVERTIME("OVERTIME", "处理超时"),
    URGE("URGE", "催办工单"),
    DISPATCH("DISPATCH", "派单"),
    BACK("BACK", "退回"),
    SEND("SEND", "抄送"),
    ACTION("ACTION", "动作"),
    MODIFY("MODIFY", "修改工单"),
    FINISH("FINISH", "结束")
    //TODO：加签类型需要单独配置，不能和审批放在一起
    ;

    @EnumValue
    @JsonValue
    private String code;
    private String desc;

    public static ApproveDealTypeEnum getByCode(String code){
        for (ApproveDealTypeEnum e : ApproveDealTypeEnum.values()) {
            if (Objects.equals(e.getCode(), code)){
                return e;
            }
        }
        throw new RuntimeException("excutorTypeEnum [" + code + "] 找不到枚举值");
    }
}
