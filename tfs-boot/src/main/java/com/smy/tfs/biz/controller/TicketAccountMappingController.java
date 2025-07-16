package com.smy.tfs.biz.controller;

import com.smy.tfs.api.dbo.TicketAccountMapping;
import com.smy.tfs.api.dto.PluginVisibleDto;
import com.smy.tfs.api.dto.TicketAccountDto;
import com.smy.tfs.api.dto.TicketAccountMappingDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.enums.RecordStatusEnum;
import com.smy.tfs.api.service.ITicketAccountMappingService;
import com.smy.tfs.api.service.ITicketAccountService;
import com.smy.tfs.common.core.controller.BaseController;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.core.page.TableDataInfo;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 账户体系映射表 前端控制器
 * </p>
 *
 * @author zzd
 * @since 2024-05-07
 */
@RestController
@ResponseBody
public class TicketAccountMappingController extends BaseController {
    @Resource
    private ITicketAccountMappingService ticketAccountMappingService;
    @Resource
    private ITicketAccountService ticketAccountService;

    /**
     * 当前登录人是否有权限看"高级查询"小浮标的权限
     * @param pluginVisibleDto
     * @return
     */
    @CrossOrigin(origins = "*")
    @PostMapping({"/ticketAccountMapping/pluginVisible","/outside/ticketAccountMapping/pluginVisible"})
    public AjaxResult pluginVisible(@Valid @RequestBody PluginVisibleDto pluginVisibleDto) {
        Response<Boolean> response = ticketAccountMappingService.pluginVisible(pluginVisibleDto);
        Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
        return new AjaxResult(code, response.getMsg(), response.getData());
    }

    @GetMapping("/ticketAccountMapping/list")
    public TableDataInfo systemManagementPage(TicketAccountMappingDto ticketAccountMappingDto) {
        startPage();
        Response<List<TicketAccountMapping>> response = ticketAccountMappingService.queryTicketAccountMappingList(ticketAccountMappingDto);
        if (!response.isSuccess()) {
            Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
            return getErrorDataTable(code,response.getMsg());
        }
        TableDataInfo tableDataInfo = getDataTable(response.getData());
        List<TicketAccountMapping> ticketAccountMappingList = response.getData();
        List<TicketAccountMappingDto> ticketAccountMappingDtoList = new ArrayList<>();

        TicketAccountDto ticketAccountDto = new TicketAccountDto();
        ticketAccountDto.setStatus(RecordStatusEnum.NORMAL.getCode());
        Response<List<TicketAccountDto>> tadResponse = ticketAccountService.selectTicketAccountList(ticketAccountDto);
        List<TicketAccountDto> tadList = new ArrayList<>();
        if (tadResponse.isSuccess()) {
            tadList = tadResponse.getData();
        }
        //构造Map<String, TicketAccountDto>: key: ticketAccountType
        Map<String, TicketAccountDto> tadMap = new HashMap<>();
        if (ObjectHelper.isNotEmpty(tadList)) {
            tadMap = tadList.stream().collect(Collectors.toMap(TicketAccountDto::getTicketAccountType, Function.identity()));
        }

        //解析AccountTypeName字段
        if (ObjectHelper.isNotEmpty(ticketAccountMappingList)) {
            for (TicketAccountMapping ticketAccountMapping : ticketAccountMappingList) {
                TicketAccountMappingDto tamd = new TicketAccountMappingDto(ticketAccountMapping);
                if (ObjectHelper.isNotEmpty(tadMap) && ObjectHelper.isNotEmpty(tadMap.get(ticketAccountMapping.getAccountType())) && ObjectHelper.isNotEmpty(tadMap.get(ticketAccountMapping.getAccountType()).getTicketAccountName())) {
                    tamd.setAccountTypeName(tadMap.get(ticketAccountMapping.getAccountType()).getTicketAccountName());
                } else {
                    tamd.setAccountTypeName(ticketAccountMapping.getAccountType());
                }
                ticketAccountMappingDtoList.add(tamd);
            }
        }

        tableDataInfo.setRows(ticketAccountMappingDtoList);
        return tableDataInfo;
    }

    @RequestMapping(value = "/ticketAccountMapping/save", method = {RequestMethod.POST,RequestMethod.PUT})
    public AjaxResult save(@Valid @RequestBody TicketAccountMappingDto ticketAccountMappingDto) {
        Response<String> response = ticketAccountMappingService.save(ticketAccountMappingDto);
        if (!response.isSuccess()) {
            Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
            return AjaxResult.error(code,response.getMsg());
        }
        return AjaxResult.success("保存成功",response.getData());
    }
    @DeleteMapping("/ticketAccountMapping/delete/{id}")
    public AjaxResult delete(@PathVariable String id) {
        Response<String> response = ticketAccountMappingService.delete(id);
        Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
        return new AjaxResult(code,response.getMsg());
    }


}
