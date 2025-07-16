package com.smy.tfs.quartz.task;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.smy.tfs.api.constants.TfsBaseConstant;
import com.smy.tfs.api.service.ITicketAccountService;
import com.smy.tfs.common.utils.SecurityUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("ticketAccountTask")
public class TicketAccountTask {
    @Resource
    private ITicketAccountService ticketAccountService;

    /**
     * 调度任务触发 每日同步账号体系
     */
    public void syncTicketRemoteAccount() {
        SecurityUtils.wrapContext(TfsBaseConstant.defaultOriginId, TfsBaseConstant.defaultUserId, TfsBaseConstant.defaultUserName,
                TfsBaseConstant.defaultUserType, TfsBaseConstant.defaultAppId, () -> {
                    ticketAccountService.syncTicketRemoteAccount();
                });
    }

    public void syncTicketRemoteAccountById(String id) {
        SecurityUtils.wrapContext(TfsBaseConstant.defaultOriginId, TfsBaseConstant.defaultUserId, TfsBaseConstant.defaultUserName,
                TfsBaseConstant.defaultUserType, TfsBaseConstant.defaultAppId, () -> {
                    ticketAccountService.doSyncTicketAccountConfigNoAuth(id);
                });
    }

    public void syncAccountMappingQywxId(String jsonParam) {
        JSONArray paramArrays = JSONUtil.parseArray(jsonParam);
        for (Object paramArray : paramArrays) {
            JSONObject paramObject = JSONUtil.parseObj(paramArray);
            String accountType = paramObject.getStr("accountType");
            String qwCorporate = paramObject.getStr("qwCorporate");
            if (StrUtil.isAllNotBlank(accountType, qwCorporate)) {
                SecurityUtils.wrapContext(TfsBaseConstant.defaultOriginId, TfsBaseConstant.defaultUserId, TfsBaseConstant.defaultUserName,
                        TfsBaseConstant.defaultUserType, TfsBaseConstant.defaultAppId, () -> {
                            ticketAccountService.syncAccountMappingQywxId(accountType, qwCorporate);
                        });
            }
        }
    }

}
