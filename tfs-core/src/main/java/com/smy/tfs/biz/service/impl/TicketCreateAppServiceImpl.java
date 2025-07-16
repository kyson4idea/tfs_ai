package com.smy.tfs.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.smy.tfs.api.constants.TfsBaseConstant;
import com.smy.tfs.api.dbo.TicketData;
import com.smy.tfs.api.dbo.TicketFormItemData;
import com.smy.tfs.api.dto.TicketAppDto;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.service.ITicketAppService;
import com.smy.tfs.api.service.ITicketCreateAppService;
import com.smy.tfs.api.service.ITicketDataService;
import com.smy.tfs.api.service.ITicketFormItemDataService;
import com.smy.tfs.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.apidocs.annotations.ApiModule;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component("ticketCreateAppService")
@org.apache.dubbo.config.annotation.Service
@ApiModule(value = "创建应用相关服务类", apiInterface = ITicketCreateAppService.class)
public class TicketCreateAppServiceImpl implements ITicketCreateAppService {

    @Resource
    private ITicketAppService ticketAppService;
    @Resource
    private ITicketFormItemDataService ticketFormItemDataService;
    @Resource
    private ITicketDataService ticketDataService;

    @Override
    public Response createTicketAppFromTicketForm(String sign, String ticketEventTag, String ticketDataId) {
        if (StrUtil.isBlank(ticketDataId)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单ID不能为空");
        }
        String signMe = DigestUtils.md5DigestAsHex(("tfs" + ticketDataId).getBytes());
        if (!sign.equals(signMe)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "签名验证失败");
        }
        List<TicketFormItemData> ticketFormItemDataList = ticketFormItemDataService.selectTicketFormByDataId(ticketDataId);
        if (CollUtil.isEmpty(ticketFormItemDataList)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单数据不能为空");
        }
        Map<String, String> formItemMap = ticketFormItemDataList.stream().collect(Collectors.toMap(TicketFormItemData::getItemLabel, TicketFormItemData::getItemValue));
        String appId = formItemMap.getOrDefault("业务唯一键", "");
        String appName = formItemMap.getOrDefault("业务名称", "");
        String appDesc = formItemMap.getOrDefault("业务描述", "");
        String appAdminUsers = formItemMap.getOrDefault("业务管理员", "");
        String accountType = formItemMap.getOrDefault("业务账户类型", "");
        if (StrUtil.hasBlank(appId, appName, appDesc, appAdminUsers, accountType)) {
            log.error("通过工单调用创建应用失败，表单参数不全");
        }

        TicketAppDto ticketAppDto = new TicketAppDto();
        ticketAppDto.setId(appId);
        ticketAppDto.setAppName(appName);
        ticketAppDto.setAppDesc(appDesc);
        appAdminUsers = appAdminUsers.replace("[", "");
        appAdminUsers = appAdminUsers.replace("]", "");
        ticketAppDto.setAppAdminUserList(Arrays.asList(appAdminUsers.split(",")));
        ticketAppDto.setAccountType(accountType);

        //获取申请人，封装到创建线程
        TicketData ticketData = ticketDataService.selectTicketDataById(ticketDataId);
        String applyUser = ticketData.getApplyUser();
        AccountInfo accountInfo = AccountInfo.ToAccountInfo(applyUser);

        SecurityUtils.wrapContext(accountInfo.getSameOriginId(), accountInfo.getAccountId(), accountInfo.getAccountName(), accountInfo.getAccountType(),
                TfsBaseConstant.defaultAppId, () -> {
                    ticketAppService.createTicketApp(ticketAppDto);
                });

        return Response.success();
    }

    public static void main(String[] args) {
        System.out.println(JSONUtil.toJsonStr(Response.success()));
    }
}
