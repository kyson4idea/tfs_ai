package com.smy.tfs.biz.service;

import com.smy.tfs.api.dto.base.Response;

public interface TicketApproveCallBackService {

    /**
     * sheet 工单审批回调
     * @param sign
     * @param ticketEventTag
     * @param ticketDataId
     * @return
     */
    Response sendApproveResultMqForSheet(String sign, String ticketEventTag, String ticketDataId);
}
