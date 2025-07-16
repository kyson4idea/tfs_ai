package com.smy.tfs.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.dingtalk.api.response.OapiV2DepartmentListsubResponse;
import com.dingtalk.api.response.OapiV2UserListResponse;
import com.smy.tfs.api.dbo.TicketAccountMapping;
import com.smy.tfs.api.dto.NotificationDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.ticket_notify.NotifyMsgDto;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.service.ITicketAccountMappingService;
import com.smy.tfs.biz.client.DingDingClient;
import com.smy.tfs.biz.client.DingDingConstant;
import com.smy.tfs.biz.service.NotificationService;
import com.smy.tfs.common.constant.CacheConstants;
import com.smy.tfs.common.core.redis.RedisCache;
import com.smy.tfs.common.exception.ServiceException;
import com.smy.tfs.common.utils.DateUtils;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.common.utils.notification.AesException;
import com.smy.tfs.common.utils.notification.QwCorporateEnum;
import com.smy.tfs.common.utils.notification.QwNotify;
import com.smy.tfs.common.utils.notification.WXBizJsonMsgCrypt;
import com.smy.tfs.common.utils.spring.SpringUtils;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    public static int CARD_RESP_CODE_TTL = 3600 * 72;
    Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    WXBizJsonMsgCrypt wxBizJsonMsgCrypt;
    @Resource
    private ITicketAccountMappingService ticketAccountMappingService;
    @Resource
    private RedisLockRegistry redisLockRegistry;
    @Autowired
    private RedisCache redisCache;

    @Override
    public void notifyQw(String message, List<String> userIdList) {
        logger.info("notify qw ,message = {}, userIdList = {}", message, userIdList);
        if (StringUtils.isEmpty(message) || CollectionUtils.isEmpty(userIdList)) {
            logger.warn("params invalidate ");
            return;
        }
        userIdList.forEach(qwId -> {
            boolean suc = QwNotify.notifyQw(message, qwId);
            if (!suc) {
                logger.error("send notification  to {}  fail", qwId);
            }
        });
    }

    @Override
    public Response notifyMsg(NotifyMsgDto notifyMsgDto) {
        if (Objects.isNull(notifyMsgDto) || StringUtils.isEmpty(notifyMsgDto.getMessage())
                || CollectionUtils.isEmpty(notifyMsgDto.getUserIdList())
                || StringUtils.isEmpty(notifyMsgDto.getUserType())) {
            logger.error("消息内容或者接收人或者接收人类型为空:{}", com.alibaba.fastjson2.JSONObject.toJSONString(notifyMsgDto));
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "消息内容或者接收人或者接收人类型为空");
        }
        String message = notifyMsgDto.getMessage();
        List<String> userIdList = notifyMsgDto.getUserIdList();
        String userType = notifyMsgDto.getUserType();
        logger.info("notify qw ,message = {}, userType={}, userIdList = {}", message, userType, userIdList);
        List<TicketAccountMapping> ticketAccountMappingList= ticketAccountMappingService.selectAccountMappingByAccountIdAndType(userIdList, userType);
        if (CollectionUtils.isEmpty(ticketAccountMappingList)) {
            String errorMsg = String.format("找不到对应的账户(userType:%s,userIdList:%s)", userType, com.alibaba.fastjson2.JSONObject.toJSONString(userIdList));
            logger.error(errorMsg);
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, errorMsg);
        }
        List<String> failedAccountNameList = new ArrayList<>();
        ticketAccountMappingList.forEach(ticketAccountMapping -> {
            String qwId = ticketAccountMapping.getQwUserId();
            String accountName = ticketAccountMapping.getAccountName();
            try {
                boolean suc = QwNotify.notifyQw(message, qwId);
                if (!suc) {
                    logger.error("消息发送给{}异常", accountName);
                    failedAccountNameList.add(accountName);
;                    }
            } catch (Exception e) {
                logger.error("消息发送给{}异常:{}", accountName, e);
                failedAccountNameList.add(accountName);
            }
        });
        if (!CollectionUtils.isEmpty(failedAccountNameList)) {
            String errorMsg = String.format("消息发送给%s异常", com.alibaba.fastjson2.JSONObject.toJSONString(failedAccountNameList));
            logger.error(errorMsg);
            return Response.error(BizResponseEnums.SYSTEM_ERROR, errorMsg);
        }
        return Response.success();
    }

    @Override
    public void notifyQwNormalMd(String ticketNo, String ticketTitle, String ticketStatus,
                                 String handlePerson, List<String> userIdList) {
        String mdTpl = "**您的工单进度已更新，请及时查看**\n{}-{}\n<font color=\"comment\">工单状态：</font>{}\n" +
                "<font color=\"comment\">当前处理人：</font>{}\n" +
                "<font color=\"comment\">更新时间：</font>{}\n[查看详情]({})";
        String md = StrUtil.format(mdTpl, ticketNo, ticketTitle, ticketStatus, handlePerson,
                DateUtils.getTime(), QwNotify.getTfsWebsite());
        this.notifyQw(md, userIdList);
    }

    @Override
    public CardNotifyRet notifyQwCard(String message, Map<String, String> buttonKeyMap, List<String> userIdList) {
        QwCardMsg msg = new QwCardMsg();
        msg.setTitle(message);
        msg.setUserIdList(userIdList);
        msg.setButtonKeyMap(buttonKeyMap);
        return notifyQwCard(msg);
    }


    @Override
    public CardNotifyRet notifyQwCard(QwCardMsg cardMsg) {
//        if ((CollectionUtils.isEmpty(cardMsg.getButtonKeyMap()) && CollectionUtils.isEmpty(cardMsg.getLinkKeyMap())) ||
//                StringUtils.isEmpty(cardMsg.getTitle()) || CollectionUtils.isEmpty(cardMsg.getUserIdList())) {
//            throw new ServiceException("params invalidate ");
//        }
        logger.info("企业微信卡片参数：message={}, buttonKeyMap={}, userIdList={}", cardMsg.getTitle(),
                JSONUtil.toJsonStr(cardMsg.getButtonKeyMap()),
                JSONUtil.toJsonStr(cardMsg.getUserIdList()));

        var kvContents = Optional.ofNullable(cardMsg.getKvContentList())
                .orElse(Collections.emptyList()).stream().map(e -> {
                    var obj = com.alibaba.fastjson2.JSONObject.of("type", e.getType().ordinal(), "keyname",
                            e.getKeyName(), "value",
                            e.getValue());
                    if (StringUtils.isNotEmpty(e.getUserId())) {
                        obj.put("media_id", e.getMediaId());
                    }
                    if (StringUtils.isNotEmpty(e.getUserId())) {
                        obj.put("userid", e.getUserId());
                    }
                    if (StringUtils.isNotEmpty(e.getUrl())) {
                        obj.put("url", e.getUrl());
                    }
                    return obj;
                }).collect(Collectors.toList());
        var mainTitle = com.alibaba.fastjson2.JSONObject.of("title", cardMsg.getTitle(),
                "desc", cardMsg.getDescription());
        boolean isNotice = CollectionUtils.isEmpty(cardMsg.getButtonKeyMap());// notice card only, not have button
        var bottomSlot = isNotice ? cardMsg.getLinkKeyMap() : cardMsg.getButtonKeyMap();
        Map<String, String> ret = QwNotify.sendQwCard(mainTitle, isNotice, bottomSlot,
                String.join("|", cardMsg.getUserIdList()), cardMsg.getJumpUrl(), kvContents);
        logger.info("企业微信卡片发送结果：{}", JSONUtil.toJsonStr(ret));
        if (!isNotice && StringUtils.isNotEmpty(ret.get("response_code"))) {
            String response_code = ret.get("response_code");
            String keyForExpire = CacheConstants.QW_CARD_MSG_CODE_KEY +
                    response_code + StrUtil.COLON +
                    String.join(StrUtil.COMMA, cardMsg.getUserIdList());
            logger.info("store key({}) for disable card code  after 72 hour ", keyForExpire);
            redisCache.setCacheObject(keyForExpire, response_code, CARD_RESP_CODE_TTL);
        }

        return CardNotifyRet.builder()
                .responseCode(ret.get("response_code"))
                .msgId(ret.get("msgid"))
                .errorCode(ret.get("errcode"))
                .errorMsg(ret.get("errmsg"))
                .build();
    }

    @Override
    public void disableCardButton(String responseCode, String newBtnText, List<String> userIds) {
        //String uidStr = String.join("|", userIds);
        logger.info("disableCardButton 参数：responseCode={}，newBtnText={}，userIds={}", responseCode, newBtnText, userIds);
        QwNotify.disableCardButton(responseCode, newBtnText, userIds);
    }

    @Override
    public String getUserId(String email, String phone) {
        if (StringUtils.isAllEmpty(email, phone)) {
            throw new ServiceException("params is invalidate");
        }
        LambdaQueryChainWrapper<TicketAccountMapping> lq = this.ticketAccountMappingService.lambdaQuery();
        if (StringUtils.isNotEmpty(email)) {
            lq.eq(TicketAccountMapping::getEmail, email);
            lq.or();
        }
        if (StringUtils.isNotEmpty(phone)) {
            lq.eq(TicketAccountMapping::getPhoneNo, phone);
        }
        TicketAccountMapping one = lq.last("limit 1").one();
        if (one != null) {
            return one.getQwUserId();
        }
        return null;
    }

    @Override
    public String createChatGroup(NotificationDto.CreateChatGroup notificationDto) {
        logger.info("createChatGroup userList = {}  groupName = {} owner = {}",
                notificationDto.getUserList(), notificationDto.getGroupName(), notificationDto.getOwner());

        // 1. create group
        String _chatId = QwNotify.createChatGroup(notificationDto.getUserList(),
                notificationDto.getGroupName(), notificationDto.getOwner(), notificationDto.getChatId());

        // 2. send first message to group to make chat room appear in qw client
        QwNotify.sendMsgToChatGroup(notificationDto.getHello(), _chatId);
        return _chatId;
    }

    @Override
    public String updateChatGroup(String wxGroupId,List<String> addUserList,List<String> delUserList) {
        logger.info("updateChatGroup userList = {}  addUserList = {} delUserList = {}",
                wxGroupId,addUserList,delUserList);

        String _code = QwNotify.updateChatGroup(wxGroupId,addUserList,delUserList);

        return _code;
    }

    /**
     * @param wxGroupId
     * @param msg
     * @return
     */
    @Override
    public String sendSimpleQwMsg(String wxGroupId, String msg) {
        return null;
    }

    public Set<String> queryChatUserList(String wxGroupId) {
        logger.info("queryChatUserList wxGroupId = {} ", wxGroupId);
        var  result = QwNotify.queryChatUserList(wxGroupId);
        return result;
    }


    @Override
    public boolean doQwCallback(String msgSignature, String timestamp, String nonce, String bodyData) {
        String eventKey = null;
        try {
            String xmlContent = wxBizJsonMsgCrypt.decryptMsg(msgSignature, timestamp, nonce, bodyData);
            JSONObject msg = JSONUtil.parseFromXml(xmlContent).getJSONObject("xml");
            logger.info("接受到企业微信后台回调:  {}", msg);
            String msgType = msg.getStr("MsgType");
            if (!"event".equalsIgnoreCase(msgType)) {
                //暂时只处理 event类型的回调
                return true;
            }

            String event = msg.getStr("Event","");
            if ("view".equals(event)){
                // event类型回调不走这块，从 http callbackController 免密登录
                return true;
            }

            // do some business for ticket
            eventKey = msg.getStr("EventKey", "");
            String userID = msg.getStr("FromUserName");
            // defaultHandler-GD100001-approval-remark  , beanName-工单编号-操作-reamrk
            String[] eventKeys = eventKey.split(StrUtil.DASHED);
            if (eventKeys.length < 2) {
                logger.info("qw callback msg event key format error, eventKey is {}", eventKey);
                return true;
            }
            ICardButtonCallBack bean = SpringUtils.getBean(eventKeys[0]);
            // 回调 业务bean
            bean.callback(eventKey, userID);
            return true;
        } catch (Exception e) {
            logger.error(String.format("doQwCallback  fail， eventKey = %s", eventKey), e);
        }
        return false;
    }

    @Override
    public String validCallbackUrl(String msgSignature, String timestamp, String nonce, String echoStr) {
        String url = "";
        try {
            url = wxBizJsonMsgCrypt.verifyURL(msgSignature, timestamp, nonce, echoStr);
        } catch (AesException e) {
            logger.error("verifyURL from qw fail", e);
        }
        return url;
    }

    @Override
    public void matchQwAndDdUserId() {
        var needToMatch = ticketAccountMappingService.lambdaQuery()
                .in(TicketAccountMapping::getMatchResult, "init", "fail")
                .and(qw -> qw.isNull(TicketAccountMapping::getQwUserId).or().isNull(TicketAccountMapping::getDdUserId))
                .and(qw -> qw.isNull(TicketAccountMapping::getMatchCount)
                        .or().le(TicketAccountMapping::getMatchCount, 20))
                .list();
        logger.info("need to match count is : {}", Opt.ofNullable(needToMatch).orElse(Collections.emptyList()).size());
        if (CollectionUtils.isEmpty(needToMatch)) {
            logger.info("needToMatch is empty , no need to match ");
            return;
        }

        // 查询企微列表
        List<Integer> depIdList = QwNotify.getDepIds(QwCorporateEnum.SMY);
        if (depIdList == null) {
            logger.info("get depId from qw return empty");
            return;
        }
        Map<String, String> qwUserIdMap = depIdList.stream()
                .flatMap(depId -> Optional.ofNullable(QwNotify.getUserFromDepId(depId, QwCorporateEnum.SMY))
                        .orElse(Collections.emptyList()).stream())
                .collect(HashMap::new, (m, v) -> m.put(v.getUserName(), v.getUserId()), HashMap::putAll);

        // 查询钉钉列表
        List<OapiV2DepartmentListsubResponse.DeptBaseResponse> deptList = new ArrayList<>();
        DingDingClient.geAllDingDingDepartmentList(deptList, Collections.singletonList(DingDingConstant.DINGDING_HEADQUARTER));

        Map<String, OapiV2UserListResponse.ListUserResponse> allDingDingUserMap = DingDingClient.getAllDingDingUserMap(deptList);
        if (CollectionUtils.isEmpty(allDingDingUserMap)) {
            logger.info("get allDingDingUserMap from dd return empty");
            return;
        }
        Map<Long, String> deptIdNameMap = deptList.stream()
                .collect(Collectors.toMap(OapiV2DepartmentListsubResponse.DeptBaseResponse::getDeptId,
                        OapiV2DepartmentListsubResponse.DeptBaseResponse::getName));


        // match
        for (TicketAccountMapping mp : needToMatch) {
            String qwUserId = mp.getQwUserId();
            String ddUserId = mp.getDdUserId();
            String ddUserDeptId = mp.getDdUserDeptId();
            String ddUserDeptName = mp.getDdUserDeptName();

            //获取企业微信
            if (StrUtil.isBlank(qwUserId)) {
                qwUserId = qwUserIdMap.get(mp.getAccountName());
                if (StrUtil.isBlank(qwUserId)) {
                    if (StrUtil.isNotEmpty(mp.getEmail())) {
                        qwUserId = QwNotify.getUserIdFromEmail(mp.getEmail(), null, 1);
                        if (StrUtil.isBlank(qwUserId)) {
                            qwUserId = QwNotify.getUserIdFromEmail(mp.getEmail(), null, 2);
                        }
                    }
                }
            }

            //获取钉钉
            if (StrUtil.isBlank(ddUserId)) {
                OapiV2UserListResponse.ListUserResponse ddUser = allDingDingUserMap.get(mp.getAccountId());
                if (ddUser == null && StrUtil.isNotBlank(mp.getEmail())) {
                    ddUser = allDingDingUserMap.get(mp.getEmail());
                }
                if (ddUser == null && StrUtil.isNotBlank(mp.getPhoneNo())) {
                    ddUser = allDingDingUserMap.get(mp.getPhoneNo());
                }
                if (ddUser == null && StrUtil.isNotBlank(mp.getAccountName())) {
                    ddUser = allDingDingUserMap.get(mp.getAccountName());
                }
                if (ddUser != null) {
                    ddUserId = ddUser.getUserid();
                    if (CollUtil.isNotEmpty(ddUser.getDeptIdList())){
                        ddUserDeptId = String.valueOf(ddUser.getDeptIdList().get(0));
                        ddUserDeptName = deptIdNameMap.getOrDefault(ddUser.getDeptIdList().get(0), ddUserDeptName);
                    }
                }
            }

            if (StrUtil.hasBlank(qwUserId, ddUserId)) {
                ticketAccountMappingService.lambdaUpdate().setSql("`match_count` = `match_count`+1")
                        .set(TicketAccountMapping::getMatchResult, "fail")
                        .set(TicketAccountMapping::getQwUserId, qwUserId)
                        .set(TicketAccountMapping::getDdUserId, ddUserId)
                        .set(TicketAccountMapping::getDdUserDeptId, ddUserDeptId)
                        .set(TicketAccountMapping::getDdUserDeptName, ddUserDeptName)
                        .set(TicketAccountMapping::getUpdateTime, new Date())
                        .eq(TicketAccountMapping::getId, mp.getId())
                        .update();
                continue;
            }
            ticketAccountMappingService.lambdaUpdate()
                    .set(TicketAccountMapping::getQwUserId, qwUserId)
                    .set(TicketAccountMapping::getDdUserId, ddUserId)
                    .set(TicketAccountMapping::getDdUserDeptId, ddUserDeptId)
                    .set(TicketAccountMapping::getDdUserDeptName, ddUserDeptName)
                    .setSql("`match_count` = `match_count`+1")
                    .set(TicketAccountMapping::getMatchResult, "success")
                    .set(TicketAccountMapping::getQyUserName, mp.getAccountName())
                    .set(TicketAccountMapping::getUpdateTime, new Date())
                    .eq(TicketAccountMapping::getId, mp.getId())
                    .update();
        }
    }

    public void disableExpireCardMsg(String responseCode) {
        redisLockRegistry.expireUnusedOlderThan(60000L);
        Lock obtain = redisLockRegistry.obtain("");
    }
}

@Component("defaultHandler")
class DefaultEventHandler implements NotificationService.ICardButtonCallBack {
    Logger logger = LoggerFactory.getLogger(DefaultEventHandler.class);

    @Override
    public void callback(String key, String userId) {
        logger.info("key = {} , userId = {}", key, userId);
    }
}