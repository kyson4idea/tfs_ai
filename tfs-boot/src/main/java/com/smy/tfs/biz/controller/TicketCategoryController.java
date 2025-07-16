package com.smy.tfs.biz.controller;

import com.smy.tfs.api.dbo.TicketApp;
import com.smy.tfs.api.dbo.TicketCategory;
import com.smy.tfs.api.dto.BatchUpdateTicketCategoryStatusDto;
import com.smy.tfs.api.dto.CategoryEnabledDto;
import com.smy.tfs.api.dto.TicketCategoryDto;
import com.smy.tfs.api.enums.CategoryStatusEnum;
import com.smy.tfs.api.enums.YESNOEnum;
import com.smy.tfs.api.service.ITicketAppService;
import com.smy.tfs.api.service.ITicketCategoryService;
import com.smy.tfs.biz.utils.AjaxResultUtil;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.core.domain.model.LoginUser;
import com.smy.tfs.common.exception.ServiceException;
import com.smy.tfs.common.utils.SecurityUtils;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import com.smy.tfs.openapi.service.ITicketCategoryServiceWrapper;
import lombok.var;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 工单分类
 * </p>
 *
 * @author yss
 * @since 2024-11-04
 */
@RestController
@ResponseBody
public class TicketCategoryController {
    @Resource
    private ITicketAppService ticketAppService;
    @Resource
    private ITicketCategoryService ticketCategoryService;

    @Resource
    private ITicketCategoryServiceWrapper ticketCategoryServiceWrapper;

    /**
     * 设置开启工单分类标识
     * @param
     * @return
     */
    @PostMapping("/ticketCategory/setCategoryEnabled")
    public AjaxResult setCategoryEnabled(@RequestBody CategoryEnabledDto categoryEnabledDto) {
        if (ObjectHelper.anyIsEmpty(categoryEnabledDto,
                categoryEnabledDto.getAppId(),
                categoryEnabledDto.getCategoryEnabled())) {
            return AjaxResult.error("业务id、工单分类开启/关闭标识为空");
        }
        String appId = categoryEnabledDto.getAppId();
        YESNOEnum categoryEnabled = categoryEnabledDto.getCategoryEnabled();
        boolean updateFlag = ticketAppService.lambdaUpdate()
                .eq(TicketApp::getId,appId)
                .set(TicketApp::getCategoryEnabled,categoryEnabled)
                .update();
        if (!updateFlag) {
            return AjaxResult.error(String.format("设置业务(%s)工单分类开启/关闭标识(%s)失败", appId, categoryEnabled));
        }
        return AjaxResult.success();
    }

    /**
     * 查询开启/关闭工单分类标识
     * @param
     * @return
     */
    @GetMapping({"/ticketCategory/getCategoryEnabled","/outside/ticketCategory/getCategoryEnabled"})
    public AjaxResult getCategoryEnabled(String appId) {
        Optional<TicketApp> opt = ticketAppService.lambdaQuery()
                .eq(TicketApp::getId,appId)
                .oneOpt();
        if (!opt.isPresent()) {
            return AjaxResult.error(String.format("相关的业务(%s)数据不存在",appId));
        }
        TicketApp ticketApp = opt.get();
        CategoryEnabledDto categoryEnabledDto = new CategoryEnabledDto();
        categoryEnabledDto.setAppId(appId);
        categoryEnabledDto.setCategoryEnabled(ticketApp.getCategoryEnabled());
        return AjaxResult.success(categoryEnabledDto);
    }

    /**
     * 根据条件查询工单分类列表
     *
     *
     * @return
     */
    @GetMapping({"/ticketCategory/queryTicketCategoryList","/outside/ticketCategory/queryTicketCategoryList"})
    public AjaxResult queryTicketCategoryList(TicketCategoryDto ticketCategoryDto) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String userName = loginUser.getNickName();
        String userId = loginUser.getUsername();
        String userType = loginUser.getUserType();
        String appId = ticketCategoryDto.getAppId();
        if (ticketCategoryDto.isCategoryEnabledCheck() && StringUtils.isNotEmpty(appId)) {
            var categoryEnabledResp = ticketCategoryService.categoryEnabled(appId);
            if (!categoryEnabledResp.isSuccess()) {
                return AjaxResult.error(String.format("查询业务（id:%s）开启分类配置异常", appId));
            }
            if (Objects.isNull(categoryEnabledResp.getData()) || YESNOEnum.NO == categoryEnabledResp.getData()) {
                return AjaxResult.error(String.format("业务（id:%s）未开启分类配置", appId));
            }
        }
        return AjaxResultUtil.responseToAjaxResult(ticketCategoryService.queryTicketCategoryList(ticketCategoryDto, userType, userId, userName));
    }

    /**
     * 根据条件查询工单分类列表
     *
     *
     * @return
     */
    @GetMapping({"/ticketCategory/categoryEnabled","/outside/ticketCategory/categoryEnabled"})
    public AjaxResult categoryEnabled(String appId) {
        return AjaxResultUtil.responseToAjaxResult(ticketCategoryServiceWrapper.categoryEnabled(appId));
    }

    /**
     *新增
     *
     * @return
     */
    @PostMapping({"/ticketCategory/add"})
    public AjaxResult add(@RequestBody TicketCategoryDto ticketCategoryDto) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String userName = loginUser.getNickName();
        String userId = loginUser.getUsername();
        String userType = loginUser.getUserType();
//        String userName = "yss";
//        String userId = "y01781";
//        String userType = "ldap";
        return AjaxResultUtil.responseToAjaxResult(ticketCategoryService.add(ticketCategoryDto, userName, userId, userType));
    }

    /**
     *编辑：可能关联多个模版，模版和分类的关系表
     *
     * @return
     */
    @PostMapping({"/ticketCategory/edit"})
    public AjaxResult edit(@RequestBody TicketCategoryDto ticketCategoryDto) {
        return AjaxResultUtil.responseToAjaxResult(ticketCategoryService.update(ticketCategoryDto));
    }

    /**
     *删除工单分类
     *
     * @return
     */
    @PostMapping({"/ticketCategory/del"})
    public AjaxResult del(@RequestBody TicketCategoryDto ticketCategoryDto) {
        Integer id = ticketCategoryDto.getId();
        return AjaxResultUtil.responseToAjaxResult(ticketCategoryService.delete(id));
    }


    /**
     *批量修改順序
     *
     * @return
     */
    @PostMapping({"/ticketCategory/updateSort"})
    public AjaxResult updateSort(@RequestBody List<TicketCategoryDto> ticketCategoryDtoList) {
        return AjaxResultUtil.responseToAjaxResult(ticketCategoryService.updateSort(ticketCategoryDtoList));
    }


    /**
     *编辑：可能关联多个模版，模版和分类的关系表
     *
     * @return
     */
    @PostMapping({"/ticketCategory/batchUpdateStatus"})
    public AjaxResult batchUpdateStatus(@RequestBody BatchUpdateTicketCategoryStatusDto batchUpdateTicketCategoryStatusDto) {
        List<Integer> idList = batchUpdateTicketCategoryStatusDto.getIdList();
        CategoryStatusEnum status = batchUpdateTicketCategoryStatusDto.getStatus();
        if (CollectionUtils.isEmpty(idList) || Objects.isNull(status)) {
            throw new ServiceException("参数异常");
        }
        List<TicketCategory> ticketCategoryList = idList.stream().map(it->{
            TicketCategory ticketCategory = new TicketCategory();
            ticketCategory.setId(it);
            ticketCategory.setStatus(batchUpdateTicketCategoryStatusDto.getStatus());
            return ticketCategory;
        }).collect(Collectors.toList());
        if (!ticketCategoryService.updateBatchById(ticketCategoryList)) {
            throw new ServiceException("状态更新异常");
        }
        return AjaxResult.success();
    }


}
