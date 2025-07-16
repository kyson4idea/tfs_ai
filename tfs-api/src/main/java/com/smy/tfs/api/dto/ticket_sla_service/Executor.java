package com.smy.tfs.api.dto.ticket_sla_service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 工单sla通知人信息对象
 *
 * @author yss
 * @date 2025-04-03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Executor implements Serializable {

    private static final long serialVersionUID = -1022987157722980917L;

    /**
     * 通知人类型
     */
    private String executorType;

    /**
     * 通知人值
     */
    private String executorValue;

}
