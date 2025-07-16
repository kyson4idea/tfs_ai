package com.smy.tfs.api.service;

import com.smy.tfs.api.dbo.TicketExecutorGroup;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smy.tfs.api.dto.*;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.ExecutorTypeEnum;

import java.util.List;

/**
 * <p>
 * 应用人员组表 服务类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
public interface ITicketExecutorGroupService extends IService<TicketExecutorGroup> {
    //查询指定应用用户组信息是否存在
    public Boolean existTicketExecutorGroupByAppAndName(String id, String executorGroupName);

    //查询应用用户组信息列表，不分页
    public List<TicketExecutorGroupDto> selectTicketExecutorGroupList(TicketExecutorGroupDto ticketExecutorGroupDto);

    // 创建应用用户组信息
    public Response<String> createTicketExecutorGroup(TicketExecutorGroupDto ticketExecutorGroupDto);

    // 更新应用用户组信息
    public Response<Boolean> updateTicketExecutorGroupFull(TicketExecutorGroupDto ticketExecutorGroupDto);

    // 更新应用用户组信息
    public TicketExecutorGroupDto updateTicketExecutorGroup(TicketExecutorGroupDto ticketExecutorGroupDto);

    // 删除应用用户组信息
    public void deleteTicketExecutorGroup(String id);

    /**
     * 根据executorTypeEnum和executorValue查询对应的executorList
     * @param executorTypeEnum
     * @param executorValue
     * @return
     */
    public String getExecutorList(ExecutorTypeEnum executorTypeEnum, String executorValue);
}
