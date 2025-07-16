package com.smy.tfs.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.smy.framework.core.config.Property;
import com.smy.tfs.api.dbo.TicketFormItemData;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.service.ITicketFormItemDataService;
import com.smy.tfs.biz.mq.producer.TfsMqProducer;
import com.smy.tfs.biz.service.TicketApproveCallBackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service("ticketApproveCallBackService")
public class TicketApproveCallBackServiceImpl implements TicketApproveCallBackService {

    @Resource
    private ITicketFormItemDataService ticketFormItemDataService;
    @Resource
    private TfsMqProducer tfsMqProducer;

    private final static String APPROVE_PASS = "pass";
    private final static String APPROVE_REFUSE = "refuse";

    @Override
    public Response sendApproveResultMqForSheet(String sign, String ticketEventTag, String ticketDataId) {
        if (StrUtil.isBlank(ticketDataId)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单ID不能为空");
        }
        if (StrUtil.isBlank(ticketEventTag)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单审批动作标识不能为空");
        }
        if (!(APPROVE_PASS.equals(ticketEventTag) || APPROVE_REFUSE.equals(ticketEventTag))) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单审批动作标识仅能为%s或%s", APPROVE_PASS, APPROVE_REFUSE));
        }
        /*String signMe = DigestUtils.md5DigestAsHex(("tfs" + ticketDataId).getBytes());
        if (!sign.equals(signMe)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "签名验证失败");
        }*/
        List<TicketFormItemData> ticketFormItemDataList = ticketFormItemDataService.selectTicketFormByDataId(ticketDataId);
        if (CollUtil.isEmpty(ticketFormItemDataList)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单数据不能为空");
        }

        Map<String, String> formItemMap = ticketFormItemDataList.stream().collect(Collectors.toMap(TicketFormItemData::getItemLabel, TicketFormItemData::getItemValue));
        String mqKey = ticketDataId;
        String systemCode = formItemMap.getOrDefault("系统码", "");
        String bussType = formItemMap.getOrDefault("业务类型", "");
        String contentJson = formItemMap.getOrDefault("申请内容", "");
        String tag = systemCode + "." + bussType;

        if (StrUtil.hasBlank(mqKey, systemCode, bussType, contentJson)) {
            log.error("oms工单{}审批回调失败，表单参数不全", ticketDataId);
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "oms工单审批回调失败，表单参数不全");
        }

        String approveTopic = "";
        if (APPROVE_PASS.equals(ticketEventTag)) {
            //通过
            approveTopic = Property.getProperty("modifyBussSheet", "modifyBussSheet");
        } else {
            //拒绝
            approveTopic = Property.getProperty("modifyBussSheetRefuse", "modifyBussSheetRefuse");
        }
        Boolean sendResult = tfsMqProducer.sendMsg(approveTopic, tag, mqKey, contentJson);
        if (sendResult) {
            log.info("oms工单{}审批回调成功", ticketDataId);
            return Response.success();
        }
        return Response.error(BizResponseEnums.SYSTEM_ERROR, "oms工单审批回调失败,mq发送失败");
    }
}
