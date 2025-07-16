package com.smy.tfs.biz.service.impl;

import cn.hutool.json.JSONUtil;
import com.smy.scm.core.utils.StringUtils;
import com.smy.tfs.api.dto.TicketDataDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.service.ITicketDataService;
import com.smy.tfs.biz.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author z01140
 * @Package: com.smy.tfs.biz.service.impl
 * @Description:
 * @CreateDate 2024/4/28 11:11
 * @UpdateDate 2024/4/28 11:11
 */
@Slf4j
@Service("approveCardButtonCallBack")
public class ApproveCardButtonCallBack implements NotificationService.ICardButtonCallBack {

    @Resource
    private ITicketDataService ticketDataService;

    /**
     * @param key  button key
     * @param qwid qw userid
     */
    @Override
    public void callback(String key, String qwid) {
        if (StringUtils.isBlank(key)) {
            log.warn("企业微信回调参数为空");
            return;
        }

        String[] keyAry = key.split("-");
        if (keyAry.length != 5) {
            log.error("企业微信回调参数格式错误。{}", key);
            return;
        }
        //beanName-工单ID-审批类型-ldap_yinshasha_殷沙沙-审批节点ID
        String ticketDataId = keyAry[1];
        String dealType = keyAry[2];
        String dealOpinion = "";
        String userInfo = keyAry[3];
        String[] userArr = userInfo.split("_");
        if(userArr.length != 3){
            log.error("企业微信审批人参数格式错误。{}", userInfo);
            return;
        }
        String dealNodeId = keyAry[4];
        Response<TicketDataDto> response = ticketDataService.dealTicketDataById(
                ticketDataId,
                dealType,
                dealOpinion,
                userArr[0],
                userArr[1],
                userArr[2],
                dealNodeId
        );
        if(!response.isSuccess()){
            log.error("企业微信卡片审批失败, response = {}",  JSONUtil.toJsonStr(response));
        }
    }
}
