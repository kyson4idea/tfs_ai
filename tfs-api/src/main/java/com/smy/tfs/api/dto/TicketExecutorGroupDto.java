package com.smy.tfs.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smy.tfs.api.valid.AddGroup;
import com.smy.tfs.api.valid.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class TicketExecutorGroupDto implements Serializable {

    private static final long serialVersionUID = 5351785916682572448L;
    @NotBlank(message = "账户体系id不能为空", groups = {UpdateGroup.class})
    private String id;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用id
        */
    @NotBlank(message = "所属应用id不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String appId;


    private List<String> appIdList;

    /**
     * 应用组名称
     */
    @NotBlank(message = "应用组名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String executorGroupName;

    /**
     * 应用组描述
     */
    @NotBlank(message = "应用组描述不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String executorGroupDesc;

    /**
     * 账户信息
     * [
     * {account_type:””,account_id:””},
     * {account_type:””,account_id:””},
     * {account_type:””,account_id:””},
     * ]
     */

    private String accountInfo;

    private String accountType;

    @NotEmpty(message = "账户信息不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private List<String> accountIdList;

    /**     * 创建者     */
    private String createBy;

    /* 创建时间 yyyy-MM-dd HH:mm:ss */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date deleteTime;

    /**
     * 状态
     */
    private String status;

    //是否需要控制用户应用权限
    private boolean needControl;


}
