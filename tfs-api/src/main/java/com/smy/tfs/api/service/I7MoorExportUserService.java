package com.smy.tfs.api.service;

import com.smy.tfs.api.dto.TicketRemoteAccountDto;
import com.smy.tfs.api.dto.base.Response;

import java.util.LinkedHashMap;
import java.util.List;

public interface I7MoorExportUserService {

    /**
     * 导出容联·七陌坐席数据
     * @return
     */
    List<TicketRemoteAccountDto> exportUserList();

    Response<LinkedHashMap> getNCSNCLJ(String inputStr);
}
