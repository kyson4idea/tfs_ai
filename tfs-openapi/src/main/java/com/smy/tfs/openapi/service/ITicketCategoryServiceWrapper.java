package com.smy.tfs.openapi.service;

import com.smy.tfs.api.dto.TicketCategoryDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.YESNOEnum;

import java.util.List;


/**
 * <p>
 * 工单分类表 服务类
 * </p>
 *
 * @author yss
 * @since 2024-05-28
 */
public interface ITicketCategoryServiceWrapper {

    //查询工单分类
    Response<List<TicketCategoryDto>> queryTicketCategoryList(TicketCategoryDto ticketCategoryDto, String userType, String userId, String userName);


    //业务id是否需要开启工单分类
    Response<YESNOEnum> categoryEnabled(String appId);





}
