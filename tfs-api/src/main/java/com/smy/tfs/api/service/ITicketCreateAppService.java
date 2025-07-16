package com.smy.tfs.api.service;

import com.smy.tfs.api.dto.base.Response;

import java.util.HashMap;

public interface ITicketCreateAppService {
    //根据工单数据创建应用
    Response createTicketAppFromTicketForm(String sign, String ticketEventTag, String ticketDataId);
}
