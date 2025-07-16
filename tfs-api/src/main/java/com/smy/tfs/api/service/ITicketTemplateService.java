package com.smy.tfs.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smy.tfs.api.dbo.TicketTemplate;
import com.smy.tfs.api.dto.QueryEnableTicketTemplateDto;
import com.smy.tfs.api.dto.TicketTemplateDto;
import com.smy.tfs.api.dto.TicketTemplateFullDto;
import com.smy.tfs.api.dto.TicketTemplateGroupDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.TicketTemplateStatusEnum;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 工单模版表 服务类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
public interface ITicketTemplateService extends IService<TicketTemplate> {

    // 查询工单模版全部信息
    public Response<TicketTemplateDto> selectTicketTemplateFullById(String id, String applyUser);

    // 查询工单模版列表，不分页
    public List<TicketTemplateDto> selectTicketTemplateList(TicketTemplateDto ticketTemplateDto);

    // 根据关键字模糊查询工单模版列表，不分页
    List<TicketTemplateGroupDto> selectTicketTemplateListWithGroup(TicketTemplateDto ticketTemplateDto);

    //仅查询工单模版列表，不分页
    public Response<List<TicketTemplateDto>> selectOnlyTicketTemplateList(boolean needControl);

    // 创建工单模版
    public Response<String> createTicketTemplate(TicketTemplateDto ticketTemplateDto, String userType, String userId, String userName);

    // 更新工单模版信息
    public Response<String> updateTicketTemplate(TicketTemplateDto ticketTemplateDto, String userType, String userId, String userName);

    /**
     * 复制工单模板
     * @param ticketTemplateDto
     * @param userType
     * @param userId
     * @param userName
     * @return
     */
    Response<String> copyTicketTemplate (TicketTemplateDto ticketTemplateDto, String userType, String userId, String userName);

    /**
     * 保存工单模板
     * @param ticketTemplateDto
     * @return
     */
    public Response save(TicketTemplateDto ticketTemplateDto, String userType, String userId, String userName);

    /**
     * 获取工单模板id
     * @return
     */
    public Response initTicketTemplate(String userType, String userId, String userName);

    /**
     * 修改指定工单模版状态为指定状态
     * @param id
     * @param newStatus
     * @return
     */
    public Response<Boolean> updateTicketTemplateStatus(String id, TicketTemplateStatusEnum newStatus);

    public void saveOrUpdateTest(TicketTemplateFullDto ticketTemplateFullDto);

    // 查询工单模版全部信息
    public Response<List<TicketTemplate>> selectTicketTemplateListByAppId(String id);

    //查询指定模版id 名称映射
    Map<String, String> selectNameMapByIdList(List<String> templateIdList);

    // 查询工单模版列表，不分页
    Response<List<TicketTemplateDto>> queryTicketTemplates(QueryEnableTicketTemplateDto queryEnableTicketTemplateDto);

    // 查询工单模版信息
    public Response<TicketTemplateDto> selectTicketTemplateById(String id);
}
