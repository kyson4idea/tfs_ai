package com.smy.tfs.api.dto.ticket_sla_service;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.smy.tfs.api.dto.base.AccountInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 工单sla通知对象Dto
 *
 * @author yss
 * @date 2025-04-03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketSlaNoticeDto implements Serializable {

    private static final long serialVersionUID = -2978912454208630325L;

    /**
     * 通知类型：before  after  current
     */
    private String remindType;

    /**
     * 分钟
     */
    private long timeValue;

    /**
     * 通知人信息列表
     */
    private List<Executor> executorList;

    /**
     * 提醒消息列表
     */
    private List<RemindMsg> remindMsgList;

    public TicketSlaNoticeDto (
            String remindType,
            long timeValue,
            String executors,
            String remindMsg
    ) {
        this.remindType = remindType;
        this.timeValue = timeValue;
        this.executorList = JSON.parseArray(executors, Executor.class);
        this.remindMsgList = JSON.parseArray(remindMsg, RemindMsg.class);
    }



}
