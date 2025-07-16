package com.smy.tfs.api.dto.query;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class TopRankingReqDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String appId;

    private String userType;

    //普诉工单
    private List<TopRankingUser> topGeneralList;

    //资深工单
    private List<TopRankingUser> topSeniorList;

}
