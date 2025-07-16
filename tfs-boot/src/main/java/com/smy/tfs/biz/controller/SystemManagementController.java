package com.smy.tfs.biz.controller;

import com.smy.tfs.api.dto.*;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.service.*;
import com.smy.tfs.api.valid.AddGroup;
import com.smy.tfs.api.valid.OperateGroup;
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
@RequestMapping("/systemManagement")
public class SystemManagementController extends BaseController {
    @Resource
    private ITicketAccountService ticketAccountService;
    @Resource
    private ITicketDataService ticketDataService;
    @Resource
    private ITicketAccountSyncRecordService ticketAccountSyncRecordService;
    @Resource
    private ITicketAppService ticketAppService;
    @Resource
    private ITicketTemplateService ticketTemplateService;

    // 分页查询账户配置列表
    @ResponseBody
    @GetMapping("/systemManagementPage")
    public TableDataInfo systemManagementPage(TicketAccountDto ticketAccountDto) {
        startPage();
        Response<List<TicketAccountDto>> response = ticketAccountService.selectTicketAccountList(ticketAccountDto);
        return getDataTable(response.getData());
    }

    // 创建账户配置
    @ResponseBody
    @PostMapping("/insertTicketAccount")
    public AjaxResult insertTicketAccount(@Validated({AddGroup.class}) @RequestBody TicketAccountDto ticketAccountDto) {
        Response<Boolean> response = ticketAccountService.insertTicketAccount(ticketAccountDto);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    // 修改账户配置
    @ResponseBody
    @PostMapping("/updateTicketAccount")
    public AjaxResult updateTicketAccount(@Validated({UpdateGroup.class}) @RequestBody TicketAccountDto ticketAccountDto) {
        Response<Boolean> response = ticketAccountService.updateTicketAccount(ticketAccountDto);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    // 删除账户配置
    @ResponseBody
    @DeleteMapping("/deleteTicketAccountById/{id}")
    public AjaxResult deleteTicketAccountById(@PathVariable String id) {
        Response<String> response = ticketAccountService.deleteTicketAccountById(id);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    // 同步账户配置
    @ResponseBody
    @PostMapping("/syncTicketAccountConfig")
    public AjaxResult syncTicketAccountConfig(@Validated({OperateGroup.class}) @RequestBody TicketAccountDto ticketAccountDto) {
        Response<String> response = ticketAccountService.doSyncTicketAccountConfig(ticketAccountDto.getId());
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    // 获取最近的账户同步记录
    @ResponseBody
    @GetMapping("/getRecentlySyncRecord/{ticketAccountId}")
    public AjaxResult getRecentlySyncRecord(@PathVariable String ticketAccountId) {
        Response<TicketAccountSyncRecordDto> response = ticketAccountSyncRecordService.getRecentlySyncRecord(ticketAccountId);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    /**
     * 应用列表下拉框
     * @return
     */
    @ResponseBody
    @GetMapping("/selectTicketAppList")
    public AjaxResult selectTicketAppList(boolean needControl) {
        Response<List<TicketAppDto>> response = ticketAppService.selectOnlyTicketAppList(needControl);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    /**
     * 工单类型下拉框
     * @return
     */
    @ResponseBody
    @GetMapping("/selectTicketTemplateList")
    public AjaxResult selectTicketTemplateList(boolean needControl) {
        Response<List<TicketTemplateDto>> response = ticketTemplateService.selectOnlyTicketTemplateList(needControl);
        return AjaxResultUtil.responseToAjaxResult(response);
    }
}
