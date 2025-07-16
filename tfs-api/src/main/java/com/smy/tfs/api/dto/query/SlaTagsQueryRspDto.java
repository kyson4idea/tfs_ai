package com.smy.tfs.api.dto.query;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

@Data
@NoArgsConstructor
public class SlaTagsQueryRspDto implements Serializable {
    private static final long serialVersionUID = -9155961408582020826L;

    /**
     * 工单编号
     */
    private String id;
    public SlaTagsQueryRspDto(Map<String,Object> map){
        if (Objects.nonNull(map.get("id"))) {
            this.id = (String)map.get("id");
        }
    }


}
