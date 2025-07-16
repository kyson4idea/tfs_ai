package com.smy.tfs.biz.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smy.tfs.api.dbo.TicketFormItemValues;
import com.smy.tfs.api.dto.AdvancedQueryDto;
import com.smy.tfs.api.dto.TicketDataListResponseDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * （表单项col和表单项value对应的平铺表） Mapper 接口
 * </p>
 *
 * @author yss
 * @since 2024-05-10
 */
public interface TicketFormItemValuesMapper extends BaseMapper<TicketFormItemValues> {

    List<TicketDataListResponseDto> selectTicketFormItemValuesList(@Param("advancedQueryDto") AdvancedQueryDto advancedQueryDto);

}
