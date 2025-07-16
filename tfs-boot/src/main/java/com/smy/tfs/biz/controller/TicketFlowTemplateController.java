package com.smy.tfs.biz.controller;

import com.smy.tfs.api.dto.TicketFlowTemplateDto;
import com.smy.tfs.api.service.ITicketFlowTemplateService;
import com.smy.tfs.common.core.domain.AjaxResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * <p>
 * 工单流程模版操作
 * </p>
 *
 * @author yss
 * @since 2024-04-18
 */
@Controller
@RequestMapping("/ticketFlowTemplate")
public class TicketFlowTemplateController {
    @Resource
    private ITicketFlowTemplateService ticketFlowTemplateService;
    /**
     * 保存工单流程模版
     * @param ticketFlowTemplateDto
     * @return
     */
    @PostMapping("/save")
    public AjaxResult save(TicketFlowTemplateDto ticketFlowTemplateDto){
        return null;
    }
}
