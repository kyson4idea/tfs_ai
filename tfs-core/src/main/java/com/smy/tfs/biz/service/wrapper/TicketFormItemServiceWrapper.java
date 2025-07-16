package com.smy.tfs.biz.service.wrapper;

import cn.hutool.core.collection.CollUtil;
import com.smy.tfs.api.dbo.TicketFormItemData;
import com.smy.tfs.api.dto.TicketFormItemDataDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.service.ITicketFormItemDataService;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.openapi.service.ITicketFormItemServiceWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.apidocs.annotations.ApiModule;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 工单表单数据表 服务实现类
 * </p>
 *
 * @author yss
 * @since 2024-05-29
 */
@Slf4j
@Component("ticketFormItemServiceWrapper")
@org.apache.dubbo.config.annotation.Service
@ApiModule(value = "工单表单对外数据服务", apiInterface = ITicketFormItemServiceWrapper.class)
public class TicketFormItemServiceWrapper implements ITicketFormItemServiceWrapper {

    @Resource
    private ITicketFormItemDataService ticketFormItemDataService;


    @Override
    public Response<TicketFormItemDataDto> selectFormItemsByTicketId(String ticketId) {
        if (StringUtils.isEmpty(ticketId)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单Id不能为空");
        }
        List<TicketFormItemData> ticketFormItemDataList = ticketFormItemDataService.selectTicketFormByDataId(ticketId);
        if (CollUtil.isEmpty(ticketFormItemDataList)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单表单数据不能为空");
        }
        List<TicketFormItemDataDto> ticketFormItemDataDtoList = ticketFormItemDataList.stream()
                .map(it-> new TicketFormItemDataDto(it)).collect(Collectors.toList());
        return Response.success(ticketFormItemDataDtoList);
    }
}