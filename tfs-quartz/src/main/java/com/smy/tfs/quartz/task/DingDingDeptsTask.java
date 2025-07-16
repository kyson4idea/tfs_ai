package com.smy.tfs.quartz.task;

import com.smy.tfs.biz.service.IDingDingService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 保存钉钉的部门树到redis缓存
 */
@Component("dingDingDeptsTask")
public class DingDingDeptsTask {

    @Resource
    private IDingDingService dingDingService;

    public void saveDingDingLowerLevelDeptsToCache(){
        dingDingService.saveDingDingLowerLevelDeptsToCache(1L);
    }


}
