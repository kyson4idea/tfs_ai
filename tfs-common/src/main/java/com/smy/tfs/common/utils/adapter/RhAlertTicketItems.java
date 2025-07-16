package com.smy.tfs.common.utils.adapter;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class RhAlertTicketItems implements AlertTicketItems {
    @Value("${rh.alert.groupName:}")
    private String groupName;

    @Value("${rh.alert.ruleId:}")
    private String ruleId;

    @Value("${rh.alert.ruleName:}")
    private String ruleName;

    @Value("${rh.alert.ruleNote:}")
    private String ruleNote;

    @Value("${rh.alert.cardId:}")
    private String cardId;

    @Value("${rh.alert.cardName:}")
    private String cardName;

    @Value("${rh.alert.targetIdent:}")
    private String targetIdent;

    @Value("${rh.alert.triggerValue:}")
    private String triggerValue;

    @Value("${rh.alert.triggerTime:}")
    private String triggerTime;

    @Value("${rh.alert.firstTriggerTime:}")
    private String firstTriggerTime;

    @Value("${rh.alert.notifyCurNumber:}")
    private String notifyCurNumber;

    @Value("${rh.alert.promQL:}")
    private String promQL;

    @Value("${rh.alert.fireMapName:}")
    private String fireMapName;

    @Value("${rh.alert.ruleProd:}")
    private String ruleProd;

    @Value("${rh.alert.isRecovered:}")
    private String isRecovered;

    @Value("${rh.alert.tagsMap:}")
    private String tagsMap;

    @Value("${rh.appId:}")
    private String appId;

    @Value("${rh.alert.ticketTemplateId:}")
    private String ticketTemplateId;

    // 融合告警组名称（夜莺上对应的业务组名称）
    @Value("${rh.alertGroupName:}")
    private String alertGroupName;
}
