package com.smy.tfs.biz.controller;

import com.smy.tfs.api.dto.TicketExecutorGroupDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.service.ITicketAccountService;
import com.smy.tfs.api.service.ITicketExecutorGroupService;
import com.smy.tfs.api.valid.AddGroup;
import com.smy.tfs.api.valid.UpdateGroup;
import com.smy.tfs.biz.utils.AjaxResultUtil;
import com.smy.tfs.common.core.controller.BaseController;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.core.page.TableDataInfo;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Controller
@ResponseBody
public class TicketExecutorGroupController extends BaseController {

    @Resource
    private ITicketExecutorGroupService ticketExecutorGroupService;
    @Resource
    private ITicketAccountService ticketAccountService;

    @PostMapping("/ticketExecutorGroup/createTicketExecutorGroup")
    public AjaxResult createTicketExecutorGroup(@Validated({AddGroup.class}) @RequestBody TicketExecutorGroupDto ticketExecutorGroupDto) {
        Response<String> response = ticketExecutorGroupService.createTicketExecutorGroup(ticketExecutorGroupDto);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    @PostMapping("/ticketExecutorGroup/updateTicketExecutorGroup")
    public AjaxResult updateTicketExecutorGroup(@Validated({UpdateGroup.class}) @RequestBody TicketExecutorGroupDto ticketExecutorGroupDto) {
        Response<Boolean> response = ticketExecutorGroupService.updateTicketExecutorGroupFull(ticketExecutorGroupDto);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    @DeleteMapping("/ticketExecutorGroup/deleteTicketExecutorGroup/{id}")
    public AjaxResult deleteTicketExecutorGroup(@PathVariable String id) {
        ticketExecutorGroupService.deleteTicketExecutorGroup(id);
        return AjaxResult.success("删除成功");
    }

    @GetMapping("/ticketExecutorGroup/selectTicketExecutorGroupPage")
    public TableDataInfo selectTicketExecutorGroupPage(TicketExecutorGroupDto ticketExecutorGroupDto) {
        List<TicketExecutorGroupDto> ticketExecutorGroupDtoList = ticketExecutorGroupService.selectTicketExecutorGroupList(ticketExecutorGroupDto);
        return getDataTable(ticketExecutorGroupDtoList);
    }

    @GetMapping("/ticketExecutorGroup/selectAllGroupList")
    public AjaxResult selectAllGroupList(TicketExecutorGroupDto ticketExecutorGroupDto) {
        List<TicketExecutorGroupDto> ticketExecutorGroupDtoList = ticketExecutorGroupService.selectTicketExecutorGroupList(ticketExecutorGroupDto);
        return AjaxResult.success(ticketExecutorGroupDtoList);
    }

    @GetMapping({"/ticketExecutorGroup/selectAppRemoteAccountList","/outside/ticketExecutorGroup/selectAppRemoteAccountList"})
    public AjaxResult selectAppRemoteAccountList(@RequestParam("accountType") String accountType) {
        return AjaxResult.success(ticketAccountService.getTicketRemoteAccountListByType(accountType));
    }


}
