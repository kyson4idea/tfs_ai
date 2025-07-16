package com.smy.tfs.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smy.tfs.api.dbo.TicketFormItemDataLog;
import com.smy.tfs.api.dto.base.Response;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface ITicketFormItemDataLogService extends IService<TicketFormItemDataLog> {

    Response selectTicketFormItemLogById(String ticketFormItemDataId);

    Response getTicketFormItemDataLogByLabel(String ticketId, String itemLabel);

    /**
     * 批量检查记录是否存在
     */
    Set<String> batchCheckOldItemLogExists(@Param("itemList") List<TicketFormItemDataLog> itemList);

}
