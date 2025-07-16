package com.smy.tfs.api.dto.query;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

@Data
@NoArgsConstructor
public class BusiQueryRspDto implements Serializable {
    private static final long serialVersionUID = -9155961408582020826L;

    /**
     * 工单编号
     */
    private String id;

    /**
     * 工单标题
     */
    private String ticketName;

    /**
     * 客户号
     */
    private String extend1;

    /**
     * 联系方式：手机号
     */
    private String extend2;

    /**
     * 工单优先级
     */
    private String extend3;

    /**
     * 反馈渠道
     */
    private String extend6;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间                        
     */
    private String updateTime;

    /**
     * 结单时间
     */
    private String ticketFinishTime;

    /**
     * 工单状态
     */
    private String ticketStatus;

    /**
     *
     * 当前处理人
     *
     */
    private String currentDealUsers;

    /**
     * 创建人
     */
    private String applyUser;

    /**
     * 已经审批完成的人
     */
    private String originalCurrentDoneUsers;
    /**
     *
     * 当前处理人
     *
     */
    private String originalCurrentDealUsers;

    /**
     * 创建人
     */
    private String originalApplyUser;


    /**
     * 所属业务
     */
    private String appName;
    /**
     * 工单模版类型名称
     */
    private String ticketTemplateName;

    /**
     * 当前节点名称
     */
    private String currentNodeName;

    /**
     * 当前节点id
     */
    private String currentNodeId;


    public BusiQueryRspDto(Map<String,Object> map){
        String createTimeStr = "";
        if (Objects.nonNull(map.get("create_time"))) {
            createTimeStr = (String)map.get("create_time");
        }
        String updateTimeStr = "";
        if (Objects.nonNull(map.get("update_time"))) {
            updateTimeStr = (String)map.get("update_time");
        }
        if (Objects.nonNull(map.get("id"))) {
            this.id = (String)map.get("id");
        }

        if (Objects.nonNull(map.get("ticket_name"))) {
            this.ticketName = (String)map.get("ticket_name");
        }
        if (Objects.nonNull(map.get("apply_user"))) {
            this.applyUser = (String)map.get("apply_user");
            this.originalApplyUser = (String)map.get("apply_user");
        }
        this.createTime = createTimeStr;
        this.updateTime = updateTimeStr;

        if (Objects.nonNull(map.get("current_deal_users"))) {
            this.currentDealUsers = (String)map.get("current_deal_users");
            this.originalCurrentDealUsers = (String)map.get("current_deal_users");
        }

        if (Objects.nonNull(map.get("current_done_users"))) {
            this.originalCurrentDoneUsers = (String)map.get("current_done_users");
        }

        if (Objects.nonNull(map.get("ticket_status"))) {
            this.ticketStatus = (String)map.get("ticket_status");
        }

        String ticketFinishTimeStr = "";
        if (Objects.nonNull(map.get("ticket_finish_time"))) {
            ticketFinishTimeStr = (String)map.get("ticket_finish_time");
        }
        this.ticketFinishTime = ticketFinishTimeStr;

        if (Objects.nonNull(map.get("extend1"))) {
            this.extend1 = (String)map.get("extend1");
        }
        if (Objects.nonNull(map.get("extend2"))) {
            this.extend2 = (String)map.get("extend2");
        }
        if (Objects.nonNull(map.get("extend3"))) {
            this.extend3 = (String)map.get("extend3");
        }
        if (Objects.nonNull(map.get("extend6"))) {
            this.extend6 = (String)map.get("extend6");
        }
        if (Objects.nonNull(map.get("app_name"))) {
            this.appName = (String)map.get("app_name");
        }
        String templateName = "";
        if (Objects.nonNull(map.get("template_name"))) {
            templateName = (String)map.get("template_name");
        }
        this.ticketTemplateName = templateName;
        if (Objects.nonNull(map.get("current_node_name"))) {
            this.currentNodeName = (String)map.get("current_node_name");
        }
    }


}
