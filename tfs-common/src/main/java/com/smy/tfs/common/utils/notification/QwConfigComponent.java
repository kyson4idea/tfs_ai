package com.smy.tfs.common.utils.notification;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class QwConfigComponent implements InitializingBean {
    @Resource
    private Environment env;

    private Map<QwCorporateEnum, QwConfig> qwConfigMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        String smyCropId = env.getProperty("qw.cropId", "wwf1d81d5ef1323b9c");
        String smyCropSec = env.getProperty("qw.cropSec", "X451-tZ5LPuk6wL46VoeGCy1KXxgCf8CC5EqO7ByrsQ");
        String smyAgentId = env.getProperty("qw.agentId", "1000119");
        QwConfig smyQwConfig = new QwConfig(smyCropId, smyCropSec, Integer.valueOf(smyAgentId));
        qwConfigMap.put(QwCorporateEnum.SMY, smyQwConfig);

        String hjsdCropId = env.getProperty("hjsd.qw.cropId", "ww20c09ecc2b2cc1b4");
        String hjsdCropSec = env.getProperty("hjsdqw.cropSec", "KP3yMNpNJKcT9MP8CcgiHJDQF5dRkrbB4qeoi-hi6AE");
        String hjsdAgentId = env.getProperty("hjsdqw.agentId", "1000003");
        QwConfig hjsdQwConfig = new QwConfig(hjsdCropId, hjsdCropSec, Integer.valueOf(hjsdAgentId));
        qwConfigMap.put(QwCorporateEnum.HJSD, hjsdQwConfig);
    }

    public QwConfig getQwConfig(QwCorporateEnum qwCorporateEnum) {
        return qwConfigMap.get(qwCorporateEnum);
    }

    public QwConfig getDefaultQwConfig() {
        return qwConfigMap.get(QwCorporateEnum.SMY);
    }
}


