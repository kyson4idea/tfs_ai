package com.smy.tfs.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smy.tfs.api.dbo.TicketData;
import com.smy.tfs.api.dto.TicketDataListRequestDto;
import com.smy.tfs.api.dto.TicketDataListResponseDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 工单数据表 Mapper 接口
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
public interface TicketDataMapper  extends BaseMapper<TicketData>  {

    /**
     * 查询工单数据列表
     * @param ticketDataListRequestDto
     * @return
     */
    List<TicketDataListResponseDto> selectTicketDataList(@Param("ticketDataDto") TicketDataListRequestDto ticketDataListRequestDto);

    /**
     *
     * @param ticketDataListRequestDto
     * @return
     */
    Integer selectTicketDataCount(@Param("ticketDataDto") TicketDataListRequestDto ticketDataListRequestDto);


//    /**
//     * 查询工单模版数量
//     * @param id 应用ID
//     * @param wxChatGroupID 微信群ID
//     * @return 工单模版总数量
//     */
//    Integer updateWxChatGroupID(@Param("id") String id, @Param("wxChatGroupID") String wxChatGroupID);

}
