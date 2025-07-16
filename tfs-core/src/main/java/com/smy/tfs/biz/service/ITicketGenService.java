package com.smy.tfs.biz.service;

import com.smy.tfs.api.dto.dynamic.TicketDataStdDto;
import com.smy.tfs.common.core.domain.model.LoginUser;

import java.util.Map;

public interface ITicketGenService {

    /**
     * 预览核心代码
     */
    public Map<String, String> previewCoreCode(TicketDataStdDto ticketDataStdDto, LoginUser loginUser);


    /**
     * 生成代码（下载方式）
     */
    public byte[] downloadCode(TicketDataStdDto ticketDataStdDto, LoginUser loginUser);
}
