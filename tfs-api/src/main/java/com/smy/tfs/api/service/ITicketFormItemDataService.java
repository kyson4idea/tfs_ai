package com.smy.tfs.api.service;

import com.smy.tfs.api.dbo.TicketFormItemData;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smy.tfs.api.dto.base.Response;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 工单表单组件数据表 服务类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
public interface ITicketFormItemDataService extends IService<TicketFormItemData> {

    /**
     * 根据工单id查询工单表单组件数据
     * @param ticketDataId
     * @return
     */
    List<TicketFormItemData> selectTicketFormByDataId(String ticketDataId);
    List<TicketFormItemData> selectTicketFormItemByParentId(String parentId);

    /**
     * 根据表单项id查询明文字段
     * @param id
     * @return
     */
    Response<String> getPlaintextById(String id);

    /**
     * 根据表单项id的List查询明文字段Map
     * @param idList
     * @return
     */
    Response<Map<String, String>> getPlaintextMapByIds(List<String> idList);



}
