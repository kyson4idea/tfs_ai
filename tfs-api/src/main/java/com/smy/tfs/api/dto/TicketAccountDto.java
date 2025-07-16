package com.smy.tfs.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smy.tfs.api.valid.AddGroup;
import com.smy.tfs.api.valid.DeleteGroup;
import com.smy.tfs.api.valid.OperateGroup;
import com.smy.tfs.api.valid.UpdateGroup;
import lombok.Data;


import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@Data
public class TicketAccountDto extends TicketAccountDubboConfigDto implements Serializable {

    private static final long serialVersionUID = -1646989702817741882L;
    /**
     * ID
     */
    @NotBlank(message = "账户体系id不能为空", groups = {DeleteGroup.class, UpdateGroup.class, OperateGroup.class})
    private String id;

    /**
     * 账户名称
     */
    @NotBlank(message = "账户体系名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String ticketAccountName;

    /**
     * 账户类型
     */
    @NotBlank(message = "账户体系类型不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String ticketAccountType;

    /**
     * 账户配置
     */
    private String ticketAccountValue;

    /**
     * 描述
     */
    @NotBlank(message = "账户体系描述不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String ticketAccountDescription;

    /**     * 创建者     */
    private String createBy;

    /* 创建时间 yyyy-MM-dd HH:mm:ss */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**     * 更新者     */
    private String updateBy;

    /**     * 更新时间 yyyy-MM-dd HH:mm:ss     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date deleteTime;

    /**
     * 状态
     */
    private String status;
}
