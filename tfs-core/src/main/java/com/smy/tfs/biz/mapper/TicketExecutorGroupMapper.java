package com.smy.tfs.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smy.tfs.api.dbo.TicketExecutorGroup;
import com.smy.tfs.api.dto.TicketExecutorGroupDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 应用人员组表 Mapper 接口
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
public interface TicketExecutorGroupMapper extends BaseMapper<TicketExecutorGroup> {

    /**
     * 根据条件查询应用人员组列表 不分页
     * @param ticketExecutorGroupDto
     * @return
     */
    List<TicketExecutorGroupDto> selectTicketExecutorGroupList(@Param("ticketExecutorGroupDto") TicketExecutorGroupDto ticketExecutorGroupDto);
}
