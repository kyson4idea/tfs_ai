package com.smy.tfs.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smy.tfs.api.dbo.TicketAccount;
import com.smy.tfs.api.dto.TicketAccountDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 工单账户体系表 Mapper 接口
 * </p>
 *
 * @author zzd
 * @since 2024-04-19
 */
public interface TicketAccountMapper extends BaseMapper<TicketAccount> {

    /**
     * 查询账号配置列表
     * @param ticketAccountDto
     * @return
     */
    List<TicketAccountDto> selectTicketAccountList(@Param("ticketAccountDto") TicketAccountDto ticketAccountDto);
}
