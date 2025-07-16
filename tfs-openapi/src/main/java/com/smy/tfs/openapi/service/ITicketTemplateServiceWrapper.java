package com.smy.tfs.openapi.service;

import com.smy.tfs.api.dto.*;
import com.smy.tfs.api.dto.base.Response;

import java.util.List;


/**
 * <p>
 * 工单模版对外数据服务
 * </p>
 *
 * @author yss
 * @since 2024-05-28
 */
public interface ITicketTemplateServiceWrapper {

    // 查询工单模版列表，不分页
    Response<List<TicketTemplateDto>> queryTicketTemplates(QueryEnableTicketTemplateDto queryEnableTicketTemplateDto);

    // 查询工单模版列表，不分页
    Response<List<TicketTemplateDto>> selectTicketTemplateList(TicketTemplateDto ticketTemplateDto);

    // 查询工单模版全部信息
    public Response<TicketTemplateDto> selectTicketTemplateFullById(String id, String applyUser);



}
