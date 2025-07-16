package com.smy.tfs.api.service;

import cn.hutool.core.map.MapUtil;
import com.smy.tfs.api.constants.QWCardMD;
import com.smy.tfs.api.dto.NotificationDto;
import com.smy.tfs.api.dto.ticket_notify.NotifyMsgDto;
import com.smy.tfs.biz.config.TfSJumpUrlProperties;
import com.smy.tfs.biz.service.NotificationService;
import com.smy.tfs.common.utils.notification.QwCorporateEnum;
import com.smy.tfs.common.utils.notification.QwNotify;
import com.smy.tfs.openapi.service.ITicketNotificationWrapper;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.util.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class INotificationServiceTest {
    @Autowired
    NotificationService notificationService;

    @Resource
    private TfSJumpUrlProperties tfSJumpUrlProperties;

    @Test
    public void notifyTest() {
        QwNotify.notifyQw("测试1", "ZhangZeDong", QwCorporateEnum.HJSD);
        QwNotify.notifyQw("测试1", "zhangzedong", QwCorporateEnum.SMY);
    }

    @Test
    public void matchQwAndDdUserIdTest(){
        notificationService.matchQwAndDdUserId();
    }

    @Test
    public void createChatGroupTest() {
        List<String> userList = new ArrayList<>();
        userList.add("owen");
        userList.add("yinshasha");
        NotificationDto.CreateChatGroup createChatGroup = new NotificationDto.CreateChatGroup(
                userList,
                "测试组名",
                "owen",
                "",
                QWCardMD.qwGroupCardStr
                        .replace("{{ticket_id}}", "GD12345")
                        .replace("{{ticket_name}}", "工单名称")
                        .replace("{{apply_user}}", "张泽东")
                        .replace("{{apply_time}}", DateFormat.getInstance().format(new Date()))
                        .replace("{{ticket_detail_url}}", tfSJumpUrlProperties.getTicketDetailUrl())
        );
        String chatId = notificationService.createChatGroup(createChatGroup);
    }

    @Test
    public void notifyQwCardTest() {
        NotificationService.QwCardMsg cardMsg = new NotificationService.QwCardMsg();
        List<String> userList = new ArrayList<>();
//        userList.add("owen");
        userList.add("yinshasha");
        String msg = "ok-yinshasha";
//        String msg = QWCardMD.qwDealCardStr
//                .replace("{{ticket_id}}", "GD12345")
//                .replace("{{ticket_name}}", "工单名称")
//                .replace("{{apply_user}}", "张泽东")
//                .replace("{{apply_time}}", DateFormat.getInstance().format(new Date()))
//                .replace("{{ticket_detail_url}}", tfSJumpUrlProperties.getTicketDetailUrl());
        Map<String, String> ccButtonMap = new LinkedHashMap<>();
//        String passKey = "approveCardButtonCallBack-1002405140002440038-PASS-tfs-1082405140001800006";
//        String rejectKey = "approveCardButtonCallBack-1002405140002440038-REJECT-tfs-1082405140001800006";
//        ccButtonMap.put(rejectKey, "驳回");
//        ccButtonMap.put(passKey, "通过");



        Map<String, String> linkMap = new HashMap<>();
        String jumpUrl = "https://www.baidu.com/";
        linkMap.put("详情", jumpUrl);
        cardMsg.setLinkKeyMap(linkMap);
        cardMsg.setTitle("ceshi");
        cardMsg.setDescription(msg);
        cardMsg.setUserIdList(userList);
//        cardMsg.setJumpUrl("");
        var chatId = notificationService.notifyQwCard(cardMsg);
    }

    @Autowired
    ITicketNotificationWrapper iTicketNotificationWrapper;
    @Test
    public void notifyMsgTest() {
        NotifyMsgDto notifyMsgDto = new NotifyMsgDto();
        notifyMsgDto.setMessage("12345");
        notifyMsgDto.setUserType("ldap");
        notifyMsgDto.setUserIdList(Arrays.asList("y01781"));
        var chatId = iTicketNotificationWrapper.notifyMsg(notifyMsgDto);
    }


    @Test
    public void notifyQwCard2Test() {
        NotificationService.QwCardMsg qwcardMsg = new NotificationService.QwCardMsg();
        qwcardMsg.setTitle("你有新的工单待处理，{{app_name}} {{apply_user}}于{{apply_time}}发起的{{ticket_name}} \n");

        Map<String, String> buttonMap = new LinkedHashMap<>();
        buttonMap.put("approveCardButtonCallBack-1002405140002440038-PASS-tfs-1082405140001800006", "通过");
        buttonMap.put("approveCardButtonCallBack-1002405140002440038-REJECT-tfs-1082405140001800006", "驳回");
        qwcardMsg.setButtonKeyMap(buttonMap);

        List<String> userIdList = new ArrayList<>();
        userIdList.add("owen");
        userIdList.add("yinshasha");
        qwcardMsg.setUserIdList(userIdList);

        String jumpUrl = tfSJumpUrlProperties.getTicketDetailUrl() + "GD12345";
        qwcardMsg.setJumpUrl(jumpUrl);

        List<NotificationService.KvContent> kvContentList = new ArrayList<>();
        kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.TEXT, "姓名", "Owen", "", "", ""));
        kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.TEXT, "性别", "男", "", "", ""));
        kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.TEXT, "年龄", "30", "", "", ""));
        kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.TEXT, "地址", "深圳", "", "", ""));
        qwcardMsg.setKvContentList(kvContentList);

        NotificationService.CardNotifyRet ret = notificationService.notifyQwCard(qwcardMsg);
        if (ret != null) {
            log.info("发送企业微信卡片消息成功，消息ID:{}", ret.getMsgId());
        }
    }

        @Test
        public void notifyQwCard_0_Test() {
            NotificationService.QwCardMsg msg = new NotificationService.QwCardMsg();
            msg.setJumpUrl(tfSJumpUrlProperties.getDashboardUrl());
            msg.setUserIdList(Collections.singletonList("owen"));
            msg.setTitle("请审批");
            msg.setDescription("点击萨克来的时间阿卡来的时间");
            NotificationService.KvContent kv2 = new NotificationService.KvContent();
            kv2.setType(NotificationService.KvContentType.LINK);
            kv2.setKeyName("link1");
            kv2.setValue("linkText1");
            kv2.setUrl("http://www.baidu.com");
            NotificationService.KvContent kv1 = new NotificationService.KvContent();
            kv1.setType(NotificationService.KvContentType.TEXT);
            kv1.setKeyName("label1");
            kv1.setValue("value1");
            msg.setKvContentList(Arrays.asList(kv1,kv2));
            Map<String, String> ccButtonMap = new LinkedHashMap<>();
            String passKey="approveCardButtonCallBack-1002405140002440038-PASS-tfs-1082405140001800006";
            String rejectKey="approveCardButtonCallBack-1002405140002440038-REJECT-tfs-1082405140001800006";
            ccButtonMap.put(passKey, "通过");
            ccButtonMap.put(rejectKey, "驳回");
            msg.setButtonKeyMap(ccButtonMap);
//            msg.setLinkKeyMap(MapUtil.of("统一工单平台", msg.getJumpUrl()));
            var chatId = notificationService.notifyQwCard(msg);
        }
}
