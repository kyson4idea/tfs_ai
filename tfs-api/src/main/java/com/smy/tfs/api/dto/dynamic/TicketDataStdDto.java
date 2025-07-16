package com.smy.tfs.api.dto.dynamic;


import lombok.Getter;
import lombok.Setter;
import org.apache.dubbo.apidocs.annotations.RequestParam;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class TicketDataStdDto implements Serializable {

    private static final long serialVersionUID = 2167907493512036003L;
    @RequestParam(value = "申请ID", example = " ", description = "申请ID")
    private String applyId;

    @RequestParam(value = "工单模版ID", example = " ", description = "工单模版ID")
    private String ticketTemplateId;

    @RequestParam(value = "业务号", example = " ", description = "保障只有一个审批中的工单")
    private String ticketBusinessKey;

    @RequestParam(value = "创建时间（选填）", example = " ", description = "")
    private Date createTime;

    @RequestParam(value = "表单内容", example = " ", description = "表单内容")
    private List<TicketFormItemStdDto> formItems;

    @RequestParam(value = "流程内容(非必填)", example = " ", description = "分配指定审批人")
    private List<TicketFlowNodeStdDto> flowNodes;

    @RequestParam(value = "工单标签", example = " ", description = "")
    private String tags;

}
