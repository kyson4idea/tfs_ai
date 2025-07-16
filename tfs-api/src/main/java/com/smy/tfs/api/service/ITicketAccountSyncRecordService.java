package com.smy.tfs.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smy.tfs.api.dbo.TicketAccountSyncRecord;
import com.smy.tfs.api.dto.TicketAccountSyncRecordDto;
import com.smy.tfs.api.dto.base.Response;

/**
 * <p>
 * 工单账户体系同步记录表 服务类
 * </p>
 *
 * @author zzd
 * @since 2024-04-23
 */
public interface ITicketAccountSyncRecordService extends IService<TicketAccountSyncRecord> {

    /**
     * 插入同步记录
     * @param ticketAccountSyncRecordDto
     */
    void createTicketAccountSyncRecord(TicketAccountSyncRecordDto ticketAccountSyncRecordDto);

    /**
     * 插入同步记录
     * @param ticketAccountId
     * @param syncResult
     * @param syncResultDes
     */
    void createTicketAccountSyncRecord(String ticketAccountId, String syncResult, String syncResultDes);


    /**
     * 获取最近的同步记录
     * @param ticketAccountId
     * @return
     */
    Response<TicketAccountSyncRecordDto> getRecentlySyncRecord(String ticketAccountId);
}
