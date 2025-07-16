package com.smy.tfs.biz.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smy.tfs.api.dbo.TicketFormItemDataLog;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.service.ITicketFormItemDataLogService;
import com.smy.tfs.biz.mapper.TicketFormItemDataLogMapper;
import com.smy.tfs.common.core.domain.R;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class TicketFormItemDataLogServiceImpl extends ServiceImpl<TicketFormItemDataLogMapper, TicketFormItemDataLog> implements ITicketFormItemDataLogService {

    @Resource
    private TicketFormItemDataLogMapper ticketFormItemDataLogMapper;

    @Override
    public Response selectTicketFormItemLogById(String ticketFormItemDataId){

        List<TicketFormItemDataLog> logs = this.lambdaQuery()
                .eq(TicketFormItemDataLog::getTicketFormItemDataId, ticketFormItemDataId)  // 根据 ticketFormItemDataId 查询日志
                .orderByDesc(TicketFormItemDataLog::getOperTime)  // 按操作时间倒序排序
                .list();  // 获取所有符合条件的记录
        if (CollectionUtil.isEmpty(logs)) {
            return Response.success(new ArrayList<TicketFormItemDataLog>());
        } else {
            return Response.success(logs);
        }
    }

    @Override
    public Response getTicketFormItemDataLogByLabel(String ticketId, String itemLabel){

        List<TicketFormItemDataLog> logs = this.lambdaQuery()
                .eq(TicketFormItemDataLog::getTicketDataId, ticketId)
                .eq(TicketFormItemDataLog::getItemLabel, itemLabel)
                .orderByDesc(TicketFormItemDataLog::getOperTime)
                .orderByDesc(TicketFormItemDataLog::getId)
                .list();

        if (CollectionUtil.isEmpty(logs)) {
            return Response.success(new ArrayList<TicketFormItemDataLog>());
        } else {
            return Response.success(logs);
        }
    }

    @Override
    public Set<String> batchCheckOldItemLogExists(List<TicketFormItemDataLog> itemLosList){

        return ticketFormItemDataLogMapper.batchCheckOldItemLogExists(itemLosList);
    }


}
