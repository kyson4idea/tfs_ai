package com.smy.tfs.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smy.tfs.api.dbo.TicketCategory;
import com.smy.tfs.api.dto.TicketCategoryDto;
import com.smy.tfs.api.dto.TicketDataDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.YESNOEnum;

import java.util.List;

/**
 * <p>
 * 应用表 工单分类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
public interface ITicketCategoryService extends IService<TicketCategory> {

    public Response<TicketCategoryDto> add(TicketCategoryDto ticketCategoryDto, String userName, String userId, String userType);

    public Response<TicketCategoryDto> update(TicketCategoryDto ticketCategoryDto);

    public Response delete(Integer id);

    public Response<List<TicketCategoryDto>> queryTicketCategoryList(TicketCategoryDto ticketCategoryDto, String userType, String userId, String userName);

    public Response updateSort(List<TicketCategoryDto> ticketCategoryDtoList);

    public Response<YESNOEnum> categoryEnabled(String appId);


}
