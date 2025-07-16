package com.smy.tfs.api.dto.ticket_sla_service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 工单sla通知信息配置
 *
 * @author yss
 * @date 2025-04-03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemindMsg implements Serializable {

    private static final long serialVersionUID = 6069364126955414661L;

    /**
     * 通知消息方式
     */
    private String remindMsgType;

    /**
     * 接口配置
     */
    private String interfaceConfig;



}
