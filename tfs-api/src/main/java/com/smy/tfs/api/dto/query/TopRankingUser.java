package com.smy.tfs.api.dto.query;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户排名统计信息
 *
 * @author ruoyi
 */
@Data
@NoArgsConstructor
public class TopRankingUser implements Serializable {
    private static final long serialVersionUID = -5164503799115493678L;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户创建的工单数量
     */
    private Long createdTickets;

    public TopRankingUser (String userId, Long createdTickets) {
        this.userId = userId;
        this.createdTickets = createdTickets;
    }


}
