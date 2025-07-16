package com.smy.tfs.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum CallBackMsgStatusEnum implements Serializable {
    SEND_INIT("SEND_INIT","未发送消息"),
    SUCCESS_MSG_SENG("SUCCESS_MSG_SENG", "发送成功消息"),
    EXCEPTION_MSG_SENG("EXCEPTION_MSG_SENG","发送异常消息"),
    ;

    private String code;
    private String desc;
}
