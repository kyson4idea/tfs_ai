package com.smy.tfs.biz.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSONObject;
import com.smy.tfs.api.dbo.TicketData;
import com.smy.tfs.api.dbo.TicketFormItemData;
import com.smy.tfs.api.dto.FlashcatAlertCallbackDto;
import com.smy.tfs.api.dto.FlashcatAlertTicketStatusDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.dynamic.TicketDataStdDto;
import com.smy.tfs.api.dto.dynamic.TicketFormItemStdDto;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.enums.FormItemTypeEnum;
import com.smy.tfs.api.enums.MonitorTypeEnum;
import com.smy.tfs.api.enums.TicketDataStatusEnum;
import com.smy.tfs.api.service.IFlashcatAlertService;
import com.smy.tfs.api.service.ITicketDataService;
import com.smy.tfs.api.service.ITicketFormItemDataService;
import com.smy.tfs.common.utils.DateUtils;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.common.utils.adapter.AlertTicketItems;
import com.smy.tfs.common.utils.adapter.FlashcatAlertTicketItems;
import com.smy.tfs.common.utils.adapter.RhAlertTicketItems;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

// TODO 由于工单系统没有接口设配器，所以先写死（根据线上环境配置的模块信息，在代码里面写死），后面最好能有设配器处理这种场景

@Slf4j
@Service
public class FlashAlertServiceImpl implements IFlashcatAlertService {

    @Value("${alert.notifyUrl:}")
    private String notifyUrl;

    @Resource
    private ITicketDataService ticketDataService;

    @Resource
    private ITicketFormItemDataService ticketFormItemDataService;

    @Resource
    private FlashcatAlertTicketItems flashcatAlertTicketItems;

    @Resource
    private RhAlertTicketItems rhAlertTicketItems;

    /**
     * 告警工单会调通知
     */
    private void notifyTicketData(String botKey, String ticketId, String alertId) {
        if (StringUtils.isEmpty(botKey)) {
            log.warn("未配置企微机器人Key，请检查请求参数");
            return;
        }

        if (StringUtils.isEmpty(notifyUrl)) {
            log.warn("未配置告警工单通知回调，请检查配置中心相关配置");
            return;
        }
        JSONObject body = JSONObject.of("ticket_id", ticketId, "alert_id", alertId, "bot_key", botKey);
        HttpClientBuilder builder = HttpClientBuilder.create();
        CloseableHttpClient client = builder.build();
        HttpPost post = new HttpPost(notifyUrl);
        post.addHeader("Accept", "application/json");
        post.addHeader("Content-Type","application/json;charset=utf-8");
        StringEntity requestEntity = new StringEntity(body.toString(),"UTF-8");
        post.setEntity(requestEntity);
        CloseableHttpResponse response = null;
        try {
            response = client.execute(post);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity,"utf8");
            log.info("call flashcat screen api result: " + result);
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (!Objects.equals(jsonObject.getString("code"), "OK")) {
                log.error("调用大屏接口【{}】失败, 结果：{}", notifyUrl, jsonObject);
                return;
            }
            log.info("调用大屏接口【{}】成功, 结果：{}", notifyUrl, jsonObject);
        } catch (Exception ex) {
            log.error("调用大屏接口【{}】失败,配置内容：{}, message：{}", notifyUrl, body.toJSONString(), ex.getMessage());
        }
    }

    /**
     * 告警与工单适配器
     */
    @Override
    public List<TicketFormItemStdDto> alertAdapter(AlertTicketItems alertTicketItems, FlashcatAlertCallbackDto flashcatAlertCallbackDto) {
        List<TicketFormItemStdDto> formItems = new ArrayList<>();
        formItems.add(new TicketFormItemStdDto(alertTicketItems.getRuleName(), flashcatAlertCallbackDto.getRuleName()));
        formItems.add(new TicketFormItemStdDto(alertTicketItems.getTriggerTime(), DateUtils.secondsToTimestamp(flashcatAlertCallbackDto.getTriggerTime())));
        formItems.add(new TicketFormItemStdDto(alertTicketItems.getTriggerValue(), flashcatAlertCallbackDto.getTriggerValue()));
        formItems.add(new TicketFormItemStdDto(alertTicketItems.getTargetIdent(), flashcatAlertCallbackDto.getTargetIdent()));
        formItems.add(new TicketFormItemStdDto(alertTicketItems.getGroupName(), flashcatAlertCallbackDto.getGroupName()));
        formItems.add(new TicketFormItemStdDto(alertTicketItems.getNotifyCurNumber(), flashcatAlertCallbackDto.getNotifyCurNumber().toString()));
        formItems.add(new TicketFormItemStdDto(alertTicketItems.getRuleId(), flashcatAlertCallbackDto.getRuleId()));
        formItems.add(new TicketFormItemStdDto(alertTicketItems.getFirstTriggerTime(), DateUtils.secondsToTimestamp(flashcatAlertCallbackDto.getFirstTriggerTime())));
        if (!Objects.equals(flashcatAlertCallbackDto.getRuleProd(), MonitorTypeEnum.FIREMAP.getCode())) {
            formItems.add(new TicketFormItemStdDto(alertTicketItems.getRuleNote(), flashcatAlertCallbackDto.getRuleNote()));
        }
        String promQL = flashcatAlertCallbackDto.getPromQL();
        String cardId = "";
        String cardName = "";
        switch (MonitorTypeEnum.getEnumByCode(flashcatAlertCallbackDto.getRuleProd())) {
            case METRIC:
                formItems.add(new TicketFormItemStdDto(alertTicketItems.getRuleProd(), MonitorTypeEnum.METRIC.getDesc()));
                break;
            case HOST:
                formItems.add(new TicketFormItemStdDto(alertTicketItems.getRuleProd(), MonitorTypeEnum.HOST.getDesc()));
                break;
            case LOG:
                formItems.add(new TicketFormItemStdDto(alertTicketItems.getRuleProd(), MonitorTypeEnum.LOG.getDesc()));
                break;
            case ANOMALY:
                formItems.add(new TicketFormItemStdDto(alertTicketItems.getRuleProd(), MonitorTypeEnum.ANOMALY.getDesc()));
                break;
            case FIREMAP:
                formItems.add(new TicketFormItemStdDto(alertTicketItems.getRuleProd(), MonitorTypeEnum.FIREMAP.getDesc()));
                promQL = "";
                JSONObject tagsMap = flashcatAlertCallbackDto.getTagsMap();
                cardId = Optional.ofNullable(tagsMap).map(map -> map.getString("card_id")).orElse("");
                cardName = Optional.ofNullable(tagsMap).map(map -> map.getString("card")).orElse("");
                formItems.add(0, new TicketFormItemStdDto(alertTicketItems.getFireMapName(), flashcatAlertCallbackDto.getPromQL()));

                String ruleNote = flashcatAlertCallbackDto.getRuleNote();
                JSONObject ruleInfo = Optional.ofNullable(ruleNote)
                        .map(note -> {
                            try {
                                return JSONObject.parseObject(note);
                            } catch (Exception e) {
                                log.warn("Error parsing JSON: " + e.getMessage());
                                return null;
                            }
                        }).orElse(null);
                int numCritical = Optional.ofNullable(ruleInfo).map(info -> info.getInteger("num_of_critical")).orElse(1);
                String addrUrl = Optional.ofNullable(ruleInfo).map(info -> info.getString("detail_url")).orElse("");
                formItems.add(4, new TicketFormItemStdDto("告警条件", String.format("连续飘红%d个周期", numCritical)));
                formItems.add(new TicketFormItemStdDto("告警链接", addrUrl, FormItemTypeEnum.LINK.getCode()));
                break;
            case NORTHSTAR:
                formItems.add(new TicketFormItemStdDto(alertTicketItems.getRuleProd(), MonitorTypeEnum.NORTHSTAR.getDesc()));
                break;
            default:
                formItems.add(new TicketFormItemStdDto(alertTicketItems.getRuleProd(), "未知"));
        }
        formItems.add(new TicketFormItemStdDto(alertTicketItems.getPromQL(), promQL));
        formItems.add(new TicketFormItemStdDto(alertTicketItems.getCardId(), cardId));
        formItems.add(new TicketFormItemStdDto(alertTicketItems.getCardName(), cardName));
        formItems.add(new TicketFormItemStdDto(alertTicketItems.getIsRecovered(), flashcatAlertCallbackDto.getIsRecovered() ? "是" : "否"));
        formItems.add(new TicketFormItemStdDto(alertTicketItems.getTagsMap(), flashcatAlertCallbackDto.getTagsMap().toString()));
        return formItems;
    }

    /**
     * 根据情况执行创建｜更新工单（如果存在相同的工单【ruleId、targetIdent相同】正在处理中，则更新工单内容）
     *
     */
    @Override
    public Response<String> createOrUpdateTicket(AlertTicketItems alertTicketItems, FlashcatAlertCallbackDto flashcatAlertCallbackDto, List<TicketFormItemStdDto> ticketFormItemStdDto, String botKey) {
        // 默认查找当天的工单
        List<TicketData> ticketDataList = getDateAlertTicketDataList(LocalDate.now(), true);
        List<String> templateIdList = Arrays.asList(alertTicketItems.getRuleId(), alertTicketItems.getTargetIdent(), alertTicketItems.getCardId());
        for (TicketData ticketData: ticketDataList) {
            List<TicketFormItemData> ticketFormItemDataList = ticketFormItemDataService.lambdaQuery().eq(TicketFormItemData::getTicketDataId, ticketData.getId())
                    .in(TicketFormItemData::getTemplateId, templateIdList).list();
            if (!ticketFormItemDataList.isEmpty() && ticketFormItemDataList.size() <= 3) {
                int matchCount = getMatchCount(alertTicketItems, flashcatAlertCallbackDto, ticketFormItemDataList);
                if (matchCount == ticketFormItemDataList.size()) {
                    // 更新工单(triggerValue, triggerTime, notifyCurNumber, isRecovered)
                    updateAlertFormItemData(alertTicketItems, flashcatAlertCallbackDto, ticketData);
                    // 发送告警工单信息通知
                    notifyTicketData(botKey, ticketData.getId(), flashcatAlertCallbackDto.getAlertId());
                    return Response.success("工单已存在且内容已更新，请前往工单列表跟踪审批");
                }
            }
        }

        // 用户处理工单早于告警恢复通知，则不处理
        if (flashcatAlertCallbackDto.getIsRecovered()) {
            return Response.success("工单已存在且已恢复，无需创建工单");
        }

        // 未找到已存在的工单，创建工单
        TicketDataStdDto ticketDataStdDto = new TicketDataStdDto();
        Response<String> ticketApplyIdRes = ticketDataService.getTicketApplyId(alertTicketItems.getAppId());
        assert ticketApplyIdRes != null && Objects.equals(ticketApplyIdRes.getCode(), BizResponseEnums.SUCCESS.getCode());
        String ticketApplyId = ticketApplyIdRes.getData();

        ticketDataStdDto.setApplyId(ticketApplyId);
        ticketDataStdDto.setTicketTemplateId(alertTicketItems.getTicketTemplateId());
        ticketDataStdDto.setFormItems(ticketFormItemStdDto);

        ticketDataService.createTicket(ticketDataStdDto, "ldap","alert_callback", "告警账号");
        // 发送告警工单信息通知
        notifyTicketData(botKey, ticketApplyId, flashcatAlertCallbackDto.getAlertId());
        return Response.success("调用成功，请前往工单列表跟踪审批");
    }

    private Integer getMatchCount(AlertTicketItems alertTicketItems, FlashcatAlertCallbackDto flashcatAlertCallbackDto, List<TicketFormItemData> ticketFormItemDataList) {
        int matchCount = 0;
        for (TicketFormItemData ticketFormItemData: ticketFormItemDataList) {
            if (Objects.equals(ticketFormItemData.getTemplateId(), alertTicketItems.getRuleId())) {
                if (Objects.equals(ticketFormItemData.getItemValue(), flashcatAlertCallbackDto.getRuleId())) {
                    matchCount++;
                    continue;
                }
            }
            if (Objects.equals(ticketFormItemData.getTemplateId(), alertTicketItems.getTargetIdent())) {
                if (Objects.equals(ticketFormItemData.getItemValue(), flashcatAlertCallbackDto.getTargetIdent())) {
                    matchCount++;
                }
            }
            if (Objects.equals(ticketFormItemData.getTemplateId(), alertTicketItems.getCardId())) {
                JSONObject tagsMap = flashcatAlertCallbackDto.getTagsMap();
                String cardId = Optional.ofNullable(tagsMap).map(map -> map.getString("card_id")).orElse("");
                if (Objects.equals(ticketFormItemData.getItemValue(), cardId)) {
                    matchCount++;
                }
            }
        }
        return matchCount;
    }

    private void updateAlertFormItemData(AlertTicketItems alertTicketItems, FlashcatAlertCallbackDto flashcatAlertCallbackDto, TicketData ticketData) {
        Map<String, String> templateIdToFieldMapping = getStringStringMap(alertTicketItems);

        Map<String, Object> fieldValueMapping = new HashMap<>();
        fieldValueMapping.put("triggerValue", flashcatAlertCallbackDto.getTriggerValue());
        fieldValueMapping.put("notifyCurNumber", flashcatAlertCallbackDto.getNotifyCurNumber().toString());
        fieldValueMapping.put("isRecovered", flashcatAlertCallbackDto.getIsRecovered() ? "是" : "否");
        fieldValueMapping.put("ruleName", flashcatAlertCallbackDto.getRuleName());

        Map<String, Long> timeFieldMapping = new HashMap<>();
        timeFieldMapping.put("triggerTime", flashcatAlertCallbackDto.getTriggerTime());

        for (Map.Entry<String, String> entry : templateIdToFieldMapping.entrySet()) {
            String templateIdKey = entry.getKey();
            String field = entry.getValue();

            Object value = fieldValueMapping.getOrDefault(field, "");
            if (timeFieldMapping.containsKey(field)) {
                value = DateUtils.secondsToTimestamp(timeFieldMapping.get(field));
            }

            ticketFormItemDataService.lambdaUpdate()
                    .eq(TicketFormItemData::getTicketDataId, ticketData.getId())
                    .eq(TicketFormItemData::getTemplateId, templateIdKey)
                    .set(TicketFormItemData::getItemValue, String.valueOf(value))
                    .set(TicketFormItemData::getUpdateTime, new Date())
                    .update();
        }
    }

    private Map<String, String> getStringStringMap(AlertTicketItems alertTicketItems) {
        Map<String, String> templateIdToFieldMapping = new HashMap<>();
        templateIdToFieldMapping.put(alertTicketItems.getTriggerValue(), "triggerValue");
        templateIdToFieldMapping.put(alertTicketItems.getTriggerTime(), "triggerTime");
        templateIdToFieldMapping.put(alertTicketItems.getNotifyCurNumber(), "notifyCurNumber");
        templateIdToFieldMapping.put(alertTicketItems.getIsRecovered(), "isRecovered");
        templateIdToFieldMapping.put(alertTicketItems.getRuleName(), "ruleName");
        return templateIdToFieldMapping;
    }


    /**
     * 处理告警
     */
    @Override
    public Response<String> handleAlert(String botKey, FlashcatAlertCallbackDto flashcatAlertCallbackDto) {
        String[] groupNames = rhAlertTicketItems.getAlertGroupName().split(",");
        AlertTicketItems alertTicketItems = Arrays.asList(groupNames).contains(flashcatAlertCallbackDto.getGroupName()) ? rhAlertTicketItems : flashcatAlertTicketItems;
        List<TicketFormItemStdDto> ticketFormItemStdDtos = alertAdapter(alertTicketItems, flashcatAlertCallbackDto);
        return createOrUpdateTicket(alertTicketItems, flashcatAlertCallbackDto, ticketFormItemStdDtos, botKey);
    }

    @Override
    public Map<String, List<FlashcatAlertTicketStatusDto>> getLastAlertTicketsByRuleIds(List<String> ruleIds, String startDateStr, boolean applyingOnly) {
        // 默认查找当天的工单
        Map<String, List<FlashcatAlertTicketStatusDto>> resultMap = new HashMap<>();
        LocalDate startDate = startDateStr == null ? LocalDate.now() : LocalDate.parse(startDateStr);
        List<TicketData> ticketDataList = getDateAlertTicketDataList(startDate, applyingOnly);
        List<String> ticketDataIds = ticketDataList.stream().map(TicketData::getId).collect(Collectors.toList());
        if (ticketDataIds.isEmpty()) {
            return resultMap;
        }

        List<String> ruleTemplateIds = Arrays.asList(rhAlertTicketItems.getRuleId(), flashcatAlertTicketItems.getRuleId());
        List<TicketFormItemData> ruleTicketFormItemDataList = ticketFormItemDataService.lambdaQuery()
                .in(TicketFormItemData::getTicketDataId, ticketDataIds)
                .in(TicketFormItemData::getTemplateId, ruleTemplateIds)
                .in(TicketFormItemData::getItemValue, ruleIds)
                .select(TicketFormItemData::getTicketDataId, TicketFormItemData::getItemValue).list();
        Map<String, String> ruleItemValues = ruleTicketFormItemDataList
                .stream()
                .collect(Collectors.toMap(
                        TicketFormItemData::getTicketDataId,
                        TicketFormItemData::getItemValue,
                        (existing, replacement) -> existing));

        List<String> cardTemplateIds = Arrays.asList(rhAlertTicketItems.getCardId(), flashcatAlertTicketItems.getCardId());
        Map<String, String> cardItemValues = getTargetItemMap(ticketDataIds, cardTemplateIds);

        Map<String, String> firstTriggerItemValues = null;
        Map<String, String> curTriggerItemValues = null;
        if (applyingOnly) {
            List<String> fistTriggerTemplateIds = Arrays.asList(rhAlertTicketItems.getFirstTriggerTime(), flashcatAlertTicketItems.getFirstTriggerTime());
            firstTriggerItemValues = getTargetItemMap(ticketDataIds, fistTriggerTemplateIds);

            List<String> curTriggerTemplateIds = Arrays.asList(rhAlertTicketItems.getTriggerTime(), flashcatAlertTicketItems.getTriggerTime());
            curTriggerItemValues = getTargetItemMap(ticketDataIds, curTriggerTemplateIds);
        }

        for (TicketData ticketData: ticketDataList) {
            String tmpTicketDataId = ticketData.getId();

            if (!ruleItemValues.containsKey(tmpTicketDataId)) {
                continue;
            }

            String tmpRuleId = ruleItemValues.get(tmpTicketDataId);
            FlashcatAlertTicketStatusDto flashcatAlertTicketStatusDto = new FlashcatAlertTicketStatusDto();
            flashcatAlertTicketStatusDto.setTicketId(ticketData.getId());
            flashcatAlertTicketStatusDto.setTicketName(ticketData.getTicketName());
            flashcatAlertTicketStatusDto.setTicketStatus(ticketData.getTicketStatus().toString());
            flashcatAlertTicketStatusDto.setCreateTime(DateUtil.formatDateTime(ticketData.getCreateTime()));
            flashcatAlertTicketStatusDto.setLastUpdateTime(DateUtil.formatDateTime(ticketData.getUpdateTime()));
            flashcatAlertTicketStatusDto.setCurProcessor(ticketData.getCurrentDealUsers());
            flashcatAlertTicketStatusDto.setCurrentDoneUsers(ticketData.getCurrentDoneUsers());
            flashcatAlertTicketStatusDto.setRuleId(tmpRuleId);
            flashcatAlertTicketStatusDto.setCardId(cardItemValues.get(tmpTicketDataId));
            if (firstTriggerItemValues != null) {
                flashcatAlertTicketStatusDto.setFirstTriggerTime(firstTriggerItemValues.get(tmpTicketDataId));
            }
            if (curTriggerItemValues != null) {
                flashcatAlertTicketStatusDto.setLastTriggerTime(curTriggerItemValues.get(tmpTicketDataId));
            }

            if (!resultMap.containsKey(tmpRuleId)) {
                List<FlashcatAlertTicketStatusDto> flashcatAlertTicketStatusDtoList = new ArrayList<>();
                flashcatAlertTicketStatusDtoList.add(flashcatAlertTicketStatusDto);
                resultMap.put(tmpRuleId, flashcatAlertTicketStatusDtoList);
            } else {
                resultMap.get(tmpRuleId).add(flashcatAlertTicketStatusDto);
            }
        }

        return resultMap;
    }

    private Map<String, String> getTargetItemMap(List<String> ticketDataIds, List<String> templateIds) {
        List<TicketFormItemData> targetTicketFormItemDataList = ticketFormItemDataService.lambdaQuery()
                .in(TicketFormItemData::getTicketDataId, ticketDataIds)
                .in(TicketFormItemData::getTemplateId, templateIds)
                .select(TicketFormItemData::getTicketDataId, TicketFormItemData::getItemValue).list();
        return targetTicketFormItemDataList
                .stream()
                .collect(Collectors.toMap(
                        TicketFormItemData::getTicketDataId,
                        TicketFormItemData::getItemValue,
                        (existing, replacement) -> existing));
    }

    private List<TicketData> getDateAlertTicketDataList(LocalDate startDate, boolean applyingOnly) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(today, LocalTime.MAX);
        List<String> ticketTemplateIds = Arrays.asList(flashcatAlertTicketItems.getTicketTemplateId(), rhAlertTicketItems.getTicketTemplateId());
        if (applyingOnly) {
            return ticketDataService.lambdaQuery()
                    .in(TicketData::getTemplateId, ticketTemplateIds)
                    .eq(TicketData::getTicketStatus, TicketDataStatusEnum.APPLYING)
                    .between(TicketData::getCreateTime, startOfDay, endOfDay).orderByDesc(TicketData::getCreateTime).list();
        }
        return ticketDataService.lambdaQuery()
                .in(TicketData::getTemplateId, ticketTemplateIds)
                .between(TicketData::getCreateTime, startOfDay, endOfDay).orderByDesc(TicketData::getCreateTime).list();
    }
}
