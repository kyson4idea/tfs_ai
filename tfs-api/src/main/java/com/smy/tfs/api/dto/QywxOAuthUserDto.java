package com.smy.tfs.api.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class QywxOAuthUserDto {
    @JSONField(name = "errcode")
    private Integer errorCode;

    @JSONField(name = "errmsg")
    private String errorMsg;

    @JSONField(name = "UserId")
    private String userId;

    @JSONField(name = "OpenId")
    private String openId;

    @JSONField(name = "DeviceId")
    private String deviceId;

    @JSONField(name = "external_userid")
    private String externalUserId;
}
