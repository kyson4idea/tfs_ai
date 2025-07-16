package com.smy.tfs.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smy.tfs.api.dbo.TicketAccountSyncRecord;
import com.smy.tfs.api.dto.TicketAccountSyncRecordDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.service.ITicketAccountSyncRecordService;
import com.smy.tfs.biz.mapper.TicketAccountSyncRecordMapper;
import com.smy.tfs.common.utils.bean.BeanHelper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 工单账户体系同步记录表 服务实现类
 * </p>
 *
 * @author zzd
 * @since 2024-04-23
 */
@Service
public class TicketAccountSyncRecordServiceImpl extends ServiceImpl<TicketAccountSyncRecordMapper, TicketAccountSyncRecord> implements ITicketAccountSyncRecordService {

    @Override
    public void createTicketAccountSyncRecord(TicketAccountSyncRecordDto ticketAccountSyncRecordDto) {
        TicketAccountSyncRecord syncRecord = BeanHelper.copyObject(ticketAccountSyncRecordDto, TicketAccountSyncRecord.class);


        this.save(syncRecord);
    }

    @Override
    public void createTicketAccountSyncRecord(String ticketAccountId, String syncResult, String syncResultDes) {
        TicketAccountSyncRecord syncRecord = new TicketAccountSyncRecord();
        syncRecord.setTicketAccountId(ticketAccountId);
        syncRecord.setSyncResult(syncResult);
        syncRecord.setSyncResultDes(syncResultDes);
        this.save(syncRecord);
    }

    @Override
    public Response<TicketAccountSyncRecordDto> getRecentlySyncRecord(String ticketAccountId) {
        LambdaQueryWrapper<TicketAccountSyncRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TicketAccountSyncRecord::getTicketAccountId, ticketAccountId)
                .orderByDesc(TicketAccountSyncRecord::getCreateTime);
        TicketAccountSyncRecord syncRecord = this.getOne(queryWrapper, false);

        return Response.success(BeanHelper.copyObject(syncRecord, TicketAccountSyncRecordDto.class));

    }
}
