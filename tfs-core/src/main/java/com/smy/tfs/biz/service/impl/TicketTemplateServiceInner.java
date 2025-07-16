package com.smy.tfs.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.smy.framework.core.util.SequenceUtil;
import com.smy.tfs.api.constants.TfsBaseConstant;
import com.smy.tfs.api.dbo.*;
import com.smy.tfs.api.dto.*;
import com.smy.tfs.api.dto.base.CompareInfo;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.ticket_sla_service.TicketSlaConfigTemplateDto;
import com.smy.tfs.api.dto.ticket_sla_service.TicketSlaTemplateDto;
import com.smy.tfs.api.enums.*;
import com.smy.tfs.api.service.*;
import com.smy.tfs.biz.service.ITicketFormItemIdColMappingService;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 工单模版表 服务实现类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Slf4j
@Service
public class TicketTemplateServiceInner {

    @Resource
    private ITicketAppService ticketAppService;

    @Resource
    private ITicketTemplateService ticketTemplateService;

    @Resource
    private ITicketFormTemplateService ticketFormTemplateService;

    @Resource
    private ITicketFormItemTemplateService ticketFormItemTemplateService;

    @Resource
    private ITicketFlowTemplateService ticketFlowTemplateService;

    @Resource
    private ITicketFlowNodeTemplateService ticketFlowNodeTemplateService;

    @Resource
    private ITicketFlowEventTemplateService ticketFlowEventTemplateService;

    @Resource
    private ITicketFlowNodeExecutorTemplateService ticketFlowNodeExecutorTemplateService;

    @Resource
    private ITicketFlowNodeActionTemplateService ticketFlowNodeActionTemplateService;

    @Resource
    private ITicketFlowNodeRuleTemplateService ticketFlowNodeRuleTemplateService;

    @Resource
    private ITicketExecutorGroupService ticketExecutorGroupService;

    @Resource
    private ITicketFormItemIdColMappingService ticketFormItemIdColMappingService;

    @Resource
    private ITicketSlaTemplateService ticketSlaTemplateService;

    @Resource
    private ITicketSlaConfigTemplateService ticketSlaConfigTemplateService;


    public Response checkCreateTicketTemplate (TicketTemplateDto ticketTemplateDto){

        Response checkTicketTemplateParamsResp = checkTicketTemplateParams(ticketTemplateDto);
        if (!checkTicketTemplateParamsResp.isSuccess()) return checkTicketTemplateParamsResp;
        String ticketTemplateId = ticketTemplateDto.getId();
        //校验工单模版
        Optional<TicketTemplate> optional = ticketTemplateService.lambdaQuery().eq(TicketTemplate::getId, ticketTemplateId).eq(TicketTemplate::getTicketStatus, TicketTemplateStatusEnum.INIT).isNull(TicketTemplate::getDeleteTime).oneOpt();
        if (!optional.isPresent()) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("初始状态的工单模板不存在,id:%s", ticketTemplateId));
        }
        String appId = ticketTemplateDto.getAppId();
        //工单模板名称不能重复
        String ticketName = ticketTemplateDto.getTicketName();
        int ticketNameNum = ticketTemplateService.lambdaQuery().eq(TicketTemplate::getTicketName, ticketName).eq(TicketTemplate::getAppId, appId).isNull(TicketTemplate::getDeleteTime).count();
        if (ticketNameNum > 0)
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单模版名称(%s)不能重复", ticketName));
        //工单模板code不能重复
        String ticketTemplateCode = ticketTemplateDto.getTicketTemplateCode();

        if (StrUtil.isBlank(ticketTemplateCode)) {
            return new Response().success(null);
        } else {
            int ticketTemplateCodeNum = ticketTemplateService.lambdaQuery().eq(TicketTemplate::getTicketTemplateCode, ticketTemplateCode).eq(TicketTemplate::getAppId, appId).isNull(TicketTemplate::getDeleteTime).count();
            if (ticketTemplateCodeNum > 0)
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单模版code(%s)不能重复", ticketTemplateCode));
            return new Response().success(null);
        }
    }

    /**
     * @param ticketTemplateDto 大的工单模版对象
     * @param version           本次版本
     * @param idColMappingMap   id和列的映射
     * @return
     */
    public Response<TicketTemplateFullDto> makeObject (TicketTemplateDto ticketTemplateDto, Integer version, Map idColMappingMap, String createBy, String updateBy, Date createTime, Date updateTime){

        TicketTemplateFullDto ticketTemplateFullDto = new TicketTemplateFullDto();
        /**
         * 组装ticketTemplate对象
         */
        TicketTemplate ticketTemplate = new TicketTemplateDto().toTicketTemplate(ticketTemplateDto);
        ticketTemplate.setTicketStatus(TicketTemplateStatusEnum.ENABLE);
        if (ObjectHelper.isNotEmpty(version)) {
            ticketTemplate.setVersion(version);
        }
        ticketTemplate.setCreateBy(createBy);
        ticketTemplate.setCreateTime(createTime);
        ticketTemplate.setUpdateBy(updateBy);
        ticketTemplate.setUpdateTime(updateTime);
        ticketTemplateFullDto.setTicketTemplate(ticketTemplate);

        /**
         * 1.组装 TicketFormTemplate对象，并放入ticketTemplateFullDto里面
         * 2.构建 idMap(前端传的TicketFormItemDTO对象的Id:后端生成Id的映射)
         */
        Response<Map<String, String>> makeTicketFormTemplateResp = makeTicketFormTemplate(ticketTemplateDto, ticketTemplateFullDto, version, idColMappingMap, createBy, updateBy, createTime, updateTime);
        if (!BizResponseEnums.SUCCESS.getCode().equals(makeTicketFormTemplateResp.getCode())) {
            return Response.error(BizResponseEnums.getEnumByCode(makeTicketFormTemplateResp.getCode()), makeTicketFormTemplateResp.getMsg());
        }
        Map<String, String> idMap = makeTicketFormTemplateResp.getData();

        /**
         * 1.组装TicketFlowTemplate对象，并放入ticketTemplateFullDto里面
         */
        Response<Map<String, String>> makeTicketFlowTemplateResp = makeTicketFlowTemplate(ticketTemplateDto, ticketTemplateFullDto, idMap, createBy, updateBy, createTime, updateTime);
        if (!BizResponseEnums.SUCCESS.getCode().equals(makeTicketFlowTemplateResp.getCode())) {
            return Response.error(BizResponseEnums.getEnumByCode(makeTicketFlowTemplateResp.getCode()), makeTicketFlowTemplateResp.getMsg());
        }
        Map<String, String> flowNodeIdMap = makeTicketFlowTemplateResp.getData();


        /**
         * 1.组装TicketSlaTemplate对象，并放入ticketTemplateFullDto里面
         */
        Response<Map<String, String>> makeTicketSlaTemplateResp = makeTicketSlaTemplate(ticketTemplateDto, ticketTemplateFullDto, flowNodeIdMap, createBy, updateBy, createTime, updateTime);
        if (!BizResponseEnums.SUCCESS.getCode().equals(makeTicketSlaTemplateResp.getCode())) {
            return Response.error(BizResponseEnums.getEnumByCode(makeTicketSlaTemplateResp.getCode()), makeTicketSlaTemplateResp.getMsg());
        }


        return Response.success(ticketTemplateFullDto);
    }

    private Response<Map<String, String>> makeTicketFlowTemplate (TicketTemplateDto ticketTemplateDto, TicketTemplateFullDto ticketTemplateFullDto, Map<String, String> idMap, String createBy, String updateBy, Date createTime, Date updateTime){
        //组装TicketFlowTemplate对象，并放入jsonObject里面
        TicketFlowTemplateDto ticketFlowTemplateDto = ticketTemplateDto.getTicketFlowTemplateDto();
        String ticketTemplateId = ticketTemplateDto.getId();
        if (ObjectHelper.isEmpty(ticketFlowTemplateDto)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "流程模板为空");
        }
        TicketFlowTemplate ticketFlowTemplate = new TicketFlowTemplateDto().toTicketFlowTemplate(ticketFlowTemplateDto);
        if (ObjectHelper.isEmpty(ticketFlowTemplate.getId()) || ticketFlowTemplate.getId().contains(TfsBaseConstant.front)) {
            ticketFlowTemplate.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_TEMPLATE));
        }
        ticketFlowTemplate.setCreateBy(createBy);
        ticketFlowTemplate.setCreateTime(createTime);
        ticketFlowTemplate.setUpdateBy(updateBy);
        ticketFlowTemplate.setUpdateTime(updateTime);
        ticketFlowTemplate.setTicketTemplateId(ticketTemplateId);
        ticketTemplateFullDto.setTicketFlowTemplate(ticketFlowTemplate);

        //组装TicketFlowNodeTemplate对象。
        String ticketFlowTemplateId = ticketFlowTemplate.getId();
        List<TicketFlowNodeTemplateDto> ticketFlowNodeTemplateDtoList = ticketFlowTemplateDto.getTicketFlowNodeTemplateDtoList();
        if (ObjectHelper.isEmpty(ticketFlowNodeTemplateDtoList)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "流程节点模板为空");
        }
        //前端传的ticketFlowNodeTemplateDto对象的Id:后端生成Id的映射
        Map<String, String> flowNodeIdMap = new HashMap();
        Response<List<TicketFlowNodeTemplate>> ticketFlowNodeTemplateListResp = makeTicketFlowNodeTemplateList(ticketFlowNodeTemplateDtoList, ticketTemplateId, ticketFlowTemplateId, createBy, updateBy, createTime, updateTime, flowNodeIdMap);
        if (!ticketFlowNodeTemplateListResp.isSuccess()) {
            return Response.error(BizResponseEnums.getEnumByCode(ticketFlowNodeTemplateListResp.getCode()), ticketFlowNodeTemplateListResp.getMsg());
        }
        List<TicketFlowNodeTemplate> ticketFlowNodeTemplateList = ticketFlowNodeTemplateListResp.getData();
        if (ObjectHelper.isEmpty(ticketFlowNodeTemplateList)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "流程节点模板为空");
        }
        ticketTemplateFullDto.setTicketFlowNodeTemplateList(ticketFlowNodeTemplateList);

        List<TicketFlowEventTemplate> ticketFlowEventTemplateList = new ArrayList<>();
        List<TicketFlowNodeRuleTemplate> ticketFlowNodeRuleTemplateList = new ArrayList<>();
        List<TicketFlowNodeExecutorTemplate> ticketFlowNodeExecutorTemplateList = new ArrayList<>();
        List<TicketFlowNodeActionTemplate> ticketFlowNodeActionTemplateList = new ArrayList<>();
        for (TicketFlowNodeTemplateDto ticketFlowNodeTemplateDto : ticketFlowNodeTemplateDtoList) {
            //组装List<TicketFlowEventTemplate>对象
            Response<List<TicketFlowEventTemplate>> ticketFlowEventTemplateListResp = makeTicketFlowEventTemplateList(ticketFlowNodeTemplateDto, ticketTemplateId, createBy, updateBy, createTime, updateTime);
            if (!ticketFlowEventTemplateListResp.isSuccess()) {
                return Response.error(BizResponseEnums.getEnumByCode(ticketFlowEventTemplateListResp.getCode()), ticketFlowEventTemplateListResp.getMsg());
            }
            List<TicketFlowEventTemplate> tfetList = ticketFlowEventTemplateListResp.getData();
            if (ObjectHelper.isNotEmpty(tfetList)) {
                ticketFlowEventTemplateList.addAll(tfetList);
            }
            //组装List<TicketFlowNodeRuleTemplate>对象
            Response<List<TicketFlowNodeRuleTemplate>> ticketFlowNodeRuleTemplateListResp = makeTicketFlowNodeRuleTemplateDtoList(ticketFlowNodeTemplateDto, ticketTemplateId, idMap, createBy, updateBy, createTime, updateTime);
            if (!ticketFlowNodeRuleTemplateListResp.isSuccess()) {
                return Response.error(BizResponseEnums.getEnumByCode(ticketFlowNodeRuleTemplateListResp.getCode()), ticketFlowNodeRuleTemplateListResp.getMsg());
            }
            List<TicketFlowNodeRuleTemplate> tfnrtList = ticketFlowNodeRuleTemplateListResp.getData();
            if (ObjectHelper.isNotEmpty(tfnrtList)) {
                ticketFlowNodeRuleTemplateList.addAll(tfnrtList);
            }
            //组装List<TicketFlowNodeExecutorTemplateDto>对象
            Response<List<TicketFlowNodeExecutorTemplate>> ticketFlowNodeExecutorTemplateListResp = makeTicketFlowNodeExecutorTemplateDtoList(ticketFlowNodeTemplateDto, ticketTemplateId, createBy, updateBy, createTime, updateTime);
            if (!ticketFlowNodeExecutorTemplateListResp.isSuccess()) {
                return Response.error(BizResponseEnums.getEnumByCode(ticketFlowNodeExecutorTemplateListResp.getCode()), ticketFlowNodeExecutorTemplateListResp.getMsg());
            }
            List<TicketFlowNodeExecutorTemplate> tfnetList = ticketFlowNodeExecutorTemplateListResp.getData();
            if (ObjectHelper.isNotEmpty(tfnetList)) {
                ticketFlowNodeExecutorTemplateList.addAll(tfnetList);
            }

            //组装List<TicketFlowNodeActionTemplateDto>对象
            Response<List<TicketFlowNodeActionTemplate>> ticketFlowNodeActionTemplateDtoListResp = makeTicketFlowNodeActionTemplateDtoList(ticketFlowNodeTemplateDto, ticketTemplateId, idMap, createBy, updateBy, createTime, updateTime);
            if (!ticketFlowNodeActionTemplateDtoListResp.isSuccess()) {
                return Response.error(BizResponseEnums.getEnumByCode(ticketFlowNodeActionTemplateDtoListResp.getCode()), ticketFlowNodeActionTemplateDtoListResp.getMsg());
            }
            List<TicketFlowNodeActionTemplate> tfnatList = ticketFlowNodeActionTemplateDtoListResp.getData();
            if (ObjectHelper.isNotEmpty(tfnatList)) {
                ticketFlowNodeActionTemplateList.addAll(tfnatList);
            }
        }
        ticketTemplateFullDto.setTicketFlowEventTemplateList(ticketFlowEventTemplateList);
        ticketTemplateFullDto.setTicketFlowNodeRuleTemplateList(ticketFlowNodeRuleTemplateList);
        ticketTemplateFullDto.setTicketFlowNodeExecutorTemplateList(ticketFlowNodeExecutorTemplateList);
        ticketTemplateFullDto.setTicketFlowNodeActionTemplateList(ticketFlowNodeActionTemplateList);
        return new Response<>().success(flowNodeIdMap);
    }

    private Response<Map<String, String>> makeTicketSlaTemplate (TicketTemplateDto ticketTemplateDto, TicketTemplateFullDto ticketTemplateFullDto, Map<String, String> flowNodeIdMap, String createBy, String updateBy, Date createTime, Date updateTime){
        //组装TicketSlaTemplate对象，并放入jsonObject里面
        TicketSlaTemplateDto ticketSlaTemplateDto = ticketTemplateDto.getTicketSlaTemplateDto();
        String ticketTemplateId = ticketTemplateDto.getId();
        if (ObjectHelper.isEmpty(ticketSlaTemplateDto)) {
            log.info(String.format("模板(%s)sla为空", ticketTemplateId));
            return Response.success();
        }
        if (ObjectHelper.isEmpty(ticketSlaTemplateDto.getId()) || ticketSlaTemplateDto.getId().contains(TfsBaseConstant.front)) {
            ticketSlaTemplateDto.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_SLA_TEMPLATE));
        }
        TicketSlaTemplate ticketSlaTemplate = new TicketSlaTemplateDto().toTicketSlaTemplate(ticketSlaTemplateDto);

        ticketSlaTemplate.setCreateBy(createBy);
        ticketSlaTemplate.setCreateTime(createTime);
        ticketSlaTemplate.setUpdateBy(updateBy);
        ticketSlaTemplate.setUpdateTime(updateTime);
        ticketSlaTemplate.setTicketTemplateId(ticketTemplateId);
        ticketTemplateFullDto.setTicketSlaTemplate(ticketSlaTemplate);

        //组装List<TicketSlaConfigTemplateDto>对象
        List<TicketSlaConfigTemplateDto> ticketSlaConfigTemplateDtoList = ticketSlaTemplateDto.getTicketSlaConfigTemplateDtoList();
        if (ObjectHelper.isEmpty(ticketSlaConfigTemplateDtoList)) {
            log.info(String.format("模板(%s)sla配置为空", ticketTemplateId));
            return Response.success();
        }
        List<TicketSlaConfigTemplate> tsctList = null;
        if (ObjectHelper.isNotEmpty(ticketSlaConfigTemplateDtoList)) {
            tsctList = ticketSlaConfigTemplateDtoList.stream()
                    .map(ticketSlaConfigTemplateDto -> {
                        TicketSlaConfigTemplate ticketSlaConfigTemplate = new TicketSlaConfigTemplateDto().toTicketSlaConfigTemplate(ticketSlaConfigTemplateDto);
                        if (StringUtils.isEmpty(ticketSlaConfigTemplate.getId()) || ticketSlaConfigTemplate.getId().contains(TfsBaseConstant.front)) {
                            String targetId = SequenceUtil.getId(TFSTableIdCode.ID_TICKET_SLA_CONFIG_TEMPLATE);
                            ticketSlaConfigTemplate.setId(targetId);
                        }
                        String configTypeContent = ticketSlaConfigTemplate.getConfigTypeContent();
                        if (StringUtils.isNotEmpty(configTypeContent)) {
                            //configTypeContent里面的节点id转换成后台数据库生成的
                            String[] nodeIds = configTypeContent.split(",");
                            List newFlowNodeIdList = Arrays.stream(nodeIds)
                                    .map(it -> {
                                        if (ObjectHelper.isNotEmpty(it) && ObjectHelper.isNotEmpty(flowNodeIdMap) && ObjectHelper.isNotEmpty(flowNodeIdMap.get(it))) {
                                            return flowNodeIdMap.get(it);
                                        } else {
                                            return it;
                                        }
                                    }).collect(Collectors.toList());
                            if (ObjectHelper.isNotEmpty(newFlowNodeIdList)) {
                                ticketSlaConfigTemplate.setConfigTypeContent(String.join(",", newFlowNodeIdList));
                            }
                        }
                        //保留上次的创建人和创建时间
                        if (StringUtils.isNotEmpty(ticketSlaConfigTemplateDto.getCreateBy())) {
                            ticketSlaConfigTemplate.setCreateBy(ticketSlaConfigTemplateDto.getCreateBy());
                        } else {
                            ticketSlaConfigTemplate.setCreateBy(createBy);
                        }
                        if (ObjectHelper.isNotEmpty(ticketSlaConfigTemplateDto.getCreateTime())) {
                            ticketSlaConfigTemplate.setCreateTime(ticketSlaConfigTemplateDto.getCreateTime());
                        } else {
                            ticketSlaConfigTemplate.setCreateTime(createTime);
                        }
                        ticketSlaConfigTemplate.setUpdateBy(updateBy);
                        ticketSlaConfigTemplate.setUpdateTime(updateTime);
                        ticketSlaConfigTemplate.setTicketSlaTemplateId(ticketSlaTemplateDto.getId());
                        ticketSlaConfigTemplate.setTicketTemplateId(ticketTemplateId);
                        return ticketSlaConfigTemplate;
                    }).collect(Collectors.toList());
        }
        ticketTemplateFullDto.setTicketSlaConfigTemplateList(tsctList);
        return new Response<>().success();
    }

    private Response<List<TicketFlowNodeExecutorTemplate>> makeTicketFlowNodeExecutorTemplateDtoList (TicketFlowNodeTemplateDto ticketFlowNodeTemplateDto, String ticketTemplateId, String createBy, String updateBy, Date createTime, Date updateTime){
        //组装List<TicketFlowNodeExecutorTemplateDto>对象
        List<TicketFlowNodeExecutorTemplateDto> ticketFlowNodeExecutorTemplateDtoList = ticketFlowNodeTemplateDto.getTicketFlowNodeExecutorTemplateDtoList();
        List<TicketFlowNodeExecutorTemplate> tfnetList = null;
        if (ObjectHelper.isNotEmpty(ticketFlowNodeExecutorTemplateDtoList)) {
            tfnetList = ticketFlowNodeExecutorTemplateDtoList.stream()
                    .map(ticketFlowNodeExecutorTemplateDto -> {
                        TicketFlowNodeExecutorTemplate ticketFlowNodeExecutorTemplate = new TicketFlowNodeExecutorTemplateDto().toTicketFlowNodeExecutorTemplate(ticketFlowNodeExecutorTemplateDto);
                        if (ObjectHelper.isEmpty(ticketFlowNodeExecutorTemplateDto.getId()) || ticketFlowNodeExecutorTemplateDto.getId().contains(TfsBaseConstant.front)) {
                            String targetId = SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_EXECUTOR_TEMPLATE);
                            ticketFlowNodeExecutorTemplate.setId(targetId);
                        }
                        ticketFlowNodeExecutorTemplate.setCreateBy(createBy);
                        ticketFlowNodeExecutorTemplate.setCreateTime(createTime);
                        ticketFlowNodeExecutorTemplate.setUpdateBy(updateBy);
                        ticketFlowNodeExecutorTemplate.setUpdateTime(updateTime);
                        ticketFlowNodeExecutorTemplate.setTicketFlowNodeTemplateId(ticketFlowNodeTemplateDto.getId());
                        ticketFlowNodeExecutorTemplate.setTicketTemplateId(ticketTemplateId);
                        return ticketFlowNodeExecutorTemplate;
                    }).collect(Collectors.toList());
        }
        return new Response<>().success(tfnetList);
    }

    private Response<List<TicketFlowNodeActionTemplate>> makeTicketFlowNodeActionTemplateDtoList (TicketFlowNodeTemplateDto ticketFlowNodeTemplateDto, String ticketTemplateId, Map<String, String> idMap, String createBy, String updateBy, Date createTime, Date updateTime){
        //组装List<TicketFlowNodeActionTemplateDto>对象
        List<TicketFlowNodeActionTemplateDto> ticketFlowNodeActionTemplateDtoList = ticketFlowNodeTemplateDto.getTicketFlowNodeActionTemplateDtoList();
        List<TicketFlowNodeActionTemplate> tfnetList = null;
        if (ObjectHelper.isNotEmpty(ticketFlowNodeActionTemplateDtoList)) {
            tfnetList = ticketFlowNodeActionTemplateDtoList.stream()
                    .map(ticketFlowNodeActionTemplateDto -> {
                        TicketFlowNodeActionTemplate ticketFlowNodeActionTemplate = new TicketFlowNodeActionTemplateDto().toTicketFlowNodeActionTemplate(ticketFlowNodeActionTemplateDto);
                        if (ObjectHelper.isEmpty(ticketFlowNodeActionTemplateDto.getId()) || ticketFlowNodeActionTemplateDto.getId().contains(TfsBaseConstant.front)) {
                            String targetId = SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_ACTION_TEMPLATE);
                            ticketFlowNodeActionTemplate.setId(targetId);
                        }
                        ticketFlowNodeActionTemplate.setCreateBy(createBy);
                        ticketFlowNodeActionTemplate.setCreateTime(createTime);
                        ticketFlowNodeActionTemplate.setUpdateBy(updateBy);
                        ticketFlowNodeActionTemplate.setUpdateTime(updateTime);
                        ticketFlowNodeActionTemplate.setTicketFlowNodeTemplateId(ticketFlowNodeTemplateDto.getId());
                        ticketFlowNodeActionTemplate.setTicketTemplateId(ticketTemplateId);
                        //{update:[{"code":"组件ID","value": "aaa"}]}
                        if (StringUtils.isNotEmpty(ticketFlowNodeActionTemplate.getActionValue())) {
                            JSONObject actionJO = JSONObject.parseObject(ticketFlowNodeActionTemplate.getActionValue());
                            if (actionJO != null) {
                                JSONArray updateJA = actionJO.getJSONArray("update_ticket");
                                if (updateJA != null && updateJA.size() > 0) {
                                    for (int i = 0; i < updateJA.size(); i++) {
                                        JSONObject updateObj = updateJA.getJSONObject(i);
                                        String code = updateObj.getString("code");
                                        if (StringUtils.isNotEmpty(code)) {
                                            if (idMap.containsKey(code)) {
                                                updateObj.put("code", idMap.get(code));
                                            }
                                        }
                                    }
                                    actionJO.put("update_ticket", updateJA);
                                }
                                ticketFlowNodeActionTemplate.setActionValue(actionJO.toJSONString());
                            }
                        }
                        return ticketFlowNodeActionTemplate;
                    }).collect(Collectors.toList());
        }
        return new Response<>().success(tfnetList);
    }

    /**
     * 组装List<TicketFlowEventTemplate>对象
     *
     * @param ticketFlowNodeTemplateDto
     * @param ticketTemplateId
     * @param idMap
     * @return
     */
    private Response<List<TicketFlowNodeRuleTemplate>> makeTicketFlowNodeRuleTemplateDtoList (TicketFlowNodeTemplateDto ticketFlowNodeTemplateDto, String ticketTemplateId, Map<String, String> idMap, String createBy, String updateBy, Date createTime, Date updateTime){
        //组装List<TicketFlowEventTemplate>对象
        List<TicketFlowNodeRuleTemplateDto> ticketFlowNodeRuleTemplateDtoList = ticketFlowNodeTemplateDto.getTicketFlowNodeRuleTemplateDtoList();
        List<TicketFlowNodeRuleTemplate> tfnrtList = null;
        if (ObjectHelper.isNotEmpty(ticketFlowNodeRuleTemplateDtoList)) {
            tfnrtList = ticketFlowNodeRuleTemplateDtoList.stream()
                    .map(ticketFlowNodeRuleTemplateDto -> {
                        TicketFlowNodeRuleTemplate ticketFlowNodeRuleTemplate = new TicketFlowNodeRuleTemplate();
                        if (ObjectHelper.isEmpty(ticketFlowNodeRuleTemplateDto.getId()) || ticketFlowNodeRuleTemplateDto.getId().contains(TfsBaseConstant.front)) {
                            String targetId = SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_RULE_TEMPLATE);
                            ticketFlowNodeRuleTemplate.setId(targetId);
                        }
                        ticketFlowNodeRuleTemplate.setCreateBy(createBy);
                        ticketFlowNodeRuleTemplate.setCreateTime(createTime);
                        ticketFlowNodeRuleTemplate.setUpdateBy(updateBy);
                        ticketFlowNodeRuleTemplate.setUpdateTime(updateTime);
                        ticketFlowNodeRuleTemplate.setTicketFlowNodeTemplateId(ticketFlowNodeTemplateDto.getId());
                        ticketFlowNodeRuleTemplate.setTicketTemplateId(ticketTemplateId);
                        String ruleInfoListStr = ticketFlowNodeRuleTemplateDto.getRuleInfoList();
                        if (ObjectHelper.isNotEmpty(ruleInfoListStr)) {
                            List<List<CompareInfo>> compareInfoTwoDList = null;
                            var compareInfoTwoDListRsp = CompareInfo.getTwoDList(ruleInfoListStr);
                            if (!compareInfoTwoDListRsp.isSuccess()) {
                                log.error("error");
                            }
                            compareInfoTwoDList = compareInfoTwoDListRsp.getData();

                            for (List<CompareInfo> compareInfoList : compareInfoTwoDList) {
                                for (CompareInfo compareInfo : compareInfoList) {
                                    String compareId = compareInfo.getCompareId();
                                    if (ObjectHelper.isNotEmpty(compareId) && ObjectHelper.isNotEmpty(idMap.get(compareId))) {
                                        compareInfo.setCompareId(idMap.get(compareId));
                                    }
                                }
                            }
                            ticketFlowNodeRuleTemplate.setRuleInfoList(JSONUtil.toJsonStr(compareInfoTwoDList));
                        }
                        return ticketFlowNodeRuleTemplate;
                    }).collect(Collectors.toList());
        }
        return new Response<>().success(tfnrtList);
    }


    private Response<List<TicketFlowEventTemplate>> makeTicketFlowEventTemplateList (TicketFlowNodeTemplateDto ticketFlowNodeTemplateDto, String ticketTemplateId, String createBy, String updateBy, Date createTime, Date updateTime){
        //组装List<TicketFlowEventTemplate>对象
        List<TicketFlowEventTemplateDto> ticketFlowEventTemplateDtoList = ticketFlowNodeTemplateDto.getTicketFlowEventTemplateDtoList();
        List<TicketFlowEventTemplate> tfetList = null;
        if (ObjectHelper.isNotEmpty(ticketFlowEventTemplateDtoList)) {
            tfetList = ticketFlowEventTemplateDtoList.stream()
                    .map(ticketFlowEventTemplateDto -> {
                        TicketFlowEventTemplate ticketFlowEventTemplate = new TicketFlowEventTemplateDto().toTicketFlowEventTemplate(ticketFlowEventTemplateDto);
                        if (ObjectHelper.isEmpty(ticketFlowEventTemplate.getId()) || ticketFlowEventTemplate.getId().contains(TfsBaseConstant.front)) {
                            ticketFlowEventTemplate.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_TEMPLATE));
                        }
                        ticketFlowEventTemplate.setCreateBy(createBy);
                        ticketFlowEventTemplate.setCreateTime(createTime);
                        ticketFlowEventTemplate.setUpdateBy(updateBy);
                        ticketFlowEventTemplate.setUpdateTime(updateTime);
                        ticketFlowEventTemplate.setTicketFlowNodeTemplateId(ticketFlowNodeTemplateDto.getId());
                        ticketFlowEventTemplate.setTicketTemplateId(ticketTemplateId);
                        return ticketFlowEventTemplate;
                    }).collect(Collectors.toList());
        }
        return new Response<>().success(tfetList);
    }

    /**
     * @param ticketFlowNodeTemplateDtoList
     * @param ticketTemplateId
     * @param ticketFlowTemplateId
     * @param createBy
     * @param updateBy
     * @param createTime
     * @param updateTime
     * @param flowNodeIdMap                 前端传的TicketFlowNodeTemplateDto对象的nodeId 和后端生成nodeId的映射。
     * @return
     */

    public Response<List<TicketFlowNodeTemplate>> makeTicketFlowNodeTemplateList (List<TicketFlowNodeTemplateDto> ticketFlowNodeTemplateDtoList, String ticketTemplateId, String ticketFlowTemplateId, String createBy, String updateBy, Date createTime, Date updateTime, Map<String, String> flowNodeIdMap){
        //组装List<TicketFlowNodeTemplate>对象
        if (ObjectHelper.isEmpty(ticketFlowNodeTemplateDtoList)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "流程节点模板为空");
        }
        List<TicketFlowNodeTemplate> ticketFlowNodeTemplateList = ticketFlowNodeTemplateDtoList.stream()
                .map(ticketFlowNodeTemplateDto -> {
                    TicketFlowNodeTemplate ticketFlowNodeTemplate = new TicketFlowNodeTemplateDto().toTicketFlowNodeTemplate(ticketFlowNodeTemplateDto);
                    String sourceId = ticketFlowNodeTemplate.getId();
                    if (ObjectHelper.isNotEmpty(sourceId) && sourceId.contains(TfsBaseConstant.front)) {
                        String targetId = SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_TEMPLATE);
                        flowNodeIdMap.put(sourceId, targetId);
                        ticketFlowNodeTemplate.setId(targetId);
                        ticketFlowNodeTemplate.setTicketTemplateId(ticketTemplateId);
                        ticketFlowNodeTemplate.setTicketFlowTemplateId(ticketFlowTemplateId);
                        //后面取值方便
                        ticketFlowNodeTemplateDto.setId(targetId);
                    }
                    ticketFlowNodeTemplate.setCreateBy(createBy);
                    ticketFlowNodeTemplate.setCreateTime(createTime);
                    ticketFlowNodeTemplate.setUpdateBy(updateBy);
                    ticketFlowNodeTemplate.setUpdateTime(updateTime);
                    return ticketFlowNodeTemplate;
                }).collect(Collectors.toList());
        ticketFlowNodeTemplateList.stream().forEach(ticketFlowNodeTemplate -> {
            String preNodeId = ticketFlowNodeTemplate.getPreNodeId();
            if (ObjectHelper.isNotEmpty(preNodeId)) {
                //preNodeId转换成后台数据库生成的
                String[] preNodeIds = preNodeId.split(",");
                List preNodeIdList = Arrays.stream(preNodeIds)
                        .map(it -> {
                            if (ObjectHelper.isNotEmpty(it) && ObjectHelper.isNotEmpty(flowNodeIdMap) && ObjectHelper.isNotEmpty(flowNodeIdMap.get(it))) {
                                return flowNodeIdMap.get(it);
                            } else {
                                return it;
                            }
                        }).collect(Collectors.toList());
                if (ObjectHelper.isNotEmpty(preNodeIdList)) {
                    ticketFlowNodeTemplate.setPreNodeId(String.join(",", preNodeIdList));
                }
            }
        });

        return Response.success(ticketFlowNodeTemplateList);

    }

    private Response<Map<String, String>> makeTicketFormTemplate (TicketTemplateDto ticketTemplateDto, TicketTemplateFullDto ticketTemplateFullDto, Integer version, Map<String, String> idColMappingMap, String createBy, String updateBy, Date createTime, Date updateTime){
        //组装TicketFormTemplate
        TicketFormTemplateDto ticketFormTemplateDto = ticketTemplateDto.getTicketFormTemplateDto();
        String ticketTemplateId = ticketTemplateDto.getId();
        if (ObjectHelper.isEmpty(ticketFormTemplateDto)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "表单模板为空");
        }
        TicketFormTemplate ticketFormTemplate = new TicketFormTemplate();
        if (ObjectHelper.isEmpty(ticketFormTemplateDto.getId()) || ticketFormTemplateDto.getId().contains(TfsBaseConstant.front)) {
            ticketFormTemplate.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FORM_TEMPLATE));
        } else {
            ticketFormTemplate.setId(ticketFormTemplateDto.getId());
        }
        ticketFormTemplate.setCreateBy(createBy);
        ticketFormTemplate.setCreateTime(createTime);
        ticketFormTemplate.setUpdateBy(updateBy);
        ticketFormTemplate.setUpdateTime(updateTime);
        ticketFormTemplate.setTicketTemplateId(ticketTemplateId);
        //填充 TicketFormTemplate
        ticketTemplateFullDto.setTicketFormTemplate(ticketFormTemplate);

        String ticketFormTemplateId = ticketFormTemplate.getId();

        List<TicketFormItemTemplateDto> ticketFormItemTemplateDtoList = ticketFormTemplateDto.getTicketFormItemTemplateDtoList();
        if (ObjectHelper.isEmpty(ticketFormItemTemplateDtoList)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "模板表单项为空");
        }
        //前端传的TicketFormItemDTO对象的Id:后端生成Id的映射
        Map<String, String> idMap = new HashMap();
        Response<List<TicketFormItemTemplate>> ticketFormItemTemplateListResp = makeTicketFormItemTemplateList(ticketTemplateId, ticketFormTemplateId, ticketFormItemTemplateDtoList, idMap, createBy, updateBy, createTime, updateTime);
        if (!ticketFormItemTemplateListResp.isSuccess()) {
            return Response.error(BizResponseEnums.getEnumByCode(ticketFormItemTemplateListResp.getCode()), ticketFormItemTemplateListResp.getMsg());
        }
        List<TicketFormItemTemplate> ticketFormItemTemplateList = ticketFormItemTemplateListResp.getData();
        if (ObjectHelper.isEmpty(ticketFormItemTemplateList)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "模板表单项为空");
        }
        //填充 List<TicketFormItemTemplate>
        ticketTemplateFullDto.setTicketFormItemTemplateList(ticketFormItemTemplateList);

        Response<List<TicketFormItemIdColMapping>> ticketFormItemIdColMappingListResp = getTicketFormItemIdColMappingList(ticketFormItemTemplateList, version, idColMappingMap);
        if (!ticketFormItemIdColMappingListResp.isSuccess()) {
            return Response.error(BizResponseEnums.getEnumByCode(ticketFormItemIdColMappingListResp.getCode()), ticketFormItemIdColMappingListResp.getMsg());
        }
        List<TicketFormItemIdColMapping> ticketFormItemIdColMappingList = ticketFormItemIdColMappingListResp.getData();
        if (ObjectHelper.isNotEmpty(ticketFormItemIdColMappingList)) {
            //填充 List<TicketFormItemIdColMapping>
            ticketTemplateFullDto.setTicketFormItemIdColMappingList(ticketFormItemIdColMappingList);
        }
        return new Response().success(idMap);
    }

    /**
     * 组装TicketFormItemTemplate对象
     */
    private Response<List<TicketFormItemTemplate>> makeTicketFormItemTemplateList (String ticketTemplateId, String ticketFormTemplateId, List<TicketFormItemTemplateDto> ticketFormItemTemplateDtoList, Map<String, String> idMap, String createBy, String updateBy, Date createTime, Date updateTime){

        if (ObjectHelper.isEmpty(ticketFormItemTemplateDtoList)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "模板表单项为空");
        }
        List<TicketFormItemTemplate> ticketFormItemTemplateList = ticketFormItemTemplateDtoList.stream()
                .map(ticketFormItemTemplateDto -> {
                    TicketFormItemTemplate ticketFormItemTemplate = new TicketFormItemTemplateDto().toTicketFormItemTemplate(ticketFormItemTemplateDto);
                    String sourceId = ticketFormItemTemplateDto.getId();
                    if (ObjectHelper.isNotEmpty(sourceId) && sourceId.contains(TfsBaseConstant.front)) {
                        String targetId = SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FORM_ITEM_TEMPLATE);
                        idMap.put(sourceId, targetId);
                        ticketFormItemTemplate.setId(targetId);
                    }
                    if (ticketFormItemTemplate.getItemType() != null) {
                        FormItemTypeEnum itemType = ticketFormItemTemplate.getItemType();

                        // 使用Set装需要加默认值的类型
                        Set<FormItemTypeEnum> targetTypes = EnumSet.of(
                                FormItemTypeEnum.INPUT, FormItemTypeEnum.TEXTAREA,
                                FormItemTypeEnum.SELECT, FormItemTypeEnum.SELECTMULTIPLE,
                                FormItemTypeEnum.CASCADER
                        );
                        if (targetTypes.contains(itemType)) {
                            if (StrUtil.isNotEmpty(ticketFormItemTemplate.getItemConfig())) {
                                try {
                                    JSONObject itemConfig = JSON.parseObject(ticketFormItemTemplate.getItemConfig());
                                    if (itemConfig != null) {
                                        itemConfig.putIfAbsent("defaultValue", null);
                                        ticketFormItemTemplate.setItemConfig(itemConfig.toJSONString(JSONWriter.Feature.WriteNulls));
                                    }
                                }catch (Exception e) {
                                    log.warn("makeTicketFormItemTemplateList JSON解析失败: {}", ticketFormItemTemplate.getItemConfig(), e);
                                }

                            } else {
                                JSONObject defaultValue = (JSONObject) new JSONObject().put("defaultValue", null);
                                ticketFormItemTemplate.setItemConfig(defaultValue.toJSONString(JSONWriter.Feature.WriteNulls));
                            }
                        }
                    }

                    ticketFormItemTemplate.setCreateBy(createBy);
                    ticketFormItemTemplate.setCreateTime(createTime);
                    ticketFormItemTemplate.setUpdateBy(updateBy);
                    ticketFormItemTemplate.setUpdateTime(updateTime);
                    ticketFormItemTemplate.setTicketTemplateId(ticketTemplateId);
                    ticketFormItemTemplate.setTicketFormTemplateId(ticketFormTemplateId);
                    return ticketFormItemTemplate;
                }).collect(Collectors.toList());

        ticketFormItemTemplateList.stream().forEach(ticketFormItemTemplate -> {
            String itemParentId = ticketFormItemTemplate.getItemParentId();
            if (ObjectHelper.isNotEmpty(itemParentId) && ObjectHelper.isNotEmpty(idMap) && ObjectHelper.isNotEmpty(idMap.get(itemParentId))) {
                //设置父节点id
                ticketFormItemTemplate.setItemParentId(idMap.get(itemParentId));
            }
            //把itemVisibleRule的compareId解析并转换。
            String itemVisibleRuleStr = ticketFormItemTemplate.getItemVisibleRule();
            if (ObjectHelper.isNotEmpty(itemVisibleRuleStr)) {
                var compareInfoTwoDListRsp = CompareInfo.getTwoDList(itemVisibleRuleStr);
                List<List<CompareInfo>> compareInfoTwoDList = new ArrayList<>();
                if (compareInfoTwoDListRsp.isSuccess()) {
                    compareInfoTwoDList = compareInfoTwoDListRsp.getData();
                    for (List<CompareInfo> compareInfoList : compareInfoTwoDList) {
                        for (CompareInfo compareInfo : compareInfoList) {
                            String compareId = compareInfo.getCompareId();
                            if (ObjectHelper.isNotEmpty(compareId) && ObjectHelper.isNotEmpty(idMap.get(compareId))) {
                                compareInfo.setCompareId(idMap.get(compareId));
                            }
                        }
                    }
                }
                ticketFormItemTemplate.setItemVisibleRule(JSONUtil.toJsonStr(compareInfoTwoDList));
            }
        });
        return Response.success(ticketFormItemTemplateList);
    }

    private List<TicketFormItemTemplate> processDefaultValue (List<TicketFormItemTemplate> templates){

        return templates;
    }

    public Response<List<TicketFormItemIdColMapping>> getTicketFormItemIdColMappingList (List<TicketFormItemTemplate> ticketFormItemTemplateList, Integer version, Map<String, String> idColMappingMap){
        //组装List<TicketFormItemIdColMapping>对象
        Field[] fields = TicketFormItemValues.class.getDeclaredFields();
        List<String> allColList = Arrays.stream(fields).filter(field -> field.getName().startsWith(TfsBaseConstant.FORM_ITEM_VALUE_PREFIX)).map(field -> StringUtils.toUnderScoreCase(field.getName())).collect(Collectors.toList());
        List<String> existColList = new ArrayList();
        if (ObjectHelper.isNotEmpty(idColMappingMap)) {
            existColList = idColMappingMap.values().stream().collect(Collectors.toList());
            allColList.removeAll(existColList);
        }
        List<TicketFormItemIdColMapping> ticketFormItemIdColMappingList = new ArrayList<>();
        for (int i = 1; i <= ticketFormItemTemplateList.size(); i++) {
            TicketFormItemTemplate ticketFormItemTemplate = ticketFormItemTemplateList.get(i - 1);
            if (ObjectHelper.isEmpty(ticketFormItemTemplate.getItemAdvancedSearch()) || !ticketFormItemTemplate.getItemAdvancedSearch().getBooleanCode())
                continue;
            String ticketFormItemId = ticketFormItemTemplate.getId();
            String ttId = ticketFormItemTemplate.getTicketTemplateId();
            TicketFormItemIdColMapping ticketFormItemIdColMapping = new TicketFormItemIdColMapping();
            //无论是新增还是修改，id都是新的。
            ticketFormItemIdColMapping.setId(SequenceUtil.getId(TFSTableIdCode.TICKET_FORM_ITEM_ID_COL_MAPPING));
            ticketFormItemIdColMapping.setTicketTemplateId(ttId);
            ticketFormItemIdColMapping.setFormItemId(ticketFormItemId);
            if (ObjectHelper.isNotEmpty(idColMappingMap) && ObjectHelper.isNotEmpty(idColMappingMap.get(ticketFormItemId))) {
                ticketFormItemIdColMapping.setFormItemValueCol(idColMappingMap.get(ticketFormItemId));
            } else {
                String itemValueCol = allColList.get(0);
                ticketFormItemIdColMapping.setFormItemValueCol(itemValueCol);
                allColList.remove(itemValueCol);
            }
            if (ObjectHelper.isNotEmpty(version)) {
                ticketFormItemIdColMapping.setVersion(version);
            }
            ticketFormItemIdColMappingList.add(ticketFormItemIdColMapping);
        }
        return new Response().success(ticketFormItemIdColMappingList);
    }


    public Response<TicketTemplateFullQueryDto> queryObjsByTicketTemplateId (String ticketTemplateId){

        TicketTemplateFullQueryDto ticketTemplateFullQueryDto = new TicketTemplateFullQueryDto();
        //ticketTemplateId可以为templateId或者ticketTemplateCode
        final String ticketTemplateIdOrCode = ticketTemplateId;
        List<TicketTemplate> ticketTemplateList = ticketTemplateService.lambdaQuery()
                .and(tt -> tt.eq(TicketTemplate::getId, ticketTemplateIdOrCode)
                        .or()
                        .eq(TicketTemplate::getTicketTemplateCode, ticketTemplateIdOrCode)
                )
                .isNull(TicketTemplate::getDeleteTime).list();
        if (ObjectHelper.isEmpty(ticketTemplateList)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("根据模板id(%s)查出的模板为空", ticketTemplateId));
        }
        TicketTemplate ticketTemplate = ticketTemplateList.get(0);
        ticketTemplateFullQueryDto.setTicketTemplate(ticketTemplate);
        ticketTemplateFullQueryDto.setVersion(ticketTemplate.getVersion());

        //取真实的模版id
        ticketTemplateId = ticketTemplate.getId();

        //ticketTemplate-form
        List<TicketFormTemplate> ticketFormTemplateList = ticketFormTemplateService.lambdaQuery().eq(TicketFormTemplate::getTicketTemplateId, ticketTemplateId).isNull(TicketFormTemplate::getDeleteTime).list();
        if (ObjectHelper.isEmpty(ticketFormTemplateList)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("根据模板id(%s)查出的表单模板为空", ticketTemplateId));
        }
        TicketFormTemplate ticketFormTemplate = ticketFormTemplateList.get(0);
        ticketTemplateFullQueryDto.setTicketFormTemplate(ticketFormTemplate);
        //ticketTemplate-form-item
        List<TicketFormItemTemplate> ticketFormItemTemplateList = ticketFormItemTemplateService.lambdaQuery().eq(TicketFormItemTemplate::getTicketTemplateId, ticketTemplateId).isNull(TicketFormItemTemplate::getDeleteTime).list();
        if (ObjectHelper.isEmpty(ticketFormItemTemplateList)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("根据模板id(%s)查出的表单项为空", ticketTemplateId));
        }
        ticketTemplateFullQueryDto.setTicketFormItemTemplateList(ticketFormItemTemplateList);

        //ticketTemplate-flow
        List<TicketFlowTemplate> ticketFlowTemplateList = ticketFlowTemplateService.lambdaQuery().eq(TicketFlowTemplate::getTicketTemplateId, ticketTemplateId).isNull(TicketFlowTemplate::getDeleteTime).list();
        if (ObjectHelper.isEmpty(ticketFlowTemplateList)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("根据模板id(%s)查出的流程模板为空", ticketTemplateId));
        }
        TicketFlowTemplate ticketFlowTemplate = ticketFlowTemplateList.get(0);
        ticketTemplateFullQueryDto.setTicketFlowTemplate(ticketFlowTemplate);
        //ticketTemplate-flow-node
        List<TicketFlowNodeTemplate> ticketFlowNodeTemplateList = ticketFlowNodeTemplateService.lambdaQuery().eq(TicketFlowNodeTemplate::getTicketTemplateId, ticketTemplateId).isNull(TicketFlowNodeTemplate::getDeleteTime).list();
        if (ObjectHelper.isEmpty(ticketFlowNodeTemplateList)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("根据模板id(%s)查出的流程节点为空", ticketTemplateId));
        }
        ticketTemplateFullQueryDto.setTicketFlowNodeTemplateList(ticketFlowNodeTemplateList);
        //ticketTemplate-flow-node-event
        List<TicketFlowEventTemplate> ticketFlowEventTemplateList = ticketFlowEventTemplateService.lambdaQuery().eq(TicketFlowEventTemplate::getTicketTemplateId, ticketTemplateId).isNull(TicketFlowEventTemplate::getDeleteTime).list();
        ticketTemplateFullQueryDto.setTicketFlowEventTemplateList(ticketFlowEventTemplateList);
        //ticketTemplate-flow-node-rule
        List<TicketFlowNodeRuleTemplate> ticketFlowNodeRuleTemplateList = ticketFlowNodeRuleTemplateService.lambdaQuery().eq(TicketFlowNodeRuleTemplate::getTicketTemplateId, ticketTemplateId).isNull(TicketFlowNodeRuleTemplate::getDeleteTime).list();
        ticketTemplateFullQueryDto.setTicketFlowNodeRuleTemplateList(ticketFlowNodeRuleTemplateList);
        //ticketTemplate-flow-node-executor
        List<TicketFlowNodeExecutorTemplate> ticketFlowNodeExecutorTemplateList = ticketFlowNodeExecutorTemplateService.lambdaQuery().eq(TicketFlowNodeExecutorTemplate::getTicketTemplateId, ticketTemplateId).isNull(TicketFlowNodeExecutorTemplate::getDeleteTime).list();
        ticketTemplateFullQueryDto.setTicketFlowNodeExecutorTemplateList(ticketFlowNodeExecutorTemplateList);
        //ticketTemplate-flow-node-action
        List<TicketFlowNodeActionTemplate> ticketFlowNodeActionTemplateList = ticketFlowNodeActionTemplateService.lambdaQuery().eq(TicketFlowNodeActionTemplate::getTicketTemplateId, ticketTemplateId).isNull(TicketFlowNodeActionTemplate::getDeleteTime).list();
        ticketTemplateFullQueryDto.setTicketFlowNodeActionTemplateList(ticketFlowNodeActionTemplateList);
        //ticketTemplate-form-item-id-col-mapping
        Response<List<TicketFormItemIdColMapping>> ticketFormItemIdColMappingListResp = getTicketFormItemIdColMappingList(ticketTemplateId);
        if (!ticketFormItemIdColMappingListResp.isSuccess()) {
            return Response.error(ticketFormItemIdColMappingListResp.getEnum(), ticketFormItemIdColMappingListResp.getMsg());
        }
        List<TicketFormItemIdColMapping> ticketFormItemIdColMappingList = ticketFormItemIdColMappingListResp.getData();
        if (ObjectHelper.isNotEmpty(ticketFormItemIdColMappingList)) {
            ticketTemplateFullQueryDto.setTicketFormItemIdColMappingList(ticketFormItemIdColMappingList);
        }
        //ticketSlaTemplate
        List<TicketSlaTemplate> tickeSlaTemplateList = ticketSlaTemplateService.lambdaQuery().eq(TicketSlaTemplate::getTicketTemplateId, ticketTemplateId).isNull(TicketSlaTemplate::getDeleteTime).list();
        if (ObjectHelper.isEmpty(tickeSlaTemplateList)) {
            log.info(String.format("根据模板id(%s)查出的sla模板为空", ticketTemplateId));
            return Response.success(ticketTemplateFullQueryDto);
        }
        TicketSlaTemplate ticketSlaTemplate = tickeSlaTemplateList.get(0);
        ticketTemplateFullQueryDto.setTicketSlaTemplate(ticketSlaTemplate);
        //ticketSlaConfigTemplateList
        List<TicketSlaConfigTemplate> ticketSlaConfigTemplateList = ticketSlaConfigTemplateService.lambdaQuery().eq(TicketSlaConfigTemplate::getTicketTemplateId, ticketTemplateId).isNull(TicketSlaConfigTemplate::getDeleteTime).list();
        if (ObjectHelper.isEmpty(ticketSlaConfigTemplateList)) {
            log.info(String.format("根据模板id(%s)查出的sla配置模版为空", ticketTemplateId));
            return Response.success(ticketTemplateFullQueryDto);
        }
        ticketTemplateFullQueryDto.setTicketSlaConfigTemplateList(ticketSlaConfigTemplateList);
        return Response.success(ticketTemplateFullQueryDto);
    }

    public Response<TicketTemplateFullQueryDto> queryTicketTemplateFullQueryDtoByTicketTemplateId (String ticketTemplateId){

        Response<TicketTemplateFullQueryDto> ticketTemplateFullQueryDtoResp = queryObjsByTicketTemplateId(ticketTemplateId);
        if (!ticketTemplateFullQueryDtoResp.isSuccess()) {
            return Response.error(ticketTemplateFullQueryDtoResp.getEnum(), ticketTemplateFullQueryDtoResp.getMsg());
        }
        TicketTemplateFullQueryDto ticketTemplateFullQueryDto = ticketTemplateFullQueryDtoResp.getData();

        Response<List<TicketFormItemIdColMapping>> ticketFormItemIdColMappingListResp = getTicketFormItemIdColMappingList(ticketTemplateId);
        if (!ticketFormItemIdColMappingListResp.isSuccess()) {
            return Response.error(ticketFormItemIdColMappingListResp.getEnum(), ticketFormItemIdColMappingListResp.getMsg());
        }
        List<TicketFormItemIdColMapping> ticketFormItemIdColMappingList = ticketFormItemIdColMappingListResp.getData();
        if (ObjectHelper.isNotEmpty(ticketFormItemIdColMappingList)) {
            ticketTemplateFullQueryDto.setTicketFormItemIdColMappingList(ticketFormItemIdColMappingList);
        }
        return new Response().success(ticketTemplateFullQueryDto);
    }

    private Response<List<TicketFormItemIdColMapping>> getTicketFormItemIdColMappingList (String ticketTemplateId){

        List<TicketFormItemIdColMapping> ticketFormItemIdColMappingList = ticketFormItemIdColMappingService.lambdaQuery().eq(TicketFormItemIdColMapping::getTicketTemplateId, ticketTemplateId)
                .isNull(TicketFormItemIdColMapping::getDeleteTime).list();
        List<TicketFormItemIdColMapping> distinctList = null;
        if (ObjectHelper.isNotEmpty(ticketFormItemIdColMappingList)) {
            distinctList = ticketFormItemIdColMappingList.stream()
                    .collect(Collectors.toMap(TicketFormItemIdColMapping::getFormItemId,
                            Function.identity(), (p1, p2) -> p1)).values().stream().collect(Collectors.toList());
        }
        return Response.success(distinctList);
    }


    public Response checkUpdateTicketTemplateParams (TicketTemplateDto ticketTemplateDto){

        Response checkTicketTemplateParamsResp = checkTicketTemplateParams(ticketTemplateDto);
        if (!checkTicketTemplateParamsResp.isSuccess()) return checkTicketTemplateParamsResp;
        String ticketTemplateId = ticketTemplateDto.getId();
        //校验工单模版
        List<TicketTemplate> ticketTemplateList = ticketTemplateService.lambdaQuery().eq(TicketTemplate::getId, ticketTemplateId).isNull(TicketTemplate::getDeleteTime).list();
        if (ObjectHelper.isEmpty(ticketTemplateList)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单模板(id为%s)不存在", ticketTemplateId));
        }
        TicketTemplate ticketTemplate = ticketTemplateList.get(0);
        TicketTemplateStatusEnum ticketStatus = ticketTemplate.getTicketStatus();
        if (!TicketTemplateStatusEnum.ENABLE.equals(ticketStatus) && !TicketTemplateStatusEnum.PAUSE.equals(ticketStatus)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单模板(id为%s,状态为%s)不存在", ticketTemplateId, ticketStatus));
        }
        String appId = ticketTemplateDto.getAppId();
        //工单模板名称不能重复
        String ticketName = ticketTemplateDto.getTicketName();
        int ticketNameNum = ticketTemplateService.lambdaQuery().eq(TicketTemplate::getTicketName, ticketName).eq(TicketTemplate::getAppId, appId).isNull(TicketTemplate::getDeleteTime).ne(TicketTemplate::getId, ticketTemplateId).count();
        if (ticketNameNum > 0)
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单模版名称(%s)不能重复", ticketName));
        //工单模板code不能重复
        String ticketTemplateCode = ticketTemplateDto.getTicketTemplateCode();
        int ticketTemplateCodeNum = ticketTemplateService.lambdaQuery().eq(TicketTemplate::getTicketTemplateCode, ticketTemplateCode).eq(TicketTemplate::getAppId, appId).isNull(TicketTemplate::getDeleteTime).ne(TicketTemplate::getId, ticketTemplateId).count();
        if (ticketTemplateCodeNum > 0)
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单模版code(%s)不能重复", ticketTemplateCode));

        return new Response().success(null);
    }

    public Response checkTicketTemplateParams (TicketTemplateDto ticketTemplateDto){

        String ticketTemplateId = ticketTemplateDto.getId();
        //校验工单模版id,如果用户传了ticketTemplateId，判断相对应的工单模版是否存在，以及其状态值。
        if (ObjectHelper.isEmpty(ticketTemplateId)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单模版id为空");
        }
        String appId = ticketTemplateDto.getAppId();
        if (ObjectHelper.isEmpty(appId)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单模版所属应用id不能为空");
        }
        if (ObjectHelper.isEmpty(ticketTemplateDto.getTicketName())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单模版名称不能为空");
        }

        //所属应用是否存在
        int appIdNum = ticketAppService.lambdaQuery().eq(TicketApp::getId, appId).count();
        if (appIdNum < 1) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("所属应用(id为%s)不存在", appId));
        }
        //TODO 优化
        //相关联的应用是否存在
        String beyondApps = ticketTemplateDto.getBeyondApps();
        if (ObjectHelper.isNotEmpty(beyondApps)) {
            for (String it : beyondApps.split(",")) {
                int ticketAppIdNum = ticketAppService.lambdaQuery().eq(TicketApp::getId, it).count();
                if (ticketAppIdNum < 1) {
                    return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("相关联的应用(id为%s)不存在", it));
                }
            }
        }
        return new Response().success(null);
    }

    public Response checkDelBeforeAddParams (TicketTemplateFullQueryDto ticketTemplateFullQueryDto){

        TicketTemplate ticketTemplate = ticketTemplateFullQueryDto.getTicketTemplate();
        if (ObjectHelper.isEmpty(ticketTemplate)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单模版为空");
        }
        ticketTemplateFullQueryDto.setDelTicketTemplateId(ticketTemplate.getId());

        TicketFormTemplate ticketFormTemplate = ticketTemplateFullQueryDto.getTicketFormTemplate();
        if (ObjectHelper.isEmpty(ticketFormTemplate)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单表单模版为空");
        }
        ticketTemplateFullQueryDto.setDelTicketFormTemplateId(ticketFormTemplate.getId());

        List<TicketFormItemTemplate> ticketFormItemTemplateList = ticketTemplateFullQueryDto.getTicketFormItemTemplateList();
        if (ObjectHelper.isEmpty(ticketFormItemTemplateList)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单表单项模版为空");
        }

        List<String> ticketFormItemTemplateIdList = ticketFormItemTemplateList.stream().map(it -> it.getId()).collect(Collectors.toList());
        ticketTemplateFullQueryDto.setDelTicketFormItemTemplateIdList(ticketFormItemTemplateIdList);

        List<TicketFormItemIdColMapping> ticketFormItemIdColMappingList = ticketTemplateFullQueryDto.getTicketFormItemIdColMappingList();
        if (ObjectHelper.isNotEmpty(ticketFormItemIdColMappingList)) {
            if (ObjectHelper.isEmpty(ticketFormItemIdColMappingList.get(0).getVersion())) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("版本号为空"));
            }
            Map<String, String> idColMappingMap = new HashMap<>();
            ticketFormItemIdColMappingList.stream().forEach(ticketFormItemIdColMapping -> {
                idColMappingMap.put(ticketFormItemIdColMapping.getFormItemId(), ticketFormItemIdColMapping.getFormItemValueCol());
            });
            if (ObjectHelper.isEmpty(idColMappingMap)) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("表单项id和对应的列名映射关系map为空"));
            }
            ticketTemplateFullQueryDto.setIdColMappingMap(idColMappingMap);
        }

        TicketFlowTemplate ticketFlowTemplate = ticketTemplateFullQueryDto.getTicketFlowTemplate();
        if (ObjectHelper.isEmpty(ticketFlowTemplate)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单流程模版为空");
        }
        ticketTemplateFullQueryDto.setDelTicketFlowTemplateId(ticketFlowTemplate.getId());

        List<TicketFlowNodeTemplate> ticketFlowNodeTemplateList = ticketTemplateFullQueryDto.getTicketFlowNodeTemplateList();
        if (ObjectHelper.isEmpty(ticketFlowNodeTemplateList)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单流程节点模版为空");
        }
        List<String> ticketFlowNodeTemplateIdList = ticketFlowNodeTemplateList.stream().map(it -> it.getId()).collect(Collectors.toList());
        ticketTemplateFullQueryDto.setDelTicketFlowNodeTemplateIdList(ticketFlowNodeTemplateIdList);

        List<TicketFlowEventTemplate> ticketFlowEventTemplateList = ticketTemplateFullQueryDto.getTicketFlowEventTemplateList();
        List<TicketFlowNodeRuleTemplate> ticketFlowNodeRuleTemplateList = ticketTemplateFullQueryDto.getTicketFlowNodeRuleTemplateList();
        List<TicketFlowNodeExecutorTemplate> ticketFlowNodeExecutorTemplateList = ticketTemplateFullQueryDto.getTicketFlowNodeExecutorTemplateList();
        List<TicketFlowNodeActionTemplate> ticketFlowNodeActionTemplateList = ticketTemplateFullQueryDto.getTicketFlowNodeActionTemplateList();
        if (ObjectHelper.isNotEmpty(ticketFlowEventTemplateList)) {
            List<String> ticketFlowEventTemplateIdList = ticketFlowEventTemplateList.stream().map(it -> it.getId()).collect(Collectors.toList());
            ticketTemplateFullQueryDto.setDelTicketFlowEventTemplateIdList(ticketFlowEventTemplateIdList);
        }
        if (ObjectHelper.isNotEmpty(ticketFlowNodeRuleTemplateList)) {
            List<String> ticketFlowNodeRuleTemplateIdList = ticketFlowNodeRuleTemplateList.stream().map(it -> it.getId()).collect(Collectors.toList());
            ticketTemplateFullQueryDto.setDelTicketFlowNodeRuleTemplateIdList(ticketFlowNodeRuleTemplateIdList);
        }
        if (ObjectHelper.isNotEmpty(ticketFlowNodeExecutorTemplateList)) {
            List<String> ticketFlowNodeExecutorTemplateIdList = ticketFlowNodeExecutorTemplateList.stream().map(it -> it.getId()).collect(Collectors.toList());
            ticketTemplateFullQueryDto.setDelTicketFlowNodeExecutorTemplateIdList(ticketFlowNodeExecutorTemplateIdList);
        }
        if (ObjectHelper.isNotEmpty(ticketFlowNodeActionTemplateList)) {
            List<String> ticketFlowNodeActionTemplateIdList = ticketFlowNodeActionTemplateList.stream().map(it -> it.getId()).collect(Collectors.toList());
            ticketTemplateFullQueryDto.setDelTicketFlowNodeActionTemplateIdList(ticketFlowNodeActionTemplateIdList);
        }
        TicketSlaTemplate ticketSlaTemplate = ticketTemplateFullQueryDto.getTicketSlaTemplate();
        if (ObjectHelper.isNotEmpty(ticketSlaTemplate)) {
            ticketTemplateFullQueryDto.setDelTicketSlaTemplateId(ticketSlaTemplate.getId());
        }
        List<TicketSlaConfigTemplate> ticketSlaConfigTemplateList = ticketTemplateFullQueryDto.getTicketSlaConfigTemplateList();
        if (ObjectHelper.isNotEmpty(ticketSlaConfigTemplateList)) {
            List<String> ticketSlaConfigTemplateIdList = ticketSlaConfigTemplateList.stream().map(it -> it.getId()).collect(Collectors.toList());
            ticketTemplateFullQueryDto.setDelTicketSlaConfigTemplateIdList(ticketSlaConfigTemplateIdList);
        }
        return new Response<>().success(null);
    }

    public TicketTemplateDto assembleTicketTemplateDto (TicketTemplateFullQueryDto ticketTemplateFullQueryDto){

        TicketTemplate ticketTemplate = ticketTemplateFullQueryDto.getTicketTemplate();
        //转换成TicketTemplateDto
        TicketTemplateDto ticketTemplateDto = new TicketTemplateDto(ticketTemplate);

        //转换成TicketFormTemplateDto
        TicketFormTemplate ticketFormTemplate = ticketTemplateFullQueryDto.getTicketFormTemplate();
        TicketFormTemplateDto ticketFormTemplateDto = new TicketFormTemplateDto(ticketFormTemplate);
        ticketTemplateDto.setTicketFormTemplateDto(ticketFormTemplateDto);

        //转换成List<TicketFormItemTemplateDto>
        List<TicketFormItemTemplate> ticketFormItemTemplateList = ticketTemplateFullQueryDto.getTicketFormItemTemplateList();
        List<TicketFormItemTemplateDto> ticketFormItemTemplateDtoList = ticketFormItemTemplateList.stream()
                .map(it -> new TicketFormItemTemplateDto(it))
                .collect(Collectors.toList());
        ticketFormTemplateDto.setTicketFormItemTemplateDtoList(ticketFormItemTemplateDtoList);

        //转换成TicketFlowTemplateDto
        TicketFlowTemplate ticketFlowTemplate = ticketTemplateFullQueryDto.getTicketFlowTemplate();
        TicketFlowTemplateDto ticketFlowTemplateDto = new TicketFlowTemplateDto(ticketFlowTemplate);
        ticketTemplateDto.setTicketFlowTemplateDto(ticketFlowTemplateDto);

        //转换成List<TicketFlowNodeTemplateDto>
        List<TicketFlowNodeTemplate> ticketFlowNodeTemplateList = ticketTemplateFullQueryDto.getTicketFlowNodeTemplateList();
        List<TicketFlowNodeTemplateDto> ticketFlowNodeTemplateDtoList = ticketFlowNodeTemplateList.stream().map(it -> new TicketFlowNodeTemplateDto(it)).collect(Collectors.toList());
        ticketFlowTemplateDto.setTicketFlowNodeTemplateDtoList(ticketFlowNodeTemplateDtoList);


        ticketFlowNodeTemplateDtoList.stream().forEach(ticketFlowNodeTemplateDto -> {

            //转换成 List<TicketFlowNodeRuleTemplateDto>
            List<TicketFlowNodeRuleTemplate> ticketFlowNodeRuleTemplateList = ticketTemplateFullQueryDto.getTicketFlowNodeRuleTemplateList().stream().filter(it -> it.getTicketFlowNodeTemplateId().equals(ticketFlowNodeTemplateDto.getId())).collect(Collectors.toList());
            List<TicketFlowNodeRuleTemplateDto> ticketFlowNodeRuleTemplateDtoList = ticketFlowNodeRuleTemplateList.stream().map(it -> new TicketFlowNodeRuleTemplateDto(it)).collect(Collectors.toList());
            if (ObjectHelper.isNotEmpty(ticketFlowNodeRuleTemplateDtoList)) {
                ticketFlowNodeTemplateDto.setTicketFlowNodeRuleTemplateDtoList(ticketFlowNodeRuleTemplateDtoList);
            }

            //转换成 List<TicketFlowNodeExecutorTemplateDto>
            List<TicketFlowNodeExecutorTemplate> ticketFlowNodeExecutorTemplateList = ticketTemplateFullQueryDto.getTicketFlowNodeExecutorTemplateList().stream().filter(it -> it.getTicketFlowNodeTemplateId().equals(ticketFlowNodeTemplateDto.getId())).collect(Collectors.toList());
            List<TicketFlowNodeExecutorTemplateDto> ticketFlowNodeExecutorTemplateDtoList = ticketFlowNodeExecutorTemplateList.stream()
                    .map(it -> {
                        TicketFlowNodeExecutorTemplateDto ticketFlowNodeExecutorTemplateDto = new TicketFlowNodeExecutorTemplateDto(it);
                        ExecutorTypeEnum executorTypeEnum = it.getExecutorType();
                        String executorValue = ticketFlowNodeExecutorTemplateDto.getExecutorValue();
                        String executorList = ticketExecutorGroupService.getExecutorList(executorTypeEnum, executorValue);
                        if (ObjectHelper.isNotEmpty(executorList)) {
                            ticketFlowNodeExecutorTemplateDto.setExecutorList(executorList);
                        }
                        return ticketFlowNodeExecutorTemplateDto;
                    }).collect(Collectors.toList());
            if (ObjectHelper.isNotEmpty(ticketFlowNodeExecutorTemplateDtoList)) {
                ticketFlowNodeTemplateDto.setTicketFlowNodeExecutorTemplateDtoList(ticketFlowNodeExecutorTemplateDtoList);
            }

            //转换成 List<TicketFlowEventTemplate>
            List<TicketFlowEventTemplate> ticketFlowEventTemplateList = ticketTemplateFullQueryDto.getTicketFlowEventTemplateList().stream().filter(it -> it.getTicketFlowNodeTemplateId().equals(ticketFlowNodeTemplateDto.getId())).collect(Collectors.toList());
            List<TicketFlowEventTemplateDto> ticketFlowEventTemplateDtoList = ticketFlowEventTemplateList.stream().map(it -> new TicketFlowEventTemplateDto(it)).collect(Collectors.toList());
            ticketFlowNodeTemplateDto.setTicketFlowEventTemplateDtoList(ticketFlowEventTemplateDtoList);

            //转换成 List<TicketFlowNodeActionTemplate>
            List<TicketFlowNodeActionTemplate> ticketFlowNodeActionTemplateList = ticketTemplateFullQueryDto.getTicketFlowNodeActionTemplateList().stream().filter(it -> it.getTicketFlowNodeTemplateId().equals(ticketFlowNodeTemplateDto.getId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(ticketFlowNodeActionTemplateList)) {
                List<TicketFlowNodeActionTemplateDto> ticketFlowNodeActionTemplateDtoList = ticketFlowNodeActionTemplateList.stream().map(it -> new TicketFlowNodeActionTemplateDto(it)).collect(Collectors.toList());
                ticketFlowNodeTemplateDto.setTicketFlowNodeActionTemplateDtoList(ticketFlowNodeActionTemplateDtoList);
            }

        });

        //转换成TicketSlaTemplateDto
        TicketSlaTemplate ticketSlaTemplate = ticketTemplateFullQueryDto.getTicketSlaTemplate();
        if (null != ticketSlaTemplate) {
            TicketSlaTemplateDto ticketSlaTemplateDto = new TicketSlaTemplateDto(ticketSlaTemplate);
            ticketTemplateDto.setTicketSlaTemplateDto(ticketSlaTemplateDto);
            //转换成List<TicketSlaConfigTemplate>
            List<TicketSlaConfigTemplate> ticketSlaConfigTemplateList = ticketTemplateFullQueryDto.getTicketSlaConfigTemplateList();
            if (CollectionUtils.isNotEmpty(ticketSlaConfigTemplateList)) {
                List<TicketSlaConfigTemplateDto> ticketSlaConfigTemplateDtoList = ticketSlaConfigTemplateList.stream()
                        .map(it -> new TicketSlaConfigTemplateDto(it))
                        .collect(Collectors.toList());
                ticketSlaTemplateDto.setTicketSlaConfigTemplateDtoList(ticketSlaConfigTemplateDtoList);
            }
        }
        return ticketTemplateDto;
    }

}
