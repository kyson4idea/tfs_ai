package com.smy.tfs.biz.controller;

import com.alibaba.fastjson.JSONObject;
import com.smy.tfs.api.dbo.TicketApp;
import com.smy.tfs.api.dto.ReqParam;
import com.smy.tfs.api.dto.TicketCategoryDto;
import com.smy.tfs.api.dto.TicketDataDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.service.ITicketAppService;
import com.smy.tfs.api.service.ITicketCategoryService;
import com.smy.tfs.api.service.ITicketDataService;
import com.smy.tfs.biz.config.TfSJumpUrlProperties;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.utils.SecurityUtils;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import com.smy.tfs.framework.web.service.SysLoginService;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.dubbo.common.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 工单/应用数据
 * </p>
 *
 * @author yss
 * @since 2024-04-18
 */
@RestController
@RequestMapping("/outside")
@Slf4j
public class OutsideController {
    @Resource
    private ITicketDataService ticketDataService;

    @Resource
    ITicketAppService ticketAppService;

    @Resource
    private SysLoginService loginService;
    @Resource
    TfSJumpUrlProperties tfSJumpUrlProperties;

    /**
     * 令牌有效期（默认30分钟）
     */
    @Value("${token.expireTime}")
    private int expireTime;

    @GetMapping("/getDataByIdWithoutAuth")
    @ResponseBody
    public AjaxResult getDataByIdWithoutAuth(String id) {
        var response = ticketDataService.selectFullTicketDataById(new ReqParam(id));
        if (response.isSuccess()) {
            TicketDataDto data = response.getData();
            data.DealUserDistinct();
            data.buildTags();
            data.DealHasApprovalAuth(SecurityUtils.getSameOriginIdOrDefault(null));
        }
        Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
        return new AjaxResult(code, response.getMsg(), response.getData());
    }

    @GetMapping("/getTicketApp")
    @ResponseBody
    @CrossOrigin(origins = "*")
    public AjaxResult getTicketApp(String appId) {
        Response<TicketApp> response = ticketAppService.queryTicketAppById(appId);
        Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
        return new AjaxResult(code, response.getMsg(), response.getData());
    }


    @GetMapping("/getUserToken")
    @ResponseBody
    public void getUserToken(@RequestParam("workOrderId") String workOrderId, @RequestParam("ldapUserId") String ldapUserId, @RequestParam("appUserId") String appUserId, @RequestParam("appId") String appId, HttpServletResponse response) throws IOException {
        log.info("域账号ldapUserId={}", ldapUserId);
        String token = loginService.loginByLdapUserId(ldapUserId);
        StringBuilder jumpUrlStringBuilder = new StringBuilder(tfSJumpUrlProperties.getTicketDetailUrl()).append(workOrderId);
        jumpUrlStringBuilder.append("&category=").append(URL.encode("needHandleByMe"));
        jumpUrlStringBuilder.append("&appId=").append(URL.encode(appId));
        String jumpUrl = jumpUrlStringBuilder.toString();
        String host;
        try {
            URI url = new URI(jumpUrl);
            host = url.getHost();
        } catch (Exception e) {
            log.error("域名解析异常", e);
            throw new RuntimeException("域名解析异常");
        }
        Cookie tokenC = new Cookie("smy_union_user_token", token);
        tokenC.setDomain(host);
        tokenC.setPath("/");
        tokenC.setMaxAge(expireTime);
        Cookie appUserIdC = new Cookie("csUserId", ldapUserId);
        appUserIdC.setDomain(host);
        appUserIdC.setPath("/");
        int expireTimeSeconds = expireTime * 60;
        appUserIdC.setMaxAge(expireTimeSeconds);
        response.addCookie(tokenC);
        response.addCookie(appUserIdC);
        log.info("workOrderId：{},csTOKEN:{},csUserId:{},重定向的jumpUrl:{}", workOrderId, JSONObject.toJSONString(tokenC), JSONObject.toJSONString(appUserIdC), jumpUrl);
//        String html = "<!DOCTYPE html><html lang=\"zh-CN\"><head><meta charset=\"UTF-8\" /><script type='text/javascript' language='javascript'> setTimeout(function() {window.location.href='"+jumpUrl+"';}, 200)</script></head></html>";
        String html = "<!DOCTYPE html><html lang=\"zh-CN\"><head><meta charset=\"UTF-8\" /><script type='text/javascript' language='javascript'>window.location.href='" + jumpUrl + "';</script></head></html>";
        response.setContentType("text/html");
        response.getWriter().print(html);
    }


    @GetMapping("/getTestData")
    @ResponseBody
    @CrossOrigin(origins = "*")
    public JSONObject getTestData(String tag) {
        try {
            tag = java.net.URLDecoder.decode(tag, "UTF-8");
        } catch (Exception ex) {
        }
        JSONObject jsonObjectItem1 = new JSONObject();
        jsonObjectItem1.put("name", "深圳" + tag);
        jsonObjectItem1.put("code", "sz" + tag);
        JSONObject jsonObjectItem2 = new JSONObject();
        jsonObjectItem2.put("name", "上海" + tag);
        jsonObjectItem2.put("code", "sh" + tag);
        JSONObject jsonObjectItem3 = new JSONObject();
        jsonObjectItem3.put("name", "北京" + tag);
        jsonObjectItem3.put("code", "bj" + tag);

        List objs = new ArrayList();
        objs.add(jsonObjectItem1);
        objs.add(jsonObjectItem2);
        objs.add(jsonObjectItem3);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rows", objs);
        jsonObject.put("msg", tag);
        return jsonObject;
    }

    @Resource
    private ITicketCategoryService ticketCategoryService;

    @GetMapping("/getTicketCategoryList")
    @ResponseBody
    @CrossOrigin(origins = "*")
    public JSONObject getTicketCategoryList() {
        TicketCategoryDto ticketCategoryDto = new TicketCategoryDto();
        ticketCategoryDto.setAppId("ARK");
        Response<List<TicketCategoryDto>> response = ticketCategoryService.queryTicketCategoryList(ticketCategoryDto, "ldap", "admin", "admin");
        List<TicketCategoryDto> list = response.getData();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rows", list);

        return jsonObject;
    }

    @GetMapping("/getArkTicketTypeList")
    @ResponseBody
    @CrossOrigin(origins = "*")
    public JSONObject getArkTicketTypeList(String tag) {
        List<JSONObject> objs = new ArrayList<>();

        String[] ticketTypes = {
                "守信计划", "中收类投诉", "APP功能投诉", "交易支付咨询",
                "还款咨询", "还款投诉", "操作类", "借款咨询",
                "借款投诉", "催收投诉", "账户类投诉", "凭证类投诉",
                "资方征信", "营销类", "回访类", "客服相关",
                "风险类投诉", "贷后资深-逾期协商", "贷后资深-催收投诉",
                "贷后资深-操作类", "黑猫投诉"
        };
        for (String type : ticketTypes) {
            addItem(objs, type, type);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rows", objs);
        jsonObject.put("msg", tag);
        return jsonObject;
    }

    @GetMapping("/getArkZifangTypeList")
    @ResponseBody
    @CrossOrigin(origins = "*")
    public JSONObject getArkZifangTypeList(String tag) {
        List<JSONObject> objs = new ArrayList<>();
        String[] zifangList = {
                "晋商银行",
                "兴业消金",
                "北银消金",
                "阳光消金",
                "百信银行",
                "中信消金",
                "金美信",
                "锡商银行",
                "众邦银行",
                "华润信托",
                "中关村银行",
                "盛银消金",
                "大兴安岭农商",
                "海尔消金",
                "华通银行",
                "蒙商（包银消金）",
                "怀化小贷",
                "兰州银行",
                "宁波通商",
                "中原消金",
                "亿联银行",
                "营口银行",
                "小米",
                "唯品富邦",
                "蓝海银行",
                "盛京银行",
                "长银消金",
                "万达小贷",
                "欢太数科-重庆携隆",
                "欢太数科-中关村银行",
                "东营银行",
                "爱建信托",
                "润楼-苏宁项目",
                "齐齐哈尔商业银行",
                "紫金农商",
                "江南农商",
                "辽宁振兴银行",
                "沈阳农商银行",
                "陕西秦农农村",
                "广东盈峰",
                "廊坊银行",
                "本溪银行",
                "盛银消费-京东",
                "马上消金",
                "众邦银行-众易贷",
                "晋商消金",
                "苏宁银行",
                "苏宁银行-润楼",
                "盛银消金-京东",
                "秦农银行",
                "蒙商消金",
                "小米金融",
                "云南信托",
                "中国外贸信托",
                "苏州银行",
                "杭银消费",
                "昊悦担保",
                "沈阳农商",
                "上海华瑞银行",
                "新网银行",
                "合作渠道-流量渠道360-智信",
                "云南信托-买单吧",
                "合作渠道-流量渠道360-汇鑫借",
                "合作渠道-流量渠道滴滴数科",
                "合作渠道-流量渠道拍拍贷",
                "合作渠道-流量渠道百融",
                "合作渠道-流量渠道vivo钱包",
                "合作渠道-流量渠道洋钱罐",
                "合作渠道-流量渠道还呗",
                "合作渠道-流量渠道京东",
                "合作渠道-流量渠道乐信",
                "合作渠道-流量渠道哈啰",
                "合作渠道-流量渠道国美",
                "合作渠道-鸿飞担保",
                "合作渠道-湖南汇鑫"
        };
        for (String type : zifangList) {
            addItem(objs, type, type);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rows", objs);
        jsonObject.put("msg", tag);
        return jsonObject;
    }


    @GetMapping("/getArkZhongshouTypeList")
    @ResponseBody
    @CrossOrigin(origins = "*")
    public JSONObject getArkZhongshouTypeList(String tag) {
        List<JSONObject> objs = new ArrayList<>();
        String[] items = {
                "不涉及",
                "信知报告",
                "橡树会员卡",
                "省呗VIP",
                "加速卡",
                "乐享卡",
                "月享卡",
                "全能卡",
                "月花卡",
                "金享卡",
                "省钱卡",
                "车主省钱卡",
                "拿钱卡",
                "乐康卡",
                "天下信用",
                "天创信用",
                "Vplus会员",
                "其他停用卡种"
        };
        for (String type : items) {
            addItem(objs, type, type);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rows", objs);
        jsonObject.put("msg", tag);
        return jsonObject;
    }

    @GetMapping("/getArkTicketBusiList")
    @ResponseBody
    @CrossOrigin(origins = "*")
    public JSONObject getArkTicketBusiList(String tag) {
        if (StringUtils.isNotEmpty(tag)) {
            tag = URL.decode(tag);
        }
        List<JSONObject> objs = new ArrayList<>();
        switch (tag) {
            case "守信计划":
                addItemsForTrustPlan(objs);
                break;
            case "中收类投诉":
                addItemsForIntermediateIncomeComplaint(objs);
                break;
            case "APP功能投诉":
                addItemsForAppFunctionComplaint(objs);
                break;
            case "交易支付咨询":
                addItemsForTransactionPaymentConsultation(objs);
                break;
            case "还款咨询":
                addItemsForRepaymentConsultation(objs);
                break;
            case "还款投诉":
                addItemsForRepaymentComplaint(objs);
                break;
            case "操作类":
                addItemsForOperation(objs);
                break;
            case "借款咨询":
                addItemsForBorrowingConsultation(objs);
                break;
            case "借款投诉":
                addItemsForBorrowingComplaint(objs);
                break;
            case "催收投诉":
                addItemsForCollectionComplaint(objs);
                break;
            case "账户类投诉":
                addItemsForAccountComplaint(objs);
                break;
            case "凭证类投诉":
                addItemsForVoucherComplaint(objs);
                break;
            case "资方征信":
                addItemsForFundingPartyCredit(objs);
                break;
            case "营销类":
                addItemsForMarketing(objs);
                break;
            case "回访类":
                addItemsForFollowUp(objs);
                break;
            case "客服相关":
                addItemsForCustomerService(objs);
                break;
            case "风险类投诉":
                addItemsForRiskComplaint(objs);
                break;
            case "贷后资深-逾期协商":
                addItemsForLateNegotiation(objs);
                break;
            case "贷后资深-催收投诉":
                addItemsForCollectionComplaintBySenior(objs);
                break;
            case "贷后资深-操作类":
                addItemsForOperationBySenior(objs);
                break;
            case "黑猫投诉":
                addItemsForBlackCatComplaint(objs);
                break;
            default:
                break;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rows", objs);
        jsonObject.put("msg", tag);
        return jsonObject;
    }

    private void addItemsForTrustPlan(List<JSONObject> objs) {
        addItem(objs, "申请退费-开通无感知", "申请退费-开通无感知");
        addItem(objs, "申请退费-权益不符合预期", "申请退费-权益不符合预期");
        addItem(objs, "申请退费-权益不能使用", "申请退费-权益不能使用");
        addItem(objs, "金融权益-借款6.6折未返现", "金融权益-借款6.6折未返现");
        addItem(objs, "金融权益-500现金奖励", "金融权益-500现金奖励");
        addItem(objs, "服务特权-5天内未逾期返现", "服务特权-5天内未逾期返现");
        addItem(objs, "生活权益-无法使用", "生活权益-无法使用");
    }

    private void addItemsForIntermediateIncomeComplaint(List<JSONObject> objs) {
        addItem(objs, "先享后付", "先享后付");
        addItem(objs, "超过退款时效未到账", "超过退款时效未到账");
        addItem(objs, "不满退款时效", "不满退款时效");
        addItem(objs, "无法购买会员卡", "无法购买会员卡");
        addItem(objs, "申请退费-否认购买会员卡", "申请退费-否认购买会员卡");
        addItem(objs, "申请退费-权益无法使用", "申请退费-权益无法使用");
        addItem(objs, "申请退费-权益无价值", "申请退费-权益无价值");
        addItem(objs, "申请解约-否认开通会员续费", "申请解约-否认开通会员续费");
        addItem(objs, "申请解约-权益无法使用", "申请解约-权益无法使用");
        addItem(objs, "申请解约-权益无价值", "申请解约-权益无价值");
        addItem(objs, "申请退费-拿钱卡手续费", "申请退费-拿钱卡手续费");
        addItem(objs, "申请退费-Vplus手续费", "申请退费-Vplus手续费");
        addItem(objs, "申请退费-月享卡手续费", "申请退费-月享卡手续费");
        addItem(objs, "申请退费-乐享卡手续费", "申请退费-乐享卡手续费");
        addItem(objs, "申请退费-加速卡手续费", "申请退费-加速卡手续费");
        addItem(objs, "申请退费-省钱卡手续费", "申请退费-省钱卡手续费");
    }

    private void addItemsForAppFunctionComplaint(List<JSONObject> objs) {
        addItem(objs, "省市无法提现", "省市无法提现");
        addItem(objs, "查询交易信息", "查询交易信息");
        addItem(objs, "非我司扣款", "非我司扣款");
    }

    private void addItemsForTransactionPaymentConsultation(List<JSONObject> objs) {
        addItem(objs, "申请退款-错误还款", "申请退款-错误还款");
        addItem(objs, "申请退款-重复扣款", "申请退款-重复扣款");
        addItem(objs, "申请退款-其他", "申请退款-其他");
        addItem(objs, "退款未到账", "退款未到账");
    }

    private void addItemsForRepaymentConsultation(List<JSONObject> objs) {
        addItem(objs, "还款计划查询", "还款计划查询");
        addItem(objs, "未逾期协商还款日", "未逾期协商还款日");
        addItem(objs, "未逾期协商还款期数", "未逾期协商还款期数");
        addItem(objs, "未逾期协商减免金额", "未逾期协商减免金额");
        addItem(objs, "全部结清退费", "全部结清退费");
        addItem(objs, "部分结清退费", "部分结清退费");
        addItem(objs, "预借款无感知-放款日当天提前结清", "预借款无感知-放款日当天提前结清");
        addItem(objs, "预借款金额不一致-放款日当天提前结清", "预借款金额不一致-放款日当天提前结清");
    }

    private void addItemsForRepaymentComplaint(List<JSONObject> objs) {
        addItem(objs, "还款异常", "还款异常");
        addItem(objs, "不认可扣款规则", "不认可扣款规则");
        addItem(objs, "未逾期协商减免", "未逾期协商减免");
        addItem(objs, "未逾期协商还款日", "未逾期协商还款日");
        addItem(objs, "未逾期协商还款期数", "未逾期协商还款期数");
        addItem(objs, "APP端不支持提前结清", "APP端不支持提前结清");
        addItem(objs, "结清状态不一致", "结清状态不一致");
        addItem(objs, "全部结清退费", "全部结清退费");
        addItem(objs, "部分结清退费", "部分结清退费");
        addItem(objs, "预借款无感知-放款日当天提前结清", "预借款无感知-放款日当天提前结清");
        addItem(objs, "预借款金额不一致-放款日当天提前结清", "预借款金额不一致-放款日当天提前结清");
    }

    private void addItemsForOperation(List<JSONObject> objs) {
        addItem(objs, "当期招领", "当期招领");
        addItem(objs, "结清招领", "结清招领");
        addItem(objs, "退款申请", "退款申请");
        addItem(objs, "撤销入账", "撤销入账");
        addItem(objs, "开取结清证明", "开取结清证明");
        addItem(objs, "开取发票", "开取发票");
        addItem(objs, "调取合同", "调取合同");
        addItem(objs, "开取代偿证明", "开取代偿证明");
    }

    private void addItemsForBorrowingConsultation(List<JSONObject> objs) {
        addItem(objs, "如何借款", "如何借款");
        addItem(objs, "借款进度", "借款进度");
        addItem(objs, "不满拒贷", "不满拒贷");
    }

    private void addItemsForBorrowingComplaint(List<JSONObject> objs) {
        addItem(objs, "借款流程", "借款流程");
        addItem(objs, "放款时效", "放款时效");
        addItem(objs, "借款银行卡被冻结", "借款银行卡被冻结");
    }

    private void addItemsForCollectionComplaint(List<JSONObject> objs) {
        addItem(objs, "第三方被催收", "第三方被催收");
        addItem(objs, "未逾期提醒还款", "未逾期提醒还款");
        addItem(objs, "本人未借款被催收", "本人未借款被催收");
    }

    private void addItemsForAccountComplaint(List<JSONObject> objs) {
        addItem(objs, "非我司扣款", "非我司扣款");
        addItem(objs, "查询交易信息", "查询交易信息");
        addItem(objs, "申请退款-错误还款", "申请退款-错误还款");
        addItem(objs, "申请退款-重复扣款", "申请退款-重复扣款");
        addItem(objs, "申请退款-其他", "申请退款-其他");
        addItem(objs, "退款未到账", "退款未到账");
        addItem(objs, "无法解绑银行卡", "无法解绑银行卡");
        addItem(objs, "无法注销&注销失败", "无法注销&注销失败");
        addItem(objs, "手机号修改失败", "手机号修改失败");
        addItem(objs, "无法更改身份证信息", "无法更改身份证信息");
    }

    private void addItemsForVoucherComplaint(List<JSONObject> objs) {
        addItem(objs, "发票投诉", "发票投诉");
        addItem(objs, "协议投诉", "协议投诉");
        addItem(objs, "结清证明投诉", "结清证明投诉");
    }

    private void addItemsForFundingPartyCredit(List<JSONObject> objs) {
        addItem(objs, "征信类投诉", "征信类投诉");
        addItem(objs, "关闭授信额度", "关闭授信额度");
    }

    private void addItemsForMarketing(List<JSONObject> objs) {
        addItem(objs, "广告活动", "广告活动");
        addItem(objs, "营销短信&电话", "营销短信&电话");
        addItem(objs, "投诉营销人员", "投诉营销人员");
    }

    private void addItemsForFollowUp(List<JSONObject> objs) {
        addItem(objs, "回访类", "回访类");
    }

    private void addItemsForCustomerService(List<JSONObject> objs) {
        addItem(objs, "投诉客服", "投诉客服");
    }

    private void addItemsForRiskComplaint(List<JSONObject> objs) {
        addItem(objs, "伪冒注册投诉", "伪冒注册投诉");
        addItem(objs, "否认借款投诉", "否认借款投诉");
        addItem(objs, "学生借款投诉", "学生借款投诉");
        addItem(objs, "监管部门来访", "监管部门来访");
        addItem(objs, "司法公安来访", "司法公安来访");
        addItem(objs, "媒体记者来访", "媒体记者来访");
        addItem(objs, "律师诉讼来访", "律师诉讼来访");
        addItem(objs, "要求停催", "要求停催");
        addItem(objs, "要求延期", "要求延期");
        addItem(objs, "催收其他", "催收其他");
    }

    private void addItemsForLateNegotiation(List<JSONObject> objs) {
        addItem(objs, "要求延期", "要求延期");
        addItem(objs, "学生借款", "学生借款");
        addItem(objs, "高频催收", "高频催收");
    }

    private void addItemsForCollectionComplaintBySenior(List<JSONObject> objs) {
        addItem(objs, "联系非本人", "联系非本人");
        addItem(objs, "承诺未达", "承诺未达");
        addItem(objs, "人员态度", "人员态度");
    }

    private void addItemsForOperationBySenior(List<JSONObject> objs) {
        addItem(objs, "逾期对公还款(工单)", "逾期对公还款(工单)");
        addItem(objs, "逾期对公入账(工单)", "逾期对公入账(工单)");
        addItem(objs, "逾期退款(工单)", "逾期退款(工单)");
    }

    private void addItemsForBlackCatComplaint(List<JSONObject> objs) {
        addItem(objs, "黑猫投诉", "黑猫投诉");
        addItem(objs, "C类导流", "C类导流");
    }

    private void addItem(List<JSONObject> objs, String name, String code) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("code", code);
        objs.add(jsonObject);
    }
}
