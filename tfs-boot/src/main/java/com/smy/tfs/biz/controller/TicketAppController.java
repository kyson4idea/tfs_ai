package com.smy.tfs.biz.controller;

import com.smy.tfs.api.dto.ExtendFieldsMappingDto;
import com.smy.tfs.api.dto.TicketAppDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.service.ITicketAppService;
import com.smy.tfs.biz.utils.AjaxResultUtil;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.utils.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * <p>
 * 应用数据
 * </p>
 *
 * @author yss
 * @since 2024-06-17
 */
@Controller
@ResponseBody
public class TicketAppController {
    @Resource
    private ITicketAppService ticketAppService;

    @GetMapping("/ticketApp/getExtendFieldsMapping")
    public AjaxResult getExtendFieldsMapping(TicketAppDto ticketAppDto){
        if (Objects.isNull(ticketAppDto)
                || StringUtils.isEmpty(ticketAppDto.getId())) {
            return AjaxResult.error("业务id为空");
        }
        Response<ExtendFieldsMappingDto> response = ticketAppService.getExtendFieldsMapping(ticketAppDto.getId());
        if (!response.isSuccess()) {
            return AjaxResultUtil.responseToAjaxResult(response);
        }
        ExtendFieldsMappingDto busiFieldsMappingDto = new ExtendFieldsMappingDto();
        if (null != response.getData()) {
            busiFieldsMappingDto.setExtendFields(response.getData().getExtendFields());
            busiFieldsMappingDto.setExtendEnabled(response.getData().getExtendEnabled());
        }
        return AjaxResultUtil.success(busiFieldsMappingDto);
    }



}
