package com.smy.tfs.biz.service.wrapper;

import com.smy.tfs.api.dto.TicketCategoryDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.YESNOEnum;
import com.smy.tfs.api.service.ITicketCategoryService;
import com.smy.tfs.openapi.service.ITicketCategoryServiceWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.apidocs.annotations.ApiModule;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 工单数据表 服务实现类
 * </p>
 *
 * @author yss
 * @since 2025-03-18
 */
@Slf4j
@Component("ticketCategoryServiceWrapper")
@org.apache.dubbo.config.annotation.Service
@ApiModule(value = "工单分类对外数据服务", apiInterface = ITicketCategoryServiceWrapper.class)
public class TicketCategoryServiceWrapper implements ITicketCategoryServiceWrapper {
    @Resource
    private ITicketCategoryService ticketCategoryService;
    @Override
    public Response<List<TicketCategoryDto>> queryTicketCategoryList(TicketCategoryDto ticketCategoryDto, String userType, String userId, String userName) {
        return ticketCategoryService.queryTicketCategoryList(ticketCategoryDto, userType, userId, userName);
    }

    @Override
    public Response<YESNOEnum> categoryEnabled(String appId) {
        return ticketCategoryService.categoryEnabled(appId);
    }
}