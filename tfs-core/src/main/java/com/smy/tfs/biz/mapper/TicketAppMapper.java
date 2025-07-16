package com.smy.tfs.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smy.tfs.api.dbo.TicketApp;
import com.smy.tfs.api.dto.TicketAppDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 应用表 Mapper 接口
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
public interface TicketAppMapper extends BaseMapper<TicketApp> {
    /**
     * 查询应用信息+应用统计分析信息
     * @param ticketAppDto
     * @return
     */
    List<TicketAppDto> selectTicketAppList(@Param("ticketAppDto") TicketAppDto ticketAppDto);

    /**
     * 统计应用id + name 数量
     * @param appId
     * @return
     */
    Integer countTicketAppByIdAndName(@Param("appId") String appId, @Param("appName") String appName);

    /**
     * 统计name 数量 排除当前id
     * @param appId
     * @return
     */
    Integer countTicketAppByNameExtendId(@Param("appId") String appId, @Param("appName") String appName);
}
