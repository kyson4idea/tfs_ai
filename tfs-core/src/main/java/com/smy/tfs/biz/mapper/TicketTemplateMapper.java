package com.smy.tfs.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smy.tfs.api.dbo.TicketTemplate;
import com.smy.tfs.api.dto.TicketTemplateDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 工单模版表 Mapper 接口
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
public interface TicketTemplateMapper extends BaseMapper<TicketTemplate> {

    /**
     * 查询工单模版列表
     * @param ticketTemplateDto
     * @return
     */
    List<TicketTemplateDto> selectTicketTemplateList(@Param("ticketTemplateDto") TicketTemplateDto ticketTemplateDto);

    /**
     * 根据关键字模糊查询工单模版列表
     * @param ticketTemplateDto
     * @return
     */
    List<TicketTemplateDto> selectEnableTicketTemplateListByKey(@Param("ticketTemplateDto") TicketTemplateDto ticketTemplateDto);
}
