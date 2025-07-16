package com.smy.tfs.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smy.framework.core.util.SequenceUtil;
import com.smy.tfs.api.dbo.TicketAccountMapping;
import com.smy.tfs.api.dbo.TicketConfig;
import com.smy.tfs.api.dbo.TicketData;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.enums.TFSTableIdCode;
import com.smy.tfs.api.service.ITicketAccountMappingService;
import com.smy.tfs.api.service.ITicketConfigService;
import com.smy.tfs.api.service.ITicketDataService;
import com.smy.tfs.biz.mapper.TicketConfigMapper;
import com.smy.tfs.common.utils.StringUtils;
import lombok.var;
import org.apache.dubbo.apidocs.annotations.ApiModule;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;

/**
 * <p>
 * 业务表 服务实现类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Component("ticketConfigServiceImpl")
@org.apache.dubbo.config.annotation.Service
@ApiModule(value = "工单配置服务", apiInterface = ITicketConfigService.class)
public class TicketConfigServiceImpl extends ServiceImpl<TicketConfigMapper, TicketConfig> implements ITicketConfigService {
    @Resource
    private ITicketDataService ticketDataService;
    @Resource
    private ITicketAccountMappingService ticketAccountMappingService;

    @Override
    public Response<TicketConfig> selectTicketConfig(String ticketDataID) {
        TicketData ticketData = ticketDataService.selectTicketDataById(ticketDataID);
        if (ticketData == null) {
            return new Response<>(null, BizResponseEnums.QUERY_ERROR, String.format("工单数据不存在，id:%s", ticketDataID));
        }
        Optional<TicketConfig> ticketConfigOpt = lambdaQuery()
                .isNull(TicketConfig::getDeleteTime)
                .eq(TicketConfig::getTicketTemplateId, ticketData.getTemplateId()).oneOpt();
        if (!ticketConfigOpt.isPresent()) {
            return new Response<>(null, BizResponseEnums.QUERY_ERROR, String.format("工单配置不存在，TemplateId:%s", ticketData.getTemplateId()));
        }
        return new Response<>(ticketConfigOpt.get(), BizResponseEnums.SUCCESS, "查询成功");
    }

    /**
     * @param ticketConfig
     * @return
     */
    @Override
    public Response<String> createTicketConfig(TicketConfig ticketConfig, String userType, String userId, String userName) {
        TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByAccountIdAndType(userId, userType);
        if (ticketAccountMapping == null || StrUtil.isBlank(ticketAccountMapping.getSameOriginId())) {
            return Response.error(BizResponseEnums.UNKNOW_EXCEPTION_CODE, "未找到有效用户信息");
        }

        String userStr = JSONUtil.toJsonStr(new AccountInfo(ticketAccountMapping.getSameOriginId(), userType, userId, userName));
        if (ticketConfig == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单配置不能为空");
        }
        if (StringUtils.isNotEmpty(ticketConfig.getId())) {
            ticketConfig.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_CONFIG));
        }
        Date now = new Date();
        ticketConfig.setCreateTime(now);
        ticketConfig.setCreateBy(userStr);
        ticketConfig.setUpdateTime(now);
        ticketConfig.setUpdateBy(userStr);
        if (save(ticketConfig)) {
            return new Response<>(ticketConfig.getId(), BizResponseEnums.SUCCESS, "创建成功");
        } else {
            return new Response<>(null, BizResponseEnums.SAVE_ERROR, "创建失败");
        }
    }

    /**
     * @param ticketConfig
     * @return
     */
    @Override
    public Response<String> updateTicketConfig(TicketConfig ticketConfig, String userType, String userId, String userName) {
        TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByAccountIdAndType(userId, userType);
        if (ticketAccountMapping == null || StrUtil.isBlank(ticketAccountMapping.getSameOriginId())) {
            return Response.error(BizResponseEnums.UNKNOW_EXCEPTION_CODE, "未找到有效用户信息");
        }

        String userStr = JSONUtil.toJsonStr(new AccountInfo(ticketAccountMapping.getSameOriginId(), userType, userId, userName));
        if (ticketConfig == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单配置不能为空");
        }
        if (StringUtils.isEmpty(ticketConfig.getId())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单配置id不能为空");
        }
        if (StringUtils.isEmpty(ticketConfig.getTicketConfigStr())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单配置值不能为空");
        }
        if (StringUtils.isEmpty(ticketConfig.getUpdateBy())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "更新人员值不能为空");
        }
        Date now = new Date();
        var lamb = this.lambdaUpdate()
                .eq(TicketConfig::getId, ticketConfig.getId())
                .isNull(TicketConfig::getDeleteTime)
                .set(TicketConfig::getTicketConfigStr, ticketConfig.getTicketConfigStr())
                .set(TicketConfig::getUpdateTime, now)
                .set(TicketConfig::getUpdateBy, userStr)
                .set(TicketConfig::getUpdateBy, ticketConfig.getUpdateBy())
                .update(ticketConfig);
        if (lamb) {
            return new Response<>(ticketConfig.getId(), BizResponseEnums.SUCCESS, "更新成功");
        } else {
            return new Response<>(null, BizResponseEnums.UPDATE_ERROR, "更新失败");
        }
    }

}
