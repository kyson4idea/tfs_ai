package com.smy.tfs.common.utils.adapter;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class FlashcatAlertTicketItems implements AlertTicketItems {
    @Value("${flashcat.alert.groupName:}")
    private String groupName;

    @Value("${flashcat.alert.ruleId:}")
    private String ruleId;

    @Value("${flashcat.alert.ruleName:}")
    private String ruleName;

    @Value("${flashcat.alert.ruleNote:}")
    private String ruleNote;

    @Value("${flashcat.alert.cardId:}")
    private String cardId;

    @Value("${flashcat.alert.cardName:}")
    private String cardName;

    @Value("${flashcat.alert.targetIdent:}")
    private String targetIdent;

    @Value("${flashcat.alert.triggerValue:}")
    private String triggerValue;

    @Value("${flashcat.alert.triggerTime:}")
    private String triggerTime;

    @Value("${flashcat.alert.firstTriggerTime:}")
    private String firstTriggerTime;

    @Value("${flashcat.alert.notifyCurNumber:}")
    private String notifyCurNumber;

    @Value("${flashcat.alert.promQL:}")
    private String promQL;

    @Value("${flashcat.alert.fireMapName:}")
    private String fireMapName;

    @Value("${flashcat.alert.ruleProd:}")
    private String ruleProd;

    @Value("${flashcat.alert.isRecovered:}")
    private String isRecovered;

    @Value("${flashcat.alert.tagsMap:}")
    private String tagsMap;

    @Value("${flashcat.appId:}")
    private String appId;

    @Value("${flashcat.alert.ticketTemplateId:}")
    private String ticketTemplateId;
}
