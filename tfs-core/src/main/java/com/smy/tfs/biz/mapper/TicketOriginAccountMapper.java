package com.smy.tfs.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smy.tfs.api.dbo.TicketOriginAccount;
import com.smy.tfs.api.dto.TicketOriginAccountDto;
import com.smy.tfs.api.dto.TicketTemplateDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zzd
 * @since 2024-07-01
 */
public interface TicketOriginAccountMapper extends BaseMapper<TicketOriginAccount> {

    /**
     * 查询源账号列表
     * @param ticketOriginAccountDto
     * @return
     */
    List<TicketOriginAccount> selectOriginAccountList(@Param("ticketOriginAccountDto") TicketOriginAccountDto ticketOriginAccountDto);
}
