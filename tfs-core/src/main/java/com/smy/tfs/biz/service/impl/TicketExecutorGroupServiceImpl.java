package com.smy.tfs.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smy.tfs.api.dbo.TicketAccountMapping;
import com.smy.tfs.api.dbo.TicketExecutorGroup;
import com.smy.tfs.api.dto.TicketExecutorGroupDto;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.enums.ExecutorTypeEnum;
import com.smy.tfs.api.enums.RecordStatusEnum;
import com.smy.tfs.api.service.ITicketAccountMappingService;
import com.smy.tfs.api.service.ITicketAppService;
import com.smy.tfs.api.service.ITicketExecutorGroupService;
import com.smy.tfs.biz.component.AccountReturnComponent;
import com.smy.tfs.biz.mapper.TicketExecutorGroupMapper;
import com.smy.tfs.common.exception.ServiceException;
import com.smy.tfs.common.utils.SecurityUtils;
import com.smy.tfs.common.utils.bean.BeanHelper;
import com.smy.tfs.common.utils.bean.BeanUtils;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.smy.tfs.common.utils.PageUtils.startPage;

/**
 * <p>
 * 应用人员组表 服务实现类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Slf4j
@Service
public class TicketExecutorGroupServiceImpl extends ServiceImpl<TicketExecutorGroupMapper, TicketExecutorGroup> implements ITicketExecutorGroupService {

    @Resource
    private ITicketAccountMappingService ticketAccountMappingService;
    @Resource
    private AccountReturnComponent accountReturnComponent;
    @Resource
    private ITicketAppService ticketAppService;

    @Override
    public Boolean existTicketExecutorGroupByAppAndName(String id, String executorGroupName) {
        if (StrUtil.isBlank(executorGroupName)) {
            throw new ServiceException("应用用户组信息唯一性校验失败，用户组名称不能为空");
        }
        LambdaQueryWrapper<TicketExecutorGroup> lambdaQueryWrapper = new LambdaQueryWrapper<TicketExecutorGroup>();
        lambdaQueryWrapper.eq(TicketExecutorGroup::getExecutorGroupName, executorGroupName);
        lambdaQueryWrapper.ne(StrUtil.isNotBlank(id), TicketExecutorGroup::getId, id);
        return this.baseMapper.selectCount(lambdaQueryWrapper) > 0;
    }

    @Override
    public List<TicketExecutorGroupDto> selectTicketExecutorGroupList(TicketExecutorGroupDto ticketExecutorGroupDto) {
        if (ticketExecutorGroupDto.isNeedControl() && !SecurityUtils.isAdmin()){
            List<String> adminAppList = ticketAppService.queryAdminAppListForCurrentUser();
            if (ObjectHelper.isEmpty(adminAppList)) {
                throw new ServiceException("此用户下没有业务");
            }
            ticketExecutorGroupDto.setAppIdList(adminAppList);
        }
        startPage();
        List<TicketExecutorGroupDto> ticketExecutorGroupDtoList = this.baseMapper.selectTicketExecutorGroupList(ticketExecutorGroupDto);
        for (TicketExecutorGroupDto executorGroupDto : ticketExecutorGroupDtoList) {
            parseAccountInfoStr(executorGroupDto);
            executorGroupDto.setStatus(executorGroupDto.getDeleteTime() != null
                    ? RecordStatusEnum.DELETED.getCode() : RecordStatusEnum.NORMAL.getCode());
            executorGroupDto.setAccountInfo(accountReturnComponent.toAccountInfoStrForFront(executorGroupDto.getAccountInfo()));
            executorGroupDto.setCreateBy(accountReturnComponent.toAccountInfoStrForFront(executorGroupDto.getCreateBy()));
        }
        return ticketExecutorGroupDtoList;
    }

    /**
     * 解析用户组用户信息json串到对象，方便前端展示
     *
     * @param executorGroupDto
     */
    private void parseAccountInfoStr(TicketExecutorGroupDto executorGroupDto) {
        String accountInfo = executorGroupDto.getAccountInfo();
        if (StrUtil.isBlank(accountInfo)) {
            return;
        }
        try {
            List<AccountInfo> accountInfoList = AccountInfo.ToAccountInfoList(accountInfo);
            if (CollUtil.isNotEmpty(accountInfoList)) {
                executorGroupDto.setAccountType(accountInfoList.get(0).getAccountType());
                executorGroupDto.setAccountIdList(accountInfoList.stream().map(AccountInfo::getAccountId).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            log.error("解析用户组用户信息失败，解析内容：{} 原因：", accountInfo, e);
        }
    }

    private void parseAccountInfoObject(TicketExecutorGroupDto executorGroupDto) {
        List<String> accountIdList = executorGroupDto.getAccountIdList();
        String accountType = executorGroupDto.getAccountType();
        List<TicketAccountMapping> ticketAccountMappingList = ticketAccountMappingService.selectEnableTicketAccountMappingList(accountType);
        Map<String, TicketAccountMapping> accountMappingMap =new HashMap<>();
        if(ticketAccountMappingList!=null&&ticketAccountMappingList.size()>0){
            for(TicketAccountMapping ticketAccountMapping:ticketAccountMappingList){
                accountMappingMap.putIfAbsent(ticketAccountMapping.getAccountId(),ticketAccountMapping);
            }
        }
        //Map<String, TicketAccountMapping> accountMappingMap = ticketAccountMappingList.stream().collect(Collectors.toMap(TicketAccountMapping::getAccountId, a->a));
        List<AccountInfo> accountInfoList = new ArrayList<>();
        for (String accountId : accountIdList) {
            if (StrUtil.isBlank(accountId)) {
                continue;
            }
            TicketAccountMapping ticketAccountMapping = accountMappingMap.get(accountId);
            if (ticketAccountMapping == null){
                continue;
            }
            AccountInfo accountInfo = new AccountInfo(ticketAccountMapping.getSameOriginId(), accountType, accountId, ticketAccountMapping.getAccountName());
            accountInfoList.add(accountInfo);
        }
        if (CollUtil.isEmpty(accountInfoList)) {
            throw new ServiceException("变更应用用户组信息失败，用户成员信息不能为空");
        }
        executorGroupDto.setAccountInfo(AccountInfo.ToAccountInfoListStr(accountInfoList));
    }

    @Override
    public Response<String> createTicketExecutorGroup(TicketExecutorGroupDto ticketExecutorGroupDto) {
        try {
            if (existTicketExecutorGroupByAppAndName(null,
                    ticketExecutorGroupDto.getExecutorGroupName())) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "当前应用的应用组名称已存在，请重新输入");
            }

            parseAccountInfoObject(ticketExecutorGroupDto);
            TicketExecutorGroup ticketExecutorGroup = new TicketExecutorGroup();
            BeanUtils.copyProperties(ticketExecutorGroupDto, ticketExecutorGroup);
            this.save(ticketExecutorGroup);

            return Response.success(ticketExecutorGroup.getId());
        } catch (ServiceException e) {
            log.error("创建应用用户组信息失败", e);
            return Response.error(BizResponseEnums.SAVE_ERROR, e.getMessage());
        } catch (Exception e) {
            log.error("创建应用用户组信息失败", e);
            return Response.error(BizResponseEnums.UNKNOW_EXCEPTION_CODE, e.getMessage());
        }
    }

    @Override
    public Response<Boolean> updateTicketExecutorGroupFull(TicketExecutorGroupDto ticketExecutorGroupDto) {
        try {
            if (existTicketExecutorGroupByAppAndName(ticketExecutorGroupDto.getId(), ticketExecutorGroupDto.getExecutorGroupName())) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "当前应用的应用组名称已存在，请重新输入");
            }
            parseAccountInfoObject(ticketExecutorGroupDto);
            TicketExecutorGroup ticketExecutorGroup = BeanHelper.copyObject(ticketExecutorGroupDto, TicketExecutorGroup.class);

            return Response.success(this.updateById(ticketExecutorGroup));
        } catch (ServiceException e) {
            log.error("更新应用用户组信息失败", e);
            return Response.error(BizResponseEnums.UPDATE_ERROR, e.getMessage());
        } catch (Exception e) {
            log.error("更新应用用户组信息失败", e);
            return Response.error(BizResponseEnums.UNKNOW_EXCEPTION_CODE, e.getMessage());
        }
    }

    @Override
    public TicketExecutorGroupDto updateTicketExecutorGroup(TicketExecutorGroupDto ticketExecutorGroupDto) {
        return null;
    }

    @Override
    public void deleteTicketExecutorGroup(String id) {
        TicketExecutorGroup ticketExecutorGroup = new TicketExecutorGroup();
        ticketExecutorGroup.setId(id);
        ticketExecutorGroup.setDeleteTime(new Date());
        this.baseMapper.updateById(ticketExecutorGroup);
    }

    public String getExecutorList(ExecutorTypeEnum executorTypeEnum, String executorValue) {
        String executorList = executorValue;
        switch (executorTypeEnum) {
            case APPLY_GROUP:
            case CA_GROUP:
            case CE_GROUP:
                List<String> executorValueList = JSONObject.parseObject(executorValue, List.class);
                List<TicketExecutorGroup> ticketExecutorGroupList = this.lambdaQuery().in(TicketExecutorGroup::getId, executorValueList).isNotNull(TicketExecutorGroup::getDeleteTime).list();
                List<AccountInfo> accountInfoList = new ArrayList<>();
                if (ObjectHelper.isNotEmpty(ticketExecutorGroupList)) {
                    ticketExecutorGroupList.stream().filter(it -> ObjectHelper.isNotEmpty(it.getAccountInfo())).forEach(it -> {
                        List<AccountInfo> aiList = AccountInfo.ToAccountInfoList(it.getAccountInfo());
                        if (ObjectHelper.isNotEmpty(aiList)) {
                            accountInfoList.addAll(aiList);
                        }
                    });
                }
                executorList = AccountInfo.ToAccountInfoListStr(accountInfoList);
            default:
        }
        return executorList;
    }
}
