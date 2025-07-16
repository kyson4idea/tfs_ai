package com.smy.tfs.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class NCSTicketDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单申请人
     */
    @JsonAlias({"applyUserId", "apply_user_id"})
    private String applyUserId;

    /**
     * 工单类型（xx类型工单）
     */
    @JsonAlias({"ticket_template_id", "ticketTemplateId"})
    private String ticketTemplateId;

    /**
     * 工单ID
     */
    @JsonAlias({"apply_id", "applyId"})
    private String applyId;

    /**
     * 工单内容
     */
    @JsonAlias({"form_items", "formItems"})
    private List<FormItem> formItemList;

    @JsonAlias({"flow_nodes", "flowNodes"})
    private List<FlowNode> flowNodes;

    @Data
    public static class FormItem {
        /**
         * 属性名称
         */
        @JsonAlias({"template_id", "templateId"})
        private String templateId;

        /**
         * 属性值
         */
        private String value;
    }

    @Data
    public static class FlowNode {
        /**
         * 审批节点名称
         */
        @JsonAlias({"node_name", "nodeName"})
        private String nodeName;

        /**
         * 审批用户
         */
        @JsonAlias({"node_user", "nodeUser"})
        // 默认给个ncs-default-user用户
        private String nodeUser = "ncs-default-user";

        /**
         * 处理时间
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonAlias({"deal_time", "dealTime"})
        private Date dealTime;

        /**
         * 审批类型
         */
        @JsonAlias({"deal_type", "dealType"})
        // 映射 deal_type_description
        private String dealType;

        /**
         * 审批说明
         */
        @JsonAlias({"deal_comment", "dealComment"})
        private String dealComment = "ncs-default-comment";
    }
}
