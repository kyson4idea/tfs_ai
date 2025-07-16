package com.smy.tfs.biz.service.wrapper;

import com.smy.tfs.api.dto.QueryEnableTicketTemplateDto;
import com.smy.tfs.api.dto.TicketCategoryDto;
import com.smy.tfs.api.dto.TicketTemplateDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.service.ITicketCategoryService;
import com.smy.tfs.api.service.ITicketTemplateService;
import com.smy.tfs.openapi.service.ITicketTemplateServiceWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.apidocs.annotations.ApiModule;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 工单模版对外数据服务
 * </p>
 *
 * @author yss
 * @since 2025-03-18
 */
@Slf4j
@Component("ticketTemplateServiceWrapper")
@org.apache.dubbo.config.annotation.Service
@ApiModule(value = "工单模版对外数据服务", apiInterface = ITicketTemplateServiceWrapper.class)
public class TicketTemplateServiceWrapper implements ITicketTemplateServiceWrapper {
    @Resource
    private ITicketTemplateService ticketTemplateService;

    @Override
    public Response<List<TicketTemplateDto>> queryTicketTemplates(QueryEnableTicketTemplateDto queryEnableTicketTemplateDto) {
        return ticketTemplateService.queryTicketTemplates(queryEnableTicketTemplateDto);
    }

    @Override
    public Response<List<TicketTemplateDto>> selectTicketTemplateList(TicketTemplateDto ticketTemplateDto) {
        return Response.success(ticketTemplateService.selectTicketTemplateList(ticketTemplateDto));
    }

    @Override
    public Response<TicketTemplateDto> selectTicketTemplateFullById(String id, String applyUser) {
        return ticketTemplateService.selectTicketTemplateFullById(id, applyUser);
    }
}