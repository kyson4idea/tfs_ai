package com.smy.tfs.quartz.task;

import com.smy.tfs.api.dbo.TicketApp;
import com.smy.tfs.api.dbo.TicketCategory;
import com.smy.tfs.api.dbo.TicketTemplate;
import com.smy.tfs.api.service.ITicketAppService;
import com.smy.tfs.api.service.ITicketCategoryService;
import com.smy.tfs.api.service.ITicketTemplateService;
import com.smy.tfs.common.constant.CacheConstants;
import com.smy.tfs.common.core.redis.RedisCache;
import com.smy.tfs.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Component("ticketCachaTask")
@Slf4j
public class TicketCachaTask {

    @Resource
    private RedisCache redisCache;
    @Resource
    private ITicketTemplateService ticketTemplateService;
    @Resource
    private ITicketAppService ticketAppService;
    @Resource
    private ITicketCategoryService ticketCategoryService;

    public void syncCacha(String syncType) {
        log.info("开始同步({})到缓存", syncType);
        try {
            if (StringUtils.isEmpty(syncType) || Objects.equals(syncType,"app")) {
                log.info("开始同步工单应用信息到缓存");
                Integer appCount = ticketAppService.lambdaQuery().count();
                if (null == appCount || 0 == appCount || appCount > 10000) {
                    log.error("工单应用不存在，或者超过10000条");
                }
                List<TicketApp> ticketAppList = ticketAppService.lambdaQuery().last("limit 10000").list();
                if (CollectionUtils.isNotEmpty(ticketAppList)) {
                    ticketAppList.stream().forEach(it->{
                        redisCache.setCacheObject(CacheConstants.TFS_TICKET_APP + it.getId(), it);
                    });
                }
            }
            if (StringUtils.isEmpty(syncType) || Objects.equals(syncType,"template")) {
                log.info("开始同步工单模板信息到缓存");
                Integer templateCount = ticketTemplateService.lambdaQuery().count();
                if (null == templateCount || 0 == templateCount || templateCount > 10000) {
                    log.error("工单模板不存在，或者超过10000条");
                }
                List<TicketTemplate> ticketTemplateList = ticketTemplateService.lambdaQuery().last("limit 10000").list();
                if (CollectionUtils.isNotEmpty(ticketTemplateList)) {
                    ticketTemplateList.stream().forEach(it->{
                        redisCache.setCacheObject(CacheConstants.TFS_TICKET_TEMPLATE + it.getId(), it);
                    });
                }
            }
            if (StringUtils.isEmpty(syncType) || Objects.equals(syncType,"category")) {
                log.info("开始同步工单分类信息到缓存");
                Integer categoryCount = ticketCategoryService.lambdaQuery().count();
                if (null == categoryCount || 0 == categoryCount || categoryCount > 10000) {
                    log.error("工单分类不存在，或者超过10000条");
                }
                List<TicketCategory> ticketCategoryList = ticketCategoryService.lambdaQuery().last("limit 10000").list();
                if (CollectionUtils.isNotEmpty(ticketCategoryList)) {
                    ticketCategoryList.stream().forEach(it->{
                        if (StringUtils.isNotEmpty(it.getTemplateId())) {
                            redisCache.setCacheObject(CacheConstants.TFS_TICKET_TEMPLATEID_CATEGORY + it.getTemplateId(), it);
                        }
                    });
                }
            }
        } catch (Exception e){
            log.error("根据syncType({})同步异常：{}", syncType, e);
        }
        log.info("结束同步({})到缓存", syncType);
    }




}
