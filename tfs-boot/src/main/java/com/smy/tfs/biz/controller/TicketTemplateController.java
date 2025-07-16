package com.smy.tfs.biz.controller;

import com.smy.tfs.api.dbo.TicketTemplate;
import com.smy.tfs.api.dto.QueryEnableTicketTemplateDto;
import com.smy.tfs.api.dto.TicketTemplateDto;
import com.smy.tfs.api.dto.TicketTemplateGroupDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.enums.TicketTemplateStatusEnum;
import com.smy.tfs.api.service.ITicketTemplateService;
import com.smy.tfs.api.valid.UpdateStatusGroup;
import com.smy.tfs.biz.utils.AjaxResultUtil;
import com.smy.tfs.common.core.controller.BaseController;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.core.page.TableDataInfo;
import com.smy.tfs.common.utils.SecurityUtils;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import lombok.var;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 工单模板操作
 * </p>
 *
 * @author yss
 * @since 2024-04-18
 */
@RestController
@ResponseBody
public class TicketTemplateController extends BaseController {

    @Resource
    private ITicketTemplateService ticketTemplateService;

    @GetMapping("/ticketTemplate/selectTicketTemplatePage")
    public TableDataInfo selectTicketTemplatePage(TicketTemplateDto ticketTemplateDto) {
        List<TicketTemplateDto> ticketTemplateDtoList = ticketTemplateService.selectTicketTemplateList(ticketTemplateDto);
        return getDataTable(ticketTemplateDtoList);
    }

    @PostMapping("/ticketTemplate/selectEnableTicketTemplateList")
    public AjaxResult selectTicketTemplateList(@RequestBody TicketTemplateDto ticketTemplateDto) {
        ticketTemplateDto.setTicketStatus(TicketTemplateStatusEnum.ENABLE.getCode());
        List<TicketTemplateDto> ticketTemplateDtoList = ticketTemplateService.selectTicketTemplateList(ticketTemplateDto);
        return AjaxResult.success(ticketTemplateDtoList);
    }

    @PostMapping({"/ticketTemplate/selectTicketTemplateListWithGroup","/outside/ticketTemplate/selectTicketTemplateListWithGroup"})
    public AjaxResult selectTicketTemplateListWithGroup(@RequestBody TicketTemplateDto ticketTemplateDto) {
        ticketTemplateDto.setTicketStatus(TicketTemplateStatusEnum.ENABLE.getCode());
        List<TicketTemplateGroupDto> ticketTemplateGroupDtoList = ticketTemplateService.selectTicketTemplateListWithGroup(ticketTemplateDto);
        return AjaxResult.success(ticketTemplateGroupDtoList);
    }



//    /**
//     * 获取工单模版Id
//     * @param
//     * @return
//     */
//    @GetMapping("/getTicketTemplateId")
//    @PreAuthorize("@ss.hasPermi('tfs:tickettemplate:get')")
//    @Log(title = "工单模版", businessType = BusinessType.OTHER)
//    @ApiOperation("工单模版")
//    public AjaxResult getTicketTemplateId(){
//        return AjaxResult.success(ticketTemplateService.getTicketTemplateId());
//    }


    /**
     * 保存工单模版
     *
     * @param ticketTemplateDto
     * @return
     */
    @PostMapping("/ticketTemplate/save")
    public AjaxResult save(@RequestBody TicketTemplateDto ticketTemplateDto) {
        String userType = getLoginUser().getUserType();
        String userId = getLoginUser().getUsername();
        String userName = getLoginUser().getNickName();
        Response response = ticketTemplateService.save(ticketTemplateDto, userType, userId, userName);
        Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
        return new AjaxResult(code, response.getMsg(), response.getData());
    }

    /**
     * 获取工单模版Id
     * @param
     * @return
     */
    @GetMapping({"/ticketTemplate/getTicketTemplateFull/{ticketTemplateId}", "/outside/ticketTemplate/getTicketTemplateFull/{ticketTemplateId}"})
    public AjaxResult getTicketTemplateFull(@PathVariable("ticketTemplateId") String ticketTemplateId){
        String applyUser = SecurityUtils.getAccountUserInfo();
        Response<TicketTemplateDto> response = ticketTemplateService.selectTicketTemplateFullById(ticketTemplateId, applyUser);
        Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
        return new AjaxResult(code,response.getMsg(),response.getData());
    }

    /**
     * 复制工单模板
     *
     * @param ticketTemplateDto
     * @return
     */
    @PostMapping({"/ticketTemplate/copyTicketTemplate", "/outside/ticketTemplate/copyTicketTemplate"})
    public AjaxResult copyTicketTemplate(@RequestBody TicketTemplateDto ticketTemplateDto){

        String userType = getLoginUser().getUserType();
        String userId = getLoginUser().getUsername();
        String userName = getLoginUser().getNickName();
        
        Response<String> response = ticketTemplateService.copyTicketTemplate(ticketTemplateDto, userType, userId, userName);
        Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());

        return new AjaxResult(code, response.getMsg(), response.getData());
    }

    /**
     * 启用工单模版
     *
     * @param ticketTemplateDto
     * @return
     */
    @PostMapping("/ticketTemplate/enableTicketTemplate")
    public AjaxResult enableTicketTemplate(@Validated({UpdateStatusGroup.class}) @RequestBody TicketTemplateDto ticketTemplateDto) {
        Response<Boolean> response = ticketTemplateService.updateTicketTemplateStatus(ticketTemplateDto.getId(), TicketTemplateStatusEnum.ENABLE);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    /**
     * 暂停工单模版
     *
     * @param ticketTemplateDto
     * @return
     */
    @PostMapping("/ticketTemplate/pauseTicketTemplate")
    public AjaxResult pauseTicketTemplate(@Validated({UpdateStatusGroup.class}) @RequestBody TicketTemplateDto ticketTemplateDto) {
        Response<Boolean> response = ticketTemplateService.updateTicketTemplateStatus(ticketTemplateDto.getId(), TicketTemplateStatusEnum.PAUSE);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    /**
     * 作废工单模版
     *
     * @param ticketTemplateDto
     * @return
     */
    @PostMapping("/ticketTemplate/cancelTicketTemplate")
    public AjaxResult cancelTicketTemplate(@Validated({UpdateStatusGroup.class}) @RequestBody TicketTemplateDto ticketTemplateDto) {
        Response<Boolean> response = ticketTemplateService.updateTicketTemplateStatus(ticketTemplateDto.getId(), TicketTemplateStatusEnum.CANCEL);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    /**
     * 这个应用下的模版，以及该应用订阅的模版
     * @param appId
     * @return
     */
    @GetMapping({"/ticketTemplate/selectTicketTemplateListByAppId/{appId}","/outside/ticketTemplate/selectTicketTemplateListByAppId/{appId}"})
    public AjaxResult selectTicketTemplateListByAppId(@PathVariable("appId") String appId) {
        Response<List<TicketTemplate>> response = ticketTemplateService.selectTicketTemplateListByAppId(appId);
        Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
        return new AjaxResult(code,response.getMsg(),response.getData());
    }

    @PostMapping({"/ticketTemplate/queryEnableTicketTemplates","/outside/ticketTemplate/queryEnableTicketTemplates"})
    public AjaxResult queryEnableTicketTemplats(@RequestBody QueryEnableTicketTemplateDto queryEnableTicketTemplateDto) {
        queryEnableTicketTemplateDto.setTicketStatus(TicketTemplateStatusEnum.ENABLE.getCode());
        Response<List<TicketTemplateDto>> response = ticketTemplateService.queryTicketTemplates(queryEnableTicketTemplateDto);
        if (!response.isSuccess()) {
            return AjaxResultUtil.responseToAjaxResult(response);
        }
        return AjaxResult.success(response.getData());
    }

    /**
     * 获取工单模版Id
     * @param
     * @return
     */
    @GetMapping({"/ticketTemplate/getTicketTemplate/{ticketTemplateId}", "/outside/ticketTemplate/getTicketTemplate/{ticketTemplateId}"})
    public AjaxResult getTicketTemplate(@PathVariable("ticketTemplateId") String ticketTemplateId){
        Response<TicketTemplateDto> response = ticketTemplateService.selectTicketTemplateById(ticketTemplateId);
        Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
        return new AjaxResult(code,response.getMsg(),response.getData());
    }


}
