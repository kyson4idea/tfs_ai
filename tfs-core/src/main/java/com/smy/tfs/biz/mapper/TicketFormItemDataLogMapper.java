package com.smy.tfs.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smy.tfs.api.dbo.TicketFormItemDataLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface TicketFormItemDataLogMapper extends BaseMapper<TicketFormItemDataLog> {

    /**
     * 批量检查记录是否存在
     */
    Set<String> batchCheckOldItemLogExists(@Param("itemLosList") List<TicketFormItemDataLog> itemLosList);

}
