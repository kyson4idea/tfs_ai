package com.smy.tfs.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 常见企业微信api调用错误码及信息
 *
 * @see <a href="https://work.weixin.qq.com/api/doc/90000/90139/90313">全局错误码</a>
 */
@Getter
@AllArgsConstructor
public enum QywxErrorCode {

    BUSYNESS(-1, "系统繁忙"),
    SUCCESS(0, "请求成功"),
    INVALID_SECRET(40001, "不合法的secret参数"),
    INVALID_USER_ID(40003, "无效的UserID"),
    INVALID_CORP_ID(40013, "不合法的CorpID"),
    INVALID_ACCESS_TOKEN(40014, "不合法的access_token"),
    INVALID_OAUTH_CODE(40029, "不合法的oauth_code"),
    INVALID_AGENT_ID(40056, "不合法的agentid"),
    INVALID_CALLBACK_URL(40057, "不合法的callbackurl或者callbackurl验证失败"),
    INVALID_PARAMETER(40058, "不合法的参数"),
    ;

    private final int code;
    private final String msg;

    public static boolean isError(int code) {
        return code != SUCCESS.code;
    }

    public static QywxErrorCode of(int code) {
        for (QywxErrorCode errorCode : values()) {
            if (errorCode.code == code) {
                return errorCode;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "QywxErrorCode{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
