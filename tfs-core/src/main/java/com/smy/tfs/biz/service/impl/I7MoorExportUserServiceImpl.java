package com.smy.tfs.biz.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.smy.ncs.service.export.cust.request.ExportCustomerInfoRequest;
import com.smy.ncs.service.export.cust.response.CustomerOverdueInfoResponse;
import com.smy.tfs.api.dto.TicketRemoteAccountDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.service.I7MoorExportUserService;
import com.smy.tfs.common.utils.DateUtils;
import com.smy.tfs.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.apidocs.annotations.ApiDoc;
import org.apache.dubbo.apidocs.annotations.ApiModule;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@Slf4j
@Component("rl7MoorExportUserService")
@org.apache.dubbo.config.annotation.Service
@ApiModule(value = "容联·七陌座席导出服务类", apiInterface = I7MoorExportUserService.class)
public class I7MoorExportUserServiceImpl implements I7MoorExportUserService {

    @Value("${rl7moor.account:}")
    private String account;

    @Value("${rl7moor.secret:}")
    private String secret;

    @Value("${rl7moor.host:}")
    private String host;

    @Override
    @ApiDoc(value = "导出座席列表", description = "导出座席座席列表")
    public List<TicketRemoteAccountDto> exportUserList() {
        List<TicketRemoteAccountDto> queryResult = new ArrayList<>();
        JSONObject jsonObject = callApi("/v20160818/account/getCCAgentsByAcc/", "{}");
        if (jsonObject != null && jsonObject.getIntValue("code") == 200) {
            Map<String, String> idToExtenMap = new HashMap<>();
            jsonObject.getJSONArray("data").forEach(innerJsonObject -> {
                JSONObject innerObj = (JSONObject) innerJsonObject;
                idToExtenMap.put(innerObj.getString("_id"), innerObj.getString("exten"));
            });
            jsonObject.getJSONArray("data").forEach(innerJsonObject -> {
                JSONObject innerObj = (JSONObject) innerJsonObject;
                TicketRemoteAccountDto ticketRemoteAccountDto = new TicketRemoteAccountDto();
                // 不用_id的原因：这里考虑使用exten（座席ID）作为用户ID，方便用户使用自己的座席ID提单
                ticketRemoteAccountDto.setUserId(innerObj.getString("exten"));
                ticketRemoteAccountDto.setUserName(innerObj.getString("displayName"));
                ticketRemoteAccountDto.setUserPhone(innerObj.getString("mobile"));
                ticketRemoteAccountDto.setUserEmail(innerObj.getString("email"));
                if (StringUtils.isNotEmpty(innerObj.getString("parentId"))) {
                    ticketRemoteAccountDto.setSuperiorId(idToExtenMap.get(innerObj.getString("parentId")));
                }
                queryResult.add(ticketRemoteAccountDto);
            });
        }
        return queryResult;
    }


    @Resource
    private com.smy.ncs.service.export.cust.ExportCustomerInfoService exportCustomerInfoService;

    /**
     * @param inputStr
     * @return
     */
    @Override
    public Response<LinkedHashMap> getNCSNCLJ(String inputStr) {
        JSONObject output = new JSONObject();
        JSONObject jsonObject = JSONObject.parseObject(inputStr);
        try {
            if (jsonObject != null && jsonObject.containsKey("ItemValue")) {
                String customerNo = jsonObject.getString("ItemValue");
                ExportCustomerInfoRequest exportCustomerInfoRequest = new ExportCustomerInfoRequest();
                exportCustomerInfoRequest.setCustomerNo(customerNo);
                CustomerOverdueInfoResponse customerOverdueInfoResponse = exportCustomerInfoService.queryOverdueInfo(exportCustomerInfoRequest);
                if (customerOverdueInfoResponse != null) {
                    if (StringUtils.isNotEmpty(customerOverdueInfoResponse.getCUserId())) {
                        output.put("催员ID", customerOverdueInfoResponse.getCUserId());
                    }
                    if (CollectionUtils.isNotEmpty(customerOverdueInfoResponse.getManageIds())) {
                        output.put("催员组长ID", customerOverdueInfoResponse.getManageIds().toString());
                    }
                    return new Response<>(output, BizResponseEnums.SUCCESS, "成功");
                }
            }
            return new Response<>(output, BizResponseEnums.SYSTEM_ERROR, "数据转换失败");
        } catch (Exception e) {
            log.error("获取催收催员异常，原因：{}", e);
            return new Response<>(output, BizResponseEnums.SYSTEM_ERROR, "数据转换异常");
        }
    }

    public JSONObject callApi(String interfacePath, String body) {
        String time = DateUtils.dateTimeNow();
        String sig = md5(account + secret + time);
        String url = host + interfacePath + account + "?sig=" + sig;
        String auth = base64(account + ":" + time);
        HttpClientBuilder builder = HttpClientBuilder.create();
        CloseableHttpClient client = builder.build();
        HttpPost post = new HttpPost(url);
        post.addHeader("Accept", "application/json");
        post.addHeader("Content-Type", "application/json;charset=utf-8");
        post.addHeader("Authorization", auth);
        StringEntity requestEntity = new StringEntity(body, "UTF-8");
        post.setEntity(requestEntity);
        CloseableHttpResponse response = null;
        try {
            response = client.execute(post);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "utf8");
            log.info("call 7moor api result: " + result);
            return JSONObject.parseObject(result);
        } catch (Exception e) {
            log.error("获取容联·七陌域控座席信息列表失败，原因：", e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("关闭容联·七陌域控座席信息接口连接失败，原因：", e);
                }
            }
        }
        return null;
    }

    public static String md5(String text) {
        return DigestUtils.md5DigestAsHex(text.getBytes()).toUpperCase();
    }

    public static String base64(String text) {
        Base64 base64 = new Base64();
        byte[] b = base64.encode(text.getBytes());
        return new String(b);
    }
}
