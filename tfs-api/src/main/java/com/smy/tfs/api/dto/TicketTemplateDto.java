package com.smy.tfs.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smy.tfs.api.dbo.TicketTemplate;
import com.smy.tfs.api.dto.ticket_sla_service.TicketSlaTemplateDto;
import com.smy.tfs.api.enums.TicketMsgArriveTypeEnum;
import com.smy.tfs.api.enums.TicketMsgBuildTypeEnum;
import com.smy.tfs.api.enums.TicketTemplateStatusEnum;
import com.smy.tfs.api.enums.YESNOEnum;
import com.smy.tfs.api.valid.UpdateStatusGroup;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class TicketTemplateDto implements Serializable {
    private static final long serialVersionUID = 9046196593876502910L;
    //是否需要控制用户应用权限
    private boolean needControl;

    //模糊搜索字段
    private String searchValue;

    /**
     * 工单模版id
     */
    @NotBlank(message = "工单模版id不能为空", groups = {UpdateStatusGroup.class})
    private String id;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 应用ID
     */
    private List<String> appIdList;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 模版状态：
     * 草稿
     * 暂停
     * 启用中
     * 作废
     */
    private String ticketStatus;

    /**
     * 工单名称
     */
    private String ticketName;

    /**
     * 说明
     */
    private String description;

    /**
     * 工单模板标识
     */
    private String ticketTemplateCode;

    /**
     * 关联的应用
     */
    private String beyondApps;

    /**
     * 接口生成标识
     */
    private String interfaceKey;

    /**
     * 工单消息生成方式
     */
    private String ticketMsgBuildType;


    /**
     * 工单消息触达方式
     */
    private String ticketMsgArriveType;


    /**
     * 工单支持修改标识
     */
    private String ticketFormChangeFlag;

    /**
     * 工单表单模版
     */
    private TicketFormTemplateDto ticketFormTemplateDto;

    /**
     * 工单流程模版
     */
    private  TicketFlowTemplateDto ticketFlowTemplateDto;

    /**
     * 工单sla模版
     */
    private TicketSlaTemplateDto ticketSlaTemplateDto;



    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date appCreateTime;

    /**
     * 工单支持时效标识
     */
    private String ticketAgingFlag;

    /**
     * 工单创建超时时长(单位小时)
     */
    private Integer ticketAgingTime;

    /**
     * 工单处理超时时长(单位小时)
     */
    private Integer ticketDealTime;

    /**
     * 显示部门名称
     */
    private String showDeptNameFlag;

    /**
     * 工单发起类型["jssdk","api","pc"]
     */
    private String applyTicketWays;

    /**
     * 是否支持查询订阅应用
     */
    private boolean supportBeyondApps = true;



    public TicketTemplateDto() {
    }

    public TicketTemplate toTicketTemplate(TicketTemplateDto ticketTemplateDto){
        TicketTemplateStatusEnum ticketTemplateStatus = null;
        if (ObjectHelper.isNotEmpty(ticketTemplateDto.getTicketStatus())) {
            ticketTemplateStatus = TicketTemplateStatusEnum.getEnumByCode(ticketTemplateDto.getTicketStatus());
        }
        TicketMsgBuildTypeEnum ticketMsgBuildType = null;
        if (ObjectHelper.isNotEmpty(ticketTemplateDto.getTicketMsgBuildType())) {
            ticketMsgBuildType = TicketMsgBuildTypeEnum.getEnumByCode(ticketTemplateDto.getTicketMsgBuildType());
        }
        TicketMsgArriveTypeEnum ticketMsgArriveType = null;
        if (ObjectHelper.isNotEmpty(ticketTemplateDto.getTicketMsgArriveType())) {
            ticketMsgArriveType = TicketMsgArriveTypeEnum.getEnumByCode(ticketTemplateDto.getTicketMsgArriveType());
        }
        YESNOEnum ticketFormChangeFlag = null;
        if (ObjectHelper.isNotEmpty(ticketTemplateDto.getTicketFormChangeFlag())) {
            ticketFormChangeFlag = YESNOEnum.getEnumByCode(ticketTemplateDto.getTicketFormChangeFlag());
        }
        YESNOEnum ticketAgingFlag = null;
        if (ObjectHelper.isNotEmpty(ticketTemplateDto.getTicketAgingFlag()))
            ticketAgingFlag = YESNOEnum.getEnumByCode(ticketTemplateDto.getTicketAgingFlag());
        YESNOEnum showDeptNameFlag = null;
        if (ObjectHelper.isNotEmpty(ticketTemplateDto.getShowDeptNameFlag()))
            showDeptNameFlag = YESNOEnum.getEnumByCode(ticketTemplateDto.getShowDeptNameFlag());

        TicketTemplate ticketTemplate = new TicketTemplate();
        ticketTemplate.setId(ticketTemplateDto.getId());
        ticketTemplate.setAppId(ticketTemplateDto.getAppId());
        ticketTemplate.setTicketStatus(ticketTemplateStatus);
        ticketTemplate.setTicketName(ticketTemplateDto.getTicketName());
        ticketTemplate.setDescription(ticketTemplateDto.getDescription());
        ticketTemplate.setTicketTemplateCode(ticketTemplateDto.getTicketTemplateCode());
        ticketTemplate.setBeyondApps(ticketTemplateDto.getBeyondApps());
        ticketTemplate.setInterfaceKey(ticketTemplateDto.getInterfaceKey());
        ticketTemplate.setTicketMsgBuildType(ticketMsgBuildType);
        ticketTemplate.setTicketMsgArriveType(ticketMsgArriveType);
        ticketTemplate.setTicketFormChangeFlag(ticketFormChangeFlag);
        ticketTemplate.setTicketAgingFlag(ticketAgingFlag);
        ticketTemplate.setTicketAgingTime(ticketTemplateDto.getTicketAgingTime());
        ticketTemplate.setTicketDealTime(ticketTemplateDto.getTicketDealTime());
        ticketTemplate.setShowDeptNameFlag(showDeptNameFlag);
        ticketTemplate.setApplyTicketWays(ticketTemplateDto.getApplyTicketWays());

        return ticketTemplate;
    }

    public TicketTemplateDto(TicketTemplate ticketTemplate){
        String ticketTemplateStatus = null;
        if (ObjectHelper.isNotEmpty(ticketTemplate.getTicketStatus())) {
            ticketTemplateStatus = ticketTemplate.getTicketStatus().getCode();
        }
        String ticketMsgBuildType = null;
        if (ObjectHelper.isNotEmpty(ticketTemplate.getTicketMsgBuildType())) {
            ticketMsgBuildType = ticketTemplate.getTicketMsgBuildType().getCode();
        }
        String ticketMsgArriveType = null;
        if (ObjectHelper.isNotEmpty(ticketTemplate.getTicketMsgArriveType())) {
            ticketMsgArriveType = ticketTemplate.getTicketMsgArriveType().getCode();
        }
        String ticketFormChangeFlag = null;
        if (ObjectHelper.isNotEmpty(ticketTemplate.getTicketFormChangeFlag())) {
            ticketFormChangeFlag = ticketTemplate.getTicketFormChangeFlag().getCode();
        }
        String ticketAgingFlag = null;
        if (ObjectHelper.isNotEmpty(ticketTemplate.getTicketAgingFlag())) {
            ticketAgingFlag = ticketTemplate.getTicketAgingFlag().getCode();
        }
        String showDeptNameFlag = null;
        if (ObjectHelper.isNotEmpty(ticketTemplate.getShowDeptNameFlag())) {
            showDeptNameFlag = ticketTemplate.getShowDeptNameFlag().getCode();
        }
        this.id = ticketTemplate.getId();
        this.appId = ticketTemplate.getAppId();
        this.ticketStatus = ticketTemplateStatus;
        this.ticketName = ticketTemplate.getTicketName();
        this.description = ticketTemplate.getDescription();
        this.ticketTemplateCode = ticketTemplate.getTicketTemplateCode();
        this.beyondApps = ticketTemplate.getBeyondApps();
        this.interfaceKey = ticketTemplate.getInterfaceKey();
        this.ticketMsgBuildType = ticketMsgBuildType;
        this.ticketMsgArriveType = ticketMsgArriveType;
        this.ticketFormChangeFlag = ticketFormChangeFlag;
        this.ticketAgingFlag = ticketAgingFlag;
        this.ticketAgingTime = ticketTemplate.getTicketAgingTime();
        this.ticketDealTime = ticketTemplate.getTicketDealTime();
        this.showDeptNameFlag = showDeptNameFlag;
        this.applyTicketWays = ticketTemplate.getApplyTicketWays();
    }

}
