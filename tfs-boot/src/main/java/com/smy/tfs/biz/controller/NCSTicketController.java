package com.smy.tfs.biz.controller;

import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.service.INCSTicketImportService;
import com.smy.tfs.biz.utils.AjaxResultUtil;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.utils.SecurityUtils;
import lombok.var;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/ncs")
public class NCSTicketController {
    @Resource
    private INCSTicketImportService ncsTicketImportService;

    @RequestMapping("/import-tickets")
    public AjaxResult importDataFromExcel(@RequestParam("file") MultipartFile file) {
        var loginUser = SecurityUtils.getLoginUser();
        AccountInfo accountInfo = new AccountInfo(loginUser.getSameOriginId(), loginUser.getUserType(), loginUser.getUsername(), loginUser.getNickName());
        return AjaxResultUtil.success(ncsTicketImportService.importDataFromExcel(file, accountInfo));
    }

    @RequestMapping("/import-extra-fields")
    public AjaxResult importExtraFields(@RequestParam("file") MultipartFile file) {
        var loginUser = SecurityUtils.getLoginUser();
        AccountInfo accountInfo = new AccountInfo(loginUser.getSameOriginId(), loginUser.getUserType(), loginUser.getUsername(), loginUser.getNickName());
        return AjaxResultUtil.success(ncsTicketImportService.importExtraDataFromExcel(file, accountInfo));
    }

    @RequestMapping("/update-ticket-values")
    public AjaxResult updateTicketCreateTimeFromExcel(@RequestParam("file") MultipartFile file) {
        return AjaxResultUtil.success(ncsTicketImportService.updateTicketValuesFromExcel(file));
    }

    @RequestMapping("/reverse-ticket-flow-nodes")
    public AjaxResult reverseTicketFlowNodeData(@RequestParam("file") MultipartFile file) {
        return AjaxResultUtil.success(ncsTicketImportService.reverseTicketFlowNodeData(file));
    }
}
