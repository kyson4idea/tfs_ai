package com.smy.tfs.api.dto.query;

import com.smy.framework.base.BaseElasticsearchEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

@Data
@NoArgsConstructor
public class FuzzyQueryRspDto extends BaseElasticsearchEntity implements Serializable {
    private static final long serialVersionUID = -125862083538406823L;
    //工单id
    private String id;
    //工单名称
    private String ticketName;
    //工单状态
    private String ticketStatus;
    //申请人
    private String applyUser;
    //创建时间,格式"yyyy-MM-dd HH:mm:ss"
    private String createTime;
    //更新时间,格式"yyyy-MM-dd HH:mm:ss"
    private String updateTime;
    //工单标签
    private String tags;

    public FuzzyQueryRspDto(Map<String, Object> hashMap) {
        if (Objects.nonNull(hashMap.get("id"))) {
            this.id = (String)hashMap.get("id");
        }
        if (Objects.nonNull(hashMap.get("ticket_name"))) {
            this.ticketName = (String)hashMap.get("ticket_name");
        }
        if (Objects.nonNull(hashMap.get("ticket_status"))) {
            this.ticketStatus = (String)hashMap.get("ticket_status");
        }
        if (Objects.nonNull(hashMap.get("apply_user"))) {
            this.applyUser = (String)hashMap.get("apply_user");
        }
        if (Objects.nonNull(hashMap.get("create_time"))) {
            this.createTime = (String) hashMap.get("create_time");
        }
        if (Objects.nonNull(hashMap.get("update_time"))) {
            this.updateTime = (String) hashMap.get("update_time");
        }
        if (Objects.nonNull(hashMap.get("tags"))) {
            this.tags = (String) hashMap.get("tags");
        }
    }
}
