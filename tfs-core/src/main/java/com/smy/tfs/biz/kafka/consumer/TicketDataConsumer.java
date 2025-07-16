package com.smy.tfs.biz.kafka.consumer;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.smy.tfs.api.dbo.ESTicketData;
import com.smy.tfs.api.dbo.TicketFormItemData;
import com.smy.tfs.api.service.ITicketFormItemDataService;
import com.smy.tfs.biz.component.TicketAppComponent;
import com.smy.tfs.biz.component.TicketCategoryComponent;
import com.smy.tfs.biz.component.TicketTemplateComponent;
import com.smy.tfs.biz.service.TicketDataESService;
import com.smy.tfs.biz.util.DateConverter;
import com.smy.tfs.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


@Slf4j
@Component
public class TicketDataConsumer {

    @Resource
    private TicketDataESService ticketDataESService;
    @Resource
    private TicketAppComponent ticketAppComponent;
    @Resource
    private TicketTemplateComponent ticketTemplateComponent;
    @Resource
    private TicketCategoryComponent ticketCategoryComponent;
    @Resource
    private ITicketFormItemDataService ticketFormItemDataService;

    @Value("${tfs.ticketData.flag:true}")
    private Boolean consumeTicketDataFlag;

    @Value("${es.ticket_data_info.index}")
    private String index;

    @KafkaListener(topics = {"${ticket.data.topic}"}, groupId = "tfs-ticketdata-group")
    public void consumeMessage(String records, Acknowledgment acknowledgment) {
        String id = "";
        try {
            if (!consumeTicketDataFlag) {
                log.info("不处理：工单表数据变更收到消息:{}", records);
                return;
            }
            log.info("开始处理：工单表数据变更收到消息:{}", records);
            records = "["+records+"]";
            JSONArray recordsJSONArray = JSONArray.parseArray(records);
            if (recordsJSONArray.isEmpty()) {
                log.info("工单表数据变更收到消息为空列表，{}",recordsJSONArray);
                return;
            }
            for (int i = 0; i < recordsJSONArray.size(); i++) {
                JSONObject recordJsonObject = recordsJSONArray.getJSONObject(i);
                if (!recordJsonObject.containsKey("data")) {
                    log.info("此条记录的data字段为空，{}",recordJsonObject.toJSONString());
                    continue;
                }
                JSONArray dataJSONArray = recordJsonObject.getJSONArray("data");
                if (CollectionUtils.isEmpty(dataJSONArray)) {
                    log.error("此条记录的data信息异常，{}",recordJsonObject.toJSONString());
                    continue;
                }
                for(int j = 0; j < dataJSONArray.size(); j++) {
                    JSONObject dataJSONObject = dataJSONArray.getJSONObject(j);
                    id = dataJSONObject.getString("id");
                    String deleteTime = dataJSONObject.getString("delete_time");
                    ConvertUtils.register(new DateConverter(), Date.class);
                    ESTicketData oldData = ticketDataESService.getById(index, id);
                    Long ts = 0L;
                    if (recordJsonObject.containsKey("ts") && null != recordJsonObject.getLong("ts")) {
                        ts = recordJsonObject.getLong("ts");
                        if (null != oldData && null != oldData.getTicket_data_ts() && oldData.getTicket_data_ts() > ts) {
                            log.info("此条记录({})的时间戳({})比旧记录时间戳({})小", id, ts, oldData.getTicket_data_ts());
                            continue;
                        }
                    }
                    //删除
                    if (StringUtils.isNotEmpty(deleteTime) && Objects.nonNull(oldData)) {
                        ticketDataESService.delete(index, id);
                        continue;
                    }
                    List<TicketFormItemData> ticketFormItemDataList = ticketFormItemDataService.lambdaQuery()
                            .eq(TicketFormItemData::getTicketDataId,id)
                            .eq(TicketFormItemData::getItemAdvancedSearch,"TRUE")
                            .isNull(TicketFormItemData::getDeleteTime)
                            .list();
                    String templateId = dataJSONObject.getString("template_id");
                    String beyondCategoryId = ticketCategoryComponent.getCategoryIdByTemplateId(templateId);
                    if (Objects.isNull(oldData)) {
                        //新增
                        ESTicketData newData = new ESTicketData(dataJSONObject, ticketFormItemDataList, ts, beyondCategoryId);
                        String appName = ticketAppComponent.getAppNameById(newData.getApp_id());
                        String templateName = ticketTemplateComponent.getTemplateNameById(newData.getTemplate_id());
                        String categoryName = ticketCategoryComponent.getCategoryNameByTemplateId(newData.getTemplate_id());
                        newData.setApp_name(appName);
                        newData.setTemplate_name(templateName);
                        newData.setBeyond_category_name(categoryName);
                        HashMap newAllTicketDataInfo = newData.getAll_ticket_data_info();
                        newAllTicketDataInfo.put("app_name", appName);
                        newAllTicketDataInfo.put("template_name", templateName);
                        newAllTicketDataInfo.put("beyond_category_name", categoryName);
                        newData.setAll_ticket_data_info(newAllTicketDataInfo);
                        ticketDataESService.create(index, newData);
                    } else {
                        //更新
                        HashMap oldAllTicketDataInfo = oldData.getAll_ticket_data_info();
                        ESTicketData newData = new ESTicketData(dataJSONObject, ticketFormItemDataList, ts, beyondCategoryId);
                        String appName = ticketAppComponent.getAppNameById(newData.getApp_id());
                        String templateName = ticketTemplateComponent.getTemplateNameById(newData.getTemplate_id());
                        String categoryName = ticketCategoryComponent.getCategoryNameByTemplateId(newData.getTemplate_id());
                        newData.setApp_name(appName);
                        newData.setTemplate_name(templateName);
                        newData.setBeyond_category_name(categoryName);
                        HashMap newAllTicketDataInfo = newData.getAll_ticket_data_info();
                        newAllTicketDataInfo.put("app_name", appName);
                        newAllTicketDataInfo.put("template_name", templateName);
                        newAllTicketDataInfo.put("beyond_category_name", categoryName);
                        //修改allTicketDataInfo字段:需要先把老数据的json字段取出来再用新数据json字段覆盖,这样原有的表单项字段就不会被覆盖。
                        oldAllTicketDataInfo.putAll(newAllTicketDataInfo);
                        //最新的AllTicketDataInfo
                        HashMap latestAllTicketDataInfo = oldAllTicketDataInfo;
                        newData.setAll_ticket_data_info(latestAllTicketDataInfo);
                        ticketDataESService.update(index, newData);
                    }
                }
            }
        } catch (Exception e) {
            log.error("处理工单表数据(id:{})变更信息异常,{}", id, e);
        } finally {
            acknowledgment.acknowledge();
        }
    }

}

