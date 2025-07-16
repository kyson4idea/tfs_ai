package com.smy.tfs.openapi.service;

import com.smy.tfs.api.dto.TicketRemoteAccountDto;
import com.smy.tfs.api.dto.base.Response;

import java.util.List;


/**
 * <p>
 * 工单账户服务类
 * </p>
 *
 * @author yss
 * @since 2024-05-28
 */
public interface ITicketAccountServiceWrapper {

    // 查询账户
    Response<List<TicketRemoteAccountDto>> getTicketRemoteAccountListByType(String accountType);






}
