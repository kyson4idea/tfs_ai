package com.smy.tfs.api.service;

import com.smy.tfs.api.dto.FlashcatAlertCallbackDto;
import com.smy.tfs.api.dto.FlashcatAlertTicketStatusDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.dynamic.TicketFormItemStdDto;
import com.smy.tfs.common.utils.adapter.AlertTicketItems;

import java.util.List;
import java.util.Map;

/**
 * 夜莺告警服务，主要用于告警与工单适配，用于根据告警回调创建相关工单以及告警收敛等。
 */

public interface IFlashcatAlertService {

    /**
     * 告警与工单适配器
     * @return
     */
    List<TicketFormItemStdDto> alertAdapter(AlertTicketItems alertTicketItems, FlashcatAlertCallbackDto flashcatAlertCallbackDto);

    /**
     * 根据情况执行创建｜更新工单（包括更新状态等）
     */
    Response<String> createOrUpdateTicket(AlertTicketItems alertTicketItems, FlashcatAlertCallbackDto flashcatAlertCallbackDto, List<TicketFormItemStdDto> ticketFormItemStdDtos, String botKey);

    /**
     * 处理告警
     * @param flashcatAlertCallbackDto
     */
    Response<String> handleAlert(String botKey, FlashcatAlertCallbackDto flashcatAlertCallbackDto);

    Map<String, List<FlashcatAlertTicketStatusDto>> getLastAlertTicketsByRuleIds(List<String> ruleIds, String startDateStr, boolean applyingOnly);
}
