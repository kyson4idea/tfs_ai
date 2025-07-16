package com.smy.tfs.api.service;

import com.smy.tfs.api.dto.base.Response;

public interface ITicketTestService {
    Response consoleLogTest(String sign, String ticketEventTag, String ticketDataId);

}
