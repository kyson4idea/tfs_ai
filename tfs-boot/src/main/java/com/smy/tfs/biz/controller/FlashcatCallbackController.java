package com.smy.tfs.biz.controller;

import com.smy.tfs.api.dto.FlashcatAlertCallbackDto;
import com.smy.tfs.api.dto.FlashcatAlertTicketStatusDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.service.IFlashcatAlertService;
import com.smy.tfs.biz.utils.AjaxResultUtil;
import com.smy.tfs.common.annotation.Anonymous;
import com.smy.tfs.common.core.domain.AjaxResult;
import org.apache.dubbo.apidocs.annotations.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/flashcat")
public class FlashcatCallbackController {
    @Resource
    private IFlashcatAlertService iFlashcatAlertService;

    @PostMapping("/alert/callback")
    @Anonymous
    public AjaxResult receiveAlert(@RequestParam("key") String key, @RequestBody FlashcatAlertCallbackDto flashcatAlertCallbackDto){
        Response<String> response = iFlashcatAlertService.handleAlert(key, flashcatAlertCallbackDto);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    @PostMapping("/alert/tickets")
    @Anonymous
    public AjaxResult getLastAlertTicketsByRuleIds(@RequestBody FlashcatAlertTicketStatusDto.AlertRuleIdsReq ruleIdsReq) {
        return AjaxResultUtil.success(iFlashcatAlertService.getLastAlertTicketsByRuleIds(ruleIdsReq.getRuleIds(), ruleIdsReq.getStartDate(), ruleIdsReq.getApplyingOnly()));
    }
}
