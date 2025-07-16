package com.smy.tfs.biz.controller;

import com.smy.tfs.api.dto.TicketFormTemplateDto;
import com.smy.tfs.api.service.ITicketFormTemplateService;
import com.smy.tfs.common.core.domain.AjaxResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * <p>
 * 工单表单模板操作
 * </p>
 *
 * @author yss
 * @since 2024-04-18
 */
@Controller
@RequestMapping("/ticketFormTemplate")
public class TicketFormTemplateController {

    @Resource
    private ITicketFormTemplateService ticketFormTemplateService;

    /**
     * 保存工单表单模版
     * @param ticketFormTemplateDto
     * @return
     */
    @PostMapping("/save")
    public AjaxResult save(TicketFormTemplateDto ticketFormTemplateDto){
        return AjaxResult.success(ticketFormTemplateService.save(ticketFormTemplateDto));
    }

}
