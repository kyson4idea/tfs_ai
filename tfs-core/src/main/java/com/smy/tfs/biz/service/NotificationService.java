package com.smy.tfs.biz.service;


import com.smy.tfs.api.dto.NotificationDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.ticket_notify.NotifyMsgDto;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 通知相关服务服务
 */
public interface NotificationService {

    /**
     * @param message    markdown can  available ,max 2048
     * @param userIdList , userId in qw
     */
    //通用消息推送
    public void notifyQw(String message, List<String> userIdList);

    /**
     * @param notifyMsgDto
     */
    //文本消息推送
    public Response notifyMsg(NotifyMsgDto notifyMsgDto);


    /**
     * @param ticketNo     工单编号
     * @param ticketTitle  工单标题
     * @param ticketStatus 工单状态
     * @param handlePerson 当前处理人
     * @param userIdList   接受消息人，企业 微信 userid
     */
    public void notifyQwNormalMd(String ticketNo, String ticketTitle, String ticketStatus, String handlePerson,
                                 List<String> userIdList);

    /**
     * @param message      card message above buttons, max 2048
     * @param buttonKeyMap mapping: button key ->  text of button
     *                     buttonKeyMap = {"bean1-GD12345-approve-xx-xx","通过"，"bean2-GD12345-reject-xx-xx","拒绝"}
     * @param userIdList   qw userid
     * @return response_code ,  use when  update button state
     */
    //推送卡片消息
    public CardNotifyRet notifyQwCard(String message, Map<String, String> buttonKeyMap, List<String> userIdList);


    CardNotifyRet notifyQwCard(QwCardMsg cardMsg);

    /**
     * disable button in card message
     *
     * @param responseCode
     * @param newBtnText
     * @param userIds
     */
    public void disableCardButton(String responseCode, String newBtnText, List<String> userIds);

    /**
     * @param email email & phone 2者传一个就可以
     * @param phone
     * @return qwUserId
     */
    String getUserId(String email, String phone);

    /**
     * @param createChatGroup@return chatId
     */
    //创建群聊
    String createChatGroup(NotificationDto.CreateChatGroup createChatGroup);

    String updateChatGroup(String wxGroupId,List<String> addUserList,List<String> delUserList);

    String sendSimpleQwMsg(String wxGroupId,String msg);


    boolean doQwCallback(String msgSignature, String timestamp, String nonce, String bodyData);


    Set<String> queryChatUserList(String wxGroupId);

    String validCallbackUrl(String msgSignature, String timestamp, String nonce, String echoStr);

    /**
     * 同步匹配企业微信和钉钉的userid
     */
    void matchQwAndDdUserId();


    /**
     * 企业微信 回调后的接口， 发送卡片消息后需要实现对应的处理逻辑
     */
    interface ICardButtonCallBack {
        /**
         * @param key    button key
         * @param userId qw userid
         */
        void callback(String key, String userId);
    }

    @Getter
    @Builder
    class CardNotifyRet {
        private String responseCode;
        private String msgId;
        private String errorCode;
        private String errorMsg;
    }

    /**
     *
     *  代表4中kv消息类型
     *      text        {
     *                 "keyname": "邀请人",
     *                 "value": "张三"
     *             },
     *      link       {
     *                 "type": 1,
     *                 "keyname": "企业微信官网",
     *                 "value": "点击访问",
     *                 "url": "https://work.weixin.qq.com"
     *             },
     *       media      {
     *                 "type": 2,
     *                 "keyname": "企业微信下载",
     *                 "value": "企业微信.apk",
     *                 "media_id": "文件的media_id"
     *             },
     *      user       {
     *                 "type": 3,
     *                 "keyname": "员工信息",
     *                 "value": "点击查看",
     *                 "userid": "zhangsan"
     *             }
     */
    enum KvContentType {
        TEXT, LINK, MEDIA, USER
    }

    @Data
    class KvContent {
        private String keyName;
        private String value;
        private String url;
        private String mediaId;
        private String userId;
        private KvContentType type;

        public KvContent() {
        }
        public KvContent(KvContentType type,String keyName,String value,String url,String mediaId,String userId){
            this.keyName=keyName;
            this.value=value;
            this.url=url;
            this.mediaId=mediaId;
            this.userId=userId;
            this.type=type;
        }
    }


    @Data
    class QwCardMsg {
        private String title;
        private String description;
        private Map<String, String> buttonKeyMap;
        private Map<String, String> linkKeyMap;
        private List<String> userIdList;
        private String jumpUrl;
        private List<KvContent> kvContentList;
    }
}

