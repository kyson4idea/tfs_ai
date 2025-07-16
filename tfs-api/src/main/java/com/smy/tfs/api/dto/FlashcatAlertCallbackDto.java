package com.smy.tfs.api.dto;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class FlashcatAlertCallbackDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private String alertId;

    /**
     * 所在业务组
     */
    @JsonProperty("group_name")
    private String groupName;

    /**
     * 告警规则ID
     */
    @JsonProperty("rule_id")
    private String ruleId;

    /**
     * 告警规则名称
     */
    @JsonProperty("rule_name")
    private String ruleName;

    /**
     * 告警规则描述
     */
    @JsonProperty("rule_note")
    private String ruleNote;

    /**
     * 告警规则目标
     */
    @JsonProperty("target_ident")
    private String targetIdent;

    /**
     * 告警规则触发时间
     */
    @JsonProperty("trigger_time")
    private Long triggerTime;

    /**
     * 告警规则触发值
     */
    @JsonProperty("trigger_value")
    private String triggerValue;


    /**
     * 告警规则标签
     */
    @JsonProperty("tags_map")
    private JSONObject tagsMap;

    /**
     * 告警是否恢复
     */
    @JsonProperty("is_recovered")
    private Boolean isRecovered;

    /**
     * 告警首次触发时间
     */
    @JsonProperty("first_trigger_time")
    private Long firstTriggerTime;

    /**
     * 告警执行次数
     */
    @JsonProperty("notify_cur_number")
    private Integer notifyCurNumber;

    /**
     * 告警语句
     */
    @JsonProperty("prom_ql")
    private String promQL;

    /**
     * 监控类型
     */
    @JsonProperty("rule_prod")
    private String ruleProd;
}
