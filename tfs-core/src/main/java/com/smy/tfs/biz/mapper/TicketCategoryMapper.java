package com.smy.tfs.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smy.tfs.api.dbo.TicketCategory;
import com.smy.tfs.api.dto.TicketAppDto;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectKey;

import java.util.List;

/**
 * <p>
 * 应用表 Mapper 接口
 * </p>
 *
 * @author yss
 * @since 2024-11-04
 */
public interface TicketCategoryMapper extends BaseMapper<TicketCategory> {

    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("insert into ticket_category " +
            "(code,name,superior_code,status,category_level,app_id,sort,template_id,create_by," +
            "create_time,update_time,update_by,delete_time) " +
            "values (#{code},#{name},#{superiorCode},#{status},#{categoryLevel},#{appId}," +
            "#{sort},#{templateId},#{createBy},now(),now(),#{updateBy},#{deleteTime});")
    Integer insertTicketCategory(TicketCategory ticketCategory);

}
