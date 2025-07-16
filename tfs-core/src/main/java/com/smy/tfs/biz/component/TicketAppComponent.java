package com.smy.tfs.biz.component;

import com.smy.tfs.api.dbo.TicketApp;
import com.smy.tfs.common.constant.CacheConstants;
import com.smy.tfs.common.core.redis.RedisCache;
import com.smy.tfs.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Slf4j
@Component
public class TicketAppComponent {
    @Resource
    private RedisCache redisCache;
    public String getAppNameById(String appId) {
        try {
            if (StringUtils.isEmpty(appId)) {
                log.error("appId({})为空", appId);
                return "";
            }
            String cacheRedisKey = CacheConstants.TFS_TICKET_APP + appId;
            TicketApp ticketApp = redisCache.getCacheObject(cacheRedisKey);
            String appName = "";
            if (Objects.isNull(ticketApp)) {
                log.warn("缓存中不存在ticketApp(id:{})", appId);
            } else {
                appName = ticketApp.getAppName();
            }
            return appName;
        } catch (Exception e){
            log.error("根据appId({})获取appName异常：{}", appId, e);
            return "";
        }
    }
}
