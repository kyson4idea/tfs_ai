package com.smy.tfs.biz.service.wrapper;

import com.smy.tfs.api.dto.TicketRemoteAccountDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.service.ITicketAccountService;
import com.smy.tfs.openapi.service.ITicketAccountServiceWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.apidocs.annotations.ApiModule;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 工单账户对外服务实现类
 * </p>
 *
 * @author yss
 * @since 2025-03-18
 */
@Slf4j
@Component("ticketAccountServiceWrapper")
@org.apache.dubbo.config.annotation.Service
@ApiModule(value = "工单账户对外数据服务", apiInterface = ITicketAccountServiceWrapper.class)
public class TicketAccountServiceWrapper implements ITicketAccountServiceWrapper {

    @Resource
    private ITicketAccountService ticketAccountService;
    @Override
    public Response<List<TicketRemoteAccountDto>> getTicketRemoteAccountListByType(String accountType) {
        return Response.success(ticketAccountService.getTicketRemoteAccountListByType(accountType));
    }
}