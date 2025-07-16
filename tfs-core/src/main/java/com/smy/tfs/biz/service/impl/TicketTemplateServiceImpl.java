package com.smy.tfs.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smy.framework.core.util.SequenceUtil;
import com.smy.tfs.api.constants.TfsBaseConstant;
import com.smy.tfs.api.dbo.*;
import com.smy.tfs.api.dto.*;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.ticket_sla_service.TicketSlaTemplateDto;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.enums.CategoryStatusEnum;
import com.smy.tfs.api.enums.TFSTableIdCode;
import com.smy.tfs.api.enums.TicketTemplateStatusEnum;
import com.smy.tfs.api.service.*;
import com.smy.tfs.biz.component.AccountReturnComponent;
import com.smy.tfs.biz.mapper.TicketTemplateMapper;
import com.smy.tfs.biz.service.ITicketFormItemIdColMappingService;
import com.smy.tfs.common.exception.ServiceException;
import com.smy.tfs.common.utils.SecurityUtils;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.common.utils.bean.BeanHelper;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.apidocs.annotations.ApiDoc;
import org.apache.dubbo.apidocs.annotations.ApiModule;
import org.apache.dubbo.apidocs.annotations.RequestParam;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.smy.tfs.common.utils.PageUtils.startPage;

/**
 * <p>
 * 工单模版表 服务实现类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Component("ticketTemplateServiceImpl")
@org.apache.dubbo.config.annotation.Service
@ApiModule(value = "应用相关服务类", apiInterface = ITicketAppService.class)
@Slf4j
public class TicketTemplateServiceImpl extends ServiceImpl<TicketTemplateMapper, TicketTemplate> implements ITicketTemplateService {

    @Resource
    private AccountReturnComponent accountReturnComponent;
    @Resource
    private ITicketAppService ticketAppService;
    @Resource
    private ITicketFormTemplateService ticketFormTemplateService;
    @Resource
    private ITicketFlowTemplateService ticketFlowTemplateService;
    @Resource
    private ITicketFormItemTemplateService ticketFormItemTemplateService;
    @Resource
    private ITicketFlowNodeTemplateService ticketFlowNodeTemplateService;
    @Resource
    private ITicketFlowEventTemplateService ticketFlowEventTemplateService;
    @Resource
    private ITicketFlowNodeRuleTemplateService ticketFlowNodeRuleTemplateService;
    @Resource
    private ITicketFlowNodeExecutorTemplateService ticketFlowNodeExecutorTemplateService;
    @Resource
    private ITicketFlowNodeActionTemplateService ticketFlowNodeActionTemplateService;
    @Resource
    private TicketTemplateServiceInner ticketTemplateServiceInner;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private ITicketFormItemIdColMappingService ticketFormItemIdColMappingService;
    @Resource
    private ITicketAccountMappingService ticketAccountMappingService;

    @Resource
    private ITicketCategoryService ticketCategoryService;

    @Resource
    private ITicketSlaTemplateService ticketSlaTemplateService;

    @Resource
    private ITicketSlaConfigTemplateService ticketSlaConfigTemplateService;


    @Override
    @ApiDoc(value = "查询工单模版全部信息", description = "查询工单模版全部信息")
    public Response<TicketTemplateDto> selectTicketTemplateFullById(@RequestParam(value = "工单模版id", description = "工单模版id") String id, String applyUser) {
        //根据工单模版id查询所有的工单模版对象
        Response<TicketTemplateFullQueryDto> queryObjsByTicketTemplateIdResp = ticketTemplateServiceInner.queryObjsByTicketTemplateId(id);
        if (!queryObjsByTicketTemplateIdResp.isSuccess()) {
            return new Response<>(null, queryObjsByTicketTemplateIdResp.getEnum(), queryObjsByTicketTemplateIdResp.getMsg());
        }
        TicketTemplateFullQueryDto ticketTemplateFullQueryDto = queryObjsByTicketTemplateIdResp.getData();
        ticketTemplateFullQueryDto.setApplyUser(applyUser);
        //组装TicketTemplateDto对象并返回。
        return new Response<>().success(ticketTemplateServiceInner.assembleTicketTemplateDto(ticketTemplateFullQueryDto));
    }


    @Override
    public List<TicketTemplateDto> selectTicketTemplateList(TicketTemplateDto ticketTemplateDto) {
        if (ticketTemplateDto.isNeedControl() && !SecurityUtils.isAdmin()) {
            List<String> adminAppList = ticketAppService.queryAdminAppListForCurrentUser();
            if (ObjectHelper.isEmpty(adminAppList)) {
                throw new ServiceException("此用户下没有业务");
            }
            ticketTemplateDto.setAppIdList(adminAppList);
        }
        startPage();
        List<TicketTemplateDto> ticketTemplateDtoList = this.baseMapper.selectTicketTemplateList(ticketTemplateDto);
        for (TicketTemplateDto templateDto : ticketTemplateDtoList) {
            templateDto.setCreateBy(accountReturnComponent.toAccountInfoStrForFront(templateDto.getCreateBy()));
            templateDto.setUpdateBy(accountReturnComponent.toAccountInfoStrForFront(templateDto.getUpdateBy()));
        }

        return ticketTemplateDtoList;
    }

    @Override
    public List<TicketTemplateGroupDto> selectTicketTemplateListWithGroup(TicketTemplateDto ticketTemplateDto) {
        List<TicketTemplateDto> ticketTemplateDtoList = this.baseMapper.selectEnableTicketTemplateListByKey(ticketTemplateDto);
        if (CollUtil.isEmpty(ticketTemplateDtoList)) {
            return new ArrayList<>();
        }
        Map<String, List<TicketTemplateDto>> ticketTemplateMap = ticketTemplateDtoList.stream().collect(Collectors.groupingBy(TicketTemplateDto::getAppId));

        List<TicketTemplateGroupDto> resultList = new ArrayList<>();
        for (String appId : ticketTemplateMap.keySet()) {
            List<TicketTemplateDto> groupTemplateList = ticketTemplateMap.get(appId);
            groupTemplateList = groupTemplateList.stream().filter(x ->
                    StringUtils.isEmpty(x.getApplyTicketWays()) || x.getApplyTicketWays().contains("pc") || x.getApplyTicketWays().contains("jssdk")
            ).sorted(Comparator.comparing(TicketTemplateDto::getCreateTime).reversed()).collect(Collectors.toList());
            if (groupTemplateList.size() > 0) {
                TicketTemplateGroupDto ticketTemplateGroupDto = new TicketTemplateGroupDto();
                ticketTemplateGroupDto.setAppId(appId);
                ticketTemplateGroupDto.setAppName(groupTemplateList.get(0).getAppName());
                ticketTemplateGroupDto.setTicketTemplateDtoList(groupTemplateList);
                resultList.add(ticketTemplateGroupDto);
            }
        }
        //TODO 排序有问题
        // resultList = resultList.stream().sorted(Comparator.comparing(TicketTemplateGroupDto::getAppCreateTime).reversed()).collect(Collectors.toList());
        return resultList;
    }

    @Override
    public Response<List<TicketTemplateDto>> selectOnlyTicketTemplateList(boolean needControl) {
        LambdaQueryWrapper<TicketTemplate> queryWrapper = new LambdaQueryWrapper<>();
        if (needControl && !SecurityUtils.isAdmin()) {
            List<String> adminAppList = ticketAppService.queryAdminAppListForCurrentUser();
            if (ObjectHelper.isEmpty(adminAppList))
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "此用户没有任何业务的管理员权限");
            queryWrapper.in(TicketTemplate::getAppId, adminAppList);
            adminAppList.stream().forEach(it -> {
                queryWrapper.or();
                queryWrapper.like(TicketTemplate::getBeyondApps, it);
            });
        }

        queryWrapper.orderByDesc(TicketTemplate::getCreateTime);
        List<TicketTemplate> ticketTemplates = this.list(queryWrapper);
        return Response.success(BeanHelper.copyList(ticketTemplates, TicketTemplateDto.class));
    }

    @Override
    @ApiDoc(value = "创建工单模版", description = "创建工单模版")
    public Response<String> createTicketTemplate(TicketTemplateDto ticketTemplateDto, String userType, String userId, String userName) {
        //校验参数
        Response response = ticketTemplateServiceInner.checkCreateTicketTemplate(ticketTemplateDto);
        if (!BizResponseEnums.SUCCESS.getCode().equals(response.getCode())) {
            return response;
        }
        //参数转换和对象组装
        Integer version = TfsBaseConstant.DEFAULT_VERSION;

        TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByAccountIdAndType(userId, userType);
        if (ticketAccountMapping == null || StrUtil.isBlank(ticketAccountMapping.getSameOriginId())) {
            return Response.error(BizResponseEnums.UNKNOW_EXCEPTION_CODE, "未找到有效用户信息");
        }
        AccountInfo accountInfo = new AccountInfo(ticketAccountMapping.getSameOriginId(), userType, userId, userName);
        String createBy = accountInfo.ToJsonString();
        String updateBy = createBy;
        Date createTime = new Date();
        Date updateTime = createTime;
        Response<TicketTemplateFullDto> makeObjectResponse = ticketTemplateServiceInner.makeObject(ticketTemplateDto, version, null, createBy, updateBy, createTime, updateTime);
        if (!BizResponseEnums.SUCCESS.getCode().equals(makeObjectResponse.getCode())) {
            return Response.error(BizResponseEnums.getEnumByCode(makeObjectResponse.getCode()), makeObjectResponse.getMsg());
        }

        //对象入库
        TicketTemplateFullDto ticketTemplateFullDto = makeObjectResponse.getData();
        return insert(ticketTemplateFullDto);
    }

    public Response<String> delBeforeAdd(TicketTemplateFullDto newTicketTemplateFullDto, TicketTemplateFullQueryDto delTicketTemplateFullDto) {
        try {
            transactionTemplate.executeWithoutResult(action -> {
                Date delTime = new Date();
                if (!this.lambdaUpdate()
                        .eq(TicketTemplate::getId, delTicketTemplateFullDto.getDelTicketTemplateId())
                        .isNull(TicketTemplate::getDeleteTime)
                        .set(TicketTemplate::getDeleteTime, delTime).update()) {
                    throw new ServiceException("工单模板更新异常");
                }
                if (!ticketFormTemplateService.lambdaUpdate()
                        .eq(TicketFormTemplate::getId, delTicketTemplateFullDto.getDelTicketFormTemplateId())
                        .isNull(TicketFormTemplate::getDeleteTime)
                        .set(TicketFormTemplate::getDeleteTime, delTime).update()) {
                    throw new ServiceException("工单模板表单更新异常");
                }
                if (!ticketFormItemTemplateService.lambdaUpdate()
                        .in(TicketFormItemTemplate::getId, delTicketTemplateFullDto.getDelTicketFormItemTemplateIdList())
                        .isNull(TicketFormItemTemplate::getDeleteTime)
                        .set(TicketFormItemTemplate::getDeleteTime, delTime).update()) {
                    throw new ServiceException("工单模板表单项更新异常");
                }
                if (!ticketFlowTemplateService.lambdaUpdate()
                        .eq(TicketFlowTemplate::getId, delTicketTemplateFullDto.getDelTicketFlowTemplateId())
                        .isNull(TicketFlowTemplate::getDeleteTime)
                        .set(TicketFlowTemplate::getDeleteTime, delTime).update()) {
                    throw new ServiceException("工单模板流程更新异常");
                }
                if (!ticketFlowNodeTemplateService.lambdaUpdate()
                        .in(TicketFlowNodeTemplate::getId, delTicketTemplateFullDto.getDelTicketFlowNodeTemplateIdList())
                        .isNull(TicketFlowNodeTemplate::getDeleteTime)
                        .set(TicketFlowNodeTemplate::getDeleteTime, delTime).update()) {
                    throw new ServiceException("工单模板流程节点更新异常");
                }
                List<String> ticketFlowEventTemplateIdList = delTicketTemplateFullDto.getDelTicketFlowEventTemplateIdList();
                if (ObjectHelper.isNotEmpty(ticketFlowEventTemplateIdList) &&
                        !ticketFlowEventTemplateService.lambdaUpdate()
                                .in(TicketFlowEventTemplate::getId, ticketFlowEventTemplateIdList)
                                .isNull(TicketFlowEventTemplate::getDeleteTime)
                                .set(TicketFlowEventTemplate::getDeleteTime, delTime).update()) {
                    throw new ServiceException("工单模板流程节点事件更新异常");
                }
                List<String> ticketFlowNodeRuleTemplateIdList = delTicketTemplateFullDto.getDelTicketFlowNodeRuleTemplateIdList();
                if (ObjectHelper.isNotEmpty(ticketFlowNodeRuleTemplateIdList) &&
                        !ticketFlowNodeRuleTemplateService.lambdaUpdate()
                                .in(TicketFlowNodeRuleTemplate::getId, ticketFlowNodeRuleTemplateIdList)
                                .isNull(TicketFlowNodeRuleTemplate::getDeleteTime)
                                .set(TicketFlowNodeRuleTemplate::getDeleteTime, delTime).update()) {
                    throw new ServiceException("工单模板流程节点规则更新异常");
                }
                List<String> ticketFlowNodeExecutorTemplateIdList = delTicketTemplateFullDto.getDelTicketFlowNodeExecutorTemplateIdList();
                if (ObjectHelper.isNotEmpty(ticketFlowNodeExecutorTemplateIdList) &&
                        !ticketFlowNodeExecutorTemplateService.lambdaUpdate()
                                .in(TicketFlowNodeExecutorTemplate::getId, ticketFlowNodeExecutorTemplateIdList)
                                .isNull(TicketFlowNodeExecutorTemplate::getDeleteTime)
                                .set(TicketFlowNodeExecutorTemplate::getDeleteTime, delTime).update()) {
                    throw new ServiceException("工单模板流程节点执行人更新异常");
                }
                List<String> ticketFlowNodeActionTemplateIdList = delTicketTemplateFullDto.getDelTicketFlowNodeActionTemplateIdList();
                if (ObjectHelper.isNotEmpty(ticketFlowNodeActionTemplateIdList) &&
                        !ticketFlowNodeActionTemplateService.lambdaUpdate()
                                .in(TicketFlowNodeActionTemplate::getId, ticketFlowNodeActionTemplateIdList)
                                .isNull(TicketFlowNodeActionTemplate::getDeleteTime)
                                .set(TicketFlowNodeActionTemplate::getDeleteTime, delTime).update()) {
                    throw new ServiceException("工单模板流程节点动作更新异常");
                }

                if (StringUtils.isNotEmpty(delTicketTemplateFullDto.getDelTicketSlaTemplateId()) && !ticketSlaTemplateService.lambdaUpdate()
                        .eq(TicketSlaTemplate::getId, delTicketTemplateFullDto.getDelTicketSlaTemplateId())
                        .isNull(TicketSlaTemplate::getDeleteTime)
                        .set(TicketSlaTemplate::getDeleteTime, delTime).update()) {
                    throw new ServiceException("工单模板sla更新异常");
                }
                if (CollectionUtils.isNotEmpty(delTicketTemplateFullDto.getDelTicketSlaConfigTemplateIdList()) && !ticketSlaConfigTemplateService.lambdaUpdate()
                        .in(TicketSlaConfigTemplate::getId, delTicketTemplateFullDto.getDelTicketSlaConfigTemplateIdList())
                        .isNull(TicketSlaConfigTemplate::getDeleteTime)
                        .set(TicketSlaConfigTemplate::getDeleteTime, delTime).update()) {
                    throw new ServiceException("工单模板sla配置更新异常");
                }

                //新增原数据的一样的记录
                if (!save(newTicketTemplateFullDto.getTicketTemplate())) {
                    throw new ServiceException("工单模板更新异常");
                }
                if (!ticketFormTemplateService.save(newTicketTemplateFullDto.getTicketFormTemplate())) {
                    throw new ServiceException("工单模板表单更新异常");
                }
                if (!ticketFormItemTemplateService.saveBatch(newTicketTemplateFullDto.getTicketFormItemTemplateList())) {
                    throw new ServiceException("工单模板表单项更新异常");
                }
                List<TicketFormItemIdColMapping> ticketFormItemIdColMappingList = newTicketTemplateFullDto.getTicketFormItemIdColMappingList();
                if (ObjectHelper.isNotEmpty(ticketFormItemIdColMappingList) && !ticketFormItemIdColMappingService.saveBatch(ticketFormItemIdColMappingList)) {
                    throw new ServiceException("表单项id和对应的列名映射关系更新异常");
                }
                if (!ticketFlowTemplateService.save(newTicketTemplateFullDto.getTicketFlowTemplate())) {
                    throw new ServiceException("工单模板流程更新异常");
                }
                if (!ticketFlowNodeTemplateService.saveBatch(newTicketTemplateFullDto.getTicketFlowNodeTemplateList())) {
                    throw new ServiceException("工单模板流程节点更新异常");
                }
                List<TicketFlowEventTemplate> ticketFlowEventTemplateList = newTicketTemplateFullDto.getTicketFlowEventTemplateList();
                if (ObjectHelper.isNotEmpty(ticketFlowEventTemplateList) && !ticketFlowEventTemplateService.saveBatch(ticketFlowEventTemplateList)) {
                    throw new ServiceException("工单模板流程节点事件更新异常");
                }
                List<TicketFlowNodeRuleTemplate> ticketFlowNodeRuleTemplateList = newTicketTemplateFullDto.getTicketFlowNodeRuleTemplateList();
                if (ObjectHelper.isNotEmpty(ticketFlowNodeRuleTemplateList) && !ticketFlowNodeRuleTemplateService.saveBatch(ticketFlowNodeRuleTemplateList)) {
                    throw new ServiceException("工单模板流程节点规则更新异常");
                }
                List<TicketFlowNodeExecutorTemplate> ticketFlowNodeExecutorTemplateList = newTicketTemplateFullDto.getTicketFlowNodeExecutorTemplateList();
                if (ObjectHelper.isNotEmpty(ticketFlowNodeExecutorTemplateList) && !ticketFlowNodeExecutorTemplateService.saveBatch(ticketFlowNodeExecutorTemplateList)) {
                    throw new ServiceException("工单模板流程节点执行人更新异常");
                }
                List<TicketFlowNodeActionTemplate> ticketFlowNodeActionTemplateList = newTicketTemplateFullDto.getTicketFlowNodeActionTemplateList();
                if (ObjectHelper.isNotEmpty(ticketFlowNodeActionTemplateList) && !ticketFlowNodeActionTemplateService.saveBatch(ticketFlowNodeActionTemplateList)) {
                    throw new ServiceException("工单模板流程节点动作更新异常");
                }

                if ( null != newTicketTemplateFullDto.getTicketSlaTemplate() && !ticketSlaTemplateService.save(newTicketTemplateFullDto.getTicketSlaTemplate())) {
                    throw new ServiceException("工单模板sla更新异常");
                }
                if (CollectionUtils.isNotEmpty(newTicketTemplateFullDto.getTicketSlaConfigTemplateList()) && !ticketSlaConfigTemplateService.saveBatch(newTicketTemplateFullDto.getTicketSlaConfigTemplateList())) {
                    throw new ServiceException("工单模板sla配置更新异常");
                }
            });
        } catch (Exception e) {
            return Response.error(BizResponseEnums.UPDATE_ERROR, e.getMessage());
        }
        return new Response().success(delTicketTemplateFullDto.getTicketTemplate().getId());
    }

    public Response<String> insert(TicketTemplateFullDto ticketTemplateFullDto) {
        TicketTemplate ticketTemplate = ticketTemplateFullDto.getTicketTemplate();
        if (ObjectHelper.isEmpty(ticketTemplate))
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "转换后的工单模板为空");
        String ticketTemplateId = ticketTemplate.getId();
        TicketFormTemplate ticketFormTemplate = ticketTemplateFullDto.getTicketFormTemplate();
        if (ObjectHelper.isEmpty(ticketFormTemplate))
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "转换后的表单模板为空");
        List<TicketFormItemTemplate> ticketFormItemTemplateList = ticketTemplateFullDto.getTicketFormItemTemplateList();
        if (ObjectHelper.isEmpty(ticketFormItemTemplateList))
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "转换后的表单项为空");
        List<TicketFormItemIdColMapping> ticketFormItemIdColMappingList = ticketTemplateFullDto.getTicketFormItemIdColMappingList();
        TicketFlowTemplate ticketFlowTemplate = ticketTemplateFullDto.getTicketFlowTemplate();
        if (ObjectHelper.isEmpty(ticketFlowTemplate))
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "转换后的流程模板为空");
        List<TicketFlowNodeTemplate> ticketFlowNodeTemplateList = ticketTemplateFullDto.getTicketFlowNodeTemplateList();
        if (ObjectHelper.isEmpty(ticketFlowNodeTemplateList))
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "转换后的流程节点为空");
        List<TicketFlowEventTemplate> ticketFlowEventTemplates = ticketTemplateFullDto.getTicketFlowEventTemplateList();
        List<TicketFlowNodeRuleTemplate> ticketFlowNodeRuleTemplates = ticketTemplateFullDto.getTicketFlowNodeRuleTemplateList();
        List<TicketFlowNodeExecutorTemplate> ticketFlowNodeExecutorTemplates = ticketTemplateFullDto.getTicketFlowNodeExecutorTemplateList();
        List<TicketFlowNodeActionTemplate> ticketFlowNodeActionTemplates = ticketTemplateFullDto.getTicketFlowNodeActionTemplateList();
//        if (ObjectHelper.isEmpty(ticketFlowEventTemplates) && ObjectHelper.isEmpty(ticketFlowNodeRuleTemplates) && ObjectHelper.isEmpty(ticketFlowNodeExecutorTemplates))
//            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION,"工单流程节点事件模版、工单流程节点规则模版、工单流程节点执行人模版都为空");

        Response response = new Response<>();
        try {
            transactionTemplate.executeWithoutResult(action -> {
                if (!this.lambdaUpdate()
                        .eq(TicketTemplate::getId, ticketTemplate.getId())
                        .eq(TicketTemplate::getTicketStatus, TicketTemplateStatusEnum.INIT)
                        .update(ticketTemplate)) {
                    throw new ServiceException("保存工单模板基础信息异常");
                }
                if (!ticketFormTemplateService.save(ticketFormTemplate)) {
                    throw new ServiceException("保存工单模板表单异常");
                }
                if (!ticketFormItemTemplateService.saveBatch(ticketFormItemTemplateList)) {
                    throw new ServiceException("保存工单模板表单项异常");
                }
                if (ObjectHelper.isNotEmpty(ticketFormItemIdColMappingList) && !ticketFormItemIdColMappingService.saveBatch(ticketFormItemIdColMappingList)) {
                    throw new ServiceException("保存表单项id和对应的列名映射关系异常");
                }
                if (!ticketFlowTemplateService.save(ticketFlowTemplate)) {
                    throw new ServiceException("保存工单模板流程异常");
                }
                if (!ticketFlowNodeTemplateService.saveBatch(ticketFlowNodeTemplateList)) {
                    throw new ServiceException("保存工单模板流程节点异常");
                }
                List<TicketFlowEventTemplate> ticketFlowEventTemplateList = ticketTemplateFullDto.getTicketFlowEventTemplateList();
                if (ObjectHelper.isNotEmpty(ticketFlowEventTemplateList)) {
                    if (!ticketFlowEventTemplateService.saveBatch(ticketFlowEventTemplateList)) {
                        throw new ServiceException("保存工单模板流程节点事件异常");
                    }
                }
                List<TicketFlowNodeRuleTemplate> ticketFlowNodeRuleTemplateList = ticketTemplateFullDto.getTicketFlowNodeRuleTemplateList();
                if (ObjectHelper.isNotEmpty(ticketFlowNodeRuleTemplateList)) {
                    if (!ticketFlowNodeRuleTemplateService.saveBatch(ticketFlowNodeRuleTemplateList)) {
                        throw new ServiceException("保存工单模板流程节点规则异常");
                    }
                }
                List<TicketFlowNodeExecutorTemplate> ticketFlowNodeExecutorTemplateList = ticketTemplateFullDto.getTicketFlowNodeExecutorTemplateList();
                if (ObjectHelper.isNotEmpty(ticketFlowNodeExecutorTemplateList)) {
                    if (!ticketFlowNodeExecutorTemplateService.saveBatch(ticketFlowNodeExecutorTemplateList)) {
                        throw new ServiceException("保存工单模板流程节点执行人异常");
                    }
                }
                List<TicketFlowNodeActionTemplate> ticketFlowNodeActionTemplateList = ticketTemplateFullDto.getTicketFlowNodeActionTemplateList();
                if (ObjectHelper.isNotEmpty(ticketFlowNodeActionTemplateList)) {
                    if (!ticketFlowNodeActionTemplateService.saveBatch(ticketFlowNodeActionTemplateList)) {
                        throw new ServiceException("保存工单模板流程节点动作异常");
                    }
                }
                TicketSlaTemplate ticketSlaTemplate = ticketTemplateFullDto.getTicketSlaTemplate();
                if (ObjectHelper.isNotEmpty(ticketSlaTemplate)) {
                    if (!ticketSlaTemplateService.save(ticketSlaTemplate)) {
                        throw new ServiceException("保存工单sla事件异常");
                    }
                }
                List<TicketSlaConfigTemplate> ticketSlaConfigTemplateList = ticketTemplateFullDto.getTicketSlaConfigTemplateList();
                if (ObjectHelper.isNotEmpty(ticketSlaConfigTemplateList)) {
                    if (!ticketSlaConfigTemplateService.saveBatch(ticketSlaConfigTemplateList)) {
                        throw new ServiceException("保存工单sla配置异常");
                    }
                }
            });
        } catch (Exception e) {
            return Response.error(BizResponseEnums.SAVE_ERROR, e.getMessage());
        }
        return response.success(ticketTemplateId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateTest(TicketTemplateFullDto ticketTemplateFullDto) {
        saveOrUpdate(ticketTemplateFullDto.getTicketTemplate());
        int i = 0;
        if (i == 0) throw new ServiceException("事务异常");
        ticketFormTemplateService.saveOrUpdate(ticketTemplateFullDto.getTicketFormTemplate());
    }

    @Override
    public Response<List<TicketTemplate>> selectTicketTemplateListByAppId(String appId) {
        List<TicketApp> ticketApp = ticketAppService.lambdaQuery()
                .eq(TicketApp::getId, appId)
                .isNull(TicketApp::getDeleteTime)
                .list();
        if (CollectionUtils.isEmpty(ticketApp)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("不存在的应用（id为：%s）", appId));
        }
        LambdaQueryWrapper<TicketTemplate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.isNull(TicketTemplate::getDeleteTime);
        queryWrapper.and(LambdaQueryWrapper -> {
            LambdaQueryWrapper.eq(TicketTemplate::getAppId, appId);
            LambdaQueryWrapper.or();
            LambdaQueryWrapper.like(TicketTemplate::getBeyondApps, appId);
        });
        return new Response<>().success(this.getBaseMapper().selectList(queryWrapper));

    }

    @Override
    public Map<String, String> selectNameMapByIdList(List<String> templateIdList) {
        if (CollUtil.isEmpty(templateIdList)) {
            return new HashMap<>();
        }
        LambdaQueryWrapper<TicketTemplate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(TicketTemplate::getId, templateIdList);
        List<TicketTemplate> ticketTemplateList = this.list(queryWrapper);

        return ticketTemplateList.stream().collect(Collectors.toMap(TicketTemplate::getId, TicketTemplate::getTicketName));
    }

    @Override
    public Response<List<TicketTemplateDto>> queryTicketTemplates(QueryEnableTicketTemplateDto queryEnableTicketTemplateDto) {
        LambdaQueryWrapper<TicketTemplate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.isNull(TicketTemplate::getDeleteTime);
        queryWrapper.eq(StringUtils.isNotEmpty(queryEnableTicketTemplateDto.getTicketStatus()), TicketTemplate::getTicketStatus, queryEnableTicketTemplateDto.getTicketStatus());
        if (StringUtils.isNotEmpty(queryEnableTicketTemplateDto.getAppId())) {
            queryWrapper.and(LambdaQueryWrapper -> {
                LambdaQueryWrapper.eq(TicketTemplate::getAppId, queryEnableTicketTemplateDto.getAppId());
                LambdaQueryWrapper.or();
                LambdaQueryWrapper.like(queryEnableTicketTemplateDto.isSupportBeyondApps(), TicketTemplate::getBeyondApps, queryEnableTicketTemplateDto.getAppId());
            });
        }
        queryWrapper.last("LIMIT 1000");
        List<TicketTemplate> ticketTemplateList = this.list(queryWrapper);
        List<TicketTemplateDto> ticketTemplateDtoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(ticketTemplateList)) {
            ticketTemplateDtoList = ticketTemplateList.stream()
                    .map(it -> new TicketTemplateDto(it))
                    .collect(Collectors.toList());
        }
        return Response.success(ticketTemplateDtoList);
    }

    @Override
    public Response<TicketTemplateDto> selectTicketTemplateById(String id) {
        List<TicketTemplate> ticketTemplateList = this.lambdaQuery()
                .eq(TicketTemplate::getId, id)
                .isNull(TicketTemplate::getDeleteTime)
                .list();
        if (ObjectHelper.isEmpty(ticketTemplateList)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("根据模板id(%s)查出的模板为空", id));
        }
        TicketTemplate ticketTemplate = ticketTemplateList.get(0);
        TicketTemplateDto ticketTemplateDto = new TicketTemplateDto(ticketTemplate);
        return Response.success(ticketTemplateDto);
    }


    @Override
    @ApiDoc(value = "更新工单模版信息", description = "更新工单模版信息")
    public Response<String> updateTicketTemplate(TicketTemplateDto ticketTemplateDto, String userType, String userId, String userName) {
        //校验参数
        Response response = ticketTemplateServiceInner.checkUpdateTicketTemplateParams(ticketTemplateDto);
        if (!response.isSuccess()) return response;

        TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByAccountIdAndType(userId, userType);
        if (ticketAccountMapping == null || StrUtil.isBlank(ticketAccountMapping.getSameOriginId())) {
            return Response.error(BizResponseEnums.UNKNOW_EXCEPTION_CODE, "未找到有效用户信息");
        }

        //根据工单模版id查询所有的工单模版对象
        Response<TicketTemplateFullQueryDto> queryObjsByTicketTemplateIdResp = ticketTemplateServiceInner.queryTicketTemplateFullQueryDtoByTicketTemplateId(ticketTemplateDto.getId());
        if (!queryObjsByTicketTemplateIdResp.isSuccess()) {
            return Response.error(BizResponseEnums.getEnumByCode(queryObjsByTicketTemplateIdResp.getCode()), queryObjsByTicketTemplateIdResp.getMsg());
        }

        /**
         * 校验
         * 返回删除的数据id列表
         */
        TicketTemplateFullQueryDto ticketTemplateFullQueryDto = queryObjsByTicketTemplateIdResp.getData();
        Response checkResp = ticketTemplateServiceInner.checkDelBeforeAddParams(ticketTemplateFullQueryDto);
        if (!checkResp.isSuccess()) return checkResp;


        //参数转换和对象组装
        Integer version = ObjectHelper.isEmpty(ticketTemplateFullQueryDto.getVersion()) ? null : ticketTemplateFullQueryDto.getVersion() + 1;
        AccountInfo accountInfo = new AccountInfo(ticketAccountMapping.getSameOriginId(), userType, userId, userName);
        String createBy = ticketTemplateFullQueryDto.getTicketTemplate().getCreateBy();
        Date createTime = ticketTemplateFullQueryDto.getTicketTemplate().getCreateTime();
        String updateBy = accountInfo.ToJsonString();
        Date updateTime = new Date();
        Response<TicketTemplateFullDto> makeObjectResp = ticketTemplateServiceInner.makeObject(ticketTemplateDto, version, ticketTemplateFullQueryDto.getIdColMappingMap(), createBy, updateBy, createTime, updateTime);
        if (!makeObjectResp.isSuccess()) {
            return Response.error(BizResponseEnums.getEnumByCode(makeObjectResp.getCode()), makeObjectResp.getMsg());
        }
        TicketTemplateFullDto ticketTemplateFullDto = makeObjectResp.getData();

        //先删老数据，新对象数据入库
        return delBeforeAdd(ticketTemplateFullDto, ticketTemplateFullQueryDto);
    }

    @Override
    public Response<String> copyTicketTemplate (TicketTemplateDto ticketDto, String userType, String userId, String userName) {

        // 生成新的 ticketTemplate id
        Response<String> response = initTicketTemplate(userType, userId, userName);
        //测试时注销
        if (!BizResponseEnums.SUCCESS.getCode().equals(response.getCode())) {
            return response;
        }
        ticketDto.setId(response.getData());

        // 将 copy ticketFormTemplateDto 中 id 校验
        Response result = Optional.ofNullable(ticketDto.getTicketFormTemplateDto())
                .map(form -> validateId(form.getId(), "Form ID 不是默认值: " + form.getId()))
                .orElse(Response.success());
        if (!BizResponseEnums.SUCCESS.getCode().equals(result.getCode())) {
            return result;
        }

        // 2. 表单项 ID
        result = Optional.ofNullable(ticketDto.getTicketFormTemplateDto())
                .map(TicketFormTemplateDto::getTicketFormItemTemplateDtoList)
                .orElse(Collections.emptyList())
                .stream()
                .map(item -> validateId(item.getId(), "Form 表单项 ID 不是默认值: " + item.getId()))
                .filter(r -> !r.isSuccess())
                .findFirst()
                .orElse(Response.success());
        if (!BizResponseEnums.SUCCESS.getCode().equals(result.getCode())) {
            return result;
        }

        // 将 copy ticketFlowTemplateDto 中 id 校验
        result = Optional.ofNullable(ticketDto.getTicketFlowTemplateDto())
                .map(flow -> validateId(flow.getId(), "Flow流程模板 ID 不是默认值: " + flow.getId()))
                .orElse(Response.success());
        if (!BizResponseEnums.SUCCESS.getCode().equals(result.getCode())) {
            return result;
        }

        //节点处理
        result = Optional.ofNullable(ticketDto.getTicketFlowTemplateDto())
                .map(TicketFlowTemplateDto::getTicketFlowNodeTemplateDtoList)
                .orElse(Collections.emptyList())
                .stream()
                .map(node -> validateId(node.getId(), "Flow流程节点 ID 不是默认值"))
                .filter(r -> !r.isSuccess())
                .findFirst()
                .orElse(Response.success());
        if (!BizResponseEnums.SUCCESS.getCode().equals(result.getCode())) {
            return result;
        }

        // 将 copy ticketSlaTemplateDto 中 id 校验
        result = Optional.ofNullable(ticketDto.getTicketSlaTemplateDto())
                .map(TicketSlaTemplateDto::getTicketSlaConfigTemplateDtoList)
                .orElse(Collections.emptyList())
                .stream()
                .map(cfg -> validateId(cfg.getId(), "SLA 配置项 ID 不是默认值: " + cfg.getId()))
                .filter(r -> !r.isSuccess())
                .findFirst()
                .orElse(Response.success());
        if (!BizResponseEnums.SUCCESS.getCode().equals(result.getCode())) {
            return result;
        }

        return createTicketTemplate(ticketDto, userType, userId, userName);
    }

    /**
     * 校验 ID 是否以 f_ 开头，不符合则抛出异常
     */
    private Response<String> validateId (String id, String msg) {

        if (StrUtil.isNotEmpty(id) && !id.startsWith("f_")) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, msg);
        } else {
            return Response.success();
        }
    }

    @Override
    public Response<String> save(TicketTemplateDto ticketTemplateDto, String userType, String userId, String userName) {
        String ticketTemplateId = ticketTemplateDto.getId();
        if (ObjectHelper.isEmpty(ticketTemplateId)) {
            //初始化TicketTemplate，并给TicketTemplateDto赋值.
            Response<String> response = initTicketTemplate(userType, userId, userName);
            if (!BizResponseEnums.SUCCESS.getCode().equals(response.getCode())) {
                return response;
            }
            ticketTemplateDto.setId(response.getData());
            return createTicketTemplate(ticketTemplateDto, userType, userId, userName);
        } else {
            return updateTicketTemplate(ticketTemplateDto, userType, userId, userName);
        }
    }

    @Override
    public Response<String> initTicketTemplate(String userType, String userId, String userName) {
        TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByAccountIdAndType(userId, userType);
        if (ticketAccountMapping == null || StrUtil.isBlank(ticketAccountMapping.getSameOriginId())) {
            return Response.error(BizResponseEnums.UNKNOW_EXCEPTION_CODE, "未找到有效用户信息");
        }

        String ticketTemplateId = SequenceUtil.getId(TFSTableIdCode.ID_TICKET_TEMPLATE);
        TicketTemplate ticketTemplate = new TicketTemplate();
        ticketTemplate.setId(ticketTemplateId);
        ticketTemplate.setAppId("");
        ticketTemplate.setTicketStatus(TicketTemplateStatusEnum.INIT);
        ticketTemplate.setTicketName("");
        AccountInfo accountInfo = new AccountInfo(ticketAccountMapping.getSameOriginId(), userType, userId, userName);
        String createBy = accountInfo.ToJsonString();
        Date createTime = new Date();
        ticketTemplate.setCreateBy(createBy);
        ticketTemplate.setCreateTime(createTime);
        if (save(ticketTemplate)) {
            return new Response().success(ticketTemplateId);
        } else {
            return Response.error(BizResponseEnums.SAVE_ERROR, String.format("工单模板(id:%s)保存异常：", ticketTemplateId));
        }
    }


    @Override
    public Response<Boolean> updateTicketTemplateStatus(String id, TicketTemplateStatusEnum newStatus) {
        TicketTemplate ticketTemplateDb = this.getById(id);
        List<String> dbCanChangeStatusList = ticketTemplateDb.getTicketStatus().getCanChangeStatus();
        if (!dbCanChangeStatusList.contains(newStatus.getCode())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "当前状态不允许变更状态为：" + newStatus.getCode());
        }
        Boolean isUnderTicketCategory = Boolean.FALSE;
        List<TicketCategory> ticketCategoryList = ticketCategoryService.lambdaQuery()
                .eq(TicketCategory::getTemplateId, id)
                .isNull(TicketCategory::getDeleteTime)
                .list();
        if (CollectionUtils.isNotEmpty(ticketCategoryList)) {
            isUnderTicketCategory = Boolean.TRUE;
        }
        Boolean finalIsUnderTicketCategory = isUnderTicketCategory;
        try {
            transactionTemplate.executeWithoutResult(action -> {
                TicketTemplate ticketTemplate = new TicketTemplate();
                ticketTemplate.setId(id);
                ticketTemplate.setTicketStatus(newStatus);
                Boolean updateTemplateStatus = this.updateById(ticketTemplate);
                if (!updateTemplateStatus) {
                    throw new RuntimeException("更新模版状态失败");
                }
                Boolean updateCategoryStatus = Boolean.TRUE;
                if (finalIsUnderTicketCategory) {
                    if (TicketTemplateStatusEnum.ENABLE.equals(newStatus)) {
                        updateCategoryStatus = ticketCategoryService.lambdaUpdate()
                                .eq(TicketCategory::getTemplateId, id)
                                .set(TicketCategory::getStatus, CategoryStatusEnum.OPEN)
                                .update();
                    }
                    if (TicketTemplateStatusEnum.PAUSE.equals(newStatus) || TicketTemplateStatusEnum.CANCEL.equals(newStatus)) {
                        updateCategoryStatus = ticketCategoryService.lambdaUpdate()
                                .eq(TicketCategory::getTemplateId, id)
                                .set(TicketCategory::getStatus, CategoryStatusEnum.STOP)
                                .update();
                    }
                    if (!(updateTemplateStatus && updateCategoryStatus)) {
                        throw new RuntimeException("更新模版状态和关联分类状态失败");
                    }
                }
            });
        } catch (Exception e) {
            return Response.error(BizResponseEnums.UPDATE_ERROR, "更新工单模板状态失败");
        }
        return Response.success(Boolean.TRUE);
    }

}
