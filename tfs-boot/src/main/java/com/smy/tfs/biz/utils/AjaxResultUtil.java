package com.smy.tfs.biz.utils;

import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.utils.bean.ObjectHelper;

public class AjaxResultUtil extends AjaxResult{

    /**
     * 将response转换为AjaxResult
     * @param response
     * @return
     */
    public static AjaxResult responseToAjaxResult(Response response) {
        Integer code = ObjectHelper.isNotEmpty(response.getCode()) ?
                Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
        return new AjaxResult(code, response.getMsg(), response.getData());
    }

}
