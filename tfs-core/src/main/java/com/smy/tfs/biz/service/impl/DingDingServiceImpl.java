package com.smy.tfs.biz.service.impl;

import com.dingtalk.api.response.OapiV2DepartmentListsubResponse;
import com.smy.tfs.api.dto.DingDingDeptInfo;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.biz.client.DingDingClient;
import com.smy.tfs.biz.client.DingDingConstant;
import com.smy.tfs.biz.service.IDingDingService;
import com.smy.tfs.common.core.redis.RedisCache;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class DingDingServiceImpl implements IDingDingService {

    @Autowired
    private RedisCache redisCache;

//    @Value("${tfs.app.dingding.remove.deptids:837431944,846330070,845948994,845948995,912750724,912901355}")
//    private String removeDeptIds ;

    @Override
    public Response<List<DingDingDeptInfo>> getDingDingLowerLevelDeptsList(long deptId) {
        return Response.success(redisCache.getCacheObject(DingDingConstant.DINGDING_DEPTS_KEY_PREFIX));
    }

    private void getDingDingNextLevelDeptsList(long deptId, List<DingDingDeptInfo> dingDingLowerLevelDeptsList) {
        List<OapiV2DepartmentListsubResponse.DeptBaseResponse> dingDingDepartmentList = DingDingClient.getDingDingDepartmentList(deptId);
        if (ObjectHelper.isNotEmpty(dingDingDepartmentList)) {
            dingDingDepartmentList.stream()
                    .filter(it -> ObjectHelper.isNotEmpty(it.getDeptId()) && ObjectHelper.isNotEmpty(it.getName()))
                    .forEach(it->{
                        DingDingDeptInfo dingDingDeptInfo = new DingDingDeptInfo();
                        dingDingDeptInfo.setDeptId(it.getDeptId());
                        dingDingDeptInfo.setDeptName(it.getName());
                        dingDingDeptInfo.setParentId(it.getParentId());
                        dingDingLowerLevelDeptsList.add(dingDingDeptInfo);
                        getDingDingNextLevelDeptsList(it.getDeptId(), dingDingLowerLevelDeptsList);
                    });
        }
    }

    public void saveDingDingLowerLevelDeptsToCache(Long deptId){
        List<DingDingDeptInfo> dingDingLowerLevelDeptsList = new ArrayList();
        getDingDingNextLevelDeptsList(deptId, dingDingLowerLevelDeptsList);
        redisCache.setCacheObject(DingDingConstant.DINGDING_DEPTS_KEY_PREFIX,dingDingLowerLevelDeptsList,DingDingConstant.DINGDING_DEPTS_EXPIRATION,TimeUnit.DAYS);
    }


}
