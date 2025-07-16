package com.smy.tfs.biz.controller;

import com.smy.tfs.api.dbo.TicketOriginAccount;
import com.smy.tfs.api.dto.TicketOriginAccountDto;
import com.smy.tfs.api.dto.TicketTemplateDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.service.ITicketOriginAccountService;
import com.smy.tfs.biz.utils.AjaxResultUtil;
import com.smy.tfs.common.core.controller.BaseController;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.core.page.TableDataInfo;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zzd
 * @since 2024-07-01
 */
@Controller
@RequestMapping("/ticketOriginAccount")
public class TicketOriginAccountController extends BaseController {

    @Resource
    private ITicketOriginAccountService ticketOriginAccountService;

    @ResponseBody
    @GetMapping("/list")
    public TableDataInfo selectTicketTemplatePage(TicketOriginAccountDto ticketOriginAccountDto) {
        startPage();
        List<TicketOriginAccount> ticketOriginAccountList = ticketOriginAccountService.selectOriginAccountList(ticketOriginAccountDto);
        return getDataTable(ticketOriginAccountList);
    }

    @ResponseBody
    @PostMapping("/update")
    public AjaxResult update(@RequestBody TicketOriginAccount ticketOriginAccount) {
        Response<String> response = ticketOriginAccountService.updateTicketOriginAccount(ticketOriginAccount);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    @ResponseBody
    @DeleteMapping("/delete/{id}")
    public AjaxResult delete(@PathVariable String id) {
        ticketOriginAccountService.deleteOriginAccount(id);
        return AjaxResult.success();
    }

    @ResponseBody
    @PostMapping("/syncOriginAccountToMapping")
    public AjaxResult syncOriginAccountToMapping(@RequestBody TicketOriginAccountDto ticketOriginAccountDto) {
        Response<String> response = ticketOriginAccountService.syncOriginAccountToMapping(ticketOriginAccountDto.getId());
        return AjaxResultUtil.responseToAjaxResult(response);
    }
}
