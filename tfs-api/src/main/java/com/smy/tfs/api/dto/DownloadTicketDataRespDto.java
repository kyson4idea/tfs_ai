package com.smy.tfs.api.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.smy.tfs.api.dbo.TicketData;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
public class DownloadTicketDataRespDto implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 工单id
     */
    @ExcelProperty("工单编号")
    private String id;

    /**
     * 工单名称
     */
    @ExcelProperty("工单名称")
    private String ticketName;

    /**
     * 应用名称
     */
    @ExcelProperty("所属业务")
    private String appName;

    /**
     * 工单模版类型名称
     */
    @ExcelProperty("工单模版")
    private String ticketTemplateName;

    /**
     * 工单状态
     */
    @ExcelProperty("工单状态")
    private String ticketStatus;

    /**
     * 创建时间
     */
    @ExcelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnWidth(20)
    private Date createTime;

    /**
     * 结单时间
     */
    @ExcelProperty("结单时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnWidth(20)
    private Date ticketFinishTime;

    /**
     * 申请人
     */
    @ExcelProperty("申请人")
    private String applyUser;

    /**
     * 受理人
     */
    @ExcelProperty("受理人")
    private String currentDealUsers;

    @ExcelProperty("${extend1}")
    private String extend1;
    @ExcelProperty("${extend2}")
    private String extend2;
    @ExcelProperty("${extend3}")
    private String extend3;
    @ExcelProperty("${extend4}")
    private String extend4;
    @ExcelProperty("${extend5}")
    private String extend5;
    @ExcelProperty("${extend6}")
    private String extend6;
    @ExcelProperty("${extend7}")
    private String extend7;
    @ExcelProperty("${extend8}")
    private String extend8;
    @ExcelProperty("${extend9}")
    private String extend9;
    @ExcelProperty("${extend10}")
    private String extend10;
    @ExcelProperty("appId")
    private String appId;
    public DownloadTicketDataRespDto(TicketData ticketData) {
        this.id = ticketData.getId();
        this.ticketName = ticketData.getTicketName();
        this.applyUser = ticketData.getApplyUser();
        if (null != ticketData.getTicketStatus()) {
            this.ticketStatus = ticketData.getTicketStatus().getMsg();
        }
        this.createTime = ticketData.getCreateTime();
        this.ticketFinishTime = ticketData.getTicketFinishTime();
        this.currentDealUsers = ticketData.getCurrentDealUsers();
        this.appName = ticketData.getAppId();
        this.ticketTemplateName = ticketData.getTemplateId();
        this.extend1 = ticketData.getExtend1();
        this.extend2 = ticketData.getExtend2();
        this.extend3 = ticketData.getExtend3();
        this.extend4 = ticketData.getExtend4();
        this.extend5 = ticketData.getExtend5();
        this.extend6 = ticketData.getExtend6();
        this.extend7 = ticketData.getExtend7();
        this.extend8 = ticketData.getExtend8();
        this.extend9 = ticketData.getExtend9();
        this.extend10 = ticketData.getExtend10();
        this.appId = ticketData.getAppId();
    }


}
