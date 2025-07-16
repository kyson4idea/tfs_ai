package com.smy.tfs.biz.service;

import com.smy.tfs.api.dto.DingDingDeptInfo;
import com.smy.tfs.api.dto.base.Response;

import java.util.List;

public interface IDingDingService {

    Response<List<DingDingDeptInfo>> getDingDingLowerLevelDeptsList(long deptId);

    void saveDingDingLowerLevelDeptsToCache(Long topLeveldeptId);



}
