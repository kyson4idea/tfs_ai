package com.smy.tfs.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smy.tfs.api.dbo.TicketApp;
import com.smy.tfs.api.dto.*;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.CallBackMsgStatusEnum;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 应用表 服务类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
public interface ITicketAppService extends IService<TicketApp> {
    //查询应用全部信息
    public TicketAppDto selectTicketAppFullById(String id);
    //根据应用id查询应用信息
    public Response<TicketApp> queryTicketAppById(String id);

    //查询应用信息列表，不分页 带天维度的统计信息
    public Response<List<TicketAppDto>> selectTicketAppListWithDayAnalysis(TicketAppDto ticketAppDto);

    //仅查询应用信息列表，不分页
    public Response<List<TicketAppDto>> selectOnlyTicketAppList(boolean needControl);

    //分页查询应用信息
    public Page<TicketAppDto> selectTicketAppPage(PageParam<TicketAppDto> page);

    // 创建应用信息
    public Response<String> createTicketApp(TicketAppDto ticketAppDto);

    // 申请创建应用信息
    Response<String> applyTicketApp(TicketAppDto ticketAppDto);

    // 更新应用信息
    public Response<TicketAppDto> updateTicketAppFull(TicketAppDto ticketAppDto);

    // 更新应用信息
    public TicketAppDto updateTicketApp(TicketAppDto ticketAppDto);

    // 删除应用信息
    public void deleteTicketApp(String id);

    // 查询当前用户是否是应用管理员
    public Boolean checkCurrentUserIsAppAdmin(String appId);

    //查询当前登录用户的管理应用
    public List<String> queryAdminAppListForCurrentUser();

    //查询指定用户的管理应用
    public List<String> queryAdminAppListForPointUser(String userName);

    //查询应用id和名称的映射表
    Map<String, String > selectNameMapByIdList(List<String> appIdList);

    public Response<String> createQWGroupAndSendMsgByNode(String ticketFlowNodeDataId, CallBackMsgStatusEnum pushMsgType);

    public Response<ExtendFieldsMappingDto> getExtendFieldsMapping(String ticketAppId);

}
