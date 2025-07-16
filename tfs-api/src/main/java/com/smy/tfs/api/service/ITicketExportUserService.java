package com.smy.tfs.api.service;

import com.smy.tfs.api.dto.TicketRemoteAccountDto;

import java.util.List;

public interface ITicketExportUserService {

    /**
     * 导出域控用户
     * @return
     */
    List<TicketRemoteAccountDto> exportLdapUserList();
}
