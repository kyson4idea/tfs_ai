package com.smy.tfs.biz.component;

import com.smy.tfs.api.dbo.TicketCategory;
import com.smy.tfs.common.constant.CacheConstants;
import com.smy.tfs.common.core.redis.RedisCache;
import com.smy.tfs.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Slf4j
@Component
public class TicketCategoryComponent {
    @Resource
    private RedisCache redisCache;

    public String getCategoryNameByTemplateId(String templateId) {
        try {
            if (StringUtils.isEmpty(templateId)) {
                log.error("templateId({})为空", templateId);
                return "";
            }
            String cacheRedisKey = CacheConstants.TFS_TICKET_TEMPLATEID_CATEGORY + templateId;
            TicketCategory ticketCategory = redisCache.getCacheObject(cacheRedisKey);
            String categoryName = "";
            if (Objects.isNull(ticketCategory)) {
                log.warn("缓存中不存在模版id({})对应ticketCategory", templateId);
            } else {
                categoryName = ticketCategory.getName();
            }
            return categoryName;
        } catch (Exception e){
            log.error("根据templateId({})获取categoryName异常：{}", templateId, e);
            return "";
        }
    }
    public String getCategoryIdByTemplateId(String templateId) {
        try {
            if (StringUtils.isEmpty(templateId)) {
                log.error("templateId({})为空", templateId);
                return "";
            }
            String cacheRedisKey = CacheConstants.TFS_TICKET_TEMPLATEID_CATEGORY + templateId;
            TicketCategory ticketCategory = redisCache.getCacheObject(cacheRedisKey);
            String categoryId = "";
            if (Objects.isNull(ticketCategory)) {
                log.warn("缓存中不存在模版id({})对应ticketCategory", templateId);
            } else {
                categoryId = String.valueOf(ticketCategory.getId());
            }
            return categoryId;
        } catch (Exception e){
            log.error("根据templateId({})获取categoryName异常：{}", templateId, e);
            return "";
        }
    }
}
