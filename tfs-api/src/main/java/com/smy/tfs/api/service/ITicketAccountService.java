package com.smy.tfs.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smy.tfs.api.dbo.TicketAccount;
import com.smy.tfs.api.dto.TicketAccountDto;
import com.smy.tfs.api.dto.TicketRemoteAccountDto;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.DeptLevelEnum;

import java.util.List;

/**
 * <p>
 * 工单账户体系表 服务类
 * </p>
 *
 * @author zzd
 * @since 2024-04-19
 */
public interface ITicketAccountService extends IService<TicketAccount> {
    /**
     * 查询账号体系配置
     */
    public TicketAccountDto selectTicketAccountById(String id);

    /**
     * 查询账号体系配置列表
     * @return
     */
    public Response<List<TicketAccountDto>> selectTicketAccountList(TicketAccountDto ticketAccountDto);

    //查询指定账号体系配置是否存在
    public Boolean existTicketAccountByUniqueKey(String id, String accountType);

    /*** 新增账号体系配置*/
    public Response<Boolean> insertTicketAccount(TicketAccountDto ticketAccountDto);

    /**
     * 修改账号体系配置
     * @return
     */
    public Response<Boolean> updateTicketAccount(TicketAccountDto ticketAccountDto);

    /**
     * 删除工单账号体系
     * @return
     */
    public Response<String> deleteTicketAccountById(String id);

    /**
     * 执行同步账户体系配置
     *
     * @param id
     * @return
     */
    public Response<String> doSyncTicketAccountConfig(String id);


    /**
     * 执行同步账户体系配置:非验权
     *
     * @param id
     * @return
     */
    public Response<String> doSyncTicketAccountConfigNoAuth(String id);


    public Response<String> syncTicketAccountGroup(String appID, String groupName, String groupDesc, List<String> accountIdList);

    /**
     * 查询指定账号体系配置下的所有远程账号列表
     *
     * @param ticketAccountType
     * @return
     */
    public List<TicketRemoteAccountDto> getTicketRemoteAccountListByType(String ticketAccountType);

    /**
     * 查询指定应用配置下的所有远程账号列表
     *
     * @param appId
     * @return
     */
    public List<TicketRemoteAccountDto> getTicketRemoteAccountListByAppId(String appId);

    /**
     * 查询指定账号体系配置下的指定用户信息
     * @param userId
     * @param ticketAccountType
     * @return
     */
    public TicketRemoteAccountDto getTicketRemoteAccountByIdAndType(String userId, String ticketAccountType);

    /**
     * 查询指定应用配置下的指定用户信息
     * @param userId
     * @param appId
     * @return
     */
    public TicketRemoteAccountDto getTicketRemoteAccountByIdAndApp(String userId, String appId);

    /**
     * 查询指定应用配置下的指定用户信息
     * @param qywxId
     * @param appId
     * @return
     */
    public TicketRemoteAccountDto getTicketRemoteAccountByQywxIdAndApp(String qywxId, String appId);


    public TicketRemoteAccountDto getLeaderByTypeAndId(String userType,String userId);

    public List<TicketRemoteAccountDto> getDeptManagersByTypeAndId(String userType,String userId);

    /**
     * 查询配置启用的账户体系，执行同步操作
     */
    void syncTicketRemoteAccount();

    public void notifyQwMsg(String message, List<String> accountList);


    List<AccountInfo> getDeptLevelAccountInfoList(DeptLevelEnum deptLevel, String accountId, String accountType);

    /**
     * 同步指定账号体系的企业微信，按不同主体同步
     * @param accountType
     * @param qwCorporate
     */
    void syncAccountMappingQywxId(String accountType, String qwCorporate);

}
