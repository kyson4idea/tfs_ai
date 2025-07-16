package com.smy.tfs.biz.controller;


import com.smy.tfs.api.dto.TicketAccountDto;
import com.smy.tfs.api.dto.TicketAppDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.RecordStatusEnum;
import com.smy.tfs.api.service.ITicketAccountService;
import com.smy.tfs.api.service.ITicketAppService;
import com.smy.tfs.api.valid.AddGroup;
import com.smy.tfs.api.valid.DeleteGroup;
import com.smy.tfs.api.valid.UpdateGroup;
import com.smy.tfs.biz.utils.AjaxResultUtil;
import com.smy.tfs.common.core.domain.AjaxResult;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 工单系统Dashboard模块相关接口
 * </p>
 */

@Controller
@ResponseBody
public class DashboardController {
    @Resource
    private ITicketAppService ticketAppService;
    @Resource
    private ITicketAccountService ticketAccountService;

    @PostMapping("/dashboard/selectTicketAppList")
    public AjaxResult selectTicketAppList(@RequestBody TicketAppDto ticketAppDto){
        Response<List<TicketAppDto>> response = ticketAppService.selectTicketAppListWithDayAnalysis(ticketAppDto);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    @PostMapping("/dashboard/applyTicketApp")
    public AjaxResult applyTicketApp(@Validated({AddGroup.class}) @RequestBody TicketAppDto ticketAppDto){
        Response<String> response = ticketAppService.applyTicketApp(ticketAppDto);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    @PostMapping("/dashboard/createTicketApp")
    public AjaxResult createTicketApp(@Validated({AddGroup.class}) @RequestBody TicketAppDto ticketAppDto){
        Response<String> response = ticketAppService.createTicketApp(ticketAppDto);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    @PostMapping("/dashboard/updateTicketAppFull")
    public AjaxResult updateTicketAppFull(@Validated({UpdateGroup.class}) @RequestBody TicketAppDto ticketAppDto){
        Response<TicketAppDto> response = ticketAppService.updateTicketAppFull(ticketAppDto);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    @PostMapping({"/dashboard/deleteTicketApp"})
    public AjaxResult deleteTicketApp(@Validated({DeleteGroup.class})@RequestBody TicketAppDto ticketAppDto){
        ticketAppService.deleteTicketApp(ticketAppDto.getId());
        return AjaxResult.success("删除成功");
    }

    @GetMapping({"/dashboard/selectAllAccountList","/outside/dashboard/selectAllAccountList"})
    public AjaxResult selectAllAccountList(){
        TicketAccountDto ticketAccountDto = new TicketAccountDto();
        ticketAccountDto.setStatus(RecordStatusEnum.NORMAL.getCode());
        Response<List<TicketAccountDto>> response = ticketAccountService.selectTicketAccountList(ticketAccountDto);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

}
