package com.smy.tfs.biz.client;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.smy.tfs.common.core.redis.RedisCache;
import com.smy.tfs.common.exception.ServiceException;
import com.smy.tfs.common.utils.spring.SpringUtils;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class DingDingClient {
    private static final String DINGDING_ACCESS_TOKEN_REDIS_KEY = "DINGDING_ACCESS_TOKEN_REDIS_KEY";
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_INTERVAL_MS = 1000;


    /**
     * 获取accessToken
     *
     * @return
     */
    public static String getAccessToken() {
        try {
            RedisCache redisCache = SpringUtils.getBean(RedisCache.class);
            String accessToken = redisCache.getCacheObject(DINGDING_ACCESS_TOKEN_REDIS_KEY);
            if (accessToken != null) {
                log.info("从缓存中获取accessToken成功");
                return accessToken;
            }

            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
            OapiGettokenRequest request = new OapiGettokenRequest();
            request.setAppkey(DingDingConstant.appKey);
            request.setAppsecret(DingDingConstant.appSecret);
            request.setHttpMethod("GET");
            OapiGettokenResponse response = client.execute(request);
            log.info("获取accessToken响应：{}", JSONUtil.toJsonStr(response.getBody()));
            if (response.getErrcode() != 0) {
                throw new ServiceException("获取访问token失败，原因：" + response.getErrmsg());
            }
            log.info("重新获取accessToken成功");
            redisCache.setCacheObject(DINGDING_ACCESS_TOKEN_REDIS_KEY, response.getAccessToken(), 7000);
            return response.getAccessToken();
        } catch (Exception e) {
            log.error("获取访问token失败，原因：", e);
            throw new ServiceException("获取访问token失败");
        }
    }

    public static List<OapiV2DepartmentListsubResponse.DeptBaseResponse> getDingDingDepartmentList(long deptId) {
        int retries = 0;
        while (retries < MAX_RETRIES) {
            try {
                String accessToken = getAccessToken();
                DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/department/listsub");
                OapiV2DepartmentListsubRequest req = new OapiV2DepartmentListsubRequest();
                req.setDeptId(deptId);
                req.setLanguage("zh_CN");
                OapiV2DepartmentListsubResponse response = client.execute(req, accessToken);
                log.debug("获取部门列表响应:" + JSONUtil.toJsonStr(response));
                if (response.getErrcode() != 0) {
                    throw new ServiceException("获取部门列表失败，原因：" + response.getErrmsg());
                }
                return response.getResult();
            } catch (Exception e) {
                log.error("查询钉钉部门列表，含所有的子部门失败，原因：", e);
                try {
                    Thread.sleep(RETRY_INTERVAL_MS);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    log.error("重试过程中发生中断：", ex);
                }
                retries += 1;
            }
        }
        throw new ServiceException("查询钉钉部门列表，含所有的子部门失败");
    }

    /**
     * 获取所有的钉钉部门
     * @param deptList
     * @param needQueryDeptIdList
     */
    public static void geAllDingDingDepartmentList(List<OapiV2DepartmentListsubResponse.DeptBaseResponse> deptList, List<Long> needQueryDeptIdList) {
        for (Long departId : needQueryDeptIdList) {
            List<OapiV2DepartmentListsubResponse.DeptBaseResponse> dingDingDepartmentList = getDingDingDepartmentList(departId);
            if (CollUtil.isNotEmpty(dingDingDepartmentList)){
                deptList.addAll(dingDingDepartmentList);
                List<Long> nextDeptIdList = dingDingDepartmentList.stream().map(OapiV2DepartmentListsubResponse.DeptBaseResponse::getDeptId).collect(Collectors.toList());
                geAllDingDingDepartmentList(deptList, nextDeptIdList);
            }
        }
    }

    /**
     * 获取所有的钉钉用户
     */
    public static List<OapiV2UserListResponse.ListUserResponse> getAllDingDingUserList(List<OapiV2DepartmentListsubResponse.DeptBaseResponse> deptList) {
        List<OapiV2UserListResponse.ListUserResponse> allUserList = new ArrayList<>();
        for (OapiV2DepartmentListsubResponse.DeptBaseResponse deptBaseResponse : deptList) {
            List<OapiV2UserListResponse.ListUserResponse> dingDingDepartUserList = getDingDingDepartUserList(deptBaseResponse.getDeptId());
            if (CollUtil.isNotEmpty(dingDingDepartUserList)){
                allUserList.addAll(dingDingDepartUserList);
            }
        }

        return allUserList;
    }

    public static Map<String, OapiV2UserListResponse.ListUserResponse> getAllDingDingUserMap(List<OapiV2DepartmentListsubResponse.DeptBaseResponse> deptList){
        List<OapiV2UserListResponse.ListUserResponse> allDingDingUserList = getAllDingDingUserList(deptList);
        if (CollUtil.isEmpty(allDingDingUserList)){
            return new HashMap<>();
        }
        Map<String, OapiV2UserListResponse.ListUserResponse> result = new HashMap<>();
        allDingDingUserList.forEach(dingDingUser -> {
            String jobNumber = dingDingUser.getJobNumber();
            String email = dingDingUser.getEmail();
            if (!StrUtil.hasBlank(jobNumber, email)){
                String fullJobNumber = email.charAt(0) + jobNumber;
                result.put(fullJobNumber, dingDingUser);
            }
            result.put(dingDingUser.getName(), dingDingUser);
            result.put(dingDingUser.getEmail(), dingDingUser);
            result.put(dingDingUser.getMobile(), dingDingUser);
        });

        return result;
    }


    public static Map<Long, OapiV2DepartmentGetResponse.DeptGetResponse> getAllDingDingDepartmentMap(List<OapiV2DepartmentListsubResponse.DeptBaseResponse> deptList) {
        Map<Long, OapiV2DepartmentGetResponse.DeptGetResponse> result = new HashMap<>();
        for (OapiV2DepartmentListsubResponse.DeptBaseResponse deptBaseResponse : deptList) {
            OapiV2DepartmentGetResponse.DeptGetResponse deptInfo = getDeptInfo(deptBaseResponse.getDeptId());
            result.put(deptInfo.getDeptId(), deptInfo);
        }
        return result;
    }

    /**
     * 获取部门用户详情
     *
     * @return void
     * @date 2023/4/19 14:47
     */
    public static List<OapiV2UserListResponse.ListUserResponse> getDingDingDepartUserList(long deptId) {
        int retries = 0;
        while (retries < MAX_RETRIES) {
            try {
                DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/list");
                OapiV2UserListRequest req = new OapiV2UserListRequest();
                req.setDeptId(deptId);
                req.setCursor(0L);
                req.setSize(50L);
                req.setOrderField("modify_desc");
                req.setContainAccessLimit(false);
                req.setLanguage("zh_CN");
                OapiV2UserListResponse rsp = client.execute(req, getAccessToken());
                log.debug("获取部门用户详情，响应：{}", JSONUtil.toJsonStr(rsp));
                if (rsp.getErrcode() != 0) {
                    throw new ServiceException("获取部门用户详情失败，原因：" + rsp.getErrmsg());
                }
                return rsp.getResult().getList();
            } catch (ApiException e) {
                log.error("获取部门用户详情失败，原因：", e);
                try {
                    Thread.sleep(RETRY_INTERVAL_MS);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    log.error("重试过程中发生中断：", ex);
                }
                retries += 1;
            }
        }
        throw new ServiceException("获取部门用户详情失败");
    }

    /**
     * 获取上级用户详情
     *
     * @param userId 用户id
     * @return 指定用户的上级信息详情
     * @date 2023/4/19 14:47
     */
    public static OapiV2UserGetResponse.UserGetResponse getSupervisorUserInfo(String userId) {
        try {
            //获取上级，如果上级为空，则默认为最高领导，上级为自己
            OapiV2UserGetResponse.UserGetResponse userFullInfo = getUserInfo(userId);
            if (userFullInfo.getManagerUserid() == null) {
                return userFullInfo;
            } else {
                return getUserInfo(userFullInfo.getManagerUserid());
            }
        } catch (Exception e) {
            log.error("获取上级用户详情失败，原因：", e);
            throw new ServiceException("获取上级用户详情失败");
        }
    }

    /**
     * 获取用户详情
     *
     * @param userId 用户id
     * @return 用户详情
     * @date 2023/4/19 14:47
     */
    public static OapiV2UserGetResponse.UserGetResponse getUserInfo(String userId) {
        int retries = 0;
        while (retries < MAX_RETRIES) {
            try {
                DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/get");
                OapiV2UserGetRequest req = new OapiV2UserGetRequest();
                req.setUserid(userId);
                req.setLanguage("zh_CN");
                OapiV2UserGetResponse rsp = client.execute(req, getAccessToken());
                log.debug("获取用户详情，响应：{}", JSONUtil.toJsonStr(rsp));
                if (rsp.getErrcode() != 0) {
                    throw new ServiceException("获取用户详情失败，原因：" + rsp.getErrmsg());
                }
                return rsp.getResult();
            } catch (ApiException e) {
                log.error("获取用户详情失败，原因：", e);
                try {
                    Thread.sleep(RETRY_INTERVAL_MS);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    log.error("重试过程中发生中断：", ex);
                }
                retries += 1;
            }
        }
        throw new ServiceException("获取用户详情失败");
    }

    /**
     * 获取部门详情
     *
     * @param deptId 部门id
     * @return 部门详情
     * @date 2024/08/27 17:01
     */
    public static OapiV2DepartmentGetResponse.DeptGetResponse getDeptInfo(long deptId) {
        int retries = 0;
        while (retries < MAX_RETRIES) {
            try {
                DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/department/get");
                OapiV2DepartmentGetRequest req = new OapiV2DepartmentGetRequest();
                req.setDeptId(deptId);
                req.setLanguage("zh_CN");
                OapiV2DepartmentGetResponse rsp = client.execute(req, getAccessToken());
                log.debug("获取部门详情，响应：{}", JSONUtil.toJsonStr(rsp));
                if (rsp.getErrcode() != 0) {
                    throw new ServiceException("获取用户详情失败，原因：" + rsp.getErrmsg());
                }
                return rsp.getResult();
            } catch (ApiException e) {
                log.error("获取部门详情失败，原因：", e);
                try {
                    Thread.sleep(RETRY_INTERVAL_MS);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    log.error("重试过程中发生中断：", ex);
                }
                retries += 1;
            }
        }
        throw new ServiceException("获取部门详情失败");
    }

    public static void main(String[] args) {
        List<OapiV2UserListResponse.ListUserResponse> dingDingDepartUserList = getDingDingDepartUserList(DingDingConstant.DINGDING_HEADQUARTER);
        System.out.println(dingDingDepartUserList);
    }
}
