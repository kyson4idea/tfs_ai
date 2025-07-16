package com.smy.tfs.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smy.framework.core.util.SequenceUtil;
import com.smy.tfs.api.dbo.*;
import com.smy.tfs.api.dto.NCSTicketValuesDto;
import com.smy.tfs.api.dto.NCSTicketDto;
import com.smy.tfs.api.dto.NCSTicketExtraDto;
import com.smy.tfs.api.dto.TicketFlowDataGroupDto;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.enums.*;
import com.smy.tfs.api.service.*;
import com.smy.tfs.biz.mapper.*;
import com.smy.tfs.biz.service.ITicketFormItemValuesService;
import com.smy.tfs.biz.service.TicketFlowNodeApproveDetailService;
import com.smy.tfs.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NCSTicketImportServiceImpl implements INCSTicketImportService {

    private static final String APP_ID = "NCS";
    private static final String ACCOUNT_TYPE = "ncs";
    private static final String DEFAULT_TICKET_TEMPLATE_ID = "1182412040000590001";
    private static final String DEFAULT_HANDLE_TICKET_TYPE = "渠道工单";

    // 分页
    private static final Integer DEFAULT_PAGE_SIZE = 1000;

    // 每次处理100工单对象
    private static final Integer DEFAULT_BATCH_SIZE = 100;

    @Resource
    private ITicketDataService ticketDataService;

    @Resource
    private ITicketFormDataService ticketFormDataService;

    @Resource
    private ITicketFlowDataService ticketFlowDataService;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private TicketDataMapper ticketDataMapper;

    @Resource
    private TicketTemplateMapper ticketTemplateMapper;

    @Resource
    private ITicketFormItemDataService ticketFormItemDataService;

    @Resource
    private ITicketFlowNodeDataService ticketFlowNodeDataService;

    @Resource
    private ITicketFlowNodeExecutorDataService ticketFlowNodeExecutorDataService;

    @Resource
    private TicketFlowNodeApproveDetailService ticketFlowNodeApproveDetailService;

    @Resource
    private ITicketFormItemValuesService ticketFormItemValuesService;

    @Resource
    private TicketFormDataMapper ticketFormDataMapper;

    @Resource
    private TicketFormItemTemplateMapper ticketFormItemTemplateMapper;

    @Resource
    private TicketFormItemIdColMappingMapper ticketFormItemIdColMappingMapper;

    @Resource
    private TicketFormItemValuesMapper ticketFormItemValuesMapper;

    @Resource
    private TicketFormItemDataMapper ticketFormItemDataMapper;

    @Resource
    private TicketFlowNodeApproveDetailMapper ticketFlowNodeApproveDetailMapper;

    @Resource
    private TicketFlowDataMapper ticketFlowDataMapper;

    @Resource
    private TicketFlowNodeDataMapper ticketFlowNodeDataMapper;

    @Resource
    private TicketFlowNodeExecutorDataMapper ticketFlowNodeExecutorDataMapper;

    @Resource
    private TicketAccountMappingMapper ticketAccountMappingMapper;

    private static AccountInfo getApplyUser(String applyUserId, Map<String, TicketAccountMapping> ticketAccountMappingMap) {
        AccountInfo applyUser;
        TicketAccountMapping ticketAccountMapping = ticketAccountMappingMap.get(applyUserId);
        if (ticketAccountMapping == null || StrUtil.isBlank(ticketAccountMapping.getSameOriginId())) {
            applyUser = null;
        } else {
            applyUser = new AccountInfo(ticketAccountMapping.getSameOriginId(), ACCOUNT_TYPE,
                    applyUserId, ticketAccountMapping.getAccountName());
        }
        return applyUser;
    }

    private static List<TicketFormItemData> buildFormItemData(
            String ticketDataId, String ticketFormDataId,
            NCSTicketDto ncsTicketDto, String loginUserStr,
            Date timeNow, Map<String, TicketFormItemTemplate> formItemTemplateMap) {
        List<TicketFormItemData> ticketFormItemDataList = new ArrayList<>();
        int itemOrder = 1;
        for (NCSTicketDto.FormItem item : ncsTicketDto.getFormItemList()) {
            TicketFormItemData ticketFormItemData = new TicketFormItemData();
            ticketFormItemData.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FORM_ITEM_DATA));
            ticketFormItemData.setTicketDataId(ticketDataId);
            ticketFormItemData.setTicketFormDataId(ticketFormDataId);
            if (formItemTemplateMap.containsKey(item.getTemplateId())) {
                ticketFormItemData.setTemplateId(formItemTemplateMap.get(item.getTemplateId()).getId());
            } else {
                ticketFormItemData.setTemplateId("-1");
            }
            ticketFormItemData.setItemOrder(itemOrder++);
            ticketFormItemData.setItemType(FormItemTypeEnum.INPUT);
            ticketFormItemData.setItemConfig("-1");
            ticketFormItemData.setItemConfigExt("-1");
            ticketFormItemData.setItemValue(item.getValue());
            ticketFormItemData.setItemLabel(item.getTemplateId());
            ticketFormItemData.setCreateBy(loginUserStr);
            ticketFormItemData.setUpdateBy(loginUserStr);
            ticketFormItemData.setCreateTime(timeNow);
            ticketFormItemData.setUpdateTime(timeNow);
            ticketFormItemDataList.add(ticketFormItemData);
        }
        return ticketFormItemDataList;
    }

    private static TicketFlowDataGroupDto buildTicketFlowDataGroup(
            String ticketDataId, String ticketFlowDataId,
            NCSTicketDto ncsTicketDto, String loginUserStr,
            Date timeNow, Map<String, TicketAccountMapping> ticketAccountMappingMap) {
        String preNodeId = "-1";
        int nodeOrder = 1;
        List<TicketFlowNodeData> ticketFlowNodeDataList = new ArrayList<>();
        List<TicketFlowNodeExecutorData> ticketFlowNodeExecutorDataList = new ArrayList<>();
        List<TicketFlowNodeApproveDetail> ticketFlowNodeApproveDetailList = new ArrayList<>();
        for (NCSTicketDto.FlowNode item : ncsTicketDto.getFlowNodes()) {
            // 构建ticket_flow_node_data
            TicketFlowNodeData ticketFlowNodeData = new TicketFlowNodeData();
            ticketFlowNodeData.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_DATA));
            ticketFlowNodeData.setNodeName(StringUtils.isEmpty(item.getNodeName()) ? "审批节点" : item.getNodeName() );
            ticketFlowNodeData.setPreNodeId(preNodeId);
            ticketFlowNodeData.setTemplateId("-1");
            ticketFlowNodeData.setTicketDataId(ticketDataId);
            ticketFlowNodeData.setTicketFlowDataId(ticketFlowDataId);
            ticketFlowNodeData.setAuditedMethod(AuditedMethodEnum.AND);
            ticketFlowNodeData.setAuditedType(AuditedType.BY_USER);
            ticketFlowNodeData.setNodeStatus(NodeStatusEnum.APPROVE_PASS);
            ticketFlowNodeData.setNodeOrder(nodeOrder++);
            ticketFlowNodeData.setCreateBy(loginUserStr);
            ticketFlowNodeData.setUpdateBy(loginUserStr);
            ticketFlowNodeData.setCreateTime(timeNow);
            ticketFlowNodeData.setUpdateTime(timeNow);
            preNodeId = ticketFlowNodeData.getId();
            ticketFlowNodeDataList.add(ticketFlowNodeData);

            // 构建ticket_flow_node_executor_data
            TicketFlowNodeExecutorData ticketFlowNodeExecutorData = new TicketFlowNodeExecutorData();
            ticketFlowNodeExecutorData.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_EXECUTOR_DATA));
            ticketFlowNodeExecutorData.setTemplateId("-1");
            ticketFlowNodeExecutorData.setTicketDataId(ticketDataId);
            ticketFlowNodeExecutorData.setTicketFlowNodeDataId(ticketFlowNodeData.getId());
            ticketFlowNodeExecutorData.setExecutorType(ExecutorTypeEnum.APPLY_MEMBER_LIST);
            ticketFlowNodeExecutorData.setExecutorValue("");
            ticketFlowNodeExecutorData.setExecutorList("");
            ticketFlowNodeExecutorData.setExecutorDoneList("");
            ticketFlowNodeExecutorData.setCreateBy(loginUserStr);
            ticketFlowNodeExecutorData.setUpdateBy(loginUserStr);
            ticketFlowNodeExecutorData.setCreateTime(timeNow);
            ticketFlowNodeExecutorData.setUpdateTime(timeNow);
            ticketFlowNodeExecutorDataList.add(ticketFlowNodeExecutorData);

            // 构建ticket_flow_node_approve_detail
            TicketFlowNodeApproveDetail ticketFlowNodeApproveDetail = new TicketFlowNodeApproveDetail();
            ticketFlowNodeApproveDetail.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_APPROVE_DETAIL));
            ticketFlowNodeApproveDetail.setTicketDataId(ticketDataId);
            ticketFlowNodeApproveDetail.setTicketFlowNodeDataId(ticketFlowNodeData.getId());
            if (ticketAccountMappingMap.containsKey(item.getNodeUser())) {
                TicketAccountMapping ticketAccountMapping = ticketAccountMappingMap.get(item.getNodeUser());
                ticketFlowNodeApproveDetail.setDealUserId(ticketAccountMapping.getAccountId());
                ticketFlowNodeApproveDetail.setDealUserName(ticketAccountMapping.getAccountName());
                ticketFlowNodeApproveDetail.setDealUserType(ticketAccountMapping.getAccountType());
            } else {
                ticketFlowNodeApproveDetail.setDealUserId("");
                ticketFlowNodeApproveDetail.setDealUserName(item.getNodeUser());
                ticketFlowNodeApproveDetail.setDealUserType(ACCOUNT_TYPE);
            }
            ticketFlowNodeApproveDetail.setDealType(ApproveDealTypeEnum.PASS);
            ticketFlowNodeApproveDetail.setDealOpinion(item.getDealComment());
            ticketFlowNodeApproveDetail.setDealTypeDescription(item.getDealType());
            ticketFlowNodeApproveDetail.setCreateBy(loginUserStr);
            ticketFlowNodeApproveDetail.setUpdateBy(loginUserStr);
            ticketFlowNodeApproveDetail.setCreateTime(item.getDealTime());
            ticketFlowNodeApproveDetail.setUpdateTime(item.getDealTime());
            ticketFlowNodeApproveDetailList.add(ticketFlowNodeApproveDetail);
        }

        TicketFlowDataGroupDto result = new TicketFlowDataGroupDto();
        result.setTicketFlowNodeDataList(ticketFlowNodeDataList);
        result.setTicketFlowNodeExecutorDataList(ticketFlowNodeExecutorDataList);
        result.setTicketFlowNodeApproveDetailList(ticketFlowNodeApproveDetailList);
        return result;
    }

    private Map<String, TicketFormItemTemplate> getFormItemTemplateMap(String ticketTemplateId) {
        LambdaQueryWrapper<TicketFormItemTemplate> formItemTempWrapper = new LambdaQueryWrapper<>();
        formItemTempWrapper.eq(TicketFormItemTemplate::getTicketTemplateId, ticketTemplateId)
                .isNull(TicketFormItemTemplate::getDeleteTime);
        List<TicketFormItemTemplate> ticketFormItemTemplateList = ticketFormItemTemplateMapper.selectList(formItemTempWrapper);
        Map<String, TicketFormItemTemplate> formItemTemplateMap = new HashMap<>();
        for (var item : ticketFormItemTemplateList) {
            // 顺序比较重要 ID > itemLabel > itemCode
            formItemTemplateMap.putIfAbsent(item.getId(), item);
            formItemTemplateMap.putIfAbsent(item.getItemLabel(), item);
            if (StringUtils.isNotEmpty(item.getItemConfig())) {
                String itemCode = (String) JSONObject.parseObject(item.getItemConfig()).get("itemCode");
                if (StringUtils.isNotEmpty(itemCode)) {
                    formItemTemplateMap.putIfAbsent(itemCode, item);
                }
            }
        }

        return formItemTemplateMap;
    }

    private void createTicketFlowRelationData(TicketFlowDataGroupDto ticketFlowDataGroupDto) {
        if (!CollectionUtils.isEmpty(ticketFlowDataGroupDto.getTicketFlowNodeDataList())) {
            ticketFlowNodeDataService.saveBatch(ticketFlowDataGroupDto.getTicketFlowNodeDataList());
        }

        if (!CollectionUtils.isEmpty(ticketFlowDataGroupDto.getTicketFlowNodeExecutorDataList())) {
            ticketFlowNodeExecutorDataService.saveBatch(ticketFlowDataGroupDto.getTicketFlowNodeExecutorDataList());
        }

        if (!CollectionUtils.isEmpty(ticketFlowDataGroupDto.getTicketFlowNodeApproveDetailList())) {
            ticketFlowNodeApproveDetailService.saveBatch(ticketFlowDataGroupDto.getTicketFlowNodeApproveDetailList());
        }
    }

    private void createTicketAdvQueryData(List<TicketData> ticketDataList, List<TicketFormItemData> ticketFormItemDataList, Map<String, String> fieldIdColMappingMap) {
        if (CollectionUtils.isEmpty(fieldIdColMappingMap)) {
            return;
        }

        List<TicketFormItemValues> ticketFormItemValuesList = new ArrayList<>();
        for (TicketData ticketData : ticketDataList) {
            TicketFormItemValues ticketFormItemValues = new TicketFormItemValues().toTicketFormItemValues(ticketData);
            boolean flag = false;
            for (String key : fieldIdColMappingMap.keySet()) {
                for (TicketFormItemData item : ticketFormItemDataList) {
                    if (key.equals(item.getTemplateId()) && ticketData.getId().equals(item.getTicketDataId())) {
                        String fieldKey = StringUtils.toLowerCamelCase(fieldIdColMappingMap.get(key));
                        Class ticketFormItemValuesClass = ticketFormItemValues.getClass();
                        try {
                            Field field = ticketFormItemValuesClass.getDeclaredField(fieldKey);
                            field.setAccessible(Boolean.TRUE);
                            field.set(ticketFormItemValues, item.getItemValue());
                            flag = true;
                        } catch (Exception e) {
                            log.error("设置formItemValue异常, {}", e.getMessage());
                            break;
                        }
                    }
                }
            }
            if (flag) {
                ticketFormItemValuesList.add(ticketFormItemValues);
            }
        }

        if (!CollectionUtils.isEmpty(ticketFormItemValuesList)) {
            ticketFormItemValuesService.saveBatch(ticketFormItemValuesList);
        }
    }

    private String handleTicketDoneUser(Map<String, TicketAccountMapping> ticketAccountMappingMap, List<NCSTicketDto.FlowNode> flowNodes) {
        List<AccountInfo> currentDealUsers = new ArrayList<>();
        for (NCSTicketDto.FlowNode flowNode : flowNodes) {
            String nodeUser = flowNode.getNodeUser();
            if (StringUtils.isNotEmpty(nodeUser)) {
                TicketAccountMapping ticketAccountMapping = ticketAccountMappingMap.get(nodeUser);
                if (ticketAccountMapping != null) {
                    currentDealUsers.add(
                            new AccountInfo(
                                    ticketAccountMapping.getSameOriginId(),
                                    ticketAccountMapping.getAccountType(),
                                    ticketAccountMapping.getAccountId(),
                                    ticketAccountMapping.getAccountName()
                            )
                    );
                }
            }
        }
        return AccountInfo.ToAccountInfoListStr(currentDealUsers);
    }

    private <T> List<T> handleUploadFile(MultipartFile file, Class<T> clazz) {
        List<T> ticketDtoList = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) {
                // 第一行是excel的标题，跳过
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                String sheetDataStr = row.getCell(0).getStringCellValue();
                if (StringUtils.isEmpty(sheetDataStr)) {
                    continue;
                }
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                try {
                    T ticketDto = objectMapper.readValue(sheetDataStr, clazz);
                    if (clazz.equals(NCSTicketDto.class) && StringUtils.isEmpty(((NCSTicketDto) ticketDto).getApplyId())) {
                        log.warn("发现工单数据出现applyId为空的记录，{}", sheetDataStr);
                        continue;
                    } else if (clazz.equals(NCSTicketExtraDto.class) && StringUtils.isEmpty(((NCSTicketExtraDto) ticketDto).getApplyId())) {
                        log.warn("发现工单数据出现applyId为空的记录，{}", sheetDataStr);
                        continue;
                    }
                    ticketDtoList.add(ticketDto);
                } catch (Exception e) {
                    log.error("解析工单数据数据【{}】失败 {}", sheetDataStr, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("文件解析失败 {}", e.getMessage());
        }

        return ticketDtoList;
    }

    // 获取模版字段映射关系 -> 为了存ticket_form_item_values表数据
    public Map<String, String> getFileIdColMappingMap(String ticketTemplateId) {
        Map<String, String> fieldIdColMappingMap = null;
        TicketTemplate ticketTemplate = ticketTemplateMapper.selectOne(
                new LambdaQueryWrapper<TicketTemplate>()
                        .eq(TicketTemplate::getId, ticketTemplateId)
                        .isNull(TicketTemplate::getDeleteTime)
        );
        if (ticketTemplate != null) {
            List<TicketFormItemIdColMapping> ticketFormItemIdColMappingList = ticketFormItemIdColMappingMapper.selectList(
                    new LambdaQueryWrapper<TicketFormItemIdColMapping>()
                            .eq(TicketFormItemIdColMapping::getTicketTemplateId, ticketTemplateId)
                            .eq(TicketFormItemIdColMapping::getVersion, ticketTemplate.getVersion())
                            .isNull(TicketFormItemIdColMapping::getDeleteTime));
            if (!ticketFormItemIdColMappingList.isEmpty()) {
                fieldIdColMappingMap = ticketFormItemIdColMappingList.stream()
                        .collect(Collectors.toMap(
                                TicketFormItemIdColMapping::getFormItemId,
                                TicketFormItemIdColMapping::getFormItemValueCol,
                                (existObj, replaceObj) -> replaceObj));
            }
        }
        return fieldIdColMappingMap;
    }

    public boolean createNcsTickets(List<NCSTicketDto> batchData, Map<String, String> fieldIdColMappingMap,
                                   Map<String, TicketFormItemTemplate> formItemTemplateMap,
                                    Map<String, TicketAccountMapping> ticketAccountMappingMap,
                                    String loginUserStr) {
        List<TicketData> ticketDataList = new ArrayList<>();
        List<TicketFormData> ticketFormDataList = new ArrayList<>();
        List<TicketFormItemData> ticketFormItemDataList = new ArrayList<>();
        List<TicketFlowData> ticketFlowDataList = new ArrayList<>();
        List<TicketFlowDataGroupDto> ticketFlowDataGroupDtoList = new ArrayList<>();
        for (NCSTicketDto ncsTicketDto : batchData) {
            AccountInfo applyUser = getApplyUser(ncsTicketDto.getApplyUserId(), ticketAccountMappingMap);
            Date now = new Date();

            // 构建工单表数据
            String ticketTemplateId = DEFAULT_TICKET_TEMPLATE_ID;
            TicketData ticketData = new TicketData();
            ticketData.setId(ncsTicketDto.getApplyId());
            ticketData.setTemplateId(ticketTemplateId);
            ticketData.setAppId(APP_ID);
            ticketData.setTicketStatus(TicketDataStatusEnum.APPLY_END);
            // 暂定为用ticketTemplateId的名称作为工单名
            ticketData.setTicketName(ncsTicketDto.getTicketTemplateId());
            ticketData.setDescription("催收系统导入工单");
            ticketData.setCurrentNodeName("");
            ticketData.setCurrentNodeId("-1");
            ticketData.setCurrentDealUsers("");
            if (DEFAULT_HANDLE_TICKET_TYPE.equals(ncsTicketDto.getTicketTemplateId())) {
                ticketData.setCurrentDoneUsers(handleTicketDoneUser(ticketAccountMappingMap, ncsTicketDto.getFlowNodes()));
            } else {
                ticketData.setCurrentDoneUsers("");
            }
            ticketData.setCurrentCcUsers("");
            ticketData.setApplyUser(JSONUtil.toJsonStr(applyUser));
            ticketData.setTicketMsgBuildType(TicketMsgBuildTypeEnum.CREATE_NONE);
            ticketData.setTicketMsgArriveType(TicketMsgArriveTypeEnum.NULL);
            ticketData.setTicketFormChangeFlag(YESNOEnum.NO);
            ticketData.setVersion(1);
            ticketData.setCreateBy(loginUserStr);
            ticketData.setCreateTime(now);
            ticketData.setUpdateBy(loginUserStr);
            ticketData.setUpdateTime(now);
            ticketDataList.add(ticketData);

            // 构建ticket_form_data数据
            TicketFormData ticketFormData = new TicketFormData();
            ticketFormData.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FORM_DATA));
            ticketFormData.setTicketDataId(ticketData.getId());
            ticketFormData.setTemplateId(ticketTemplateId);
            ticketFormData.setCreateBy(loginUserStr);
            ticketFormData.setUpdateBy(loginUserStr);
            ticketFormData.setCreateTime(now);
            ticketFormData.setUpdateTime(now);
            ticketFormDataList.add(ticketFormData);

            // 构建 ticket_form_item_data
            ticketFormItemDataList.addAll(buildFormItemData(
                    ticketData.getId(), ticketFormData.getId(), ncsTicketDto,
                    loginUserStr, now, formItemTemplateMap));

            // 构建ticket_flow_data
            TicketFlowData ticketFlowData = new TicketFlowData();
            ticketFlowData.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_DATA));
            ticketFlowData.setTicketDataId(ticketData.getId());
            ticketFlowData.setTemplateId("-1");
            ticketFlowData.setCreateBy(loginUserStr);
            ticketFlowData.setUpdateBy(loginUserStr);
            ticketFlowData.setCreateTime(now);
            ticketFlowData.setUpdateTime(now);
            ticketFlowDataList.add(ticketFlowData);

            // 构建 ticket_flow_node_data & ticket_flow_node_executor_data & ticket_flow_node_approve_detail
            TicketFlowDataGroupDto ticketFlowDataGroupDto = buildTicketFlowDataGroup(
                    ticketData.getId(), ticketFlowData.getId(), ncsTicketDto, loginUserStr, now, ticketAccountMappingMap);
            ticketFlowDataGroupDtoList.add(ticketFlowDataGroupDto);
        }

        try {
            transactionTemplate.execute(action -> {
                ticketDataService.saveBatch(ticketDataList);
                ticketFormDataService.saveBatch(ticketFormDataList);
                ticketFlowDataService.saveBatch(ticketFlowDataList);
                ticketFormItemDataService.saveBatch(ticketFormItemDataList);
                // 创建新的数据 ticket_flow_node_data & ticket_flow_node_executor_data & ticket_flow_node_approve_detail
                List<TicketFlowNodeData> ticketFlowNodeDataList = new ArrayList<>();
                List<TicketFlowNodeExecutorData> ticketFlowNodeExecutorDataList = new ArrayList<>();
                List<TicketFlowNodeApproveDetail> ticketFlowNodeApproveDetailList = new ArrayList<>();
                for (TicketFlowDataGroupDto ticketFlowDataGroupDto : ticketFlowDataGroupDtoList) {
                    ticketFlowNodeDataList.addAll(ticketFlowDataGroupDto.getTicketFlowNodeDataList());
                    ticketFlowNodeExecutorDataList.addAll(ticketFlowDataGroupDto.getTicketFlowNodeExecutorDataList());
                    ticketFlowNodeApproveDetailList.addAll(ticketFlowDataGroupDto.getTicketFlowNodeApproveDetailList());
                }
                ticketFlowNodeDataService.saveBatch(ticketFlowNodeDataList);
                ticketFlowNodeExecutorDataService.saveBatch(ticketFlowNodeExecutorDataList);
                ticketFlowNodeApproveDetailService.saveBatch(ticketFlowNodeApproveDetailList);

                // 构建“高级”查询表数据 ticket_form_item_values
                createTicketAdvQueryData(ticketDataList, ticketFormItemDataList, fieldIdColMappingMap);
                return null;
            });
        } catch (Exception e) {
            log.error("导入工单失败 error: {}", e.getMessage());
            return false;
        }

        return true;
    }

    public boolean updateNcsTicket(TicketData ticketData, NCSTicketDto ncsTicketDto,
                                   Map<String, String> fieldIdColMappingMap,
                                   Map<String, TicketFormItemTemplate> formItemTemplateMap,
                                   Map<String, TicketAccountMapping> ticketAccountMappingMap,
                                   String loginUserStr) {
        AccountInfo applyUser = getApplyUser(ncsTicketDto.getApplyUserId(), ticketAccountMappingMap);
        Date now = new Date();

        // 更新ticket_data数据
        String ticketTemplateId = DEFAULT_TICKET_TEMPLATE_ID;
        ticketData.setApplyUser(JSONUtil.toJsonStr(applyUser));
        if (DEFAULT_HANDLE_TICKET_TYPE.equals(ncsTicketDto.getTicketTemplateId())) {
            ticketData.setCurrentDoneUsers(handleTicketDoneUser(ticketAccountMappingMap, ncsTicketDto.getFlowNodes()));
        } else {
            ticketData.setCurrentDoneUsers("");
        }
        ticketData.setTicketName(ncsTicketDto.getTicketTemplateId());
        ticketData.setTemplateId(ticketTemplateId);
        ticketData.setUpdateBy(loginUserStr);
        ticketData.setUpdateTime(now);

        TicketFormData ticketFormData = ticketFormDataMapper.selectOne(
                new LambdaQueryWrapper<TicketFormData>()
                        .eq(TicketFormData::getTicketDataId, ticketData.getId())
                        .isNull(TicketFormData::getDeleteTime));
        TicketFlowData ticketFlowData = ticketFlowDataMapper.selectOne(
                new LambdaQueryWrapper<TicketFlowData>()
                        .eq(TicketFlowData::getTicketDataId, ticketData.getId())
                        .isNull(TicketFlowData::getDeleteTime));

        // 构建 ticket_form_item_data
        List<TicketFormItemData> ticketFormItemDataList = buildFormItemData(
                ticketData.getId(), ticketFormData.getId(), ncsTicketDto,
                loginUserStr, now, formItemTemplateMap);

        // 构建 ticket_flow_node_data & ticket_flow_node_executor_data & ticket_flow_node_approve_detail
        TicketFlowDataGroupDto ticketFlowDataGroupDto = buildTicketFlowDataGroup(
                ticketData.getId(), ticketFlowData.getId(), ncsTicketDto, loginUserStr, now, ticketAccountMappingMap);

        try {
            transactionTemplate.execute(action -> {
                int count = ticketDataMapper.updateById(ticketData);
                if (count != 1) {
                    return false;
                }

                // 更新旧的 ticket_form_data
                LambdaUpdateWrapper<TicketFormData> upTicketFormDataWrapper = new LambdaUpdateWrapper<>();
                upTicketFormDataWrapper.eq(TicketFormData::getId, ticketFormData.getId())
                        .isNull(TicketFormData::getDeleteTime)
                        .set(TicketFormData::getTemplateId, ticketTemplateId)
                        .set(TicketFormData::getUpdateBy, loginUserStr)
                        .set(TicketFormData::getUpdateTime, now);
                ticketFormDataMapper.update(null, upTicketFormDataWrapper);

                // 将旧的 ticket_form_item_data 关联数据软删除
                LambdaUpdateWrapper<TicketFormItemData> upFormItemDataWrapper = new LambdaUpdateWrapper<>();
                upFormItemDataWrapper.eq(TicketFormItemData::getTicketDataId, ticketData.getId())
                        .isNull(TicketFormItemData::getDeleteTime)
                        .set(TicketFormItemData::getDeleteTime, now);
                ticketFormItemDataMapper.update(null, upFormItemDataWrapper);

                // 将旧的 ticket_form_item_values 关联数据软删除
                LambdaUpdateWrapper<TicketFormItemValues> upFormItemValuesWrapper = new LambdaUpdateWrapper<>();
                upFormItemValuesWrapper.eq(TicketFormItemValues::getTicketDataId, ticketData.getId())
                        .isNull(TicketFormItemValues::getDeleteTime)
                        .set(TicketFormItemValues::getDeleteTime, now);
                ticketFormItemValuesMapper.update(null, upFormItemValuesWrapper);

                // 将旧的 ticket_flow_node_data 关联数据软删除
                LambdaUpdateWrapper<TicketFlowNodeData> upFlowNodeDataWrapper = new LambdaUpdateWrapper<>();
                upFlowNodeDataWrapper.eq(TicketFlowNodeData::getTicketDataId, ticketData.getId())
                        .isNull(TicketFlowNodeData::getDeleteTime)
                        .set(TicketFlowNodeData::getDeleteTime, now);
                ticketFlowNodeDataMapper.update(null, upFlowNodeDataWrapper);

                // 将旧的 ticket_flow_node_executor_data 关联数据软删除
                LambdaUpdateWrapper<TicketFlowNodeExecutorData> upFlowNodeExecutorDataWrapper = new LambdaUpdateWrapper<>();
                upFlowNodeExecutorDataWrapper.eq(TicketFlowNodeExecutorData::getTicketDataId, ticketData.getId())
                        .isNull(TicketFlowNodeExecutorData::getDeleteTime)
                        .set(TicketFlowNodeExecutorData::getDeleteTime, now);
                ticketFlowNodeExecutorDataMapper.update(null, upFlowNodeExecutorDataWrapper);

                // 将旧的 ticketFlowNodeApproveDetailList 关联数据软删除
                LambdaUpdateWrapper<TicketFlowNodeApproveDetail> upFlowNodeApproveDetailWrapper = new LambdaUpdateWrapper<>();
                upFlowNodeApproveDetailWrapper.eq(TicketFlowNodeApproveDetail::getTicketDataId, ticketData.getId())
                        .isNull(TicketFlowNodeApproveDetail::getDeleteTime)
                        .set(TicketFlowNodeApproveDetail::getDeleteTime, now);
                ticketFlowNodeApproveDetailMapper.update(null, upFlowNodeApproveDetailWrapper);

                if (!CollectionUtils.isEmpty(ticketFormItemDataList)) {
                    ticketFormItemDataService.saveBatch(ticketFormItemDataList);
                }
                // 创建新的数据 ticket_flow_node_data & ticket_flow_node_executor_data & ticket_flow_node_approve_detail
                createTicketFlowRelationData(ticketFlowDataGroupDto);
                // 构建“高级”查询表数据 ticket_form_item_values
                List<TicketData> ticketDataList = new ArrayList<>();
                ticketDataList.add(ticketData);
                createTicketAdvQueryData(ticketDataList, ticketFormItemDataList, fieldIdColMappingMap);
                return null;
            });
        } catch (Exception e) {
            log.error("更新导入工单失败 error: {}", e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public Map<String, Integer> importDataFromExcel(MultipartFile file, AccountInfo loginUser) {
        Map<String, Integer> resultMap = new HashMap<>();
        List<NCSTicketDto> ncsTicketDtoList = handleUploadFile(file, NCSTicketDto.class);
        if (ncsTicketDtoList.isEmpty()) {
            resultMap.put("updateCount", 0);
            resultMap.put("createCount", 0);
            resultMap.put("failCount", 0);
            log.info("工单数据更新结果：更新0条，新建0条，失败0条");
            return resultMap;
        }

        String ticketTemplateId = DEFAULT_TICKET_TEMPLATE_ID;
        Map<String, String> fileIdColMappingMap = getFileIdColMappingMap(ticketTemplateId);
        Map<String, TicketFormItemTemplate> formItemTemplateMap = getFormItemTemplateMap(ticketTemplateId);
        List<TicketAccountMapping> ticketAccountMappingList = ticketAccountMappingMapper.selectList(
                new LambdaQueryWrapper<TicketAccountMapping>()
                        .eq(TicketAccountMapping::getAccountType, ACCOUNT_TYPE)
                        .isNull(TicketAccountMapping::getDeleteTime));
        Map<String, TicketAccountMapping> ticketAccountMappingMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(ticketAccountMappingList)) {
            ticketAccountMappingMap = ticketAccountMappingList.stream()
                    .collect(Collectors.toMap(
                            TicketAccountMapping::getAccountId,
                            mapping -> mapping,
                            (existObj, replaceObj) -> replaceObj));
        }

        Map<String, NCSTicketDto> ncsTicketDtoMap = ncsTicketDtoList.stream()
                .filter(dto -> StringUtils.isNotEmpty(dto.getApplyId()))
                .collect(Collectors.toMap(
                        NCSTicketDto::getApplyId,
                        dto -> dto,
                        (existObj, replaceObj) -> replaceObj));

        int updateCount = 0;
        int createCount = 0;
        int failCount = 0;

        int pageSize = DEFAULT_PAGE_SIZE;  // 避免ID过长导致查询失败
        int total = ncsTicketDtoMap.keySet().size();
        int totalPages = (total + pageSize - 1) / pageSize;
        List<NCSTicketDto> allCreateTicketList = new ArrayList<>();
        for (int i = 0; i < totalPages; i++) {
            Set<String> curTicketIds = ncsTicketDtoMap.keySet().stream()
                    .skip((long) i * pageSize).limit(pageSize).collect(Collectors.toSet());

            List<TicketData> dbTickets = ticketDataService.lambdaQuery()
                    .in(TicketData::getId, curTicketIds)
                    .eq(TicketData::getAppId, APP_ID)
                    .isNull(TicketData::getDeleteTime)
                    .list();
            Set<String> existApplyIds = dbTickets.stream().map(TicketData::getId).collect(Collectors.toSet());

            // 待更新的工单
            for (TicketData dbTicketData : dbTickets) {
                // TODO 待批量更新优化
                boolean flag = updateNcsTicket(dbTicketData, ncsTicketDtoMap.get(dbTicketData.getId()),
                        fileIdColMappingMap, formItemTemplateMap,
                        ticketAccountMappingMap, loginUser.ToJsonString());
                updateCount += flag ? 1 : 0;
                failCount += flag ? 0 : 1;
            }

            // 存储新建工单对象
            for (String curTicketId : curTicketIds) {
                NCSTicketDto ncsTicketDto = ncsTicketDtoMap.get(curTicketId);
                if (!existApplyIds.contains(ncsTicketDto.getApplyId())) {
                    allCreateTicketList.add(ncsTicketDto);
                }
            }
        }

        // 新增工单，批量提交
        int batchSize = DEFAULT_BATCH_SIZE;
        List<List<NCSTicketDto>> batches = new ArrayList<>();
        for (int i = 0; i < allCreateTicketList.size(); i += batchSize) {
            int end = Math.min(i + batchSize, allCreateTicketList.size());
            batches.add(allCreateTicketList.subList(i, end));
        }

        for (List<NCSTicketDto> batchData : batches) {
            List<String> applyIdList = batchData.stream().map(NCSTicketDto::getApplyId).collect(Collectors.toList());
            log.info("开始处理工单IDs: {}", applyIdList);
            boolean flag = createNcsTickets(batchData, fileIdColMappingMap,
                    formItemTemplateMap, ticketAccountMappingMap, loginUser.ToJsonString());
            createCount += flag ? batchData.size() : 0;
            failCount += flag ? 0 : batchData.size();
        }

        resultMap.put("updateCount", updateCount);
        resultMap.put("createCount", createCount);
        resultMap.put("failCount", failCount);
        log.info("工单数据更新结果：更新{}条，新建{}条，失败{}条", updateCount, createCount, failCount);

        return resultMap;
    }


    /**
     * 导入工单额外表单项数据，只支持新增
     */

    @Override
    public Map<String, Integer> importExtraDataFromExcel(MultipartFile file, AccountInfo loginUser) {
        Map<String, Integer> resultMap = new HashMap<>();
        List<NCSTicketExtraDto> ncsTicketExtraDtoList = handleUploadFile(file, NCSTicketExtraDto.class);
        if (ncsTicketExtraDtoList.isEmpty()) {
            resultMap.put("updateCount", 0);
            resultMap.put("failCount", 0);
            log.info("工单额外表单项数据更新结果：更新0条，失败0条");
            return resultMap;
        }

        int total = ncsTicketExtraDtoList.size();
        int updateCount = 0;
        int failCount = 0;

        int batchSize = DEFAULT_BATCH_SIZE;
        List<List<NCSTicketExtraDto>> batches = new ArrayList<>();
        for (int i = 0; i < total; i += batchSize) {
            int end = Math.min(i + batchSize, total);
            batches.add(ncsTicketExtraDtoList.subList(i, end));
        }

        String loginUserStr = loginUser.ToJsonString();
        for (List<NCSTicketExtraDto> batchData : batches) {
            Date now = new Date();
            List<TicketFormItemData> ticketFormItemDataList = new ArrayList<>();
            List<String> applyIdList = batchData.stream().map(NCSTicketExtraDto::getApplyId).collect(Collectors.toList());
            log.info("开始处理工单IDs: {}, {}", applyIdList, applyIdList.size());
            List<TicketFormData> ticketFormDataList = ticketFormDataMapper.selectList(
                    new LambdaQueryWrapper<TicketFormData>()
                            .in(TicketFormData::getTicketDataId, applyIdList));
            Map<String, String> ticketFormDataIdMap = ticketFormDataList.stream()
                    .collect(Collectors.toMap(
                            TicketFormData::getTicketDataId, TicketFormData::getId,
                            (existObj, replaceObj) -> replaceObj));
            for (NCSTicketExtraDto ncsTicketExtraDto : batchData) {
                if (CollectionUtils.isEmpty(ncsTicketExtraDto.getFormItemList())) {
                    log.warn("工单ID:{} 表单项列表为空", ncsTicketExtraDto.getApplyId());
                    continue;
                }
                for (NCSTicketExtraDto.FormItem item : ncsTicketExtraDto.getFormItemList()) {
                    TicketFormItemData ticketFormItemData = new TicketFormItemData();
                    ticketFormItemData.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FORM_ITEM_DATA));
                    ticketFormItemData.setTicketDataId(ncsTicketExtraDto.getApplyId());
                    if (ticketFormDataIdMap.containsKey(ncsTicketExtraDto.getApplyId())) {
                        ticketFormItemData.setTicketFormDataId(ticketFormDataIdMap.get(ncsTicketExtraDto.getApplyId()));
                    } else {
                        log.warn("工单ID:{} 没有对应的表单数据ID", ncsTicketExtraDto.getApplyId());
                        ticketFormItemData.setTicketFormDataId("-1");
                    }
                    ticketFormItemData.setTemplateId("-1");
                    ticketFormItemData.setItemOrder(99);
                    ticketFormItemData.setItemType(FormItemTypeEnum.TEXTAREA);
                    ticketFormItemData.setItemConfig("-1");
                    ticketFormItemData.setItemConfigExt("-1");
                    ticketFormItemData.setItemValue(item.getValue());
                    ticketFormItemData.setItemLabel(item.getTemplateId());
                    ticketFormItemData.setCreateBy(loginUserStr);
                    ticketFormItemData.setUpdateBy(loginUserStr);
                    ticketFormItemData.setCreateTime(now);
                    ticketFormItemData.setUpdateTime(now);
                    ticketFormItemDataList.add(ticketFormItemData);
                }
            }
            try {
                ticketFormItemDataService.saveBatch(ticketFormItemDataList);
                log.info("批量保存工单额外表单项数据成功, {}", ticketFormItemDataList.size());
                updateCount += batchData.size();
            } catch (Exception e) {
                log.error("批量保存工单额外表单项数据异常", e);
                failCount += batchData.size();
            }
        }

        resultMap.put("updateCount", updateCount);
        resultMap.put("failCount", failCount);
        log.info("工单额外表单项数据更新结果：更新{}条，失败{}条", updateCount, failCount);
        return resultMap;
    }

    @Override
    public Map<String, Integer> updateTicketValuesFromExcel(MultipartFile file) {
        Map<String, Integer> resultMap = new HashMap<>();
        List<NCSTicketValuesDto> ncsTicketValuesDtos = handleUploadFile(file, NCSTicketValuesDto.class);
        if (ncsTicketValuesDtos.isEmpty()) {
            resultMap.put("updateCount", 0);
            resultMap.put("failCount", 0);
            log.info("工单高级查询数据更新结果：更新0条，失败0条");
            return resultMap;
        }

        int updateCount = 0;
        int failCount = 0;

        for (NCSTicketValuesDto ncsTicketValuesDto : ncsTicketValuesDtos) {
            if (StringUtils.isBlank(ncsTicketValuesDto.getApplyId())) {
                log.warn("工单ID为空，跳过");
                continue;
            }
            Date now = new Date();
            try {
                LambdaUpdateWrapper<TicketFormItemValues> upFormItemValuesWrapper = new LambdaUpdateWrapper<>();
                upFormItemValuesWrapper.eq(TicketFormItemValues::getTicketDataId, ncsTicketValuesDto.getApplyId())
                        .set(TicketFormItemValues::getFormItemValue1, ncsTicketValuesDto.getFormItemValue1())
                        .set(TicketFormItemValues::getFormItemValue2, ncsTicketValuesDto.getFormItemValue2())
                        .set(TicketFormItemValues::getUpdateTime, now);
                ticketFormItemValuesMapper.update(null, upFormItemValuesWrapper);
                updateCount += 1;
            } catch (Exception e) {
                log.error("更新工单高级查询异常", e);
                failCount += 1;
            }
        }
        resultMap.put("updateCount", updateCount);
        resultMap.put("failCount", failCount);
        log.info("工单高级查询数据更新结果：更新{}条，失败{}条", updateCount, failCount);
        return resultMap;
    }

    @Override
    public Map<String, Integer> reverseTicketFlowNodeData(MultipartFile file) {
        Map<String, Integer> resultMap = new HashMap<>();
        List<NCSTicketValuesDto> ncsTicketValuesDtos = handleUploadFile(file, NCSTicketValuesDto.class);
        if (ncsTicketValuesDtos.isEmpty()) {
            resultMap.put("updateCount", 0);
            resultMap.put("failCount", 0);
            log.info("工单审批节点更新结果：更新0条，失败0条");
            return resultMap;
        }

        int updateCount = 0;
        int failCount = 0;
        for (NCSTicketValuesDto ncsTicketValuesDto : ncsTicketValuesDtos) {
            if (StringUtils.isBlank(ncsTicketValuesDto.getApplyId())) {
                log.warn("工单ID为空，跳过");
                continue;
            }
            Date now = new Date();
            try {
                List<TicketFlowNodeData> dbTicketFlowNodeDataList = ticketFlowNodeDataService.lambdaQuery()
                        .in(TicketFlowNodeData::getTicketDataId, ncsTicketValuesDto.getApplyId())
                        .isNull(TicketFlowNodeData::getDeleteTime)
                        .orderByDesc(TicketFlowNodeData::getNodeOrder)
                        .list();
                String currentNodeId = "-1";
                int nodeOrder = 1;
                for (TicketFlowNodeData ticketFlowNodeData : dbTicketFlowNodeDataList) {
                    ticketFlowNodeData.setPreNodeId(currentNodeId);
                    ticketFlowNodeData.setNodeOrder(nodeOrder++);
                    ticketFlowNodeData.setUpdateTime(now);
                    currentNodeId = ticketFlowNodeData.getId();
                }

                transactionTemplate.execute(action -> {
                    for (TicketFlowNodeData ticketFlowNodeData : dbTicketFlowNodeDataList) {
                        ticketFlowNodeDataMapper.updateById(ticketFlowNodeData);
                    }
                    return null;
                });
                updateCount += 1;
            } catch (Exception e) {
                log.error("工单审批节点更新异常", e);
                failCount += 1;
            }
        }

        resultMap.put("updateCount", updateCount);
        resultMap.put("failCount", failCount);
        log.info("工单审批节点更新结果：更新{}条，失败{}条", updateCount, failCount);
        return resultMap;
    }
}
