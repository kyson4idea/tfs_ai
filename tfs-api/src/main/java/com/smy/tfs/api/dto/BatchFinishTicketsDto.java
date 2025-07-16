package com.smy.tfs.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author z01140
 * @Package: com.smy.tfs.api.dto
 * @Description:
 * @CreateDate 2024/4/28 16:55
 * @UpdateDate 2024/4/28 16:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchFinishTicketsDto implements Serializable {

    private static final long serialVersionUID = -2648219083323781637L;

    private String startTime;

    private String endTime;

    private String ticketDataId;

    /**
     * 处理意见(必填)
     */
    private String dealOpinion;
}
