package com.smy.tfs.biz.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.smy.cif.domain.CstCustIdInfo;
import com.smy.cif.domain.CstCustInfo;
import com.smy.cif.dto.CustOrgInfo;
import com.smy.cif.service.CustIdInfoService;
import com.smy.cif.service.CustInfoService;
import com.smy.fsp.client.FileUtil;
import com.smy.fsp.client.urlsign.UrlSignUtil;
import com.smy.tfs.api.constants.TfsBaseConstant;
import com.smy.tfs.api.dbo.TicketData;
import com.smy.tfs.api.dbo.TicketExecutorGroup;
import com.smy.tfs.api.dbo.TicketFlowNodeExecutorData;
import com.smy.tfs.api.dbo.TicketFormItemData;
import com.smy.tfs.api.dto.ReqParam;
import com.smy.tfs.api.dto.TicketDataDto;
import com.smy.tfs.api.dto.TicketFormItemDataDto;
import com.smy.tfs.api.dto.TicketFormUpdateDto;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.dynamic.TicketDataStdDto;
import com.smy.tfs.api.dto.dynamic.TicketFormItemStdDto;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.enums.ExecutorTypeEnum;
import com.smy.tfs.api.enums.TicketDataStatusEnum;
import com.smy.tfs.api.service.*;
import com.smy.tfs.common.core.redis.RedisCache;
import com.smy.tfs.common.utils.http.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service("arkMiaoDaServiceImpl")
public class ArkMiaoDaServiceImpl implements IArkMiaoDaService {
    // 1000000001 1836558971
    @Value("${ticket.miaoda.appId:1836558971}")
    private String appId;
    // admin123456 sVsJSHQLUs2ncLn4
    @Value("${ticket.miaoda.appKey:sVsJSHQLUs2ncLn4}")
    private String appKey;
    @Resource
    ITicketDataService ticketDataService;

    @Resource
    ITicketFormItemDataService ticketFormItemDataService;

    @Resource
    ITicketFlowNodeExecutorDataService ticketFlowNodeExecutorDataService;

    @Resource
    ITicketExecutorGroupService ticketExecutorGroupService;

    @Resource
    ITicketAccountMappingService ticketAccountMappingService;

    @Resource
    private CustInfoService custInfoService;

    @Resource
    CustIdInfoService custIdInfoService;

    @Autowired
    private RedisCache redisCache;

    private static String tokenString = "MIAO_DA_TOKEN_STRING";

    private String convertTimeStr(String timestampStr) {
        if (StringUtils.isEmpty(timestampStr)) {
            return "";
        }
        try {
            long timestamp = Long.parseLong(timestampStr);
            Instant instant = Instant.ofEpochSecond(timestamp);
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return localDateTime.format(formatter);
        } catch (Exception e) {
            return String.format("时间戳格式错误：%s", timestampStr);
        }
    }


    private List<TicketFormItemStdDto> initUpdateFormItems(List<TicketFormItemStdDto> formItemStdDtos) {
        if (formItemStdDtos == null) {
            formItemStdDtos = new ArrayList<>();
        }
        return formItemStdDtos;
    }

    private List<TicketFormItemStdDto> initInsertFormItems(List<TicketFormItemStdDto> formItemStdDtos) {
        if (formItemStdDtos == null) {
            formItemStdDtos = new ArrayList<>();
        }
        formItemStdDtos.add(new TicketFormItemStdDto("方舟处理状态", ""));

        TicketFormItemStdDto phoneItemStdDto = formItemStdDtos.stream().filter(x -> (Arrays.asList("联系方式", "phone").contains(x.getTemplateId()))).findFirst().orElse(null);

        //根据外部手机号码，补充内部信息（客户号、客户姓名、身份证号码）
        String custNo = "待补充";
        String custName = "待补充";
        String iDNo = "待补充";
        if (phoneItemStdDto != null && StringUtils.isNotEmpty(phoneItemStdDto.getValue())) {
            List<CstCustInfo> CstCustInfoList = custInfoService.queryCustInfoIncludeCancelListByMobileList(Arrays.asList(phoneItemStdDto.getValue()), "SMYS");
            if (CollectionUtils.isNotEmpty(CstCustInfoList)) {
                CstCustInfo smyCustInfo = CstCustInfoList.stream().filter(x -> x.getCustOrg().equals("SMYS")).findFirst().orElse(null);
                if (smyCustInfo != null) {
                    if (StringUtils.isNotEmpty(smyCustInfo.getCustNo())) {
                        custNo = smyCustInfo.getCustNo();
                    }
                    if (StringUtils.isNotEmpty(smyCustInfo.getCustName())) {
                        custName = smyCustInfo.getCustName();
                    }
                    //List<CustOrgInfo> custOrgInfoList = custInfoService.queryCustOrgByMobile(phoneItemStdDto.getValue());
                    CstCustIdInfo smyOrgInfo = custIdInfoService.queryCustIdInfoByCustNoWithCancel(smyCustInfo.getCustNo());
                    if (smyOrgInfo != null && StringUtils.isNotEmpty(smyOrgInfo.getIdNo())) {
                        if (smyOrgInfo.getIdNo().length() > 4) {
                            iDNo = "****" + smyOrgInfo.getIdNo().substring(smyOrgInfo.getIdNo().length() - 4, smyOrgInfo.getIdNo().length());
                        } else {
                            iDNo = smyOrgInfo.getIdNo();
                        }
                    }
                }
            }
        }
        formItemStdDtos.add(new TicketFormItemStdDto("客户号", custNo));
        formItemStdDtos.add(new TicketFormItemStdDto("客户名称", custName));
        formItemStdDtos.add(new TicketFormItemStdDto("身份证号", iDNo));


        formItemStdDtos.add(new TicketFormItemStdDto("工单类型", "黑猫投诉"));
        formItemStdDtos.add(new TicketFormItemStdDto("业务类型", "黑猫投诉"));
        formItemStdDtos.add(new TicketFormItemStdDto("风险客户类型", "无风险"));
        formItemStdDtos.add(new TicketFormItemStdDto("中收产品类型", "不涉及"));
        formItemStdDtos.add(new TicketFormItemStdDto("反馈渠道", "网络平台-黑猫"));
        formItemStdDtos.add(new TicketFormItemStdDto("所诉资方", "待补充"));
        formItemStdDtos.add(new TicketFormItemStdDto("跟进状态", "未跟进"));

        formItemStdDtos.add(new TicketFormItemStdDto("喵达投诉单", Arrays.asList(
                "黑猫投诉编号",
                "投诉链接",
                "投诉问题",
                "投诉要求",
                "涉诉⾦额",
                "投诉内容",
                "投诉⼈"
        ).toString(), "PANEL"));
        formItemStdDtos.add(new TicketFormItemStdDto("客服补充数据", Arrays.asList(
                "反馈渠道",
                "工单类型",
                "业务类型",
                "客户号",
                "客户名称",
                "身份证号",
                "联系⽅式",
                "投诉预留⼿机号",
                "风险客户类型",
                "所诉资方",
                "中收产品类型",
                "补充材料（不对客）"
        ).toString(), "PANEL"));
        formItemStdDtos.add(new TicketFormItemStdDto("喵达处理进度", Arrays.asList(
                "发起时间",
                "分配时间",
                "最终解决时间",
                "公开状态",
                "剩余申诉次数",
                "剩余结案次数",
                "结案状态",
                "黑猫回复状态",
                "投诉进度",
                "公开状态",
                "跟进状态"
        ).toString(), "PANEL"));
        return formItemStdDtos;
    }

    private List<TicketFormItemStdDto> convertFormItems(JSONObject jsonObject, List<TicketFormItemData> itemDataList, String addOrAll) {
        if (jsonObject == null) {
            return null;
        }
        HashMap<String, String> labelValueMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(itemDataList)) {
            for (TicketFormItemData itemData : itemDataList) {
                labelValueMap.put(itemData.getItemLabel(), itemData.getItemValue());
            }
        }
        List<TicketFormItemStdDto> formItems = new ArrayList<>();
        String replyDes = null;
        for (String key : jsonObject.keySet()) {
            if (Arrays.asList("attaches", "reply_details", "co_complete_info", "co_complete_info", "co_complete_attaches").contains(key)) {
                if ("attaches".equals(key)) {
                    JSONArray attacheValues = JSONArray.parseArray(jsonObject.getString(key));
                    if (attacheValues != null && attacheValues.size() > 0) {
                        JSONArray attacheItems = new JSONArray();
                        if ("add".equals(addOrAll) && StringUtils.isNotEmpty(labelValueMap.get("附件信息"))) {
                            attacheItems = JSONArray.parseArray(labelValueMap.get("附件信息"));
                        }
                        for (int i = 0; i < attacheValues.size(); i++) {
                            JSONObject attacheValue = attacheValues.getJSONObject(i);
                            JSONObject newItem = new JSONObject();
                            newItem.put("类型", attacheValue.getString("type"));

                            JSONArray valueJOs = new JSONArray();
                            JSONObject valueJO = new JSONObject();
                            valueJO.put("type", attacheValue.getString("type"));
                            valueJO.put("value", "查看附件");
                            JSONObject configJO = new JSONObject();
                            configJO.put("url", exchangeUrl(attacheValue.getString("src"), null));
                            valueJO.put("config", configJO);
                            valueJOs.add(valueJO);
                            newItem.put("内容", valueJOs);
                            attacheItems.add(newItem);
                        }
                        formItems.add(new TicketFormItemStdDto("附件信息", JSONArray.toJSONString(attacheItems), "TABLE"));
                    }
                    continue;
                }

                if ("reply_details".equals(key)) {
                    JSONArray replyDetailValues = JSONArray.parseArray(jsonObject.getString(key));
                    if (replyDetailValues != null && replyDetailValues.size() > 0) {
                        //根据回复时间排序
                        int n = replyDetailValues.size();
                        for (int i = 0; i < n - 1; i++) {
                            for (int j = 0; j < n - i - 1; j++) {
                                JSONObject json1 = replyDetailValues.getJSONObject(j);
                                JSONObject json2 = replyDetailValues.getJSONObject(j + 1);

                                String time1 = json1.getString("replyed_at");
                                String time2 = json2.getString("replyed_at");
                                if (StringUtils.isEmpty(time1) && StringUtils.isEmpty(time2)) {
                                    continue;
                                }
                                if (StringUtils.isEmpty(time1)) {
                                    continue;
                                }
                                if (StringUtils.isEmpty(time2)) {
                                    Object temp = replyDetailValues.get(j);
                                    replyDetailValues.set(j, replyDetailValues.get(j + 1));
                                    replyDetailValues.set(j + 1, temp);
                                    continue;
                                }
                                int result = time2.compareTo(time1); // 时间大的排在前面
                                if (result > 0) {
                                    Object temp = replyDetailValues.get(j);
                                    replyDetailValues.set(j, replyDetailValues.get(j + 1));
                                    replyDetailValues.set(j + 1, temp);
                                }
                            }
                        }

                        JSONObject newReplay = replyDetailValues.getJSONObject(0);
                        String sender = newReplay.getString("sender");
                        if (StringUtils.isNotEmpty(sender)) {
                            if ("1".equals(sender)) {
                                replyDes = "已补充待商家回复";
                            } else if ("2".equals(sender)) {
                                replyDes = "商家已回复";
                            } else {
                                replyDes = "未知：" + sender;
                            }
                        }

                        JSONArray replyDetailItems = new JSONArray();
                        if ("add".equals(addOrAll) && StringUtils.isNotEmpty(labelValueMap.get("回复详情"))) {
                            replyDetailItems = JSONArray.parseArray(labelValueMap.get("回复详情"));
                        }
                        for (int i = 0; i < replyDetailValues.size(); i++) {
                            JSONObject replyDetailValue = replyDetailValues.getJSONObject(i);
                            JSONObject newItem = new JSONObject();
                            newItem.put("回复人", convertSender(replyDetailValue.getString("sender")));
                            newItem.put("回复内容", replyDetailValue.getString("content"));
                            newItem.put("回复是否隐藏", convertContentHide(replyDetailValue.getString("content_hide")));
                            newItem.put("回复时间", convertTimeStr(replyDetailValue.getString("replyed_at")));
                            newItem.put("附件是否隐藏", convertAttachHide(replyDetailValue.getString("attach_hide")));

                            JSONArray jsonArray = replyDetailValue.getJSONArray("attaches");
                            if (jsonArray != null && jsonArray.size() > 0) {
                                JSONArray jsonInputArray = new JSONArray();
                                for (int j = 0; j < jsonArray.size(); j++) {
                                    JSONObject jsonItem = jsonArray.getJSONObject(j);
                                    JSONObject jsonInputItem = new JSONObject();
                                    jsonInputItem.put("type", jsonItem.getString("type"));
                                    jsonInputItem.put("value", "查看附件");
                                    JSONObject configJO = new JSONObject();
                                    configJO.put("url", exchangeUrl(jsonItem.getString("src"), null));
                                    jsonInputItem.put("config", configJO);
                                    jsonInputArray.add(jsonInputItem);
                                }
                                newItem.put("回复附件", jsonInputArray);
                            } else {
                                newItem.put("回复附件", "无");
                            }
                            if (0 == replyDetailItems.size()) {
                                replyDetailItems.add(newItem);
                            } else {
                                for (int itemIndex = 0; itemIndex < replyDetailItems.size(); itemIndex++) {
                                    JSONObject item = replyDetailItems.getJSONObject(itemIndex);
                                    if (Objects.equals(item.getString("回复人"), newItem.getString("回复人")) && Objects.equals(item.getString("回复内容"), newItem.getString("回复内容")) && Objects.equals(item.getString("回复时间"), newItem.getString("回复时间"))) {
                                        break;
                                    }
                                    if (itemIndex == replyDetailItems.size() - 1) {
                                        replyDetailItems.add(newItem);
                                        break;
                                    }
                                }
                            }
                        }
                        formItems.add(new TicketFormItemStdDto("回复详情", JSONArray.toJSONString(replyDetailItems), "TABLE"));
                    }
                    continue;
                }
                if ("co_complete_attaches".equals(key)) {
                    JSONArray coCompleteAttacheValues = JSONArray.parseArray(jsonObject.getString(key));
                    if (coCompleteAttacheValues != null && coCompleteAttacheValues.size() > 0) {
                        JSONArray coCompleteAttacheItems = new JSONArray();
                        if ("add".equals(addOrAll) && StringUtils.isNotEmpty(labelValueMap.get("结案附件信息"))) {
                            coCompleteAttacheItems = JSONArray.parseArray(labelValueMap.get("结案附件信息"));
                        }
                        for (int i = 0; i < coCompleteAttacheValues.size(); i++) {
                            JSONObject coCompleteAttacheValue = coCompleteAttacheValues.getJSONObject(i);
                            JSONObject newItem = new JSONObject();
                            newItem.put("类型", coCompleteAttacheValue.getString("type"));

                            JSONArray valueJOs = new JSONArray();
                            JSONObject valueJO = new JSONObject();
                            valueJO.put("type", coCompleteAttacheValue.getString("type"));
                            valueJO.put("value", "查看附件");
                            JSONObject configJO = new JSONObject();
                            configJO.put("url", exchangeUrl(coCompleteAttacheValue.getString("src"), null));
                            valueJO.put("config", configJO);
                            valueJOs.add(valueJO);
                            newItem.put("内容", valueJOs);
                            coCompleteAttacheItems.add(newItem);
                        }
                        formItems.add(new TicketFormItemStdDto("结案附件信息", JSONArray.toJSONString(coCompleteAttacheItems), "TABLE"));
                    }
                    continue;
                }
                if ("co_complete_info".equals(key)) {
                    JSONArray coCompleteInfoValues = JSONArray.parseArray(jsonObject.getString(key));
                    if (coCompleteInfoValues != null && coCompleteInfoValues.size() > 0) {
                        JSONArray coCompleteInfoItems = new JSONArray();
                        if ("add".equals(addOrAll) && StringUtils.isNotEmpty(labelValueMap.get("结案详情"))) {
                            coCompleteInfoItems = JSONArray.parseArray(labelValueMap.get("结案详情"));
                        }
                        for (int i = 0; i < coCompleteInfoValues.size(); i++) {
                            JSONObject coCompleteInfoValue = coCompleteInfoValues.getJSONObject(i);
                            JSONObject newItem = new JSONObject();
                            newItem.put("结案解决方案", coCompleteInfoValue.getString("co_complete_solution"));
                            newItem.put("结案申请原因", coCompleteInfoValue.getString("co_complete_reason"));
                            newItem.put("结案申请时间", convertTimeStr(coCompleteInfoValue.getString("co_complete_begin")));

                            JSONArray jsonArray = coCompleteInfoValue.getJSONArray("co_complete_attaches");
                            if (jsonArray != null && jsonArray.size() > 0) {
                                JSONArray jsonInputArray = new JSONArray();
                                for (int j = 0; j < jsonArray.size(); j++) {
                                    JSONObject jsonItem = jsonArray.getJSONObject(j);
                                    JSONObject jsonInputItem = new JSONObject();
                                    jsonInputItem.put("type", jsonItem.getString("type"));
                                    jsonInputItem.put("value", "查看附件");
                                    JSONObject configJO = new JSONObject();
                                    configJO.put("url", exchangeUrl(jsonItem.getString("src"), null));
                                    jsonInputItem.put("config", configJO);
                                    jsonInputArray.add(jsonInputItem);
                                }
                                newItem.put("结案附件", jsonInputArray);
                            } else {
                                newItem.put("结案附件", "无");
                            }
                            if (0 == coCompleteInfoItems.size()) {
                                coCompleteInfoItems.add(newItem);
                            } else {
                                for (int itemIndex = 0; itemIndex < coCompleteInfoItems.size(); itemIndex++) {
                                    JSONObject item = coCompleteInfoItems.getJSONObject(itemIndex);
                                    if (Objects.equals(item.getString("结案解决方案"), newItem.getString("结案解决方案")) && Objects.equals(item.getString("结案申请原因"), newItem.getString("结案申请原因")) && Objects.equals(item.getString("结案申请时间"), newItem.getString("结案申请时间"))) {
                                        break;
                                    }
                                    if (itemIndex == coCompleteInfoItems.size() - 1) {
                                        coCompleteInfoItems.add(newItem);
                                        break;
                                    }
                                }
                            }
                        }
                        formItems.add(new TicketFormItemStdDto("结案详情", JSONArray.toJSONString(coCompleteInfoItems), "TABLE"));
                    }
                    continue;
                }
                throw new RuntimeException("不支持的key:" + key);
            } else {
                if (Arrays.asList("created_at", "assigned_at", "replyed_at", "eval_at", "completed_at", "co_complete_at", "co_complete_begin", "auto_complete_at", "user_complete_at", "complete_at").contains(key)) {
                    jsonObject.put(key, convertTimeStr(jsonObject.getString(key)));
                }
                if ("exposed".equals(key)) {
                    jsonObject.put(key, convertExposed(jsonObject.getString(key)));
                }
                formItems.add(new TicketFormItemStdDto(key, jsonObject.getString(key)));
            }
        }
        if (StringUtils.isNotEmpty(replyDes)) {
            formItems.add(new TicketFormItemStdDto("黑猫回复状态", replyDes));
        }
        if (!jsonObject.containsKey("co_complete_status")) {
            formItems.add(new TicketFormItemStdDto("co_complete_status", "未发起结案"));
        }
        return formItems;
    }


    //公开状态 0.未公开 1.已公开
    private String convertExposed(String exposed) {
        if (StringUtils.isEmpty(exposed)) {
            return "";
        }
        if ("0".equals(exposed)) {
            return "未公开";
        } else if ("1".equals(exposed)) {
            return "已公开";
        } else {
            return String.format("未知状态：%s", exposed);
        }
    }

    //回复是否隐藏 0.代表公开 1.代表隐藏
    private String convertContentHide(String contentHide) {
        if (StringUtils.isEmpty(contentHide)) {
            return "";
        }
        if ("0".equals(contentHide)) {
            return "公开";
        } else if ("1".equals(contentHide)) {
            return "隐藏";
        } else {
            return String.format("未知状态：%s", contentHide);
        }
    }

    //附件是否隐藏 0.代表公开 1.代表隐藏
    private String convertAttachHide(String contentHide) {
        if (StringUtils.isEmpty(contentHide)) {
            return "";
        }
        if ("0".equals(contentHide)) {
            return "公开";
        } else if ("1".equals(contentHide)) {
            return "隐藏";
        } else {
            return String.format("未知状态：%s", contentHide);
        }
    }

    //回复⼈：1.⽤户补充 2. 商家回复
    private String convertSender(String sender) {
        if (StringUtils.isEmpty(sender)) {
            return "";
        }
        if ("1".equals(sender)) {
            return "用户补充";
        } else if ("2".equals(sender)) {
            return "商家回复";
        } else {
            return String.format("未知状态：%s", sender);
        }
    }

    private byte[] downloadFile(String fileUrl) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(fileUrl);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    return EntityUtils.toByteArray(entity);
                }
            }
        }
        return new byte[0];
    }

    private String exchangeUrl(String url, String fileName) {
        try {
            if (StringUtils.isEmpty(url)) {
                return url;
            }
            //从url上获取文件名称
            if (StringUtils.isEmpty(fileName)) {
                int lastIndex = url.lastIndexOf("/");
                int queryIndex = url.indexOf("?");
                fileName = url.substring(lastIndex + 1, queryIndex > lastIndex ? queryIndex : url.length());
            }
            if (StringUtils.isEmpty(fileName)) {
                fileName = "default.jpg";
            }
            byte[] fileBytes = downloadFile(url);
            String fspFileUrl = FileUtil.uploadBuilder()
                    .fileName(fileName)
                    .fileByte(fileBytes)
                    .sceneType(TfsBaseConstant.FSP_UPLOAD_SCENE_TYPE)
                    .upload();
//            try {
//                fspFileUrl = UrlSignUtil.urlSignBuilder().url(fspFileUrl).isPublic(true).getSignUrl();
//            } catch (Exception ex) {
//                log.error("url内网转外网失败", ex);
//            }
            return fspFileUrl;
        } catch (Exception e) {
            log.error("黑猫URL转换失败  error:", e);
            return url;
        }
    }

    private String getToken() {
        String tokenStr = redisCache.getCacheObject(tokenString);
        if (StringUtils.isNotEmpty(tokenStr)) {
            return tokenStr;
        }
        String tokenStringReps = HttpUtils.sendGet(String.format("https://api-miaoda.sina.com.cn/auth/token?uid=%s&key=%s", appId, appKey));
        if (StringUtils.isEmpty(tokenStringReps)) {
            throw new RuntimeException("喵达接口,获取token失败");
        }
        JSONObject jsonObject = JSONObject.parseObject(tokenStringReps);
        if (!jsonObject.containsKey("result")) {
            throw new RuntimeException("喵达接口,获取token失败,result 不存在");
        }
        JSONObject result = jsonObject.getJSONObject("result");
        if (!result.containsKey("status")) {
            throw new RuntimeException("喵达接口,获取token失败,result.status 不存在");
        }
        JSONObject status = result.getJSONObject("status");
        if (!status.containsKey("code")) {
            throw new RuntimeException("喵达接口,获取token失败,result.status.code 不存在");
        }
        String code = status.getString("code");
        if (!"200".equals(code)) {
            throw new RuntimeException("喵达接口,获取token失败,result.status.code 不是200");
        }
        JSONObject data = result.getJSONObject("data");
        tokenStr = data.getString("token");
        if (StringUtils.isEmpty(tokenStr)) {
            throw new RuntimeException("喵达接口,获取token失败,result.data.token 不存在");
        }
        String expireTimeStr = data.getString("expire");
        if (StringUtils.isEmpty(expireTimeStr)) {
            throw new RuntimeException("喵达接口,获取token失败,result.data.expire 不存在");
        }
        redisCache.setCacheObject(tokenString, tokenStr, 160, TimeUnit.MINUTES);
        return tokenStr;
    }

    /**
     * 向指定 URL 发送GET方法的请求
     *
     * @param token     发送请求的 token
     * @param status    投诉单状态4—待回复 6—已回复 7—已完成。选填，不填默认为所有有效投诉。
     * @param st        起始时间（分配时间）选填，但必须和结束时间同时出现。若不填，则默认⼀周内。
     * @param et        结束时间（分配时间）选填，但必须和起始时间同时出现。若不填，则默认⼀周内。
     * @param page      ⻚码。选填，默认为第⼀⻚
     * @param page_size 每⻚数量。选填，默认每⻚10条，最多每⻚30条，超过30条按照30条查询
     * @return 返回当前数据列表
     */
    private JSONArray getMiaoDaTicketList(String token, String status, String st, String et, String page, String page_size) {
        String baseUrl = String.format("https://api-miaoda.sina.com.cn/complaint/detail_v2", token);
        String queryString = "";
        if (StringUtils.isNotEmpty(status)) {
            queryString += "&status=" + status;
        }
        if (StringUtils.isNotEmpty(st)) {
            queryString += "&st=" + st;
        }
        if (StringUtils.isNotEmpty(et)) {
            queryString += "&et=" + et;
        }
        if (StringUtils.isNotEmpty(page)) {
            queryString += "&page=" + page;
        }
        if (StringUtils.isNotEmpty(page_size)) {
            queryString += "&page_size=" + page_size;
        }

        HttpClientBuilder builder = HttpClientBuilder.create();
        CloseableHttpClient client = builder.build();
        HttpGet get = new HttpGet(baseUrl + "?" + queryString);
        get.addHeader("Content-Type", "application/json");
        get.addHeader("Authorization", "MiaoDa " + token);

        CloseableHttpResponse response = null;
        try {
            response = client.execute(get);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity, "utf8");
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (!jsonObject.containsKey("result")) {
                    throw new RuntimeException("喵达接口,获取投诉列表:Response does not contain 'result' key");
                }
                JSONObject jsonResult = jsonObject.getJSONObject("result");
                if (!jsonResult.containsKey("status")) {
                    throw new RuntimeException("喵达接口,获取投诉列表:Response does not contain 'result.status' key");
                }
                if (!jsonResult.getJSONObject("status").containsKey("code")) {
                    throw new RuntimeException("喵达接口,获取投诉列表:Response does not contain 'result.status.code' key");
                }
                String code = jsonResult.getJSONObject("status").getString("code");
                if (!"200".equals(code)) {
                    throw new RuntimeException("喵达接口,获取投诉列表: 返回编码不为200," + code);
                }
                JSONArray data = jsonResult.getJSONObject("data").getJSONArray("complaints");
                return data;
            } else {
                log.warn("Response entity is null");
                return null;
            }
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("Error closing response", e);
                }
            }
        }
    }

    @Override
    public void syncMiaoDaNewTicketCore(String status, String st, String et, String page, String page_size) {
        //获取黑猫待处理数据
        String tokenString = getToken();
        int pageSiz = Integer.valueOf(page_size);
        JSONArray jsonArray = getMiaoDaTicketList(tokenString, status, st, et, page, page_size);
        if (jsonArray == null || jsonArray.size() < 1) {
            log.info("喵达接口,获取数据为空");
            return;
        }
        for (int i = 0; i < jsonArray.size() && i < pageSiz; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String itemStr = JSONObject.toJSONString(jsonObject);
            String sn = jsonObject.getString("sn");
            if (StringUtils.isEmpty(sn)) {
                log.info("喵达接口,获取数据为空");
                return;
            }
            TicketData ticketData = ticketDataService.lambdaQuery()
                    .isNull(TicketData::getDeleteTime)
                    .eq(TicketData::getAppId, "ARK")
                    .eq(TicketData::getTicketBusinessKey, sn).oneOpt().orElse(null);
            if (ticketData == null) {
                Response<String> applyResp = ticketDataService.getTicketApplyId("ARK");
                if (!applyResp.isSuccess()) {
                    log.error("喵达接口,获取工单申请ID失败：" + applyResp.getData());
                    return;
                }
                TicketDataStdDto ticketDataStdDto = new TicketDataStdDto();
                ticketDataStdDto.setApplyId(applyResp.getData());
                ticketDataStdDto.setTicketTemplateId("miao_da_flow");
                ticketDataStdDto.setTicketBusinessKey(sn);
                List<TicketFormItemStdDto> formItemStdDtos = new ArrayList<>();
                formItemStdDtos.addAll(convertFormItems(jsonObject, null, null));
                //初始数据
                formItemStdDtos = initInsertFormItems(formItemStdDtos);
                ticketDataStdDto.setFormItems(formItemStdDtos);
                Response<String> response = ticketDataService.createTicket(ticketDataStdDto, "ldap", "tfs_system", "工单平台");
                if (!response.isSuccess()) {
                    log.error("喵达接口,创建工单失败：" + response.getData());
                }
            } else {
                String ticketVersion = ticketData.getExtend4();
                //外部数据有调整
                if (StringUtils.isNotEmpty(jsonObject.getString("status_no")) && !jsonObject.getString("status_no").equals(ticketVersion)) {
                    List<TicketFormItemData> ticketFormItemDataList = ticketFormItemDataService.lambdaQuery()
                            .isNull(TicketFormItemData::getDeleteTime)
                            .eq(TicketFormItemData::getTicketDataId, ticketData.getId()).list();

                    TicketFormUpdateDto ticketFormDataDto = new TicketFormUpdateDto();
                    ticketFormDataDto.setTicketDataId(ticketData.getId());
                    ticketFormDataDto.setDealDescription("喵达数据同步");
                    //初始数据
                    List<TicketFormItemStdDto> formItemStdDtos = new ArrayList<>();
                    formItemStdDtos = initUpdateFormItems(formItemStdDtos);
                    formItemStdDtos.addAll(convertFormItems(jsonObject, ticketFormItemDataList, null));
                    ticketFormDataDto.setFormItems(formItemStdDtos);
                    ticketFormDataDto.setMode("DYNAMIC");

                    Response<String> response = ticketDataService.updateTicketFormData(ticketFormDataDto, "ldap", "tfs_system", "工单平台");
                    if (!response.isSuccess()) {
                        log.error("喵达接口,更新工单失败：" + response.getMsg());
                    }
                }
            }
        }
    }

    @Override
    public void replyMiaoDaTicketCore(String snNo, String replyContent, String hideAttach, String hideContent, List<String> images, List<String> videos) {
        String token = getToken();
        String baseUrl = "https://api-miaoda.sina.com.cn/complaint/reply";
        HttpClientBuilder builder = HttpClientBuilder.create();
        CloseableHttpClient client = builder.build();
        HttpPost post = new HttpPost(baseUrl);
        post.addHeader("Accept", "application/json");
        post.addHeader("Content-Type", "application/json;charset=utf-8");
        post.addHeader("Authorization", "MiaoDa " + token);
        JSONObject body = new JSONObject();
        body.put("sns", snNo);
        body.put("content", replyContent);
        if (StringUtils.isNotEmpty(hideAttach)) {
            body.put("hide_attach", Integer.parseInt(hideAttach));
        }
        if (StringUtils.isNotEmpty(hideContent)) {
            body.put("hide_content", Integer.parseInt(hideContent));
        }
        if (CollectionUtils.isNotEmpty(images)) {
            body.put("images", images);
        }
        if (CollectionUtils.isNotEmpty(videos)) {
            body.put("videos", videos);
        }
        CloseableHttpResponse response = null;
        try {
            StringEntity entity = new StringEntity(body.toString(), "utf-8");
            post.setEntity(entity);
            response = client.execute(post);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                String result = EntityUtils.toString(responseEntity, "utf8");
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (!jsonObject.containsKey("result")) {
                    throw new RuntimeException("喵达接口,回复投诉:Response does not contain 'result' key");
                }
                JSONObject jsonResult = jsonObject.getJSONObject("result");
                if (!jsonResult.containsKey("status")) {
                    throw new RuntimeException("喵达接口,回复投诉:Response does not contain 'result.status' key");
                }
                JSONObject jsonStatus = jsonResult.getJSONObject("status");
                if (!jsonStatus.containsKey("code")) {
                    throw new RuntimeException("喵达接口,回复投诉:Response does not contain 'result.status.code' key");
                }
                String code = jsonStatus.getString("code");
                if (!"200".equals(code)) {
                    throw new RuntimeException("喵达接口,回复投诉: 返回编码不为200," + code + jsonStatus.getString("msg"));
                }
                log.info("喵达回复成功,sn:{} ret:{}", snNo, result);
            }
        } catch (ClientProtocolException e) {
            log.info("喵达回复失败,sn:{} ret:{}", snNo, e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.info("喵达回复失败,sn:{} ret:{}", snNo, e);
            throw new RuntimeException(e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("Error closing response", e);
                }
            }
        }
    }

    @Override
    public void completeMiaoDaTicketCore(String snNo, String reason, String solution, String hide_attach, List<String> images, List<String> videos) {
        String token = getToken();
        String baseUrl = "https://api-miaoda.sina.com.cn/complaint/complete";
        HttpClientBuilder builder = HttpClientBuilder.create();
        CloseableHttpClient client = builder.build();
        HttpPost post = new HttpPost(baseUrl);
        post.addHeader("Accept", "application/json");
        post.addHeader("Content-Type", "application/json;charset=utf-8");
        post.addHeader("Authorization", "MiaoDa " + token);
        JSONObject body = new JSONObject();
        body.put("sn", snNo);
        if (StringUtils.isEmpty(reason)) {
            throw new RuntimeException("结案类型不能为空," + reason);
        }
        body.put("reason", Integer.valueOf(reason));
        body.put("solution", solution);
        if (StringUtils.isNotEmpty(hide_attach)) {
            body.put("hide_attach", Integer.valueOf(hide_attach));
        }
        if (CollectionUtils.isNotEmpty(images)) {
            body.put("images", images);
        }
        if (CollectionUtils.isNotEmpty(videos)) {
            body.put("videos", videos);
        }
        CloseableHttpResponse response = null;
        try {
            StringEntity entity = new StringEntity(body.toString(), "utf-8");
            post.setEntity(entity);
            response = client.execute(post);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                String result = EntityUtils.toString(responseEntity, "utf8");
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (!jsonObject.containsKey("result")) {
                    throw new RuntimeException("申请结案接口,返回异常:Response does not contain 'result' key");
                }
                JSONObject jsonResult = jsonObject.getJSONObject("result");
                if (!jsonResult.containsKey("status")) {
                    throw new RuntimeException("申请结案接口,返回异常:Response does not contain 'result.status' key");
                }
                JSONObject jsonStatus = jsonResult.getJSONObject("status");
                if (!jsonStatus.containsKey("code")) {
                    throw new RuntimeException("申请结案接口,返回异常:Response does not contain 'result.status.code' key");
                }
                String code = jsonStatus.getString("code");
                if (!"200".equals(code)) {
                    throw new RuntimeException("申请结案接口,返回异常: 编码不为200," + code + jsonStatus.getString("msg"));
                }
                log.info("喵达结案成功,sn:{} ret:{}", snNo, result);
            }
        } catch (ClientProtocolException e) {
            log.error("喵达结案失败,sn:{} exception:{}", snNo, e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("喵达结案失败,sn:{} exception:{}", snNo, e);
            throw new RuntimeException(e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("Error closing response", e);
                }
            }
        }
    }

    @Override
    public void appealMiaoDaTicketCore(String sns, String content, String type, String dup_sns, List<String> images, List<String> videos) {
        String token = getToken();
        String baseUrl = "https://api-miaoda.sina.com.cn/complaint/appeal";
        HttpClientBuilder builder = HttpClientBuilder.create();
        CloseableHttpClient client = builder.build();
        HttpPost post = new HttpPost(baseUrl);
        post.addHeader("Accept", "application/json");
        post.addHeader("Content-Type", "application/json;charset=utf-8");
        post.addHeader("Authorization", "MiaoDa " + token);
        JSONObject body = new JSONObject();
        body.put("sns", sns);
        body.put("content", content);
        body.put("type", Integer.valueOf(type));
        if (StringUtils.isNotEmpty(dup_sns)) {
            body.put("dup_sns", dup_sns);
        }
        if (CollectionUtils.isNotEmpty(images)) {
            body.put("images", images);
        }
        if (CollectionUtils.isNotEmpty(videos)) {
            body.put("videos", videos);
        }
        CloseableHttpResponse response = null;
        try {
            StringEntity entity = new StringEntity(body.toString(), "utf-8");
            post.setEntity(entity);
            response = client.execute(post);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                String result = EntityUtils.toString(responseEntity, "utf8");
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (!jsonObject.containsKey("result")) {
                    throw new RuntimeException("申诉接口,返回异常:Response does not contain 'result' key");
                }
                JSONObject jsonResult = jsonObject.getJSONObject("result");
                if (!jsonResult.containsKey("status")) {
                    throw new RuntimeException("申诉接口,返回异常:Response does not contain 'result.status' key");
                }
                JSONObject jsonStatus = jsonResult.getJSONObject("status");
                if (!jsonStatus.containsKey("code")) {
                    throw new RuntimeException("申诉接口,返回异常:Response does not contain 'result.status.code' key");
                }
                String code = jsonStatus.getString("code");
                if (!"200".equals(code)) {
                    throw new RuntimeException("申诉接口,返回异常: 编码不为200," + code + jsonStatus.getString("msg"));
                }
                log.info("喵达申诉成功,sn:{} ret:{}", sns, result);
            }
        } catch (ClientProtocolException e) {
            log.error("喵达申诉失败,sn:{} ret:{}", sns, e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("喵达申诉失败,sn:{} ret:{}", sns, e);
            throw new RuntimeException(e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("Error closing response", e);
                }
            }
        }
    }

    @Override
    public void updateMiaoDaTicketCore(String sn, String status_no) {
        String token = getToken();
        String baseUrl = "https://api-miaoda.sina.com.cn/complaint/update?sn=" + sn + "&status_no=" + status_no;
        HttpClientBuilder builder = HttpClientBuilder.create();
        CloseableHttpClient client = builder.build();
        HttpGet get = new HttpGet(baseUrl);
        get.addHeader("Accept", "application/json");
        get.addHeader("Content-Type", "application/json;charset=utf-8");
        get.addHeader("Authorization", "MiaoDa " + token);
        JSONArray bodyArray = new JSONArray();
        JSONObject body = new JSONObject();
        body.put("sn", sn);
        body.put("status_no", status_no);
        bodyArray.add(body);
        CloseableHttpResponse response = null;
        try {
            response = client.execute(get);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                String result = EntityUtils.toString(responseEntity, "utf8");
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (!jsonObject.containsKey("result")) {
                    throw new RuntimeException("更新接口,返回异常:Response does not contain 'result' key");
                }
                JSONObject jsonResult = jsonObject.getJSONObject("result");
                if (!jsonResult.containsKey("status")) {
                    throw new RuntimeException("更新接口,返回异常:Response does not contain 'result.status' key");
                }
                JSONObject jsonStatus = jsonResult.getJSONObject("status");
                if (!jsonStatus.containsKey("code")) {
                    throw new RuntimeException("更新接口,返回异常:Response does not contain 'result.status.code' key");
                }
                String code = jsonStatus.getString("code");
                if (!"200".equals(code)) {
                    throw new RuntimeException("更新接口,返回异常: 编码不为200," + code + jsonStatus.getString("msg"));
                }
                JSONArray jsonDataArr = jsonResult.getJSONArray("data");
                if (jsonDataArr.size() > 0) {
                    JSONObject jsonData = jsonDataArr.getJSONObject(0);
                    TicketData ticketData = ticketDataService.lambdaQuery()
                            .isNull(TicketData::getDeleteTime)
                            .eq(TicketData::getAppId, "ARK")
                            .eq(TicketData::getTicketBusinessKey, sn).oneOpt().orElse(null);
                    if (ticketData == null) {
                        log.error("对应sn:%s工单数据不存在", sn);
                        return;
                    }
                    String ticketVersion = ticketData.getExtend4();

                    //外部数据有调整
                    if (StringUtils.isNotEmpty(jsonData.getString("status_no")) && !jsonData.getString("status_no").equals(ticketVersion)) {

                        List<TicketFormItemData> ticketFormItemDataList = ticketFormItemDataService.lambdaQuery()
                                .isNull(TicketFormItemData::getDeleteTime)
                                .eq(TicketFormItemData::getTicketDataId, ticketData.getId()).list();

                        TicketFormUpdateDto ticketFormDataDto = new TicketFormUpdateDto();
                        ticketFormDataDto.setTicketDataId(ticketData.getId());
                        ticketFormDataDto.setDealDescription("喵达数据同步");
                        //初始数据
                        List<TicketFormItemStdDto> formItemStdDtos = new ArrayList<>();
                        formItemStdDtos = initUpdateFormItems(formItemStdDtos);
                        formItemStdDtos.addAll(convertFormItems(jsonData, ticketFormItemDataList, "add"));
                        ticketFormDataDto.setFormItems(formItemStdDtos);
                        ticketFormDataDto.setMode("DYNAMIC");
                        Response<String> updateResponse = ticketDataService.updateTicketFormData(ticketFormDataDto, "ldap", "tfs_system", "工单平台");
                        if (!updateResponse.isSuccess()) {
                            log.error("喵达更新数据失败：" + updateResponse.getMsg());
                        } else {
                            log.info("喵达更新数据成功,sn:{} status_no:{}", sn, status_no);
                        }
                    }
                }
            }
        } catch (ClientProtocolException e) {
            log.error("喵达更新数据失败,sn:{} status_no:{}", sn, status_no);
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("喵达更新数据失败,sn:{} status_no:{}", sn, status_no);
            throw new RuntimeException(e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("Error closing response", e);
                }
            }
        }
    }

    @Override
    public void updateMiaoDaTicketJob(String appid, String templateId) {
        List<TicketData> ticketDataList = ticketDataService.lambdaQuery()
                .isNull(TicketData::getDeleteTime)
                .eq(TicketData::getAppId, appid)
                .eq(TicketData::getTicketStatus, TicketDataStatusEnum.APPLYING)
                .eq(TicketData::getTemplateId, templateId)
                .orderByDesc(TicketData::getUpdateTime)
                .list();
        if (CollectionUtils.isNotEmpty(ticketDataList)) {
            log.info("开始更新黑猫工单数据,数量: {}", ticketDataList.size());
            for (TicketData ticketData : ticketDataList) {
                try {
                    updateMiaoDaTicketCore(ticketData.getTicketBusinessKey(), ticketData.getExtend4());
                } catch (Exception e) {
                    log.error("更新喵达工单失败 updateMiaoDaTicketJob ticketDataId {} error {}", ticketData.getId(), e.getMessage());
                }
            }
        }
    }

    public void updateMiaoDaTicketSingle(String ticketDataId) {
        try {
            TicketData ticketData = ticketDataService.lambdaQuery()
                    .isNull(TicketData::getDeleteTime)
                    .eq(TicketData::getId, ticketDataId)
                    .eq(TicketData::getTicketStatus, TicketDataStatusEnum.APPLYING)
                    .oneOpt().orElse(null);
            if (ticketData != null) {
                updateMiaoDaTicketCore(ticketData.getTicketBusinessKey(), ticketData.getExtend4());
            }
        } catch (Exception ex) {
            log.error("更新喵达工单失败 updateMiaoDaTicketSingle ticketDataId: {} error {}", ticketDataId, ex.getMessage());
        }
    }

    @Override
    public Response<String> dispatchMiaoDaTicketCallBack(String sign, String ticketEventTag, String ticketDataId) {
        JSONObject callBackJson = JSONObject.parseObject(ticketEventTag);
        if (callBackJson == null) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "解析工单异常");
        }
        String executorType = callBackJson.getString("executor_type");
        String accountType = callBackJson.getString("account_type");
        List<String> accountIdList = callBackJson.getList("executor_id_list", String.class);
        if (StringUtils.isAnyEmpty(executorType, accountType) || CollectionUtils.isEmpty(accountIdList)) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "参数缺失:" + executorType + accountType + accountIdList);
        }
        TicketData ticketData = ticketDataService.lambdaQuery()
                .isNull(TicketData::getDeleteTime)
                .eq(TicketData::getId, ticketDataId).oneOpt().orElse(null);
        if (ticketData == null) {
            log.error("对应id:{}工单数据不存在", ticketDataId);
            return new Response<>(String.format("对应id:%s工单数据不存在", ticketDataId), BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("对应id:%s工单数据不存在", ticketDataId));
        }
        List<TicketFlowNodeExecutorData> executorDataList = ticketFlowNodeExecutorDataService.lambdaQuery()
                .isNull(TicketFlowNodeExecutorData::getDeleteTime)
                .eq(TicketFlowNodeExecutorData::getTicketDataId, ticketDataId)
                .list();
        if (CollectionUtils.isNotEmpty(executorDataList)) {
            List<AccountInfo> accountInfoList = null;
            if (ExecutorTypeEnum.APPLY_MEMBER_LIST.getCode().equals(executorType)) {
                accountInfoList = ticketAccountMappingService.getAccountInfoByAccountIdAndType(accountIdList, accountType);
            }
            if (ExecutorTypeEnum.APPLY_GROUP.getCode().equals(executorType)) {
                List<TicketExecutorGroup> ticketExecutorGroupList = ticketExecutorGroupService.lambdaQuery()
                        .select(TicketExecutorGroup::getAccountInfo)
                        .in(TicketExecutorGroup::getId, accountIdList)
                        .isNull(TicketExecutorGroup::getDeleteTime)
                        .list();
                List<String> accountInfoStrList = ticketExecutorGroupList.stream()
                        .filter(it -> StringUtils.isNotEmpty(it.getAccountInfo()))
                        .map(it -> it.getAccountInfo()).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(accountInfoStrList)) {
                    accountInfoList = AccountInfo.ToAccountInfoList(accountInfoStrList);
                }
            }
            if (CollectionUtils.isNotEmpty(accountInfoList)) {
                for (TicketFlowNodeExecutorData executorData : executorDataList) {
                    if (executorData.getExecutorList() != null && executorData.getExecutorList().contains("派单人")) {
                        executorData.setExecutorList(AccountInfo.ToAccountInfoListStr(accountInfoList));
                        boolean updateRes = ticketFlowNodeExecutorDataService.lambdaUpdate()
                                .isNull(TicketFlowNodeExecutorData::getDeleteTime)
                                .eq(TicketFlowNodeExecutorData::getId, executorData.getId())
                                .set(TicketFlowNodeExecutorData::getExecutorList, executorData.getExecutorList())
                                .update();
                        if (updateRes == false) {
                            log.error("派单失败,更新工单失败,审批人id:{}", executorData.getId());
                        }
                    }
                }
            }
        }
        return new Response<>("人员派单成功", BizResponseEnums.SUCCESS, "人员派单成功");
    }

    @Override
    public Response<String> replyMiaoDaTicketCallBack(String sign, String ticketEventTag, String ticketDataId) {
        try {
            Response<String> snResp = getSnString(ticketDataId);
            if (!snResp.isSuccess()) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单数据查询失败:" + snResp.getMsg());
            }
            String sn = snResp.getData();
            String hide_content = "1";//默认隐藏
            String hide_attach = "1";//默认隐藏
            String commentStr = null;
            List<String> images = new ArrayList<>();
            List<String> videos = new ArrayList<>();

            JSONObject callBackJson = JSONObject.parseObject(ticketEventTag);
            JSONObject commentInfo = callBackJson.getJSONObject("detail_opinion");
            if (commentInfo == null) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "解析处理意见异常");
            }
            commentStr = commentInfo.getString("commentStrInfo");
            JSONArray commentFiles = commentInfo.getJSONArray("commentFileInfo");
            List<String> commentTags = commentInfo.getList("commentTagInfo", String.class);
            if (StringUtils.isEmpty(commentStr)) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "回复喵达失败,回复内容不能为空");
            }
            if (CollectionUtils.isNotEmpty(commentFiles)) {
                for (int i = 0; i < commentFiles.size(); i++) {
                    JSONObject commentFile = commentFiles.getJSONObject(i);
                    String url = commentFile.getString("url");
                    if (StringUtils.isEmpty(url)) {
                        return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "回复喵达失败,附件url不能为空");
                    }
                    if (url.contains(".jpg") || url.contains(".png") || url.contains(".jpeg")) {
                        images.add(UrlSignUtil.urlSignBuilder().url(url).isPublic(true).getSignUrl());
                    } else if (url.contains(".mp4") || url.contains(".mp3")) {
                        videos.add(UrlSignUtil.urlSignBuilder().url(url).isPublic(true).getSignUrl());
                    } else {
                        return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "回复喵达失败,回复附件格式不支持:" + url);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(commentTags)) {
                if (commentTags.contains("公开内容（选填）")) {
                    hide_content = "0";
                }
                if (commentTags.contains("隐藏内容（选填）")) {
                    hide_content = "1";
                }
                if (commentTags.contains("公开附件（选填）")) {
                    hide_attach = "0";
                }
                if (commentTags.contains("隐藏附件（选填）")) {
                    hide_attach = "1";
                }
            }
            replyMiaoDaTicketCore(sn, commentStr, hide_attach, hide_content, images, videos);
        } catch (Exception e) {
            log.error("回复喵达失败", e);
            return Response.error(BizResponseEnums.SYSTEM_ERROR, StringUtils.isNotEmpty(e.getMessage()) ? e.getMessage() : "回复喵达失败");
        }
        HashMap<String, String> ret = new HashMap<>();
        ret.put("commentStrInfo", "回复喵达成功");
        ret.put("commentVisible", "false");
        updateMiaoDaTicketSingle(ticketDataId);
        return new Response("回复喵达成功", BizResponseEnums.SUCCESS, JSONObject.toJSONString(ret));
    }

    @Override
    public Response<String> autoReplyMiaoDaTicketCallBack(String sign, String ticketEventTag, String ticketDataId) {
        try {
            Response<String> snResp = getSnString(ticketDataId);
            if (!snResp.isSuccess()) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单数据查询失败:" + snResp.getMsg());
            }
            String sn = snResp.getData();
            String hide_attach = "1";
            String commentStr =
                    "您好，非常抱歉给您造成困扰，为了更快处理您的问题，您可以在服务时间9:00-21:00\n" +
                            "联系在线客服:关注省呗官方微信【小省在线】(ID:shengbei01)。\n" +
                            "联系电话客服:拨打4000001564热线。";
            replyMiaoDaTicketCore(sn, commentStr, hide_attach, hide_attach, null, null);
        } catch (Exception e) {
            log.error("自动回复喵达失败", e);
            return Response.error(BizResponseEnums.SYSTEM_ERROR, StringUtils.isNotEmpty(e.getMessage()) ? e.getMessage() : "自动回复喵达失败");
        }
        return Response.success("自动回复喵达成功");
    }

    @Override
    public Response<String> appealMiaoDaTicketCallBack(String sign, String ticketEventTag, String ticketDataId) {
        try {
            Response<String> snResp = getSnString(ticketDataId);
            if (!snResp.isSuccess()) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单数据查询失败:" + snResp.getMsg());
            }
            String sn = snResp.getData();
            String commentStr = null;
            String type = "";
            String dup_sns = "";//重复投诉，代表所重复的投诉单号
            List<String> images = new ArrayList<>();
            List<String> videos = new ArrayList<>();

            JSONObject callBackJson = JSONObject.parseObject(ticketEventTag);
            JSONObject commentInfo = callBackJson.getJSONObject("detail_opinion");
            if (commentInfo == null) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "解析处理意见异常");
            }
            commentStr = commentInfo.getString("commentStrInfo");
            JSONArray commentFiles = commentInfo.getJSONArray("commentFileInfo");
            List<String> commentTags = commentInfo.getList("commentTagInfo", String.class);
            if (StringUtils.isEmpty(commentStr)) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "黑猫申诉失败,回复内容不能为空");
            }
            if (CollectionUtils.isEmpty(commentFiles)) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "黑猫申诉失败,附件不能为空");
            }
            for (int i = 0; i < commentFiles.size(); i++) {
                JSONObject commentFile = commentFiles.getJSONObject(i);
                String url = commentFile.getString("url");
                if (StringUtils.isEmpty(url)) {
                    return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "黑猫申诉失败,附件url不能为空");
                }
                if (url.contains(".jpg") || url.contains(".png") || url.contains(".jpeg")) {
                    images.add(UrlSignUtil.urlSignBuilder().url(url).isPublic(true).getSignUrl());
                } else if (url.contains(".mp4") || url.contains(".mp3")) {
                    videos.add(UrlSignUtil.urlSignBuilder().url(url).isPublic(true).getSignUrl());
                } else {
                    return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "黑猫申诉失败,回复附件格式不支持:" + url);
                }
            }
            //1代表"重复投诉" 2代表"⾮本商户投诉"
            if (CollectionUtils.isNotEmpty(commentTags) && commentTags.contains("重复投诉（二选一）")) {
                type = "1";
                dup_sns = findFirstLongNumber(commentStr, 5);
                if (StringUtils.isEmpty(dup_sns)) {
                    return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "黑猫申诉失败，重复投诉类型需要回复重复的黑猫单号");
                }
            }
            if (CollectionUtils.isNotEmpty(commentTags) && commentTags.contains("非本商户投诉（二选一）")) {
                type = "2";
            }
            appealMiaoDaTicketCore(sn, commentStr, type, dup_sns, images, videos);
        } catch (Exception e) {
            log.error("黑猫申诉失败", e);
            return Response.error(BizResponseEnums.SYSTEM_ERROR, StringUtils.isNotEmpty(e.getMessage()) ? e.getMessage() : "黑猫申诉失败");
        }
        HashMap<String, String> ret = new HashMap<>();
        ret.put("commentStrInfo", "黑猫申诉成功");
        ret.put("commentVisible", "false");
        updateMiaoDaTicketSingle(ticketDataId);
        return new Response("黑猫申诉成功", BizResponseEnums.SUCCESS, JSONObject.toJSONString(ret));
    }

    @Override
    public Response<String> completeMiaoDaTicketCallBack(String sign, String ticketEventTag, String ticketDataId) {
        try {
            Response<String> snResp = getSnString(ticketDataId);
            if (!snResp.isSuccess()) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单数据查询失败:" + snResp.getMsg());
            }
            String sn = snResp.getData();
            String hide_attach = "1";//1：默认隐藏
            String reason = null;
            String solution = null;
            List<String> images = new ArrayList<>();
            List<String> videos = new ArrayList<>();

            JSONObject callBackJson = JSONObject.parseObject(ticketEventTag);
            if (StringUtils.isEmpty(callBackJson.getString("detail_opinion"))) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "黑猫结案申请失败,回复内容不能为空");
            }
            if (!callBackJson.getString("detail_opinion").startsWith("{")) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "黑猫结案申请失败,回复内容格式不正确");
            }
            JSONObject commentInfo = callBackJson.getJSONObject("detail_opinion");
            if (commentInfo == null) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "解析处理意见异常:" + snResp.getMsg());
            }
            solution = commentInfo.getString("commentStrInfo");
            JSONArray commentFiles = commentInfo.getJSONArray("commentFileInfo");
            List<String> commentTags = commentInfo.getList("commentTagInfo", String.class);
            if (StringUtils.isEmpty(solution)) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "黑猫结案申请失败,回复内容不能为空");
            }
            if (CollectionUtils.isNotEmpty(commentTags)) {
                if (commentTags.contains("已与用户沟通并达成一致（三选一）")) {
                    reason = "1";
                }
                if (commentTags.contains("联系不上用户（三选一）")) {
                    reason = "2";
                }
                if (commentTags.contains("最终解决方案（三选一）")) {
                    reason = "3";
                }
            } else {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "黑猫结案申请失败,结案类型不能为空");
            }
            if (CollectionUtils.isEmpty(commentFiles)) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "黑猫结案申请失败,附件不能为空");
            }
            if (CollectionUtils.isNotEmpty(commentFiles)) {
                if (commentTags.contains("公开附件（选填）")) {
                    hide_attach = "0";
                }
                if (commentTags.contains("隐藏附件（选填）")) {
                    hide_attach = "1";
                }
                for (int i = 0; i < commentFiles.size(); i++) {
                    JSONObject commentFile = commentFiles.getJSONObject(i);
                    String url = commentFile.getString("url");
                    if (StringUtils.isEmpty(url)) {
                        return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "黑猫结案申请失败,附件url不能为空");
                    }
                    if (url.contains(".jpg") || url.contains(".png") || url.contains(".jpeg")) {
                        images.add(UrlSignUtil.urlSignBuilder().url(url).isPublic(true).getSignUrl());
                    } else if (url.contains(".mp4") || url.contains(".mp3")) {
                        videos.add(UrlSignUtil.urlSignBuilder().url(url).isPublic(true).getSignUrl());
                    } else {
                        return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "黑猫结案申请失败,附件格式不支持:" + url);
                    }
                }
            } else {
                //没文件，就公开附件
                hide_attach = "0";
            }
            completeMiaoDaTicketCore(sn, reason, solution, hide_attach, images, videos);
        } catch (Exception e) {
            log.error("黑猫结案申请失败", e);
            return Response.error(BizResponseEnums.SYSTEM_ERROR, StringUtils.isNotEmpty(e.getMessage()) ? e.getMessage() : "黑猫结案申请失败");
        }
        HashMap<String, String> ret = new HashMap<>();
        ret.put("commentStrInfo", "黑猫结案申请成功");
        ret.put("commentVisible", "false");
        updateMiaoDaTicketSingle(ticketDataId);
        return new Response("黑猫结案申请成功", BizResponseEnums.SUCCESS, JSONObject.toJSONString(ret));
    }

    @Override
    public Response<String> allMiaoDaTicketCallBack(String sign, String ticketEventTag, String ticketDataId) {
        JSONObject callBackJson = JSONObject.parseObject(ticketEventTag);
        if (callBackJson != null) {
            String detailDes = callBackJson.getString("detail_type_des");
            if (StringUtils.isNotEmpty(detailDes)) {
                if (detailDes.contains("申诉")) {
                    return appealMiaoDaTicketCallBack(sign, ticketEventTag, ticketDataId);
                }
                if (detailDes.contains("结案")) {
                    return completeMiaoDaTicketCallBack(sign, ticketEventTag, ticketDataId);
                }
                if (detailDes.contains("黑猫回复")) {
                    return replyMiaoDaTicketCallBack(sign, ticketEventTag, ticketDataId);
                }
                if (detailDes.equals("派单")) {
                    return dispatchMiaoDaTicketCallBack(sign, ticketEventTag, ticketDataId);
                }
            }
        }
        HashMap<String, String> ret = new HashMap<>();
        ret.put("commentStrInfo", "修改工单成功");
        ret.put("commentVisible", "false");
        return new Response("修改工单成功", BizResponseEnums.SUCCESS, JSONObject.toJSONString(ret));
    }

    private Response<String> getSnString(String ticketDataId) {
        Response<TicketDataDto> ticketDataDtoResponse = ticketDataService.selectFullTicketDataById(new ReqParam(ticketDataId));
        if (!ticketDataDtoResponse.isSuccess()) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单数据查询失败:" + ticketDataDtoResponse.getMsg());
        }
        TicketDataDto ticketDataDto = ticketDataDtoResponse.getData();
        List<TicketFormItemDataDto> formItemList = ticketDataDto.getTicketFormDataDto().getTicketFormItemDataDtoList();
        String sn = null;
        for (TicketFormItemDataDto formItemDataDto : formItemList) {
            if (StringUtils.isNotEmpty(formItemDataDto.getItemConfig())) {
                JSONObject configObj = JSONObject.parseObject(formItemDataDto.getItemConfig());
                if (configObj != null && "sn".equals(configObj.getString("itemCode"))) {
                    sn = formItemDataDto.getItemValue();
                    break;
                }
            }
        }
        if (StringUtils.isEmpty(sn)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单数据查询失败,未找到SN字段编号");
        }
        return Response.success(sn);
    }

    private String findFirstLongNumber(String str, int length) {
        // 使用正则表达式匹配长度超过5位的数字
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\d{" + length + ",}");
        java.util.regex.Matcher matcher = pattern.matcher(str);

        if (matcher.find()) {
            return matcher.group();
        }
        return null; // 如果没有找到符合条件的数字，返回null
    }
}
