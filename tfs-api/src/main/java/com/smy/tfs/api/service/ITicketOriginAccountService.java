package com.smy.tfs.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smy.tfs.api.dbo.TicketAccountMapping;
import com.smy.tfs.api.dbo.TicketOriginAccount;
import com.smy.tfs.api.dto.TicketOriginAccountDto;
import com.smy.tfs.api.dto.TicketTemplateDto;
import com.smy.tfs.api.dto.base.Response;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zzd
 * @since 2024-07-01
 */
public interface ITicketOriginAccountService extends IService<TicketOriginAccount> {

    /**
     * 插入一个源用户信息
     *
     * @param ticketAccountMapping
     * @return
     */
    TicketOriginAccount insertOriginAccount (TicketAccountMapping ticketAccountMapping);

    /**
     * 查询或者插入然后返回一个源用户信息
     *
     * @param ticketAccountMapping
     * @return
     */
    TicketOriginAccount selectOrInsertOriginAccount (TicketAccountMapping ticketAccountMapping);

    /**
     * 根据账户映射表查询对应的源用户信息，没有则返回null
     */
    TicketOriginAccount selectOriginAccountByMappingInfo (TicketAccountMapping ticketAccountMapping);

    /**
     * 将源用户信息转换为账户映射表
     *
     * @param ticketOriginAccount
     * @param ticketAccountMapping
     */
    void parseOriginAccountToAccountMapping (TicketOriginAccount ticketOriginAccount, TicketAccountMapping ticketAccountMapping);


    /**
     * 将源用户信息转换为账户映射表
     *
     * @param ticketOriginAccount
     * @param ticketAccountMapping
     */
    void parseOriginAccountToAccountMappingNew (TicketOriginAccount ticketOriginAccount, TicketAccountMapping ticketAccountMapping);


    /**
     * 批量匹配同步源用户信息
     */
    void batchMatchOriginAccountInfo ();

    void batchSyncFullMatchOriginAccountInfo ();

    /**
     * 同步源用户信息到账户映射表
     *
     * @param id
     */
    Response<String> syncOriginAccountToMapping (String id);

    /**
     * 查询源账号信息列表
     *
     * @param ticketOriginAccountDto
     * @return
     */
    List<TicketOriginAccount> selectOriginAccountList (TicketOriginAccountDto ticketOriginAccountDto);

    /**
     * 删除源账号信息
     *
     * @param id
     */
    void deleteOriginAccount (String id);

    /**
     * 更新源账号信息
     *
     * @param ticketOriginAccount
     * @return
     */
    Response<String> updateTicketOriginAccount (TicketOriginAccount ticketOriginAccount);

    void syncSuccessAccountToMapping (List<TicketOriginAccount> successAccountList);

    void syncSuccessAccountToMappingNew (List<TicketOriginAccount> successAccountList);

}
