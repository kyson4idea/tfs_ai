package com.smy.tfs.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smy.tfs.api.dbo.TicketAccountMapping;
import com.smy.tfs.api.dto.PluginVisibleDto;
import com.smy.tfs.api.dto.TicketAccountMappingDto;
import com.smy.tfs.api.dto.TicketRemoteAccountDto;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.Response;

import java.util.List;

/**
 * <p>
 * 账户体系映射表 服务类
 * </p>
 *
 * @author zzd
 * @since 2024-05-07
 */
public interface ITicketAccountMappingService extends IService<TicketAccountMapping> {
    /**
     * 查询数据库指定账户类型的所有映射列表
     * @param accountType
     * @return
     */
    List<TicketAccountMapping> selectTicketAccountMappingList(String accountType);

    /**
     * 查询数据库指定有效的账户类型的所有映射列表
     * @param accountType
     * @return
     */
    List<TicketAccountMapping> selectEnableTicketAccountMappingList(String accountType);

    /**
     * 查询数据库指定账户及类型的所有映射列表
     * @param accountIdList
     * @param accountType
     * @return
     */
    List<TicketAccountMapping> selectAccountMappingByAccountIdAndType(List<String> accountIdList, String accountType);

    /**
     * 查询指定用户账号类型的映射信息
     * @param accountId
     * @param accountType
     * @return
     */
    TicketAccountMapping selectAccountMappingByAccountIdAndType(String accountId, String accountType);
    TicketAccountMapping selectAccountMappingByQywxIdAndType(String qywxId, String accountType);

    /**
     * 根据远程账号信息初始化映射数据
     * @param ticketRemoteAccountDto
     */
    void initTicketAccountMapping(TicketRemoteAccountDto ticketRemoteAccountDto);

    /**
     * 根据远程账号信息更新映射数据
     * @param ticketRemoteAccountDto
     */
    void updateTicketAccountMappingByRemoteAccount(TicketRemoteAccountDto ticketRemoteAccountDto);

    /**
     * 查询数据库指定账户及类型的所有映射列表 转换对象
     * @param accountIdList
     * @param accountType
     * @return
     */
    List<AccountInfo> getAccountInfoByAccountIdAndType(List<String> accountIdList, String accountType);

    /**
     * 当前登录人是否有权限看"高级查询"小浮标的权限
     * @param pluginVisibleDto
     * @return
     */
    Response<Boolean> pluginVisible(PluginVisibleDto pluginVisibleDto);


    /**
     * 查询账户体系映射表映射列表
     * @param ticketAccountMappingDto
     * @return
     */
    Response<List<TicketAccountMapping>> queryTicketAccountMappingList(TicketAccountMappingDto ticketAccountMappingDto);

    /**
     * 保存账户体系映射表映射记录
     * @param ticketAccountMappingDto
     * @return
     */
    Response<String> save(TicketAccountMappingDto ticketAccountMappingDto);

    /**
     * 根据删除账户体系映射表映射记录
     * @param id
     * @return
     */
    Response delete(String id);

    /**
     * 根据ddId查询指定账号体系账号
     * @param userType
     * @param ddId
     * @return
     */
    TicketAccountMapping selectAccountByTypeAndDdUserId(String userType, String ddId);

    /**
     * 根据qywxId查询指定账号体系账号
     * @param userType
     * @param qywxUserId
     * @return
     */
    TicketAccountMapping selectAccountByTypeAndQywxUserId(String userType, String qywxUserId);
}
