package com.smy.tfs.biz.controller;

import com.smy.tfs.api.dto.DingDingDeptInfo;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.biz.client.DingDingConstant;
import com.smy.tfs.biz.service.IDingDingService;
import com.smy.tfs.common.core.controller.BaseController;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 钉钉数据操作web接口
 * </p>
 *
 * @author yss
 * @since 2024-04-18
 */
@RestController
@ResponseBody
public class DingDingController extends BaseController {
    @Resource
    private IDingDingService dingDingService;

    /**
     * 获取部门树
     * @param deptId
     * @return
     */
    @GetMapping({"/dingding/queryDepts","/outside/dingding/queryDepts"})
    public AjaxResult queryDepts(Long deptId) {
        deptId = DingDingConstant.DINGDING_HEADQUARTER;
        Response<List<DingDingDeptInfo>> response = dingDingService.getDingDingLowerLevelDeptsList(deptId);
        Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
        return new AjaxResult(code, response.getMsg(), response.getData());
    }



}
