package com.smy.tfs.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;


@Getter
@AllArgsConstructor
public enum MonitorTypeEnum implements Serializable {
    METRIC("metric", "Metric"),
    HOST("host", "Host"),
    LOG("logging", "Log"),
    ANOMALY("anomaly", "Anomaly"),
    FIREMAP("firemap", "灭火图"),
    NORTHSTAR("northstar", "北极星"),
    ;
    private final String code;
    private final String desc;

    public static MonitorTypeEnum getEnumByCode(String code) {
        for (MonitorTypeEnum mt : values()) {
            if (mt.code.equals(code)) {
                return mt;
            }
        }
        return METRIC;
    }
}
