package com.smy.tfs.common.constant;

/**
 * 缓存的key 常量
 *
 * @author ruoyi
 */
public class CacheConstants {
    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "tfs-boot:login_tokens:";

    public static final String LOGIN_JSSDK_TOKEN_KEY = "tfs-boot:jssdk_login_tokens:";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "tfs-boot:captcha_codes:";

    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "tfs-boot:sys_config:";

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "tfs-boot:sys_dict:";

    /**
     * 防重提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "tfs-boot:repeat_submit:";

    /**
     * 限流 redis key
     */
    public static final String RATE_LIMIT_KEY = "tfs-boot:rate_limit:";

    /**
     * 登录账户密码错误次数 redis key
     */
    public static final String PWD_ERR_CNT_KEY = "tfs-boot:pwd_err_cnt:";

    /**
     * 企业微信access token
     */
    public static final String QW_ACCESS_TOKEN_KEY = "tfs-boot:qw_access_token:";

    /**
     * 企业微信 card msg的code
     */
    public static final String QW_CARD_MSG_CODE_KEY = "tfs-boot:qw_card_msg_code:";

    /**
     * 域账号类型名称映射缓存
     */
    public static final String TFS_TICKET_ACCOUNT_TYPE_NAME = "tfs-boot:ticket_account_type_name:";

    /**
     * appId和ticket_app的映射缓存
     */
    public static final String TFS_TICKET_APP = "tfs-boot:ticket_app:";

    /**
     * appId和ticket_category的映射缓存
     */
    public static final String TFS_TICKET_TEMPLATEID_CATEGORY = "tfs-boot:ticket_templateid_category:";

    /**
     * appId和ticket_category的映射缓存
     */
    public static final String TFS_TICKET_TEMPLATE = "tfs-boot:ticket_template:";

}
