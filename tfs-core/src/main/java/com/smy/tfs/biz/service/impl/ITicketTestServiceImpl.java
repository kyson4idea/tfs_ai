package com.smy.tfs.biz.service.impl;

import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.service.ITicketTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("ticketTestService")
public class ITicketTestServiceImpl implements ITicketTestService {
    @Override
    public Response consoleLogTest(String sign, String ticketEventTag, String ticketDataId) {
        log.info("===========================");
        log.info("ITicketTestServiceImpl consoleLogTest sign:{}, ticketEventTag:{}, ticketDataId:{} ", sign, ticketEventTag, ticketDataId);
        return Response.success();
    }
}
