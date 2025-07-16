package com.smy.tfs.biz.controller;

import com.smy.tfs.api.dto.NotificationDto;
import com.smy.tfs.biz.service.NotificationService;
import com.smy.tfs.common.annotation.Anonymous;
import com.smy.tfs.common.core.domain.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 通知模块 前端控制器
 * </p>
 *
 * @author w01322
 * @since 2024-04-19
 */
@Controller
@Api("通知模块 前端控制器")
@RequestMapping("/notification")
public class NotificationController {


    @Resource
    private NotificationService notificationService;

    @PostMapping("/createChatRoom")
    @ResponseBody
    @ApiOperation("创建聊天群")
    public AjaxResult createChatRoom(@RequestBody NotificationDto.CreateChatGroup createChatGroup) {
        String chatId = notificationService.createChatGroup(createChatGroup);
        return AjaxResult.success("创建成功", chatId);
    }

    @PostMapping("/test")
    @ResponseBody
    @ApiOperation("test")
    @Anonymous
    public AjaxResult test() {

//        userId 转换 相关
//        String userId = notificationService.getUserId("huangmingyi@smyfinancial.com","");
//        System.out.println(userId);

//     普通消息
        /*notificationService.notifyQwNormalMd("GD1002","网络负载高","审批中","魏斌", Lists.newArrayList("WeiBin"));*/

//     卡片消息
//        NotificationService.CardNotifyRet ret = notificationService.notifyQwCard("工单需要你审核",
//                JSONObject.of("gdApprove-NO123344", "click").to(new TypeReference<Map<String, String>>() {
//                }), Lists.newArrayList("WeiBin"));
//        System.out.println(ret.getResponseCode());

//        禁用卡片按钮
//        notificationService.disableCardButton("z1TSFpEZ-FyS6BkItU3VYVeMD__Zo9TQpcERq6Kwd48","disable" , Lists.newArrayList("WeiBin"));
//        notificationService.matchQwUserId();

        String msgStr = "{\n" +
                "  \"title\": \"title_e298819376c9\",\n" +
                "  \"buttonKeyMap\": {\"k1\":\"btn1\",\"k2\":\"btn1\"},\n" +
                "  \"userIdList\": [\n" +
                "    \"WeiBin\"\n" +
                "  ],\n" +
                "  \"jumpUrl\": \"http://www.baidu.com\",\n" +
                "  \"kvContentList\": [\n" +
                "    {\n" +
                "      \"keyName\": \"baidu1\",\n" +
                "      \"value\": \"baidu\",\n" +
                "      \"url\": \"http://www.baidu.com\",\n" +
                "      \"type\": 1\n" +
                "    }\n,"+
                "   {\n" +
                "      \"keyName\": \"text\",\n" +
                "      \"value\": \"t1\",\n" +
                "      \"type\": 0\n" +
                "    }\n" +
                "  "+
                "  ]\n" +
                "}";
//        notificationService.notifyQwCard(JSONUtil.toBean(msgStr, NotificationService.QwCardMsg.class));
        return AjaxResult.success();
    }

    @PostMapping("/qw/callback")
    @Anonymous
    @ApiOperation("企业微信回调地址")
    public ResponseEntity<String> qwCallback(@RequestParam String msg_signature, @RequestParam String timestamp,
                                             @RequestParam String nonce, @RequestBody String bodyData) {
        boolean ret = notificationService.doQwCallback(msg_signature, timestamp, nonce, bodyData);
        if (ret) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/qw/callback")
    @ApiOperation("回调地址验证")
    @Anonymous
    public ResponseEntity<String> validCallbackUrl(@RequestParam String msg_signature, @RequestParam String timestamp,
                                                   @RequestParam String nonce, @RequestParam String echostr) {
        String url = notificationService.validCallbackUrl(msg_signature, timestamp, nonce, echostr);
        return new ResponseEntity<>(url, HttpStatus.OK);
    }

}
