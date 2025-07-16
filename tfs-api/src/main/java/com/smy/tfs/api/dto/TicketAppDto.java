package com.smy.tfs.api.dto;

import com.smy.tfs.api.dbo.TicketExecutorGroup;
import com.smy.tfs.api.enums.YESNOEnum;
import com.smy.tfs.api.valid.AddGroup;
import com.smy.tfs.api.valid.DeleteGroup;
import com.smy.tfs.api.valid.UpdateGroup;
import lombok.Data;
import org.apache.dubbo.apidocs.annotations.RequestParam;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class TicketAppDto implements Serializable {

    private static final long serialVersionUID = 7658180680992438553L;
    /**
     * $column.columnComment
     */
    @RequestParam(value = "业务id", example = "1001", description = "业务id")
    @NotBlank(message = "业务id不能为空", groups = {AddGroup.class, UpdateGroup.class, DeleteGroup.class})
    private String id;

    @RequestParam(value = "搜索值", example = "1001", description = "搜索值")
    private String searchValue;

    /**
     * 业务名称
     */
    @RequestParam(value = "业务名称", example = "催收", description = "业务名称")
    @NotBlank(message = "业务名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String appName;

    /**
     * 业务描述
     */
    @RequestParam(value = "业务描述", example = "催收", description = "业务描述")
    @NotBlank(message = "业务描述不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String appDesc;

    /*** 业务管理员（只能域账号）* []     */
    /*** 业务管理员（只能域账号）* [{accountType:”ldap“,accountId:”y01781”},{accountType:”ldap”,accountId:”y01782”}]    */
    @RequestParam(value = "业务管理员", example = "[{accountType:”ldap“,accountId:”y01781”},{accountType:”ldap”,accountId:”y01782”}]", description = "业务管理员")
    private String appAdminUsers;

    /*** 业务管理员（只能域账号）* []     */
    @RequestParam(value = "业务管理员", example = "[{accountType:”ldap“,accountId:”y01781”},{accountType:”ldap”,accountId:”y01782”}]", description = "业务管理员")
    @NotEmpty(message = "业务管理员不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private List<String> appAdminUserList;

    /**
     * 业务账户类型
     */
    @RequestParam(value = "业务账户类型", description = "业务账户类型")
    @NotBlank(message = "业务账户类型不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String accountType;


    /**
     * $column.columnComment
     */
    @RequestParam(value = "记录删除时间", example = "2024-04-17 15:00:00", description = "记录删除时间")
    private Date deleteTime;

    /**
     * 创建者
     */
    @RequestParam(value = "创建者", example = "y01781", description = "创建者")
    private String createBy;

    /* 创建时间 yyyy-MM-dd HH:mm:ss */
    @RequestParam(value = "记录创建时间", example = "2024-04-17 15:00:00", description = "记录创建时间")
    private Date createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间 yyyy-MM-dd HH:mm:ss
     */
    @RequestParam(value = "记录更新时间", example = "2024-04-17 15:00:00", description = "记录更新时间")
    private Date updateTime;

    /**
     * 备注
     */
    @RequestParam(value = "备注", example = "此业务只给催收部门用", description = "备注")
    private String remark;

    /*业务用户组信息*/
    @RequestParam(value = "业务用户组信息", example = "此业务只给催收部门用", description = "业务用户组信息")
    private List<TicketExecutorGroup> ticketExecutorGroupList;

    /**
     * 当前审核量
     */
    @RequestParam(value = "提单人数", description = "提单人数")
    private Integer createByCount;

    /**
     * 每日申请量
     */
    @RequestParam(value = "审批人数", description = "审批人数")
    private Integer executorCount;

    /**
     * 每日完成量
     */
    @RequestParam(value = "工单申请量", description = "工单申请量")
    private Integer applyCount;

    /**
     * 每日完成量
     */
    @RequestParam(value = "工单完成量", description = "工单完成量")
    private Integer doneCount;

    /**
     * 每日完成量
     */
    @RequestParam(value = "未完成工单量", description = "未完成工单量")
    private Integer applyingCount;

    /**
     * 工单平均审批时效（s）
     */
    @RequestParam(value = "工单平均审批时效", description = "工单平均审批时效")
    private Integer ticketAvgEfficiency;

    @RequestParam(value = "工单平均审批时效", description = "工单平均审批时效")
    private String ticketAvgEfficiencyStr;

    @RequestParam(value = "时间周期（周/天）", description = "时间周期（周/天）")
    private String category;

    /**
     * 统计开始日期 2024-03-23 00:00:00
     */
    @RequestParam(value = "统计开始日期", description = "统计开始日期")
    private String startDateStr;

    /**
     * 统计结束日期 2024-03-23 23:59:59
     */
    @RequestParam(value = "统计结束日期", description = "统计结束日期")
    private String endDateStr;

    /**
     * 是否是业务管理员
     */
    @RequestParam(value = "是否是业务管理员", description = "是否是业务管理员")
    private Boolean isAppAdmin;

    /**
     * 业务状态
     */
    @RequestParam(value = "业务状态", description = "业务状态")
    private String appStatus;

    @RequestParam(value = "分析日期", description = "分析日期")
    private String analysisDateStr;

    /**
     * 企微群聊ID
     */
    private String wxChatGroupId;

    /**
     * 是否开启工单分类
     */
    private YESNOEnum categoryEnabled;

    /**
     * 是否开启工单扩展字段
     */
    private YESNOEnum extendEnabled;

    /**
     * 扩展字段
     */
    private String extendFields;
}
