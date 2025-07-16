package com.smy.tfs.common.utils.notification;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.smy.tfs.common.constant.CacheConstants;
import com.smy.tfs.common.constant.Constants;
import com.smy.tfs.common.core.redis.RedisCache;
import com.smy.tfs.common.exception.ServiceException;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.common.utils.http.HttpUtils;
import com.smy.tfs.common.utils.uuid.UUID;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class QwNotify {

    @Getter
    private static String tfsWebsite;

    private static RedisCache redisCache;

    private static QwConfigComponent qwConfigComponent;

    @Value("${qw.tfsWebsite:https://ticket.smyoa.com}")
    public void setTfsWebsite (String tfsWebsite) {

        QwNotify.tfsWebsite = tfsWebsite;
    }

    @Resource
    public void setRedisCache (RedisCache redisCache) {

        QwNotify.redisCache = redisCache;
    }

    @Resource
    public void setQwConfigComponent (QwConfigComponent qwConfigComponent) {

        QwNotify.qwConfigComponent = qwConfigComponent;
    }

    public static boolean notifyQw (String message, String qwId) {

        boolean result = true;
        Map<QwCorporateEnum, List<String>> qwCorporateEnumListMap = getQwCorporateEnumByQwId(qwId);
        for (QwCorporateEnum qwCorporateEnum : qwCorporateEnumListMap.keySet()) {
            List<String> qwIdList = qwCorporateEnumListMap.get(qwCorporateEnum);
            if (CollUtil.isNotEmpty(qwIdList) && !notifyQw(message, CollUtil.join(qwIdList, "|"), qwCorporateEnum)) {
                result = false;
            }

        }
        return result;
    }

    public static boolean notifyQw (String message, String qwId, QwCorporateEnum qwCorporateEnum) {

        String token = getAccessToken(qwCorporateEnum);
        QwConfig qwConfig = qwConfigComponent.getQwConfig(qwCorporateEnum);
        JSONObject param = new JSONObject();

        //企微id 替换
        qwId = qwId.replaceAll(qwCorporateEnum.getCorporateSign(), "");
        param.put("touser", qwId);
        param.put("msgtype", "markdown");
        param.put("agentid", qwConfig.getAgentId());
        param.put("markdown", JSONObject.of("content", message));
        String resp = HttpUtils.sendSSLPostJSON(
                "https://qyapi.weixin.qq.com/cgi-bin/message/send", String.format("access_token=%s", token),
                param.toJSONString());
        if ("".equalsIgnoreCase(resp)) {
            throw new ServiceException("call wx to notifyQw   fail");
        }

        JSONObject respObj = JSON.parseObject(resp);
        if (!Objects.equals(respObj.getOrDefault("errcode", -1), 0)) {
            log.error("notifyQw fail, resp = {}", resp);
            return false;
        }
        return true;
    }

    public static String getAccessToken () {

        return getAccessToken(QwCorporateEnum.SMY);
    }

    public static String getAccessToken (QwCorporateEnum qwCorporateEnum) {

        QwConfig qwConfig = qwConfigComponent.getQwConfig(qwCorporateEnum);
        String tokenFromCache = redisCache.getCacheObject(getAccessTokenRedisKey(qwConfig));
        if (!StrUtil.isEmpty(tokenFromCache)) {
            return tokenFromCache;
        }
        return refreshAccessToken(qwCorporateEnum);
    }

    public static String refreshAccessToken () {

        return refreshAccessToken(QwCorporateEnum.SMY);
    }

    public static String refreshAccessToken (QwCorporateEnum qwCorporateEnum) {

        QwConfig qwConfig = qwConfigComponent.getQwConfig(qwCorporateEnum);
        String resp = HttpUtils.sendGet(String.format(
                "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s",
                qwConfig.getCropId(), qwConfig.getCropSec()));
        if ("".equalsIgnoreCase(resp)) {
            throw new ServiceException("call wx to fetch access token fail");
        }
        JSONObject respObj = JSON.parseObject(resp);
        if (!Objects.equals(respObj.getOrDefault("errcode", -1), 0)) {
            log.error("get qw access token fail, resp = {}", resp);
        }
        String accessToken = respObj.getString("access_token");
        redisCache.setCacheObject(getAccessTokenRedisKey(qwConfig), accessToken,
                respObj.getIntValue("expires_in", 1200) - 300);
        return accessToken;
    }

    private static String getAccessTokenRedisKey (QwConfig qwConfig) {

        return CacheConstants.QW_ACCESS_TOKEN_KEY + qwConfig.getCropId() + "-" + qwConfig.getCropSec();
    }

    public static List<Integer> getDepIds (QwCorporateEnum qwCorporateEnum) {

        String resp = HttpUtils.sendSSLPostJSON("https://qyapi.weixin.qq.com/cgi-bin/department/simplelist",
                String.format("access_token=%s&limit=%s", getAccessToken(qwCorporateEnum), 10000), null);
        if ("".equalsIgnoreCase(resp)) {
            throw new ServiceException("call qw to get dept id  fail");
        }

        JSONObject respObj = JSON.parseObject(resp);
        if (!Objects.equals(respObj.getOrDefault("errcode", -1), 0)) {
            log.warn(String.format("get dept ids from qw fail, resp = %s", resp));
            return new ArrayList<>();
        }
        return respObj.getList("department_id", JSONObject.class).stream()
                .map(e -> e.getInteger("id")).collect(Collectors.toList());
    }

    public static List<QwNotify.U> getUserFromDepId (Integer depId, QwCorporateEnum qwCorporateEnum) {

        String resp = HttpUtils.sendSSLGet("https://qyapi.weixin.qq.com/cgi-bin/user/simplelist",
                String.format("access_token=%s&department_id=%s", getAccessToken(qwCorporateEnum), depId), Constants.UTF8);
        if ("".equalsIgnoreCase(resp)) {
            throw new ServiceException("call qw to getUserFromDepId  fail");
        }

        JSONObject respObj = JSON.parseObject(resp);
        if (!Objects.equals(respObj.getOrDefault("errcode", -1), 0)) {
            log.warn(String.format("get userid getUserFromDepId, resp = %s", resp));
            return null;
        }
        return respObj.getList("userlist", JSONObject.class).stream()
                .map(e -> {
                    U u = new U();
                    u.setUserId(e.getString("userid"));
                    u.setUserName(e.getString("name"));
                    return u;
                }).collect(Collectors.toList());
    }

    //TODO getUserListFromDepId
    // https://qyapi.weixin.qq.com/cgi-bin/user/list?access_token=ACCESS_TOKEN&department_id=DEPARTMENT_ID

    public static List<QwNotify.UDetails> getUserListFromDepId (Integer depId, QwCorporateEnum qwCorporateEnum) {

        String resp = HttpUtils.sendSSLGet("https://qyapi.weixin.qq.com/cgi-bin/user/list",
                String.format("access_token=%s&department_id=%s", getAccessToken(qwCorporateEnum), depId), Constants.UTF8);

        if (StringUtils.isBlank(resp)) {
            throw new ServiceException("call qw to getUserListFromDepId fail");
        }
        JSONObject respObj = JSON.parseObject(resp);
        if (!Objects.equals(respObj.getOrDefault("errcode", -1), 0)) {
            log.warn(String.format("get userDetails getUserListFromDepId, resp = %s", resp));
            return new ArrayList();
        }

        return respObj.getList("userlist", JSONObject.class).stream()
                .map(e -> {
                    UDetails uDetails = new UDetails();
                    uDetails.setUserId(e.getString("userid"));
                    uDetails.setUserName(e.getString("name"));
                    uDetails.setEmail(e.getString("email"));
                    uDetails.setMobile(e.getString("mobile"));
                    return uDetails;
                }).collect(Collectors.toList());
    }


    public static String createChatGroup (List<String> userList, String groupName, String owner, String chatId) {

        String token = getAccessToken();
        JSONObject params = new JSONObject();
        params.put("name", groupName);
        params.put("owner", owner);
        params.put("userlist", userList);
        params.put("chatid", chatId);
        String resp = HttpUtils.sendSSLPostJSON("https://qyapi.weixin.qq.com/cgi-bin/appchat/create",
                String.format("access_token=%s", token),
                params.toJSONString());
        if ("".equalsIgnoreCase(resp)) {
            throw new ServiceException("call wx to createChatGroup fail");
        }

        JSONObject respObj = JSON.parseObject(resp);
        if (!Objects.equals(respObj.getOrDefault("errcode", -1), 0)) {
            throw new ServiceException(String.format("createChatGroup fail, resp = %s", resp));
        }
        return respObj.getString("chatid");
    }

    public static String updateChatGroup (String wxGroupId, List<String> addUserList, List<String> delUserList) {

        String token = getAccessToken();
        JSONObject params = new JSONObject();
        params.put("chatid", wxGroupId);
        if (CollectionUtils.isNotEmpty(addUserList)) {
            params.put("add_user_list", addUserList);
        }
        if (CollectionUtils.isNotEmpty(delUserList)) {
            params.put("del_user_list", delUserList);
        }
        String resp = HttpUtils.sendSSLPostJSON("https://qyapi.weixin.qq.com/cgi-bin/appchat/update",
                String.format("access_token=%s", token),
                params.toJSONString());
        if ("".equalsIgnoreCase(resp)) {
            throw new ServiceException("call wx to updateChatGroup   fail");
        }

        JSONObject respObj = JSON.parseObject(resp);
        if (!Objects.equals(respObj.getOrDefault("errcode", -1), 0)) {
            throw new ServiceException(String.format("updateChatGroup fail, resp = %s", resp));
        }
        return respObj.getString("errcode");
    }

    public static Set<String> queryChatUserList (String wxGroupId) {

        String token = getAccessToken();
        JSONObject params = new JSONObject();
        params.put("chatid", wxGroupId);
        String resp = HttpUtils.sendSSLPostJSON("https://qyapi.weixin.qq.com/cgi-bin/appchat/get",
                String.format("access_token=%s", token),
                params.toJSONString());
        if ("".equalsIgnoreCase(resp)) {
            throw new ServiceException("call wx to queryChatUserList   fail");
        }

        JSONObject respObj = JSON.parseObject(resp);
        if (!Objects.equals(respObj.getOrDefault("errcode", -1), 0)) {
            throw new ServiceException(String.format("queryChatUserList fail, resp = %s", resp));
        }
        respObj.getJSONObject("chat_info");
        return null;
    }


    public static void sendMsgToChatGroup (String hello, String chatId) {

        String token = getAccessToken();
        JSONObject params = new JSONObject();
        params.put("chatid", chatId);
        params.put("msgtype", "markdown");
        params.put("markdown", JSONObject.of("content", hello));

        String resp = HttpUtils.sendSSLPostJSON(
                "https://qyapi.weixin.qq.com/cgi-bin/appchat/send", String.format("access_token=%s", token),
                params.toJSONString());
        if ("".equalsIgnoreCase(resp)) {
            throw new ServiceException("call wx to sendMsgToChatGroup   fail");
        }

        JSONObject respObj = JSON.parseObject(resp);
        if (!Objects.equals(respObj.getOrDefault("errcode", -1), 0)) {
            throw new ServiceException(String.format("sendMsgToChatGroup fail, resp = %s", resp));
        }
    }

    /**
     * 仅单人的发送卡片
     *
     * @param mainTitle
     * @param isNotice
     * @param bottomSlot
     * @param uid
     * @param jumpUrl
     * @param kvContents
     * @return
     */
    public static Map<String, String> sendQwCard (JSONObject mainTitle, boolean isNotice, Map<String, String> bottomSlot,
                                                  String uid, String jumpUrl, List<JSONObject> kvContents)
    {

        Map<QwCorporateEnum, List<String>> qwCorporateEnumListMap = getQwCorporateEnumByQwId(uid);
        Map<String, String> result = new HashMap<>();
        for (QwCorporateEnum qwCorporateEnum : qwCorporateEnumListMap.keySet()) {
            List<String> qwIdList = qwCorporateEnumListMap.get(qwCorporateEnum);
            if (CollUtil.isNotEmpty(qwIdList)) {
                result = sendQwCard(mainTitle, isNotice, bottomSlot, CollUtil.join(qwIdList, "|"), jumpUrl, kvContents, qwCorporateEnum);
            }
        }
        return result;
    }

    public static Map<String, String> sendQwCard (JSONObject mainTitle, boolean isNotice, Map<String, String> bottomSlot,
                                                  String uid, String jumpUrl, List<JSONObject> kvContents, QwCorporateEnum qwCorporateEnum)
    {

        if (StringUtils.isEmpty(uid)) {
            throw new IllegalArgumentException("uid is empty");
        }
        String token = getAccessToken(qwCorporateEnum);
        JSONObject content = JSONObject.of("main_title", mainTitle, "card_type", "button_interaction",
                "task_id", UUID.fastUUID().toString(true));
        if (isNotice) {
            content.fluentPut("jump_list", bottomSlot.entrySet().stream()
                            .map(e -> JSONObject.of("title", e.getKey(), "type", 1, "url", e.getValue()))
                            .collect(Collectors.toList()))
                    .fluentPut("card_type", "text_notice");
        } else {
            List<JSONObject> buttons = bottomSlot.entrySet().stream().map(
                    e -> {
                        int defaultButtonStyle = 4;
                        if ("通过".equals(e.getValue()) || "同意".equals(e.getValue())) {
                            defaultButtonStyle = 1;
                        } else if ("驳回".equals(e.getValue()) || "拒绝".equals(e.getValue())) {
                            defaultButtonStyle = 3;
                        }
                        return JSONObject.of("text", e.getValue(), "type", 0, "key", e.getKey(), "style", defaultButtonStyle);
                    }
            ).collect(Collectors.toList());
            content.fluentPut("button_list", buttons);
        }
        if (CollectionUtils.isNotEmpty(kvContents)) {
            content.fluentPut("horizontal_content_list", kvContents);
        }
        if (StringUtils.isNotEmpty(jumpUrl)) {
            content.fluentPut("card_action", JSONObject.of("type", 1, "url", jumpUrl));
        }

        QwConfig qwConfig = qwConfigComponent.getQwConfig(qwCorporateEnum);
        uid = uid.replaceAll(qwCorporateEnum.getCorporateSign(), "");
        JSONObject params = JSONObject.of("touser", uid, "msgtype", "template_card", "agentid",
                qwConfig.getAgentId(), "template_card", content);
        String resp = HttpUtils.sendSSLPostJSON("https://qyapi.weixin.qq.com/cgi-bin/message/send",
                "access_token=" + token, params.toJSONString());
        if ("".equalsIgnoreCase(resp)) {
            throw new ServiceException("call wx to send card message   fail");
        }

        JSONObject respObj = JSON.parseObject(resp);
        if (!Objects.equals(respObj.getOrDefault("errcode", -1), 0)) {
            log.error(String.format("send card message fail, resp = %s", resp));
        }
        HashMap<String, String> result = new HashMap<>();
        for (String key : respObj.keySet()) {
            result.put(key, respObj.getString(key));
        }
        return result;
    }

    /**
     * 看逻辑也是仅单人的
     *
     * @param cardResponseCode
     * @param newButtonText
     * @param uidStrList
     */
    public static void disableCardButton (String cardResponseCode, String newButtonText, List<String> uidStrList) {

        Map<QwCorporateEnum, List<String>> qwCorporateEnumListMap = getQwCorporateEnumByQwId(CollUtil.join(uidStrList, "|"));
        for (QwCorporateEnum qwCorporateEnum : qwCorporateEnumListMap.keySet()) {
            List<String> qwIdList = qwCorporateEnumListMap.get(qwCorporateEnum);
            if (CollUtil.isNotEmpty(qwIdList)) {
                disableCardButton(cardResponseCode, newButtonText, qwIdList, qwCorporateEnum);
            }
        }
    }

    public static void disableCardButton (String cardResponseCode, String newButtonText, List<String> uidStr, QwCorporateEnum qwCorporateEnum) {

        String token = getAccessToken(qwCorporateEnum);
        if (StringUtils.isEmpty(uidStr)) {
            log.warn("禁止发全员消息");
            return;
        }
        QwConfig qwConfig = qwConfigComponent.getQwConfig(qwCorporateEnum);
        uidStr = uidStr.stream().map(item -> item.replaceAll(qwCorporateEnum.getCorporateSign(), ""))
                .collect(Collectors.toList());
        JSONObject params = JSONObject.of("userids", uidStr, "agentid", qwConfig.getAgentId(), "response_code", cardResponseCode,
                "button", JSONObject.of("replace_name", newButtonText));
        String resp = HttpUtils.sendSSLPostJSON("https://qyapi.weixin.qq.com/cgi-bin/message/update_template_card",
                "access_token=" + token, params.toJSONString());
        if ("".equalsIgnoreCase(resp)) {
            throw new ServiceException("call wx to disable card btn   fail");
        }

        JSONObject respObj = JSON.parseObject(resp);
        Object errorCode = respObj.getOrDefault("errcode", -1);
        if (Objects.equals(errorCode, 60140)) {
            log.warn("code({}) 过期或失效,幂等成功", cardResponseCode);
        } else if (!Objects.equals(errorCode, 0)) {
            throw new ServiceException(String.format("disable card btn fail, resp = %s", resp));
        }
    }

    public static String getUserIdFromPhone (String phone, String token) {

        if (StringUtils.isEmpty(token)) {
            token = getAccessToken();
        }
        JSONObject params = new JSONObject();
        params.put("mobile", phone);
        String resp = HttpUtils.sendSSLPostJSON("https://qyapi.weixin.qq.com/cgi-bin/user/getuserid",
                String.format("access_token=%s", token),
                params.toJSONString());
        if ("".equalsIgnoreCase(resp)) {
            throw new ServiceException("call qw to get user id  fail");
        }

        JSONObject respObj = JSON.parseObject(resp);
        if (!Objects.equals(respObj.getOrDefault("errcode", -1), 0)) {
            log.warn(String.format("get userid from qw fail, resp = %s", resp));
            return null;
        }
        return respObj.getString("userid");
    }

    public static String getUserIdFromEmail (String email, String token, int email_type) {

        if (StringUtils.isEmpty(token)) {
            token = getAccessToken();
        }
        JSONObject params = JSONObject.of("email", email, "email_type", email_type);
        String resp = HttpUtils.sendSSLPostJSON("https://qyapi.weixin.qq.com/cgi-bin/user/get_userid_by_email",
                String.format("access_token=%s", token),
                params.toJSONString());
        if ("".equalsIgnoreCase(resp)) {
            throw new ServiceException("call qw to get user id  fail");
        }

        JSONObject respObj = JSON.parseObject(resp);
        if (!Objects.equals(respObj.getOrDefault("errcode", -1), 0)) {
            log.warn(String.format("get userid from qw fail, resp = %s", resp));
            return null;
        }
        return respObj.getString("userid");
    }

    private static Map<QwCorporateEnum, List<String>> getQwCorporateEnumByQwId (String qwUserId) {

        Map<QwCorporateEnum, List<String>> result = new HashMap<>();
        String[] userIdArray = qwUserId.split("\\|");
        for (String userId : userIdArray) {
            QwCorporateEnum tempEnum = QwCorporateEnum.SMY;
            for (QwCorporateEnum qwCorporateEnum : QwCorporateEnum.values()) {
                if (userId.endsWith(qwCorporateEnum.getCorporateSign())) {
                    tempEnum = qwCorporateEnum;
                    break;
                }
            }
            List<String> userIdList = result.getOrDefault(tempEnum, new ArrayList<>());
            userIdList.add(userId);
            result.put(tempEnum, userIdList);
        }
        return result;
    }

    @Data
    public static class U {

        private String userName;

        private String userId;

    }

    @Data
    public static class UDetails {

        private String userId;

        private String userName;

        private String mobile;

        private String email;

    }

}
