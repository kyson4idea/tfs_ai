package com.smy.tfs.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smy.tfs.api.dbo.TicketData;
import com.smy.tfs.api.dto.DownloadTicketDataReqDto;
import com.smy.tfs.api.dto.DownloadTicketDataRespDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.query.*;
import com.smy.tfs.common.core.page.TableDataInfo;

import java.util.List;


/**
 * <p>
 * 工单数据表 服务类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
public interface ITicketDataQueryService extends IService<TicketData> {

    //查询工单列表
    Response<List<DownloadTicketDataRespDto>> queryDownloadTicketDataList(DownloadTicketDataReqDto downloadTicketDataReqDto);

    /**
     * 普通查询工单列表(jssdk)
     * @param fuzzyQueryReqDto
     * @return
     */
    TableDataInfo<FuzzyQueryRspDto> fuzzyQuery(FuzzyQueryReqDto fuzzyQueryReqDto, String sameOriginId, String userType, String userId, String userName);

    /**
     * 普通查询工单列表条数(jssdk)
     * @param fuzzyQueryReqDto
     * @return
     */
    Response<Long> fuzzyQueryCount(FuzzyQueryReqDto fuzzyQueryReqDto, String sameOriginId, String userType, String userId, String userName);

    /**
     * 高级查询工单列表(jssdk)
     * @param accurateQueryReqDto
     * @return
     */
    TableDataInfo accurateQuery(AccurateQueryReqDto accurateQueryReqDto);

    /**
     * 通用查询工单列表(pc端-超管查询)
     * @param superAdminQueryReqDto
     * @return
     */
    TableDataInfo superAdminQuery(SuperAdminQueryReqDto superAdminQueryReqDto, String sameOriginId, String userType, String userId, String userName);

    /**
     * 通用查询工单列表(pc端-业务管理员查询)
     * @param busiAdminQueryReqDto
     * @return
     */
    TableDataInfo busiAdminQuery(BusiAdminQueryReqDto busiAdminQueryReqDto, String sameOriginId, String userType, String userId, String userName);

    /**
     * 通用查询工单列表(pc端/企微-我审批的/我处理的)
     * @param ownQueryReqDto
     * @return
     */
    TableDataInfo ownQuery(OwnQueryReqDto ownQueryReqDto, String sameOriginId, String userType, String userId, String userName);


    /**
     * 外部业务：工单列表查询 给方舟用
     * @param busiQueryReqDto
     * @param userType
     * @param userId
     * @param userName
     * @return
     */
    RemoteTableDataInfo<BusiQueryRspDto> busiQuery(BusiQueryReqDto busiQueryReqDto, String userType, String userId, String userName);


    /**
     * 外部业务：工单列表查询
     * @param busiCommonESQueryReqDto
     * @return
     */
    RemoteTableDataInfo<BusiCommonESQueryRspDto> busiCommonESQuery(BusiCommonESQueryReqDto busiCommonESQueryReqDto);

    /**
     * 用于sla配置，关于是否包含相关tags的查询
     * @param slaTagsQueryReqDto
     * @param userType
     * @param userId
     * @param userName
     * @return
     */
    RemoteTableDataInfo<SlaTagsQueryRspDto> slaTagsQuery(SlaTagsQueryReqDto slaTagsQueryReqDto, String userType, String userId, String userName);

    /**
     * 外部业务：工单池的工单总量
     * @param busiQueryReqDto
     * @param userType
     * @param userId
     * @param userName
     * @return
     */
    Response<Long> busiQueryCount(BusiQueryReqDto busiQueryReqDto, String userType, String userId, String userName);

    /**
     * 查询工单处理阶段数据统计：包括 待处理、处理中、已完结
     * @param processStageCountReqDto
     * @param userType
     * @param userId
     * @param userName
     * @return
     */
    Response<ProcessStageCountRespDto> processStageQueryCount(ProcessStageCountReqDto processStageCountReqDto, String userType, String userId, String userName);

    /**
     * 工单提交阶段：包括 新建、退回
     * @param userType
     * @param userId
     * @param userName
     * @return
     */
    Response<SubmitStageCountRespDto> submitStageQueryCount(SubmitStageCountReqDto submitStageCountReqDto, String userType, String userId, String userName);

    /**
     * 获得工单排行榜 top 10
     * @param topRankingReqDto
     * @param userType
     * @param userId
     * @param userName
     * @return
     */
    Response<TopRankingRespDto> getTopRanking(TopRankingReqDto topRankingReqDto, String userType, String userId, String userName);

    /**
     * 统计待分配的工单数
     * @param ticketDispatchCountReqDto
     * @return
     */
    public Response<TicketDispatchCountRspDto> getTicketDispatchCountList(TicketDispatchCountReqDto ticketDispatchCountReqDto, String userType, String userId, String userName);
}
