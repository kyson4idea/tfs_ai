package com.smy.tfs.biz.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.smy.tfs.api.dto.QywxOAuthUserDto;
import com.smy.tfs.api.enums.QywxErrorCode;
import com.smy.tfs.biz.service.IQywxService;
import com.smy.tfs.common.exception.ServiceException;
import com.smy.tfs.common.utils.notification.QwNotify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class IQywxServiceImpl implements IQywxService {
    public static final String GET_USER_INFO_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=%s&code=%s";

    @Override
    public QywxOAuthUserDto authorize(String code) {
        QywxOAuthUserDto qywxOAuthUserDTO;
        String accessToken = QwNotify.getAccessToken();
        String resp = null;
        resp = HttpUtil.get(String.format(GET_USER_INFO_URL, accessToken, code));
        qywxOAuthUserDTO = JSONObject.parseObject(resp, QywxOAuthUserDto.class);
        if (QywxErrorCode.INVALID_ACCESS_TOKEN.getCode() == qywxOAuthUserDTO.getErrorCode()) { //access_token主动失效, 重试一次
            accessToken = QwNotify.refreshAccessToken();
            resp = HttpUtil.get(String.format(GET_USER_INFO_URL, accessToken, code));
            qywxOAuthUserDTO = JSONObject.parseObject(resp, QywxOAuthUserDto.class);
        }
        if (QywxErrorCode.isError(qywxOAuthUserDTO.getErrorCode())) {
            QywxErrorCode errorCode = QywxErrorCode.of(qywxOAuthUserDTO.getErrorCode());
            log.error("获取认证主体信息失败: {}, 错误信息: {}", errorCode, qywxOAuthUserDTO.getErrorMsg());
            throw new ServiceException("获取认证主体信息失败");
        }
        return qywxOAuthUserDTO;
    }
}
