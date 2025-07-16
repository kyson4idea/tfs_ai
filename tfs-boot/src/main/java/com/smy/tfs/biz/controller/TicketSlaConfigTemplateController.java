package com.smy.tfs.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smy.tfs.api.dbo.TicketSlaConfigTemplate;
import com.smy.tfs.api.dto.ticket_sla_service.TicketSlaConfigTemplateDto;
import com.smy.tfs.api.service.ITicketSlaConfigTemplateService;
import com.smy.tfs.biz.mapper.TicketSlaConfigTemplateMapper;
import com.smy.tfs.common.core.controller.BaseController;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.exception.ServiceException;
import com.smy.tfs.common.utils.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 工单sla模板配置操作
 * </p>
 *
 * @author yss
 * @since 2025-03-21
 */
@RestController
@ResponseBody
public class TicketSlaConfigTemplateController extends BaseController {

    @Resource
    private ITicketSlaConfigTemplateService ticketSlaConfigTemplateService;
    @Resource
    private TicketSlaConfigTemplateMapper ticketSlaConfigTemplateWrapper;

    @PostMapping("/ticketSlaConfigTemplate/list")
    public AjaxResult list(@RequestBody TicketSlaConfigTemplateDto ticketSlaConfigTemplateDto) {
        if (StringUtils.isBlank(ticketSlaConfigTemplateDto.getTicketTemplateId())) {
            throw new ServiceException("工单模版id为空");
        }
        LambdaQueryWrapper<TicketSlaConfigTemplate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TicketSlaConfigTemplate::getTicketTemplateId, ticketSlaConfigTemplateDto.getTicketTemplateId());
        List<TicketSlaConfigTemplate> ticketSlaConfigTemplateList = ticketSlaConfigTemplateWrapper.selectList(lambdaQueryWrapper);
        return AjaxResult.success(ticketSlaConfigTemplateList);
    }

    @PostMapping("/ticketSlaConfigTemplate/updateStatus")
    public AjaxResult updateStatus(@RequestBody TicketSlaConfigTemplateDto ticketSlaConfigTemplateDto) {
        if (StringUtils.isBlank(ticketSlaConfigTemplateDto.getId()) || Objects.isNull(ticketSlaConfigTemplateDto.getStatus())) {
            throw new ServiceException("参数异常");
        }
        TicketSlaConfigTemplate ticketSlaConfigTemplate = new TicketSlaConfigTemplate();
        ticketSlaConfigTemplate.setId(ticketSlaConfigTemplateDto.getId());
        ticketSlaConfigTemplate.setStatus(ticketSlaConfigTemplateDto.getStatus());
        boolean saveFlag = ticketSlaConfigTemplateService.updateById(ticketSlaConfigTemplate);
        if (!saveFlag) {
            throw new ServiceException("保存异常");
        }
        return AjaxResult.success();
    }




}
