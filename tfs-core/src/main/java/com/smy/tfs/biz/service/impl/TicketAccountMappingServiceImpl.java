package com.smy.tfs.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smy.framework.core.util.SequenceUtil;
import com.smy.tfs.api.dbo.TicketAccountMapping;
import com.smy.tfs.api.dbo.TicketApp;
import com.smy.tfs.api.dto.PluginVisibleDto;
import com.smy.tfs.api.dto.TicketAccountMappingDto;
import com.smy.tfs.api.dto.TicketRemoteAccountDto;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.enums.TFSTableIdCode;
import com.smy.tfs.api.enums.YESNOEnum;
import com.smy.tfs.api.service.ITicketAccountMappingService;
import com.smy.tfs.api.service.ITicketAppService;
import com.smy.tfs.biz.mapper.TicketAccountMappingMapper;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 账户体系映射表 服务实现类
 * </p>
 *
 * @author zzd
 * @since 2024-05-07
 */
@Service
public class TicketAccountMappingServiceImpl extends ServiceImpl<TicketAccountMappingMapper, TicketAccountMapping> implements ITicketAccountMappingService {
    @Resource
    private ITicketAppService ticketAppService;

    @Override
    public List<TicketAccountMapping> selectTicketAccountMappingList(String accountType) {
        LambdaQueryWrapper<TicketAccountMapping> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TicketAccountMapping::getAccountType, accountType);
        queryWrapper.isNull(TicketAccountMapping::getDeleteTime);

        return this.list(queryWrapper);
    }

    @Override
    public List<TicketAccountMapping> selectEnableTicketAccountMappingList(String accountType) {
        LambdaQueryWrapper<TicketAccountMapping> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TicketAccountMapping::getAccountType, accountType);
        queryWrapper.isNull(TicketAccountMapping::getDeleteTime);

        return this.list(queryWrapper);
    }

    @Override
    public List<TicketAccountMapping> selectAccountMappingByAccountIdAndType(List<String> accountIdList, String accountType) {
        LambdaQueryWrapper<TicketAccountMapping> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TicketAccountMapping::getAccountType, accountType);
        queryWrapper.in(TicketAccountMapping::getAccountId, accountIdList);
        queryWrapper.isNull(TicketAccountMapping::getDeleteTime);
        return this.list(queryWrapper);
    }

    @Override
    public TicketAccountMapping selectAccountMappingByAccountIdAndType(String accountId, String accountType) {
        if (ObjectHelper.anyIsEmpty(accountId, accountType)) {
            throw new RuntimeException(String.format("用户ID或用户类型为空，accountId:%s  accountType:%s", accountId, accountType));
        }
        List<TicketAccountMapping> ticketAccountMappingList = this.lambdaQuery()
                .isNull(TicketAccountMapping::getDeleteTime)
                .eq(TicketAccountMapping::getAccountId, accountId)
                .eq(TicketAccountMapping::getAccountType, accountType).list();
        if (CollectionUtils.isEmpty(ticketAccountMappingList)) {
            return null;
        }
        return ticketAccountMappingList.get(0);
    }

    @Override
    public TicketAccountMapping selectAccountMappingByQywxIdAndType(String qywxId, String accountType) {
        List<TicketAccountMapping> list = lambdaQuery().eq(TicketAccountMapping::getQwUserId, qywxId)
                .eq(TicketAccountMapping::getAccountType, accountType)
                .isNull(TicketAccountMapping::getDeleteTime)
                .last("limit 1").list();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public void initTicketAccountMapping(TicketRemoteAccountDto ticketRemoteAccountDto) {
        TicketAccountMapping ticketAccountMapping = new TicketAccountMapping();
        ticketAccountMapping.setAccountId(ticketRemoteAccountDto.getUserId());
        ticketAccountMapping.setAccountName(ticketRemoteAccountDto.getUserName());
        ticketAccountMapping.setAccountType(ticketRemoteAccountDto.getUserType());

        ticketAccountMapping.setPhoneNo(ticketRemoteAccountDto.getUserPhone());
        ticketAccountMapping.setEmail(ticketRemoteAccountDto.getUserEmail());

        ticketAccountMapping.setQwUserId(ticketRemoteAccountDto.getQywxId());
        ticketAccountMapping.setDdUserId(ticketRemoteAccountDto.getDingDingId());

        ticketAccountMapping.setSystemAccount((ticketRemoteAccountDto.getSystemUser() != null && ticketRemoteAccountDto.getSystemUser()) ? Boolean.TRUE : Boolean.FALSE);
        this.save(ticketAccountMapping);
    }

    @Override
    public void updateTicketAccountMappingByRemoteAccount(TicketRemoteAccountDto ticketRemoteAccountDto) {
        String userId = ticketRemoteAccountDto.getUserId();
        String userType = ticketRemoteAccountDto.getUserType();
        TicketAccountMapping ticketAccountMappingDb = selectAccountMappingByAccountIdAndType(userId, userType);
        if (ticketAccountMappingDb == null) {
            initTicketAccountMapping(ticketRemoteAccountDto);
        } else if (StrUtil.hasBlank(ticketAccountMappingDb.getDdUserId(), ticketAccountMappingDb.getPhoneNo(), ticketAccountMappingDb.getEmail())) {
            lambdaUpdate().set(StrUtil.isBlank(ticketAccountMappingDb.getDdUserId()), TicketAccountMapping::getDdUserId, ticketRemoteAccountDto.getDingDingId())
                    .set(StrUtil.isBlank(ticketAccountMappingDb.getPhoneNo()), TicketAccountMapping::getPhoneNo, ticketRemoteAccountDto.getUserPhone())
                    .set(StrUtil.isBlank(ticketAccountMappingDb.getEmail()), TicketAccountMapping::getEmail, ticketRemoteAccountDto.getUserEmail())
                    .eq(TicketAccountMapping::getAccountId, userId)
                    .eq(TicketAccountMapping::getAccountType, userType)
                    .isNull(TicketAccountMapping::getDeleteTime)
                    .update();
        }
    }

    @Override
    public List<AccountInfo> getAccountInfoByAccountIdAndType(List<String> accountIdList, String accountType) {
        List<TicketAccountMapping> ticketAccountMappingList = selectAccountMappingByAccountIdAndType(accountIdList, accountType);

        List<AccountInfo> result = new ArrayList<>();
        for (TicketAccountMapping ticketAccountMapping : ticketAccountMappingList) {
            AccountInfo accountInfo = new AccountInfo();
            BeanUtil.copyProperties(ticketAccountMapping, accountInfo);
            result.add(accountInfo);
        }
        return result;
    }

    @Override
    public Response<Boolean> pluginVisible(PluginVisibleDto pluginVisibleDto) {
        String appId = pluginVisibleDto.getAppId();
        Optional<TicketApp> ticketAppOpt = ticketAppService.lambdaQuery()
                .eq(TicketApp::getId, appId)
                .select(TicketApp::getAccountType)
                .oneOpt();
        if (!ticketAppOpt.isPresent())
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("此appId(%s)无对应的账户", appId));
        TicketApp ticketApp = ticketAppOpt.get();
        String accountType = ticketApp.getAccountType();
        if (ObjectHelper.isEmpty(accountType))
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("此appId(%s)无对应的账户类型", appId));
        String accountId = pluginVisibleDto.getUserId();
        Integer count = this.lambdaQuery()
                .eq(TicketAccountMapping::getAccountId, accountId)
                .eq(TicketAccountMapping::getAccountType, accountType)
                .count();
        if (count < 1) return Response.success(Boolean.FALSE);
        return Response.success(Boolean.TRUE);
    }

    @Override
    public Response<List<TicketAccountMapping>> queryTicketAccountMappingList(TicketAccountMappingDto ticketAccountMappingDto) {
        LambdaQueryWrapper<TicketAccountMapping> queryWrapper = new LambdaQueryWrapper();
        if (ObjectHelper.isNotEmpty(ticketAccountMappingDto.getId())) {
            queryWrapper.like(TicketAccountMapping::getId, ticketAccountMappingDto.getId());
        }
        if (ObjectHelper.isNotEmpty(ticketAccountMappingDto.getAccountId())) {
            queryWrapper.like(TicketAccountMapping::getAccountId, ticketAccountMappingDto.getAccountId());
        }
        if (ObjectHelper.isNotEmpty(ticketAccountMappingDto.getAccountType())) {
            queryWrapper.eq(TicketAccountMapping::getAccountType, ticketAccountMappingDto.getAccountType());
        }
        if (ObjectHelper.isNotEmpty(ticketAccountMappingDto.getAccountName())) {
            queryWrapper.like(TicketAccountMapping::getAccountName, ticketAccountMappingDto.getAccountName());
        }
        if (StrUtil.isNotBlank(ticketAccountMappingDto.getSameOriginId())) {
            queryWrapper.like(TicketAccountMapping::getSameOriginId, ticketAccountMappingDto.getSameOriginId());
        }
        if (StrUtil.isNotBlank(ticketAccountMappingDto.getHasOriginId()) && StrUtil.equals(ticketAccountMappingDto.getHasOriginId(), "Y")) {
            queryWrapper.isNotNull(TicketAccountMapping::getSameOriginId);
        }
        if (StrUtil.isNotBlank(ticketAccountMappingDto.getHasOriginId()) && StrUtil.equals(ticketAccountMappingDto.getHasOriginId(), "N")) {
            queryWrapper.isNull(TicketAccountMapping::getSameOriginId);
        }
//        if (ObjectHelper.isNotEmpty(ticketAccountMappingDto.getMatchResult())) {
//            queryWrapper.like(TicketAccountMapping::getMatchResult, ticketAccountMappingDto.getMatchResult());
//        }
//        if (ObjectHelper.isNotEmpty(ticketAccountMappingDto.getMatchCount())) {
//            queryWrapper.like(TicketAccountMapping::getMatchCount, ticketAccountMappingDto.getMatchCount());
//        }
//        if (ObjectHelper.isNotEmpty(ticketAccountMappingDto.getSystemAccount())) {
//            queryWrapper.eq(TicketAccountMapping::getSystemAccount, ticketAccountMappingDto.getSystemAccount());
//        }
        if (ObjectHelper.isNotEmpty(ticketAccountMappingDto.getPhoneNo())) {
            queryWrapper.like(TicketAccountMapping::getPhoneNo, ticketAccountMappingDto.getPhoneNo());
        }
        if (ObjectHelper.isNotEmpty(ticketAccountMappingDto.getEmail())) {
            queryWrapper.like(TicketAccountMapping::getEmail, ticketAccountMappingDto.getEmail());
        }
        if (ObjectHelper.isNotEmpty(ticketAccountMappingDto.getDdUserId())) {
            queryWrapper.like(TicketAccountMapping::getDdUserId, ticketAccountMappingDto.getDdUserId());
        }
        if (ObjectHelper.isNotEmpty(ticketAccountMappingDto.getQwUserId())) {
            queryWrapper.like(TicketAccountMapping::getQwUserId, ticketAccountMappingDto.getQwUserId());
        }
        if (ObjectHelper.isNotEmpty(ticketAccountMappingDto.getQyUserName())) {
            queryWrapper.like(TicketAccountMapping::getQyUserName, ticketAccountMappingDto.getQyUserName());
        }
        queryWrapper.orderByDesc(TicketAccountMapping::getUpdateTime);
        return Response.success(this.list(queryWrapper));
    }

    @Override
    public Response<String> save(TicketAccountMappingDto ticketAccountMappingDto) {
        if (ObjectHelper.isNotEmpty(ticketAccountMappingDto.getId())) {
            Integer count = this.lambdaQuery()
                    .eq(TicketAccountMapping::getAccountType, ticketAccountMappingDto.getAccountType())
                    .eq(TicketAccountMapping::getAccountId, ticketAccountMappingDto.getAccountId())
                    .ne(TicketAccountMapping::getId, ticketAccountMappingDto.getId())
                    .isNull(TicketAccountMapping::getDeleteTime)
                    .count();
            if (count > 0) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "同一账户类型下账户id重复");
            }
        } else {
            Integer count = this.lambdaQuery()
                    .eq(TicketAccountMapping::getAccountType, ticketAccountMappingDto.getAccountType())
                    .eq(TicketAccountMapping::getAccountId, ticketAccountMappingDto.getAccountId())
                    .isNull(TicketAccountMapping::getDeleteTime)
                    .count();
            if (count > 0) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "同一账户类型下账户id重复");
            }
        }
        TicketAccountMapping ticketAccountMapping = new TicketAccountMappingDto().toTicketAccountMapping(ticketAccountMappingDto);
        if (ObjectHelper.isEmpty(ticketAccountMapping.getId())) {
            ticketAccountMapping.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_ACCOUNT_MAPPING_LACK));
        }

        if (!this.saveOrUpdate(ticketAccountMapping)) {
            log.error("保存异常:" + JSONUtil.toJsonStr(ticketAccountMapping));
            return Response.error(BizResponseEnums.SAVE_ERROR, "保存异常");
        }
        return Response.success(ticketAccountMapping.getId());
    }

    @Override
    public Response delete(String id) {
        if (ObjectHelper.isEmpty(id)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "id不能为空");
        }
        if (!this.lambdaUpdate().eq(TicketAccountMapping::getId, id)
                .isNull(TicketAccountMapping::getDeleteTime)
                .set(TicketAccountMapping::getDeleteTime, new Date()).update()) {
            log.error("删除异常:" + id);
            return Response.error(BizResponseEnums.DEL_ERROR, "删除数据异常");
        }
        return Response.success();
    }

    @Override
    public TicketAccountMapping selectAccountByTypeAndDdUserId(String userType, String ddId) {
        return this.lambdaQuery().eq(TicketAccountMapping::getAccountType, userType)
                .eq(TicketAccountMapping::getDdUserId, ddId)
                .last("limit 1").one();
    }

    @Override
    public TicketAccountMapping selectAccountByTypeAndQywxUserId(String userType, String qywxUserId) {
        return this.lambdaQuery().eq(TicketAccountMapping::getAccountType, userType)
                .eq(TicketAccountMapping::getQwUserId, qywxUserId)
                .last("limit 1").one();
    }
}
