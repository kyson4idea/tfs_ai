package com.smy.tfs.biz.controller;

import com.smy.tfs.api.dto.TicketFormItemDataDto;
import com.smy.tfs.api.dto.TicketRemoteAccountDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.query.*;
import com.smy.tfs.api.enums.AuditedType;
import com.smy.tfs.api.enums.BusiQueryUserDealTypeEnum;
import com.smy.tfs.api.service.*;
import com.smy.tfs.biz.kafka.consumer.TicketDataConsumer;
import com.smy.tfs.biz.service.TicketDataApproveService;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.core.domain.model.LoginUser;
import com.smy.tfs.common.utils.DateUtils;
import com.smy.tfs.common.utils.SecurityUtils;
import com.smy.tfs.openapi.service.ITicketFormItemServiceWrapper;
import com.smy.tfs.quartz.task.TicketSlaTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/ticketTest")
public class TicketTestController {

    @Resource
    private IArkMiaoDaService arkMiaoDaService;

    @Resource
    private ITicketTestService ticketTestService;

    @Resource
    private ITicketDataQueryService ticketDataQueryService;

    @Resource
    private TicketDataConsumer ticketDataConsumer;

    @Resource
    private ITicketOriginAccountService ticketOriginAccountService;

    @Resource
    private ITicketExportUserService ticketExportUserService;

    @PostMapping("/batchMatchOriginAccountInfo")
    public AjaxResult batchMatchOriginAccountInfo() {

        ticketOriginAccountService.batchSyncFullMatchOriginAccountInfo();
        return AjaxResult.success();
    }


    @PostMapping("/consoleLogTest")
    public AjaxResult consoleLogTest(String sign, String ticketEventTag, String ticketDataId) {

        ticketTestService.consoleLogTest(sign, ticketEventTag, ticketDataId);
        return AjaxResult.success();
    }

    @GetMapping("/arkSync")
    public AjaxResult arkSync(String status, String st, String et, String page, String page_size) {

        try {
            arkMiaoDaService.syncMiaoDaNewTicketCore(status, st, et, page, page_size);
            return AjaxResult.success();
        } catch (Exception e) {
            log.error("arkSync error:{}", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    @GetMapping("/arkUpdate")
    public AjaxResult arkUpdate(String sn, String status_no) {
        try {
            arkMiaoDaService.updateMiaoDaTicketCore(sn, status_no);
            return AjaxResult.success();
        } catch (Exception e) {
            log.error("arkSync error:{}", e);
            return AjaxResult.error(e.getMessage());
        }
    }


    @GetMapping("/arkUpdateJob")
    public AjaxResult arkUpdateJob(String appId, String templateId) {
        try {
            arkMiaoDaService.updateMiaoDaTicketJob(appId, templateId);
            return AjaxResult.success();
        } catch (Exception e) {
            log.error("arkSync error:{}", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    @GetMapping("/arkComplete")
    public AjaxResult arkComplete(String snNo, String reason, String solution, String hideAttach, ArrayList<String> images, ArrayList<String> videos) {

        try {
            arkMiaoDaService.completeMiaoDaTicketCore(snNo, reason, solution, hideAttach, images, videos);
            return AjaxResult.success();
        } catch (Exception e) {
            log.error("arkComplete error:{}", e);
            return AjaxResult.error(e.getMessage());
        }
    }


    @GetMapping("/arkReply")
    public AjaxResult arkReply(String snNo, String replyContent, String hideAttach, String hideContent, ArrayList<String> images, ArrayList<String> videos) {

        try {
            arkMiaoDaService.replyMiaoDaTicketCore(snNo, replyContent, hideAttach, hideContent, images, videos);
            return AjaxResult.success();
        } catch (Exception e) {
            log.error("arkReply error:{}", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    @GetMapping("/arkReplyCallBack")
    public AjaxResult arkReplyCallBack(String sign, String ticketEventTag, String ticketDataId) {

        try {
            arkMiaoDaService.replyMiaoDaTicketCallBack(sign, ticketEventTag, ticketDataId);
            return AjaxResult.success();
        } catch (Exception e) {
            log.error("arkReplyCallBack error:{}", e);
            return AjaxResult.error(e.getMessage());
        }
    }


    @GetMapping("/arkCompleteCallBack")
    public AjaxResult arkCompleteCallBack(String sign, String ticketEventTag, String ticketDataId) {

        try {
            arkMiaoDaService.completeMiaoDaTicketCallBack(sign, ticketEventTag, ticketDataId);
            return AjaxResult.success();
        } catch (Exception e) {
            log.error("arkCompleteCallBack error:{}", e);
            return AjaxResult.error(e.getMessage());
        }
    }


    @GetMapping("/arkAppealCallBack")
    public AjaxResult arkAppealCallBack(String sign, String ticketEventTag, String ticketDataId) {

        try {
            arkMiaoDaService.appealMiaoDaTicketCallBack(sign, ticketEventTag, ticketDataId);
            return AjaxResult.success();
        } catch (Exception e) {
            log.error("arkAppealCallBack error:{}", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/processStageQueryCount")
    public AjaxResult processStageQueryCount(@RequestBody ProcessStageCountReqDto processStageCountReqDto) {

        LoginUser loginUser = SecurityUtils.getLoginUser();
        String userType = loginUser.getUserType();
        String userId = loginUser.getUsername();
        String userName = loginUser.getNickName();
        return AjaxResult.success(ticketDataQueryService.processStageQueryCount(processStageCountReqDto, userType, userId, userName));
    }

    @PostMapping("/submitStageQueryCount")
    public AjaxResult submitStageQueryCount(@RequestBody SubmitStageCountReqDto submitStageCountReqDto) {

        LoginUser loginUser = SecurityUtils.getLoginUser();
        String userType = loginUser.getUserType();
        String userId = loginUser.getUsername();
        String userName = loginUser.getNickName();
        return AjaxResult.success(ticketDataQueryService.submitStageQueryCount(submitStageCountReqDto, userType, userId, userName));
    }

    @PostMapping("/getTopRanking")
    public AjaxResult getTopRanking(@RequestBody TopRankingReqDto topRankingReqDto) {

        LoginUser loginUser = SecurityUtils.getLoginUser();
        String userType = loginUser.getUserType();
        String userId = loginUser.getUsername();
        String userName = loginUser.getNickName();
        return AjaxResult.success(ticketDataQueryService.getTopRanking(topRankingReqDto, userType, userId, userName));
    }

    @PostMapping("/getTicketDispatchCountList")
    public AjaxResult getTicketDispatchCountList(@RequestBody TicketDispatchCountReqDto ticketDispatchCountReqDto) {

        LoginUser loginUser = SecurityUtils.getLoginUser();
        String userType = loginUser.getUserType();
        String userId = loginUser.getUsername();
        String userName = loginUser.getNickName();
        return AjaxResult.success(ticketDataQueryService.getTicketDispatchCountList(ticketDispatchCountReqDto, userType, userId, userName));
    }

    @PostMapping("/consumeMessage")
    public AjaxResult consumeMessage() {

        LoginUser loginUser = SecurityUtils.getLoginUser();
        String records = "{\"data\":[{\"id\":\"1002504020008502104\",\"template_id\":\"1182406050001280001\",\"app_id\":\"tfs\",\"ticket_status\":\"APPLY_END\",\"ticket_name\":\"yss测试19\",\"description\":\"yss测试19\",\"current_node_name\":\"\",\"current_deal_users\":\"[{}]\",\"current_done_users\":\"[{\\\"accountType\\\":\\\"ldap\\\",\\\"accountId\\\":\\\"y01781\\\",\\\"accountName\\\":\\\"殷沙沙\\\",\\\"sameOriginId\\\":\\\"10373\\\"},{\\\"accountType\\\":\\\"ldap\\\",\\\"accountId\\\":\\\"tfs_system\\\",\\\"accountName\\\":\\\"tfs_system\\\",\\\"sameOriginId\\\":\\\"10656\\\"}]\",\"current_cc_users\":\"[{\\\"accountType\\\":\\\"ldap\\\",\\\"accountId\\\":\\\"y01781\\\",\\\"accountName\\\":\\\"殷沙沙\\\",\\\"sameOriginId\\\":\\\"10373\\\"}]\",\"create_time\":\"2025-04-02 20:25:09\",\"create_by\":\"{\\\"accountType\\\":\\\"ldap\\\",\\\"accountId\\\":\\\"y01781\\\",\\\"accountName\\\":\\\"殷沙沙\\\",\\\"sameOriginId\\\":\\\"10373\\\"}\",\"update_time\":\"2025-04-03 16:04:48\",\"update_by\":\"{\\\"accountType\\\":\\\"ldap\\\",\\\"accountId\\\":\\\"tfs_system\\\",\\\"accountName\\\":\\\"tfs_system\\\",\\\"sameOriginId\\\":\\\"10656\\\"}\",\"delete_time\":null,\"current_node_id\":\"-1\",\"ticket_finish_time\":\"2025-04-03 16:04:34\",\"beyond_apps\":\"\",\"ticket_template_code\":null,\"interface_key\":null,\"apply_user\":\"{\\\"accountType\\\":\\\"ldap\\\",\\\"accountId\\\":\\\"y01781\\\",\\\"accountName\\\":\\\"殷沙沙\\\",\\\"sameOriginId\\\":\\\"10373\\\"}\",\"ticket_msg_build_type\":\"AUDITOR_CREATE\",\"ticket_msg_arrive_type\":\"WECOM\",\"ticket_form_change_flag\":\"NO\",\"wx_chat_group_id\":null,\"version\":\"27\",\"ticket_aging_flag\":null,\"tags\":null,\"base_flow\":\"[\\\"1082504020012115905-审批\\\",\\\"1082504020012115906-审批\\\",\\\"1082504020012115907-审批\\\"]\",\"apply_ticket_ways\":\"\",\"ticket_business_key\":null,\"extend10\":null,\"extend2\":null,\"extend1\":null,\"extend3\":null,\"extend4\":null,\"extend5\":null,\"extend6\":null,\"extend7\":null,\"extend8\":null,\"extend9\":null}],\"database\":\"tfs\",\"es\":1743670722000,\"id\":1205305,\"isDdl\":false,\"mysqlType\":{\"id\":\"varchar(255)\",\"template_id\":\"varchar(255)\",\"app_id\":\"varchar(255)\",\"ticket_status\":\"varchar(255)\",\"ticket_name\":\"varchar(255)\",\"description\":\"varchar(255)\",\"current_node_name\":\"varchar(255)\",\"current_deal_users\":\"text\",\"current_done_users\":\"text\",\"current_cc_users\":\"text\",\"create_time\":\"datetime\",\"create_by\":\"varchar(255)\",\"update_time\":\"datetime\",\"update_by\":\"varchar(255)\",\"delete_time\":\"datetime\",\"current_node_id\":\"varchar(255)\",\"ticket_finish_time\":\"datetime\",\"beyond_apps\":\"varchar(255)\",\"ticket_template_code\":\"varchar(255)\",\"interface_key\":\"varchar(255)\",\"apply_user\":\"varchar(255)\",\"ticket_msg_build_type\":\"varchar(100)\",\"ticket_msg_arrive_type\":\"varchar(100)\",\"ticket_form_change_flag\":\"varchar(100)\",\"wx_chat_group_id\":\"varchar(100)\",\"version\":\"int(11)\",\"ticket_aging_flag\":\"varchar(20)\",\"tags\":\"varchar(255)\",\"base_flow\":\"text\",\"apply_ticket_ways\":\"varchar(20)\",\"ticket_business_key\":\"varchar(100)\",\"extend10\":\"text\",\"extend2\":\"text\",\"extend1\":\"varchar(255)\",\"extend3\":\"text\",\"extend4\":\"text\",\"extend5\":\"text\",\"extend6\":\"text\",\"extend7\":\"text\",\"extend8\":\"text\",\"extend9\":\"text\"},\"old\":[{\"update_time\":\"2025-04-03 16:04:47\"}],\"pkNames\":[\"id\"],\"sql\":\"\",\"sqlType\":{\"id\":12,\"template_id\":12,\"app_id\":12,\"ticket_status\":12,\"ticket_name\":12,\"description\":12,\"current_node_name\":12,\"current_deal_users\":2005,\"current_done_users\":2005,\"current_cc_users\":2005,\"create_time\":93,\"create_by\":12,\"update_time\":93,\"update_by\":12,\"delete_time\":93,\"current_node_id\":12,\"ticket_finish_time\":93,\"beyond_apps\":12,\"ticket_template_code\":12,\"interface_key\":12,\"apply_user\":12,\"ticket_msg_build_type\":12,\"ticket_msg_arrive_type\":12,\"ticket_form_change_flag\":12,\"wx_chat_group_id\":12,\"version\":4,\"ticket_aging_flag\":12,\"tags\":12,\"base_flow\":2005,\"apply_ticket_ways\":12,\"ticket_business_key\":12,\"extend10\":-4,\"extend2\":-4,\"extend1\":12,\"extend3\":-4,\"extend4\":-4,\"extend5\":-4,\"extend6\":-4,\"extend7\":-4,\"extend8\":-4,\"extend9\":-4},\"table\":\"ticket_data\",\"ts\":1743670884540,\"type\":\"UPDATE\"}";
        Acknowledgment acknowledgment = new Acknowledgment() {
            @Override
            public void acknowledge() {

            }
        };
        ticketDataConsumer.consumeMessage(records, acknowledgment);
        return AjaxResult.success();
    }

    @Resource
    private TicketSlaTask ticketSlaTask;

    @PostMapping("/runTicketSlaTask")
    public AjaxResult runTicketSlaTask(String templateId) {

        ticketSlaTask.run(templateId);
        return AjaxResult.success();
    }

    @Resource
    private ITicketDataActService iTicketDataActService;

    @PostMapping("/addBusiTags")
    public AjaxResult addBusiTags(String ticketDataId) {

        iTicketDataActService.addBusiTags("", "", ticketDataId);
        return AjaxResult.success();
    }

    @PostMapping("/exportLdapUserList")
    public AjaxResult testExportLdapUserList() {

        List<TicketRemoteAccountDto> ticketRemoteAccountDtos = ticketExportUserService.exportLdapUserList();
        return AjaxResult.success();
    }

    @PostMapping("/busiQueryCount")
    public AjaxResult busiQueryCount(String customerNo) {

        BusiQueryReqDto busiQueryReqDto = new BusiQueryReqDto();
        busiQueryReqDto.setAppIdList(Arrays.asList("ARK"));
        busiQueryReqDto.setUserDealType(BusiQueryUserDealTypeEnum.MY_DEPT_APPLY_WAITING_DISPATCH);
        busiQueryReqDto.setExtend1(customerNo); //客户号
        busiQueryReqDto.setUpdateStartTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, DateUtils.getYesterdayDate()));
        busiQueryReqDto.setUpdateEndTime(DateUtils.getTime());
        Response<Long> response = ticketDataQueryService.busiQueryCount(busiQueryReqDto, "ldap", "tfs_system", "tfs_system");
        Map map = new HashMap<>();
        //待处理
        map.put("waiting_dispatch", response.getData());

        BusiQueryReqDto bqrd = new BusiQueryReqDto();
        bqrd.setAppIdList(Arrays.asList("ARK"));
        bqrd.setUserDealType(BusiQueryUserDealTypeEnum.ALL_APPLYING);
        bqrd.setExtend1(customerNo);
        bqrd.setUpdateStartTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, DateUtils.getYesterdayDate()));
        bqrd.setUpdateEndTime(DateUtils.getTime());
        bqrd.setTicketStatusList(Arrays.asList("APPLYING"));

        Response<Long> res = ticketDataQueryService.busiQueryCount(bqrd, "ldap", "tfs_system", "tfs_system");
        //处理中
        map.put("waiting_handle", res.getData());
        return AjaxResult.success(map);
    }

    @Resource
    private ITicketFormItemServiceWrapper ticketFormItemServiceWrapper;

    @PostMapping("/selectFormItemsByTicketId")
    public AjaxResult selectFormItemsByTicketId(String tId) {

        Response<TicketFormItemDataDto> res = ticketFormItemServiceWrapper.selectFormItemsByTicketId(tId);
        return AjaxResult.success(res);
    }

    @Resource
    private TicketDataApproveService ticketDataApproveService;

    /**
     * 自动审批
     * @param ticketDataId, auditedType, currentNodeId
     * @return
     */
    @GetMapping("/autoApprove")
    public AjaxResult ticketDataApproveService(String ticketDataId, AuditedType auditedType, String currentNodeId) {
        ticketDataApproveService.autoApprove(ticketDataId, auditedType, currentNodeId);
        return AjaxResult.success();
    }


}
