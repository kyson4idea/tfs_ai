package com.smy.tfs.biz.component;

import com.smy.tfs.api.dbo.TicketTemplate;
import com.smy.tfs.common.constant.CacheConstants;
import com.smy.tfs.common.core.redis.RedisCache;
import com.smy.tfs.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Slf4j
@Component
public class TicketTemplateComponent {
    @Resource
    private RedisCache redisCache;
    public String getTemplateNameById(String templateId) {
        try {
            if (StringUtils.isEmpty(templateId)) {
                log.error("templateId({})为空", templateId);
                return "";
            }
            String cacheRedisKey = CacheConstants.TFS_TICKET_TEMPLATE + templateId;
            TicketTemplate ticketTemplate = redisCache.getCacheObject(cacheRedisKey);
            String templateName = "";
            if (Objects.isNull(ticketTemplate)) {
                log.warn("缓存中不存在ticketTemplate(id:{})", templateId);
            } else {
                templateName = ticketTemplate.getTicketName();
            }
            return templateName;
        } catch (Exception e){
            log.error("根据templateId({})获取templateName异常：{}", templateId, e);
            return "";
        }
    }

    public String getTemplateCodeById(String templateId) {
        try {
            if (StringUtils.isEmpty(templateId)) {
                log.error("templateId({})为空", templateId);
                return "";
            }
            String cacheRedisKey = CacheConstants.TFS_TICKET_TEMPLATE + templateId;
            TicketTemplate ticketTemplate = redisCache.getCacheObject(cacheRedisKey);
            String templateCode = "";
            if (Objects.isNull(ticketTemplate)) {
                log.warn("缓存中不存在ticketTemplate(id:{})", templateId);
            } else {
                templateCode = ticketTemplate.getTicketTemplateCode();
            }
            return templateCode;
        } catch (Exception e){
            log.error("根据templateId({})获取templateCode异常：{}", templateId, e);
            return "";
        }
    }
}
