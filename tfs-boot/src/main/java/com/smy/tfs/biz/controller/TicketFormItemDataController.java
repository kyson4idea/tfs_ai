package com.smy.tfs.biz.controller;

import cn.hutool.core.util.StrUtil;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.service.ITicketFormItemDataLogService;
import com.smy.tfs.api.service.ITicketFormItemDataService;
import com.smy.tfs.common.annotation.Log;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.enums.BusinessType;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 工单表单项数据
 * </p>
 *
 * @author yss
 * @since 2024-05-31
 */
@RestController
@ResponseBody
public class TicketFormItemDataController {

    @Resource
    private ITicketFormItemDataService ticketFormItemDataService;

    @Resource
    private ITicketFormItemDataLogService ticketFormItemDataLogService;

    @GetMapping({"/ticketFormItemData/getTicketFormItemDataLogById/{ticketFormItemDataId}","/outside/ticketFormItemData/getTicketFormItemDataLogById/{ticketFormItemDataId}"})
    @Log(title = "通过id查询ticketFormItemData日志", businessType = BusinessType.OTHER)
    public AjaxResult getTicketFormItemDataLogById(@PathVariable("ticketFormItemDataId") String ticketFormItemDataId){
        Response response =  ticketFormItemDataLogService.selectTicketFormItemLogById(ticketFormItemDataId);
        if (!response.isSuccess()) {
            Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
            return AjaxResult.error(code,response.getMsg());
        }
        return AjaxResult.success("查询成功",response.getData());
    }

    @PostMapping({"/ticketFormItemData/getTicketFormItemDataLogByLabel","/outside/ticketFormItemData/getTicketFormItemDataLogByLabel"})
    @Log(title = "通过label查询ticketFormItemData日志", businessType = BusinessType.OTHER)
    public AjaxResult getTicketFormItemDataLogByLabel(@RequestBody Map<String, String> ticketIdAndLabelMap){

        String ticketId = ticketIdAndLabelMap.get("ticketId");
        String itemLabel = ticketIdAndLabelMap.get("itemLabel");

        if(StrUtil.isBlank(ticketId) || StrUtil.isBlank(itemLabel)){
            return AjaxResult.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION.getCode(), BizResponseEnums.CHECK_PARAMS_EXCEPTION.getMsg());
        }

        Response response =  ticketFormItemDataLogService.getTicketFormItemDataLogByLabel(ticketId, itemLabel);
        if (!response.isSuccess()) {
            Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
            return AjaxResult.error(code,response.getMsg());
        }
        return AjaxResult.success("查询成功",response.getData());
    }

    /**
     * 根据组件id查询明文字段值
     * @param id
     * @return
     */
    @GetMapping({"/ticketFormItemData/getPlaintextById","/outside/ticketFormItemData/getPlaintextById"})
    @Log(title = "查询掩码明文", businessType = BusinessType.OTHER)
    public AjaxResult getPlaintextById(String id){
        Response<String> response =  ticketFormItemDataService.getPlaintextById(id);
        if (!response.isSuccess()) {
            Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
            return AjaxResult.error(code,response.getMsg());
        }
        return AjaxResult.success("查询成功",response.getData());
    }

    /**
     * 根据组件idList查询掩码明文Map
     * @param idList
     * @return
     */
    @PostMapping({"/ticketFormItemData/getPlaintextMapByIds","/outside/ticketFormItemData/getPlaintextMapByIds"})
    @Log(title = "查询掩码明文Map", businessType = BusinessType.OTHER)
    public AjaxResult getPlaintextMapByIds(@RequestBody List<String> idList){
        Response<Map<String, String>> response =  ticketFormItemDataService.getPlaintextMapByIds(idList);
        if (!response.isSuccess()) {
            Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
            return AjaxResult.error(code,response.getMsg());
        }
        return AjaxResult.success("查询成功",response.getData());
    }

}
