package com.smy.tfs.api.constants;

import java.io.Serializable;

public class TfsBaseConstant implements Serializable {

    private static final long serialVersionUID = -5904780299083007947L;

    public static final String listJoinSequence = ";";

    public static final String defaultAppId = "tfs";
    public static final String defaultOriginId = "10000";
    public static final String defaultUserId = "admin";
    public static final String defaultUserName = "admin";
    public static final String defaultUserType = "ldap";

    public static final String TFS_APPLY_APP_TEMPLATE = "tfs.apply.app.template";

    public static final String defaultTicketTemplateName = "初始化模板";

    public static final String front="f_";

    public static final String TFS_SYSTEM_USER="tfs_system";

    public static final String TFS_SYSTEM_ACCOUNT_TYPE = "ldap";
    public static final String TFS_SYSTEM_APP_TYPE = "tfs-boot";

    // fap 上传文件的场景
    public static final String FSP_UPLOAD_SCENE_TYPE = "tfs-file-ticket-upload";

    //组件key
    public static final String FORM_ITEM_VALUE_PREFIX="formItemValue";

    //默认版本号
    public static final Integer DEFAULT_VERSION = 1;

    //创建工单时效超时
    public static final String AGING_OVER_TIME = "\"工单超时\"";

    //处理工单时效超时
    public static final String DEAL_OVER_TIM = "\"处理超时\"";

    //系统处理
    public static final String SYS = "sys";


}
