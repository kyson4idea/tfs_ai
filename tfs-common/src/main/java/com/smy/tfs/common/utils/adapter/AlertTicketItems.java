package com.smy.tfs.common.utils.adapter;

public interface AlertTicketItems {
    String getGroupName();
    void setGroupName(String groupName);

    String getRuleId();
    void setRuleId(String ruleId);

    String getRuleName();
    void setRuleName(String ruleName);

    String getRuleNote();
    void setRuleNote(String ruleNote);

    String getCardId();
    void setCardId(String cardId);

    String getCardName();
    void setCardName(String cardName);

    String getTargetIdent();
    void setTargetIdent(String targetIdent);

    String getTriggerValue();
    void setTriggerValue(String triggerValue);

    String getTriggerTime();
    void setTriggerTime(String triggerTime);

    String getFirstTriggerTime();
    void setFirstTriggerTime(String firstTriggerTime);

    String getNotifyCurNumber();
    void setNotifyCurNumber(String notifyCurNumber);

    String getPromQL();
    void setPromQL(String promQL);

    String getFireMapName();
    void setFireMapName(String fireMapName);

    String getRuleProd();
    void setRuleProd(String ruleProd);

    String getIsRecovered();
    void setIsRecovered(String isRecovered);

    String getTagsMap();
    void setTagsMap(String tagsMap);

    String getTicketTemplateId();
    void setTicketTemplateId(String ticketTemplateId);

    String getAppId();
    void setAppId(String appId);
}
