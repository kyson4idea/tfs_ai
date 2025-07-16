package com.smy.tfs.api.dto.ticket_sla_service;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewTag implements Serializable {

    private static final long serialVersionUID = 5756007327458814628L;

    /**
     * tag值
     */
    private String tagValue;

    /**
     * tag唯一值
     */
    private String tagUniqueValue;

    /**
     * 标准打标时间
     */
    private String tagTime;

    /**
     * 配置：
     * {"slaConfigType":"","slaConfigTypeValue":"","slaConfigTemplateId":"","slaRemindType":"","slaTimeValue":""}
     */
    private JSONObject tagConfig;
}
