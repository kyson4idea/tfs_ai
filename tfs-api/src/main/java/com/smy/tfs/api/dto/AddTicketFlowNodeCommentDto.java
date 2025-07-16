package com.smy.tfs.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author z01140
 * @Package: com.smy.tfs.api.dto
 * @Description:
 * 节点评论DTO
 * @CreateDate 2024/5/9 15:44
 * @UpdateDate 2024/5/9 15:44
 */
@Data
public class AddTicketFlowNodeCommentDto implements Serializable {

    private static final long serialVersionUID = 3467022156098162452L;
    /**
     * 工单ID
     */
    private String ticketDataId;

    /**
     * 业务动作类型描述
     */
    private String dealDescription;

    /**
     * 业务动作类型描述
     */
    private String dealTypeDescription;

    /**
     * 评论节点
     */
    private String nodeId;

    /**
     * 评论内容
     */
    private String dealContent;

    /**
     * 评论附件信息
     */
    private String commentFileInfo;


    private List<String> commentTagInfo;
}
