package com.smy.tfs.biz.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smy.tfs.api.dbo.*;
import com.smy.tfs.api.dto.TicketAnalysisDataDto;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.enums.TicketAnalysisDataTypeEnum;
import com.smy.tfs.api.enums.TicketDataStatusEnum;
import com.smy.tfs.api.service.*;
import com.smy.tfs.biz.mapper.TicketAnalysisDataMapper;
import com.smy.tfs.common.utils.DateUtils;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.common.utils.TimeConverter;
import com.smy.tfs.common.utils.adapter.FlashcatAlertTicketItems;
import com.smy.tfs.common.utils.adapter.RhAlertTicketItems;
import com.smy.tfs.common.utils.bean.BeanHelper;
import com.smy.tfs.common.utils.bean.BeanUtils;
import com.smy.tfs.common.utils.http.HttpUtils;
import com.smy.tfs.common.utils.notification.QwConfigComponent;
import com.smy.tfs.common.utils.notification.QwNotify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 应用维度分析数据 服务实现类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Service
public class TicketAnalysisDataServiceImpl extends ServiceImpl<TicketAnalysisDataMapper, TicketAnalysisData> implements ITicketAnalysisDataService {

    Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private TicketAnalysisDataMapper ticketAnalysisDataMapper;

    @Resource
    private ITicketAccountMappingService ticketAccountMappingService;

    @Resource
    private ITicketAppService ticketAppService;

    @Resource
    private FlashcatAlertTicketItems flashcatAlertTicketItems;

    @Resource
    private RhAlertTicketItems rhAlertTicketItems;

    @Resource
    private ITicketFormItemDataService ticketFormItemDataService;

    @Resource
    private ITicketDataService ticketDataService;

    @Resource
    private QwConfigComponent qwConfigComponent;

    @Override
    public Map<String, Integer> getOverview(String appId) {
        Map<String, Integer> result = new HashMap<>();

        // 获取工单数量
        Integer ticketCount = ticketAnalysisDataMapper.countTickets(appId);
        result.put("ticketCount", ticketCount);

        // 获取工单模版数量
        Integer templateCount = ticketAnalysisDataMapper.countTemplates(appId);
        result.put("templateCount", templateCount);

        // 获取提单用户数量
        Integer createUserCount = ticketAnalysisDataMapper.countCreateUsers(appId);
        result.put("createUserCount", createUserCount);

        return result;
    }

    @Override
    public List<TicketAnalysisDataDto.TicketBaseStatistic> getTimeRangeTicketBaseStatistics(TicketAnalysisDataDto.TicketAnalysisQuery ticketAnalysisQuery) {
        return ticketAnalysisDataMapper.getTimeRangeTicketBaseStatistics(ticketAnalysisQuery);
    }

    @Override
    public List<TicketAnalysisDataDto.TicketStatusCount> getTimeRangeTicketDoneCount(TicketAnalysisDataDto.TicketAnalysisQuery ticketAnalysisQuery) {
        return ticketAnalysisDataMapper.getTimeRangeTicketDoneCount(ticketAnalysisQuery);
    }

    @Override
    public List<TicketAnalysisDataDto.TicketAvgEfficiency> getTimeRangeTicketAvgEfficiency(TicketAnalysisDataDto.TicketAnalysisQuery ticketAnalysisQuery) {
        return ticketAnalysisDataMapper.getTimeRangeTicketAvgEfficiency(ticketAnalysisQuery);
    }

    @Override
    public List<TicketAnalysisDataDto.TicketExecutorCount> getTimeRangeTicketExecutorCount(TicketAnalysisDataDto.TicketAnalysisQuery ticketAnalysisQuery) {
        return ticketAnalysisDataMapper.getTimeRangeTicketExecutorCount(ticketAnalysisQuery);
    }

    @Override
    public List<TicketAnalysisDataDto.TicketTopXCreateBy> getTimeRangeTicketTopXCreateBy(TicketAnalysisDataDto.TicketAnalysisQuery ticketAnalysisQuery) {
        return ticketAnalysisDataMapper.getTimeRangeTicketTopXCreateBy(ticketAnalysisQuery);
    }

    @Override
    public List<TicketAnalysisDataDto> getPrevPeriodTicketAnalysisData(TicketAnalysisDataDto.TicketAnalysisQuery ticketAnalysisQuery) {
        TicketAnalysisDataDto.TicketAnalysisQuery prevTicketAnalysisQuery = TicketAnalysisDataDto.TicketAnalysisQuery.copyAsPrevPeriod(ticketAnalysisQuery);
        QueryWrapper<TicketAnalysisData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("DATE(start_date)", DateUtils.parseDateToStr("yyyy-MM-dd", new Date(prevTicketAnalysisQuery.getStartTimestamp().getTime())))
                .eq("DATE(end_date)", DateUtils.parseDateToStr("yyyy-MM-dd", new Date(prevTicketAnalysisQuery.getEndTimestamp().getTime())))
                .eq("category", prevTicketAnalysisQuery.getCategory());
        if (prevTicketAnalysisQuery.getAppId() != null) {
            queryWrapper.eq("app_id", prevTicketAnalysisQuery.getAppId());
        }
        queryWrapper.groupBy("app_id");
        List<TicketAnalysisData> prevTicketAnalysisDataList = ticketAnalysisDataMapper.selectList(queryWrapper);
        return BeanHelper.copyList(prevTicketAnalysisDataList, TicketAnalysisDataDto.class);
    }

    public Map<String, TicketAnalysisDataDto> getTimeRangeTicketAnalysisData(TicketAnalysisDataDto.TicketAnalysisQuery ticketAnalysisQuery) {
        List<TicketApp> ticketAppList = ticketAppService.lambdaQuery().isNull(TicketApp::getDeleteTime).list();
        Map<String, TicketAnalysisDataDto> aggregatedDataMap = new HashMap<>();
        ticketAppList.forEach(ticketApp -> {
            TicketAnalysisDataDto ticketAnalysisDataDto = new TicketAnalysisDataDto();
            ticketAnalysisDataDto.setAppId(ticketApp.getId());
            ticketAnalysisDataDto.setStartDate(new Date(ticketAnalysisQuery.getStartTimestamp().getTime()));
            ticketAnalysisDataDto.setEndDate(new Date(ticketAnalysisQuery.getEndTimestamp().getTime()));
            ticketAnalysisDataDto.setCategory(ticketAnalysisQuery.getCategory());
            aggregatedDataMap.put(ticketApp.getId(), ticketAnalysisDataDto);
        });
        List<TicketAnalysisDataDto.TicketBaseStatistic> ticketBaseStatisticList = this.getTimeRangeTicketBaseStatistics(ticketAnalysisQuery);
        ticketBaseStatisticList.forEach(ticketBaseStatistic -> {
            TicketAnalysisDataDto ticketAnalysisDataDto = aggregatedDataMap.get(ticketBaseStatistic.getAppId());
            if (ticketAnalysisDataDto != null) {
                ticketAnalysisDataDto.setApplyCount(ticketBaseStatistic.getApplyCount());
                ticketAnalysisDataDto.setApplyingCount(ticketBaseStatistic.getApplyingCount());
                ticketAnalysisDataDto.setWithdrawCount(ticketBaseStatistic.getWithdrawCount());
                ticketAnalysisDataDto.setRejectCount(ticketBaseStatistic.getRejectCount());
                aggregatedDataMap.put(ticketBaseStatistic.getAppId(), ticketAnalysisDataDto);
            }
        });

        List<TicketAnalysisDataDto.TicketStatusCount> ticketDoneCountList = this.getTimeRangeTicketDoneCount(ticketAnalysisQuery);
        ticketDoneCountList.forEach(ticketDoneCount -> {
            TicketAnalysisDataDto ticketAnalysisDataDto = aggregatedDataMap.get(ticketDoneCount.getAppId());
            if (ticketAnalysisDataDto != null) {
                ticketAnalysisDataDto.setDoneCount(ticketDoneCount.getStatusCount());
            }
        });

        List<TicketAnalysisDataDto.TicketAvgEfficiency> ticketAvgEfficiencyList = this.getTimeRangeTicketAvgEfficiency(ticketAnalysisQuery);
        ticketAvgEfficiencyList.forEach(ticketAvgEfficiency -> {
            TicketAnalysisDataDto ticketAnalysisDataDto = aggregatedDataMap.get(ticketAvgEfficiency.getAppId());
            if (ticketAnalysisDataDto != null) {
                ticketAnalysisDataDto.setTicketAvgEfficiency(ticketAvgEfficiency.getTicketAvgEfficiency().intValue());
            }
        });

        List<TicketAnalysisDataDto.TicketExecutorCount> ticketExecutorCountList = this.getTimeRangeTicketExecutorCount(ticketAnalysisQuery);
        ticketExecutorCountList.forEach(ticketExecutorCount -> {
            TicketAnalysisDataDto ticketAnalysisDataDto = aggregatedDataMap.get(ticketExecutorCount.getAppId());
            if (ticketAnalysisDataDto != null) {
                ticketAnalysisDataDto.setExecutorCount(ticketExecutorCount.getExecutorCount());
            }
        });

        List<TicketAnalysisDataDto.TicketTopXCreateBy> ticketTopXCreateByList = this.getTimeRangeTicketTopXCreateBy(ticketAnalysisQuery);
        ticketTopXCreateByList.forEach(ticketTopXCreateBy -> {
            TicketAnalysisDataDto ticketAnalysisDataDto = aggregatedDataMap.get(ticketTopXCreateBy.getAppId());
            if (ticketAnalysisDataDto != null) {
                String tmpBeforeTop3CreateBy = Optional.ofNullable(ticketAnalysisDataDto.getTop3CreateBy()).orElse("");
                String currentTop3CreateBy = ticketTopXCreateBy.getCreateBy();
                if (StringUtils.isNotEmpty(currentTop3CreateBy)) {
                    try {
                        AccountInfo accountInfo = JSON.parseObject(currentTop3CreateBy, AccountInfo.class);
                        if (accountInfo != null) {
                            currentTop3CreateBy = String.format("%s:%s-%s", accountInfo.getAccountType(), accountInfo.getAccountId(), accountInfo.getAccountName());
                        } else {
                            logger.warn("解析createBy得到的AccountInfo对象为null");
                        }
                    } catch (JSONException e) {
                        // 如果createBy不是有效的JSON对象，记录错误
                        logger.error("解析createBy 为AccountInfo对象时发生错误: {}", e.getMessage());
                    }
                }

                if (StringUtils.isNotEmpty(currentTop3CreateBy)) {
                    ticketAnalysisDataDto.setTop3CreateBy(tmpBeforeTop3CreateBy.isEmpty() ? currentTop3CreateBy : String.format("%s;%s", tmpBeforeTop3CreateBy, currentTop3CreateBy));
                }
            }
        });

        List<TicketAnalysisDataDto> prevPeriodTicketAnalysisDataList = this.getPrevPeriodTicketAnalysisData(ticketAnalysisQuery);
        prevPeriodTicketAnalysisDataList.forEach(prevPeriodTicketAnalysisData -> {
            TicketAnalysisDataDto ticketAnalysisDataDto = aggregatedDataMap.get(prevPeriodTicketAnalysisData.getAppId());
            if (ticketAnalysisDataDto != null) {
                ticketAnalysisDataDto.setLastTop3CreateBy(prevPeriodTicketAnalysisData.getTop3CreateBy());
                ticketAnalysisDataDto.setLastCreateByCount(prevPeriodTicketAnalysisData.getCreateByCount());
                ticketAnalysisDataDto.setLastExecutorCount(prevPeriodTicketAnalysisData.getExecutorCount());
                ticketAnalysisDataDto.setLastApplyCount(prevPeriodTicketAnalysisData.getApplyCount());
                ticketAnalysisDataDto.setLastDoneCount(prevPeriodTicketAnalysisData.getDoneCount());
                ticketAnalysisDataDto.setLastApplyingCount(prevPeriodTicketAnalysisData.getApplyingCount());
                ticketAnalysisDataDto.setLastWithdrawCount(prevPeriodTicketAnalysisData.getWithdrawCount());
                ticketAnalysisDataDto.setLastRejectCount(prevPeriodTicketAnalysisData.getLastRejectCount());
                ticketAnalysisDataDto.setLastTicketAvgEfficiency(prevPeriodTicketAnalysisData.getTicketAvgEfficiency());
            }
        });

        return aggregatedDataMap;
    }


    /**
     * 实时获取当天工单数据（如果应用下没有产生任何工单数据，map不会有该应用信息）
     */
    public Map<String, TicketAnalysisDataDto> getRealTimeTicketAnalysisData() {
        TicketAnalysisDataDto.TicketAnalysisQuery ticketAnalysisQuery = new TicketAnalysisDataDto.TicketAnalysisQuery();
        LocalDate currentDate = LocalDate.now();
        ticketAnalysisQuery.setStartTimestamp(DateUtils.getDateBeforeStart(currentDate, 0));
        ticketAnalysisQuery.setEndTimestamp(DateUtils.getDateBeforeEnd(currentDate, 0));
        ticketAnalysisQuery.setCategory(TicketAnalysisDataTypeEnum.DAY.getCode());
        ticketAnalysisQuery.setTopX(3);
        return this.getTimeRangeTicketAnalysisData(ticketAnalysisQuery);
    }

    public void saveOrUpdate(TicketAnalysisData ticketAnalysisData, boolean updateStrategy) {
        // 根据app_id start_data(日期), end_data(日期）,category 查找是否存在记录，并按照更新策略决定是否更新
        QueryWrapper<TicketAnalysisData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", ticketAnalysisData.getAppId())
                .eq("DATE(start_date)", DateUtils.parseDateToStr("yyyy-MM-dd", ticketAnalysisData.getStartDate()))
                .eq("DATE(end_date)", DateUtils.parseDateToStr("yyyy-MM-dd", ticketAnalysisData.getEndDate()))
                .eq("category", ticketAnalysisData.getCategory())
                .last("LIMIT 1");
        TicketAnalysisData dbTicketAnalysisData = ticketAnalysisDataMapper.selectOne(queryWrapper);
        if (dbTicketAnalysisData == null) {
            this.save(ticketAnalysisData);
            return;
        }
        BeanUtils.copyProperties(ticketAnalysisData, dbTicketAnalysisData, "id", "createBy", "createTime");
        if (updateStrategy) {
            dbTicketAnalysisData.setUpdateTime(new Date());
            this.updateById(dbTicketAnalysisData);
        }
    }


    public List<TicketAnalysisData> saveTimeRangeTicketData(Timestamp startTimestamp, Timestamp endTimestamp, String category, boolean updateStrategy) {
        TicketAnalysisDataDto.TicketAnalysisQuery ticketAnalysisQuery = new TicketAnalysisDataDto.TicketAnalysisQuery();
        ticketAnalysisQuery.setStartTimestamp(startTimestamp);
        ticketAnalysisQuery.setEndTimestamp(endTimestamp);
        ticketAnalysisQuery.setCategory(category);
        ticketAnalysisQuery.setTopX(3);
        Map<String, TicketAnalysisDataDto> ticketAnalysisDataDtoMap = this.getTimeRangeTicketAnalysisData(ticketAnalysisQuery);

        return ticketAnalysisDataDtoMap.values().stream()
                .map(dto -> {
                    TicketAnalysisData ticketAnalysisData = new TicketAnalysisData();
                    BeanUtils.copyProperties(dto, ticketAnalysisData);
                    return ticketAnalysisData;
                })
                .peek(data -> this.saveOrUpdate(data, updateStrategy))
                .collect(Collectors.toList());
    }


    @Override
    public JSONObject buildQwCardContent(TicketAnalysisDataDto ticketAnalysisDataDto, List<String> qwUsers) {
        int lastApplyCount = ticketAnalysisDataDto.getLastApplyCount();
        int lastDoneCount = ticketAnalysisDataDto.getLastDoneCount();
        int lastApplyingCount = ticketAnalysisDataDto.getLastApplyingCount();
        int lastWithdrawCount = ticketAnalysisDataDto.getLastWithdrawCount();
        int lastRejectCount = ticketAnalysisDataDto.getLastRejectCount();
        // int lastExecutorCount = ticketAnalysisDataDto.getLastExecutorCount();
        // int lastCreateByCount = ticketAnalysisDataDto.getLastCreateByCount();
        String top3CreateByUsers = ticketAnalysisDataDto.getTop3CreateBy() != null ? ticketAnalysisDataDto.getTop3CreateBy() : "-";

        // 同比增长
        double applyGrowthRate = (ticketAnalysisDataDto.getApplyCount() - lastApplyCount) / (double)(lastApplyingCount != 0 ? lastApplyCount : 1) * 100;
        double doneGrowthRate = (ticketAnalysisDataDto.getDoneCount() - lastDoneCount) / (double)(lastDoneCount != 0 ? lastApplyCount : 1) * 100;
        double applyingGrowthRate = (ticketAnalysisDataDto.getApplyingCount() - lastApplyingCount) / (double)(lastApplyingCount != 0 ? lastApplyingCount : 1) * 100;
        double withdrawGrowthRate = (ticketAnalysisDataDto.getWithdrawCount() - lastWithdrawCount) / (double)(lastWithdrawCount != 0 ? lastWithdrawCount : 1) * 100;
        double rejectGrowthRate = (ticketAnalysisDataDto.getRejectCount() - lastRejectCount) / (double)(lastRejectCount != 0 ? lastRejectCount : 1) * 100;
        // double executorGrowthRate = (ticketAnalysisDataDto.getExecutorCount() - lastExecutorCount) / (double)(lastExecutorCount != 0 ? lastExecutorCount : 1) * 100;
        // double createByGrowthRate = (ticketAnalysisDataDto.getCreateByCount() - lastCreateByCount) / (double)(lastCreateByCount != 0 ? lastCreateByCount : 1) * 100;

        int ticketAvgEfficiency = ticketAnalysisDataDto.getTicketAvgEfficiency() != null ? ticketAnalysisDataDto.getTicketAvgEfficiency() : 0;
        int lastTicketAvgEfficiency = ticketAnalysisDataDto.getLastTicketAvgEfficiency() != null ? ticketAnalysisDataDto.getLastTicketAvgEfficiency() : 0;
        int efficiencyChange = ticketAvgEfficiency - lastTicketAvgEfficiency;
        String efficiencyDesc;
        if (efficiencyChange > 0) {
            efficiencyDesc = "增加" + TimeConverter.convertSecondsToDetailedTime(efficiencyChange);
        } else if (efficiencyChange < 0) {
            efficiencyDesc = "减少" + TimeConverter.convertSecondsToDetailedTime(efficiencyChange);
        } else {
            efficiencyDesc = "无变化";
        }

        JSONObject cardContentJson = new JSONObject();
        cardContentJson.put("touser", String.join("|", qwUsers));
        cardContentJson.put("msgtype", "template_card");
        cardContentJson.put("agentid", qwConfigComponent.getDefaultQwConfig().getAgentId());
        JSONObject templateCardJson = new JSONObject();
        templateCardJson.put("card_type", "text_notice");
        templateCardJson.put("source", JSONObject.of("desc", "统一工单系统", "desc_color", 1));
        templateCardJson.put("main_title", JSONObject.of(
                "title", String.format("%s工单总结 %s-%s",
                        TicketAnalysisDataTypeEnum.DAY.getCode().equals(ticketAnalysisDataDto.getCategory()) ? "一日" : "一周",
                        DateUtils.parseDateToStr("yyyy.MM.dd", ticketAnalysisDataDto.getStartDate()),
                        DateUtils.parseDateToStr("yyyy.MM.dd", ticketAnalysisDataDto.getEndDate())
                ),
                "desc", String.format("业务：%s", ticketAnalysisDataDto.getAppName())
        ));
        templateCardJson.put("emphasis_content", JSONObject.of(
                "title", String.format("%d", ticketAnalysisDataDto.getApplyCount()),
                "desc", String.format("申请工单数量（同比 %s%.2f%%）", applyGrowthRate > 0 ? "+" : "", applyGrowthRate)
        ));
        templateCardJson.put("sub_title_text", "工单核心数据概览");
        // NOTICE: horizontal_content_list长度不能超过六个（暂时拿掉工单申请人数、审批人数）
        templateCardJson.put("horizontal_content_list", JSONArray.of(
                JSONObject.of("keyname", "已完成工单", "value", String.format("%d（同比 %s%.2f%%）", ticketAnalysisDataDto.getDoneCount(), doneGrowthRate > 0 ? "+" : "", doneGrowthRate)),
                JSONObject.of("keyname", "审批中工单", "value", String.format("%d（同比 %s%.2f%%）", ticketAnalysisDataDto.getApplyingCount(), applyingGrowthRate > 0 ? "+" : "", applyingGrowthRate)),
                JSONObject.of("keyname", "已撤回中工单", "value", String.format("%d（同比 %s%.2f%%）", ticketAnalysisDataDto.getWithdrawCount(), withdrawGrowthRate > 0 ? "+" : "", withdrawGrowthRate)),
                JSONObject.of("keyname", "审批拒绝工单", "value", String.format("%d（同比 %s%.2f%%）", ticketAnalysisDataDto.getRejectCount(), rejectGrowthRate > 0 ? "+" : "", rejectGrowthRate)),
                // JSONObject.of("keyname", "工单申请人数", "value", String.format("%d（同比 %s%.2f%%）", ticketAnalysisDataDto.getCreateByCount(), createByGrowthRate > 0 ? "+" : "", createByGrowthRate)),
                // JSONObject.of("keyname", "工单审批人数", "value", String.format("%d（同比 %s%.2f%%）", ticketAnalysisDataDto.getExecutorCount(), executorGrowthRate > 0 ? "+" : "", executorGrowthRate)),
                JSONObject.of("keyname", "提单人Top3", "value", top3CreateByUsers),
                JSONObject.of("keyname", "工单平均时效", "value", String.format("%s（同比 %s）", TimeConverter.convertSecondsToDetailedTime(ticketAvgEfficiency), efficiencyDesc))
        ));
        templateCardJson.put("jump_list", JSONArray.of(JSONObject.of("type", 1, "url", QwNotify.getTfsWebsite(), "title", "统一工单系统")));
        templateCardJson.put("card_action", JSONObject.of("type", 1, "url", QwNotify.getTfsWebsite()));
        cardContentJson.put("template_card", templateCardJson);
        cardContentJson.put("enable_id_trans", 0);
        cardContentJson.put("enable_duplicate_check", 0);
        cardContentJson.put("duplicate_check_interval", 1800);

        return cardContentJson;
    }
    @Override
    public void sendPrevPeriodTicketSummary(LocalDate currentLocalDate, List<String> appIds, String category) {
        TicketAnalysisDataDto.TicketAnalysisQuery prevTicketAnalysisQuery = new TicketAnalysisDataDto.TicketAnalysisQuery();
        if (TicketAnalysisDataTypeEnum.WEEK.getCode().equals(category)) {
            LocalDate[] prevWeekRange = DateUtils.getPreviousWeekRange(currentLocalDate);
            prevTicketAnalysisQuery.setStartTimestamp(DateUtils.getDateBeforeStart(prevWeekRange[0], 0));
            prevTicketAnalysisQuery.setEndTimestamp(DateUtils.getDateBeforeEnd(prevWeekRange[1], 0));
        } else if (TicketAnalysisDataTypeEnum.DAY.getCode().equals(category)) {
            prevTicketAnalysisQuery.setStartTimestamp(DateUtils.getDateBeforeStart(currentLocalDate, 1));
            prevTicketAnalysisQuery.setEndTimestamp(DateUtils.getDateBeforeEnd(currentLocalDate, 1));
        } else {
            logger.warn(StringUtils.format("不支持的类别：{}", category));
            return;
        }
        prevTicketAnalysisQuery.setAppIds(appIds);
        prevTicketAnalysisQuery.setCategory(category);

        List<TicketAnalysisDataDto> prevPeriodTicketAnalysisDataList = ticketAnalysisDataMapper.getAppTicketAnalysisData(prevTicketAnalysisQuery);

        // 获取上上个周期的数据作为补充（如果上周没有产生数据
        TicketAnalysisDataDto.TicketAnalysisQuery comparedTicketAnalysisQuery = TicketAnalysisDataDto.TicketAnalysisQuery.copyAsPrevPeriod(prevTicketAnalysisQuery);
        List<TicketAnalysisDataDto> comparedDataList = ticketAnalysisDataMapper.getAppTicketAnalysisData(comparedTicketAnalysisQuery);

        Map<String, TicketAnalysisDataDto> comparedDataMap = new HashMap<>();
        for (TicketAnalysisDataDto dto : comparedDataList) {
            comparedDataMap.put(dto.getAppId(), dto);
        }

        Set<AccountInfo> adminUserSet = new HashSet<>();
        for (TicketAnalysisDataDto dataDto : prevPeriodTicketAnalysisDataList) {
            // 当申请工单数量为null的时候，说明上个周期（日/周）没有数据，需要去上上个周期拿数据填充
            dataDto.setStartDate(new Date(prevTicketAnalysisQuery.getStartTimestamp().getTime()));
            dataDto.setEndDate(new Date(prevTicketAnalysisQuery.getEndTimestamp().getTime()));
            if (StringUtils.isEmpty(dataDto.getCategory())) {
                dataDto.setCategory(category);
                TicketAnalysisDataDto comparedDto = comparedDataMap.get(dataDto.getAppId());
                if (comparedDto != null && StringUtils.isNotEmpty(comparedDto.getCategory())) {
                    dataDto.setLastApplyCount(comparedDto.getApplyCount());
                    dataDto.setLastDoneCount(comparedDto.getDoneCount());
                    dataDto.setLastExecutorCount(comparedDto.getExecutorCount());
                    dataDto.setLastTop3CreateBy(comparedDto.getTop3CreateBy());
                    dataDto.setLastTicketAvgEfficiency(comparedDto.getTicketAvgEfficiency());
                    dataDto.setLastCreateByCount(comparedDto.getCreateByCount());
                    dataDto.setLastApplyingCount(comparedDto.getApplyingCount());
                }
            }
            List<AccountInfo> adminUsers = dataDto.getAppAdminUsers() == null ? new ArrayList<>() : JSON.parseArray(dataDto.getAppAdminUsers(), AccountInfo.class);
            List<AccountInfo> validAdminUsers = adminUsers.stream()
                    .filter(accountInfo -> StringUtils.isNoneEmpty(accountInfo.getAccountId(), accountInfo.getAccountType()))
                    .collect(Collectors.toList());
            adminUserSet.addAll(validAdminUsers);
        }

        // 构造用户key（accountId:accountType）与企微id的映射
        Map<String, String> userKey2QwIdMap = adminUserSet.stream().collect(Collectors.toMap(
                accountInfo -> String.format("%s:%s", accountInfo.getAccountId(), accountInfo.getAccountType()),
                accountInfo -> {
                    TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByAccountIdAndType(accountInfo.getAccountId(), accountInfo.getAccountType());
                    return ticketAccountMapping == null ? "" : (ticketAccountMapping.getQwUserId() == null ? "" : ticketAccountMapping.getQwUserId());
                },
                (v1, v2) -> v1,
                HashMap::new
        ));

        String token = QwNotify.getAccessToken();
        for (TicketAnalysisDataDto dataDto : prevPeriodTicketAnalysisDataList) {
            List<AccountInfo> adminUsers = dataDto.getAppAdminUsers() == null ? new ArrayList<>() : JSON.parseArray(dataDto.getAppAdminUsers(), AccountInfo.class);
            List<String> qwUsers = adminUsers.stream().filter(
                    account -> !StringUtils.isAnyEmpty(account.getAccountId(), account.getAccountName(), account.getAccountType()))
                    .map(account -> userKey2QwIdMap.get(String.format("%s:%s", account.getAccountId(), account.getAccountType())))
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toList());
            if (qwUsers.isEmpty()) {
                logger.warn("未找到对应的企微用户ID, {}", dataDto.getAppAdminUsers());
                continue;
            }
            JSONObject cardContent = buildQwCardContent(dataDto, qwUsers);
            callQw(cardContent, token);
        }
    }

    private void callQw(JSONObject cardContent, String token) {
        // 推送企微工单卡片信息到相关工单负责人
        try {
            logger.info("start sending ticket report: {}", cardContent.toJSONString());
            String resp = HttpUtils.sendSSLPostJSON("https://qyapi.weixin.qq.com/cgi-bin/message/send",
                    "access_token=" + token, cardContent.toJSONString());
            JSONObject respObj = JSON.parseObject(resp);
            if (!Objects.equals(respObj.getOrDefault("errcode", -1), 0)) {
                logger.error("send ticket report message fail, {}", resp);
                return;
            }
            logger.info("send ticket report success, {}", respObj.getString("msgid"));
        } catch (Exception e) {
            logger.error("call qw api(Ticket Report) failed", e);
            QwNotify.notifyQw(StringUtils.format("{}, error: {}", cardContent.toJSONString(), e), "songbing");
        }
    }

    @Override
    public void sendBizTicketAlertSummary(String[] ticketTemplateIds, String[] bizRuleNameArr, Timestamp curDeadLineTimestamp, Timestamp hisStartTimestamp, String[] notifyQwUserArr) {
        List<TicketData> ticketDataRangeList = ticketDataService.lambdaQuery()
                .in(TicketData::getTemplateId, ticketTemplateIds)
                .ge(TicketData::getCreateTime, hisStartTimestamp)
                .select(TicketData::getId, TicketData::getTicketStatus, TicketData::getCreateTime, TicketData::getTicketFinishTime)
                .list();

        Map<String, TicketData> ticketDataMap = ticketDataRangeList.stream()
                .collect(Collectors.toMap(TicketData::getId, Function.identity()));

        List<String> ruleNameIds = Arrays.asList(rhAlertTicketItems.getRuleName(), flashcatAlertTicketItems.getRuleName());
        LambdaQueryWrapper<TicketFormItemData> queryWrapper = new LambdaQueryWrapper<>();
        if (!ticketDataMap.isEmpty()) {
            queryWrapper.in(TicketFormItemData::getTicketDataId, ticketDataMap.keySet());
        }
        queryWrapper.in(TicketFormItemData::getTemplateId, ruleNameIds);
        queryWrapper.and(wrapper -> {
            for (String bizRuleName : bizRuleNameArr) {
                wrapper.or(wp -> wp.like(TicketFormItemData::getItemValue, bizRuleName));
            }
        });

        queryWrapper.select(TicketFormItemData::getTicketDataId);
        List<TicketFormItemData> ticketFormItemDataList = ticketFormItemDataService.list(queryWrapper);

        List<String> ticketDataIdList = ticketFormItemDataList.stream()
                .map(TicketFormItemData::getTicketDataId)
                .collect(Collectors.toList());

        List<TicketData> ticketDataList = null;
        if (ticketDataIdList.isEmpty() || ticketDataMap.isEmpty()) {
            ticketDataList = new ArrayList<>();
        } else {
            ticketDataList = ticketDataIdList.stream()
                    .map(ticketDataMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        int totalApplyTicketCount = ticketDataList.size();
        int hisUnfinishedTicketCount = 0;
        int hisFinishedTicketCount = 0;
        int curApplyTicketCount = 0;
        int curUnfinishedTicketCount = 0;
        int curFinishedTicketCount = 0;
        for (TicketData ticketData : ticketDataList) {
            if (ticketData.getTicketStatus().equals(TicketDataStatusEnum.APPLY_END) || ticketData.getTicketStatus().equals(TicketDataStatusEnum.REJECT)) {
                if (ticketData.getCreateTime().after(Timestamp.valueOf(LocalDate.now().atStartOfDay())) && ticketData.getCreateTime().before(curDeadLineTimestamp)) {
                    curApplyTicketCount++;
                    if (ticketData.getTicketFinishTime() != null && ticketData.getTicketFinishTime().after(Timestamp.valueOf(LocalDate.now().atStartOfDay())) && ticketData.getTicketFinishTime().before(curDeadLineTimestamp)) {
                        curFinishedTicketCount++;
                    }
                } else {
                    hisFinishedTicketCount++;
                }
            } else {
                if (ticketData.getCreateTime().after(Timestamp.valueOf(LocalDate.now().atStartOfDay())) && ticketData.getCreateTime().before(curDeadLineTimestamp)) {
                    curUnfinishedTicketCount++;
                    curApplyTicketCount++;
                } else {
                    hisUnfinishedTicketCount++;
                }
            }
        }

        JSONObject cardContentJson = new JSONObject();
        cardContentJson.put("touser", String.join("|", notifyQwUserArr));
        cardContentJson.put("msgtype", "template_card");
        cardContentJson.put("agentid", qwConfigComponent.getDefaultQwConfig().getAgentId());
        JSONObject templateCardJson = new JSONObject();
        templateCardJson.put("card_type", "text_notice");
        templateCardJson.put("source", JSONObject.of("desc", "统一工单系统", "desc_color", 1));
        templateCardJson.put("main_title", JSONObject.of(
                "title", String.format("业务每日告警工单总结（%s）", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))),
                "desc", ""));
        templateCardJson.put("emphasis_content", JSONObject.of(
                "title", String.format("%d", curApplyTicketCount),
                "desc", "当日告警工单申请工单数量"
        ));
        templateCardJson.put("sub_title_text", "告警工单核心数据概览");
        // 当日（D-day）工单申请：当日0点-17点前新增创建的工单
        // 当日已完成：当日创建且当日17点前已结单的工单
        // 当日未完成：当日创建且当日17点前未结单的工单
        // 历史工单申请：开始日-（D-day-1）已创建的工单数
        // 历史已完成：开始日-（D-day-1）创建的工单中，截止到当天17:00已完成的工单数
        // 历史未完成：开始日-（D-day-1）创建的工单中，截止到当天17:00未完成的工单数
        templateCardJson.put("horizontal_content_list", JSONArray.of(
                JSONObject.of("keyname", "当日已完成", "value", String.format("%d", curFinishedTicketCount)),
                JSONObject.of("keyname", "当日未完成", "value", String.format("%d", curUnfinishedTicketCount)),
                JSONObject.of("keyname", "历史申请量", "value", String.format("%d", totalApplyTicketCount - curApplyTicketCount)),
                JSONObject.of("keyname", "历史已完成", "value", String.format("%d", hisFinishedTicketCount)),
                JSONObject.of("keyname", "历史未完成", "value", String.format("%d", hisUnfinishedTicketCount))
        ));
        templateCardJson.put("jump_list", JSONArray.of(JSONObject.of("type", 1, "url", QwNotify.getTfsWebsite(), "title", "统一工单系统")));
        templateCardJson.put("card_action", JSONObject.of("type", 1, "url", QwNotify.getTfsWebsite()));
        cardContentJson.put("template_card", templateCardJson);
        cardContentJson.put("enable_id_trans", 0);
        cardContentJson.put("enable_duplicate_check", 0);
        cardContentJson.put("duplicate_check_interval", 1800);
        callQw(cardContentJson, QwNotify.getAccessToken());
    }
}
