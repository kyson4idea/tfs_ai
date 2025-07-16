package com.smy.tfs.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smy.tfs.api.dbo.TicketAccountMapping;
import com.smy.tfs.api.dbo.TicketApp;
import com.smy.tfs.api.dbo.TicketCategory;
import com.smy.tfs.api.dbo.TicketTemplate;
import com.smy.tfs.api.dto.TicketCategoryDto;
import com.smy.tfs.api.dto.TicketRemoteAccountDto;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.enums.CategoryLevelEnum;
import com.smy.tfs.api.enums.TicketTemplateStatusEnum;
import com.smy.tfs.api.enums.YESNOEnum;
import com.smy.tfs.api.service.*;
import com.smy.tfs.biz.mapper.TicketCategoryMapper;
import com.smy.tfs.biz.mapper.TicketTemplateMapper;
import com.smy.tfs.common.exception.ServiceException;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component("ticketCategoryService")
public class TicketCategoryServiceImpl extends ServiceImpl<TicketCategoryMapper, TicketCategory> implements ITicketCategoryService {

    @Resource
    private ITicketTemplateService ticketTemplateService;
    @Resource
    private TicketTemplateMapper ticketTemplateMapper;
//    @Resource
//    private TicketCategoryMapper ticketCategoryMapper;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private ITicketAccountMappingService ticketAccountMappingService;
    @Resource
    private ITicketAppService ticketAppService;
    @Resource
    private ITicketAccountService ticketAccountService;

    @Override
    public Response<TicketCategoryDto> add(TicketCategoryDto ticketCategoryDto, String userName, String userId, String userType) {
        if (ObjectHelper.anyIsEmpty(ticketCategoryDto,
                ticketCategoryDto.getName(),
                ticketCategoryDto.getStatus(),
                ticketCategoryDto.getSort(),
                ticketCategoryDto.getAppId(),
                ticketCategoryDto.getCategoryLevel())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "分类名称、状态、顺序、所属业务、级别不能为空");
        }
        if (CategoryLevelEnum.ONE_LEVEL == ticketCategoryDto.getCategoryLevel()
                && StringUtils.isNotEmpty(ticketCategoryDto.getSuperiorCode()) ) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("分类(层级：%s)的上级只能为空", ticketCategoryDto.getCategoryLevel().getDesc()));
        }
        if (CategoryLevelEnum.ONE_LEVEL != ticketCategoryDto.getCategoryLevel()
            && StringUtils.isEmpty(ticketCategoryDto.getSuperiorCode()) ) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("分类(层级：%s)的上级不能为空", ticketCategoryDto.getCategoryLevel().getDesc()));
        }
        String newTemplateId = ticketCategoryDto.getTemplateId();
        if (StringUtils.isNotEmpty(newTemplateId)) {
            Optional<TicketTemplate> opt = ticketTemplateService.lambdaQuery()
                    .eq(TicketTemplate::getId, newTemplateId)
                    .isNull(TicketTemplate::getDeleteTime)
                    .oneOpt();
            if (!opt.isPresent()) {
                throw new ServiceException(String.format("新绑定模板(id:%s)数据不存在", newTemplateId));
            }
            TicketTemplate ticketTemplate = opt.get();
            TicketTemplateStatusEnum ticketStatus = ticketTemplate.getTicketStatus();
            if (!TicketTemplateStatusEnum.ENABLE.equals(ticketStatus)) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单模板(id为%s,状态为%s)不是启用中", newTemplateId, ticketStatus));
            }
        }
        String name = ticketCategoryDto.getName();
//        Integer count = this.lambdaQuery()
//                .eq(TicketCategory::getName, name)
//                .eq(TicketCategory::getAppId, ticketCategoryDto.getAppId())
//                .isNull(TicketCategory::getDeleteTime)
//                .count();
//        if (count > 0) {
//            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("同业务下已存在同名(%s)的分类", name));
//        }
        String superiorCode = ticketCategoryDto.getSuperiorCode();
        if (StringUtils.isNotEmpty(superiorCode)) {
            Optional<TicketCategory> opt = this.lambdaQuery()
                    .eq(TicketCategory::getCode, superiorCode)
                    .isNull(TicketCategory::getDeleteTime)
                    .oneOpt();
            if (!opt.isPresent()) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("上一层级分类(%s)不存在", superiorCode));
            }
            //TODO 校验：上级不能是三级,一级节点没有上级，二级只能挂在一级下面，三级只能挂在二级下面
            //TODO 静默清空
            TicketCategory superiorTicketCategory = opt.get();
            if (StringUtils.isNotEmpty(superiorTicketCategory.getTemplateId())) {
                boolean clearTemplateIdFlag = this.lambdaUpdate()
                        .eq(TicketCategory::getCode, superiorCode)
                        .set(TicketCategory::getTemplateId, null)
                        .update();
                if (!clearTemplateIdFlag) {
                    return Response.error(BizResponseEnums.SAVE_ERROR, String.format("清空上一层级分类模版(code:%s)失败", superiorCode));
                }
            }
        }
        TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByAccountIdAndType(userId, userType);
        if (ticketAccountMapping == null || StrUtil.isBlank(ticketAccountMapping.getSameOriginId())) {
            return Response.error(BizResponseEnums.UNKNOW_EXCEPTION_CODE, "未找到有效用户信息");
        }
        AccountInfo accountInfo = new AccountInfo(ticketAccountMapping.getSameOriginId(), userType, userId, userName);
        String createBy = accountInfo.ToJsonString();
        String updateBy = createBy;
        ticketCategoryDto.setCreateBy(createBy);
        ticketCategoryDto.setUpdateBy(updateBy);
        try {
            savaCategoryAndTemplate(ticketCategoryDto);
        } catch (Exception e){
            return Response.error(BizResponseEnums.SAVE_ERROR,e.getMessage());
        }
        return Response.success(ticketCategoryDto);
    }

    private void savaCategoryAndTemplate(TicketCategoryDto ticketCategoryDto){
        transactionTemplate.executeWithoutResult(action -> {
            TicketCategory ticketCategory = new TicketCategoryDto().toTicketCategory(ticketCategoryDto);
            Integer row = this.getBaseMapper().insertTicketCategory(ticketCategory);
            if ( 1 != row ) {
                throw new ServiceException(String.format("分类(名称:%s)新增异常", ticketCategory.getName()));
            }
            Integer tcId = ticketCategory.getId();
            String code = String.valueOf(ticketCategory.getId());
            String superiorCode = ticketCategoryDto.getSuperiorCode();
            if (StringUtils.isNotEmpty(superiorCode)) {
                code = superiorCode + "_" + code;
            }
            ticketCategoryDto.setId(tcId);
            ticketCategoryDto.setCode(code);
            boolean updateFlag = this.lambdaUpdate()
                    .eq(TicketCategory::getId, tcId)
                    .isNull(TicketCategory::getDeleteTime)
                    .set(TicketCategory::getCode,code)
                    .update();
            if (!updateFlag) {
                throw new ServiceException(String.format("分类(%s)的code值(%s)更新异常", tcId, code));
            }
        });
    }

    @Override
    public Response<TicketCategoryDto> update(TicketCategoryDto ticketCategoryDto) {
        if (ObjectHelper.anyIsEmpty(ticketCategoryDto,
                ticketCategoryDto.getId())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "分类ID不能为空");
        }
        Integer id = ticketCategoryDto.getId();
        Optional<TicketCategory> opt = this.lambdaQuery()
                .eq(TicketCategory::getId, id)
                .isNull(TicketCategory::getDeleteTime)
                .oneOpt();
        if (!opt.isPresent()) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("不存在的分类(id：%s)", id));
        }
        String appId = opt.get().getAppId();
        String name = ticketCategoryDto.getName();
        ticketCategoryDto.setCode(opt.get().getCode());
        ticketCategoryDto.setSuperiorCode(opt.get().getSuperiorCode());
//        if (StringUtils.isNotEmpty(name)) {
//            Integer count = this.lambdaQuery()
//                    .ne(TicketCategory::getId, id)
//                    .eq(TicketCategory::getName, name)
//                    .eq(TicketCategory::getAppId, appId)
//                    .isNull(TicketCategory::getDeleteTime)
//                    .count();
//            if (count > 0) {
//                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("同一个业务下已存在同名(%s)的分类", name));
//            }
//        }
        LambdaUpdateChainWrapper<TicketCategory> ticketCategoryUpdateWrapper = new LambdaUpdateChainWrapper<>(this.getBaseMapper());
        ticketCategoryUpdateWrapper.eq(TicketCategory::getId, ticketCategoryDto.getId());
        ticketCategoryUpdateWrapper.isNull(TicketCategory::getDeleteTime);
        if (Objects.nonNull(ticketCategoryDto.getStatus())) {
            ticketCategoryUpdateWrapper.set(TicketCategory::getStatus, ticketCategoryDto.getStatus());
        }
        if (StringUtils.isNotEmpty(ticketCategoryDto.getName())) {
            ticketCategoryUpdateWrapper.set(TicketCategory::getName, ticketCategoryDto.getName());
        }
        if (StringUtils.isNotEmpty(ticketCategoryDto.getTemplateId())) {
            String newTemplateId = ticketCategoryDto.getTemplateId();
            Optional<TicketTemplate> ticketTemplateOpt = ticketTemplateService.lambdaQuery()
                    .eq(TicketTemplate::getId, newTemplateId)
                    .isNull(TicketTemplate::getDeleteTime)
                    .oneOpt();
            if (!ticketTemplateOpt.isPresent()) {
                throw new ServiceException(String.format("新绑定模板(id:%s)数据不存在", newTemplateId));
            }
            TicketTemplate ticketTemplate = ticketTemplateOpt.get();
            TicketTemplateStatusEnum ticketStatus = ticketTemplate.getTicketStatus();
            if (!TicketTemplateStatusEnum.ENABLE.equals(ticketStatus)) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单模板(id为%s,状态为%s)不是启用中", newTemplateId, ticketStatus));
            }
            ticketCategoryUpdateWrapper.set(TicketCategory::getTemplateId, ticketCategoryDto.getTemplateId());
        }
        if (StringUtils.isEmpty(ticketCategoryDto.getTemplateId())) {
            ticketCategoryUpdateWrapper.set(TicketCategory::getTemplateId, null);
        }
        boolean ticketCategoryUpdateflag = ticketCategoryUpdateWrapper.update();
        if ( !ticketCategoryUpdateflag ) {
            return Response.error(BizResponseEnums.SAVE_ERROR,String.format("分类(id:%s)修改异常", ticketCategoryDto.getId()));
        }
        return Response.success(ticketCategoryDto);
    }
    @Override
    public Response delete(Integer id) {
        Optional<TicketCategory> opt = this.lambdaQuery()
                .eq(TicketCategory::getId, id)
                .isNull(TicketCategory::getDeleteTime)
                .oneOpt();
        if (!opt.isPresent()) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("不存在的分类(id:%s)数据", id));
        }
        String code = opt.get().getCode();
        List<Integer> idList = new ArrayList<>();
        idList.add(id);
        //上一级
        List<TicketCategory> superiorList = this.lambdaQuery()
                .eq(TicketCategory::getSuperiorCode, code)
                .isNull(TicketCategory::getDeleteTime)
                .list();
        List superiorCodeList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(superiorList)) {
            List superiorIdList= superiorList.stream().map(it->it.getId()).collect(Collectors.toList());
            idList.addAll(superiorIdList);
            superiorCodeList= superiorList.stream().map(it->it.getCode()).collect(Collectors.toList());
        }
        //上两级
        List<TicketCategory> superiorSuperiorList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(superiorCodeList)) {
            superiorSuperiorList = this.lambdaQuery()
                    .in(TicketCategory::getSuperiorCode, superiorCodeList)
                    .isNull(TicketCategory::getDeleteTime)
                    .list();
        }
        if (CollectionUtils.isNotEmpty(superiorSuperiorList)) {
            List superiorSuperiorIdList= superiorSuperiorList.stream().map(it->it.getId()).collect(Collectors.toList());
            idList.addAll(superiorSuperiorIdList);
        }
        boolean updateFlag = this.lambdaUpdate()
                .in(TicketCategory::getId, idList)
                .isNull(TicketCategory::getDeleteTime)
                .set(TicketCategory::getDeleteTime, new Date())
                .update();
        if (!updateFlag) {
            throw new ServiceException(String.format("删除分类(id:%s)异常", id));
        }
        return Response.success();
    }

    @Override
    public Response<List<TicketCategoryDto>> queryTicketCategoryList(TicketCategoryDto ticketCategoryDto, String userType, String userId, String userName) {
        LambdaQueryWrapper<TicketCategory> tcLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tcLambdaQueryWrapper.isNull(TicketCategory::getDeleteTime);
        List<String> reqAppIdList = new ArrayList<>();
        if (StrUtil.isNotBlank(ticketCategoryDto.getAppId())) {
            reqAppIdList = Arrays.asList(ticketCategoryDto.getAppId().split(","));
        }
        List<String> queryAppIdList = reqAppIdList;
        if (ticketCategoryDto.isNeedControl() && (!"ldap".equals(userType) || !"admin".equals(userId))) {
            //添加当前操作人，方便关联查询
            TicketRemoteAccountDto ticketRemoteAccountDto = ticketAccountService.getTicketRemoteAccountByIdAndType(userId, userType);
            if (null == ticketRemoteAccountDto || StringUtils.isEmpty(ticketRemoteAccountDto.getSameOriginId())) {
                String errMsg = String.format("不存在的账户userId:%s,userType:%s", userId, userType);
                log.info(errMsg);
                return Response.success(new ArrayList<>());
            }
            String sameOriginId = ticketRemoteAccountDto.getSameOriginId();
            String currentUsername = String.format("\"sameOriginId\":\"%s\"", sameOriginId);
            List<String> adminAppIdList = ticketAppService.queryAdminAppListForPointUser(currentUsername);
            if (ObjectHelper.isEmpty(adminAppIdList)) {
                String errMsg = String.format("此用户(userId:%s,userType:%s)没有任何业务的管理员权限", userId, userType);
                log.info(errMsg);
                return Response.success(new ArrayList<>());
            }
            //如果请求条件reqAppIdList有值，则取交集
            if (ObjectHelper.isEmpty(reqAppIdList)) {
                queryAppIdList = adminAppIdList;
            } else {
                queryAppIdList = reqAppIdList.stream()
                        .filter(adminAppIdList::contains)
                        .collect(Collectors.toList());
            }
        }
        if (StringUtils.isNotEmpty(ticketCategoryDto.getName())) {
            tcLambdaQueryWrapper.like(TicketCategory::getName, ticketCategoryDto.getName());
        }
        if (Objects.nonNull(ticketCategoryDto.getStatus())) {
            tcLambdaQueryWrapper.eq(TicketCategory::getStatus, ticketCategoryDto.getStatus());
        }
        if (Objects.nonNull(ticketCategoryDto.getCategoryLevel())) {
            tcLambdaQueryWrapper.eq(TicketCategory::getCategoryLevel, ticketCategoryDto.getCategoryLevel());
        }
        LambdaQueryWrapper<TicketTemplate> ticketTemplateLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ticketTemplateLambdaQueryWrapper.isNull(TicketTemplate::getDeleteTime);
        if (CollectionUtils.isNotEmpty(queryAppIdList)) {
            tcLambdaQueryWrapper.in(TicketCategory::getAppId, queryAppIdList);
            if (!ticketCategoryDto.isSupportBeyondApps()) {
                ticketTemplateLambdaQueryWrapper.in(TicketTemplate::getAppId, queryAppIdList);
            }
        }
        if (StringUtils.isNotEmpty(ticketCategoryDto.getApplyTicketWaysStr())) {
            String applyTicketWaysStr = ticketCategoryDto.getApplyTicketWaysStr();
            List<String> atList = Arrays.asList(applyTicketWaysStr.split("\\,"));
            List<String> applyTicketWaysList = new ArrayList<>(atList);
            ticketTemplateLambdaQueryWrapper.and(LambdaQueryWrapper->{
                if (applyTicketWaysList.contains("empty")) {
                    LambdaQueryWrapper.isNull(TicketTemplate::getApplyTicketWays);
                    LambdaQueryWrapper.or();
                    LambdaQueryWrapper.eq(TicketTemplate::getApplyTicketWays,"");
                    LambdaQueryWrapper.or();
                    Iterator<String> iterator = applyTicketWaysList.iterator();
                    while (iterator.hasNext()) {
                        String applyTicketWays = iterator.next();
                        if (StringUtils.equals(applyTicketWays,"empty")) {
                            iterator.remove();
                        }
                    }
                }
                for (String applyTicketWays: applyTicketWaysList) {
                    LambdaQueryWrapper.like(TicketTemplate::getApplyTicketWays, applyTicketWays);
                    LambdaQueryWrapper.or();
                }
            });
        }
//        List<TicketTemplate> ticketTemplateList = new ArrayList<>();
//        if (!ticketCategoryDto.isSupportBeyondApps() || StringUtils.isNotEmpty(ticketCategoryDto.getApplyTicketWaysStr())) {
//            ticketTemplateList = ticketTemplateMapper.selectList(ticketTemplateLambdaQueryWrapper);
//            if (CollectionUtils.isEmpty(ticketTemplateList)) {
//                return Response.success(new ArrayList<>());
//            }
//        }
        List<TicketTemplate> ticketTemplateList = ticketTemplateMapper.selectList(ticketTemplateLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(ticketTemplateList)) {
            return Response.success(new ArrayList<>());
        }
        List<String> ticketTemplateIdList = new ArrayList<>();
        Map<String,String> applyTicketWaysMap = new HashMap<>();
        Map<String,String> templateIdCodeMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(ticketTemplateList)) {
            ticketTemplateIdList = ticketTemplateList.stream()
                    .map(it -> it.getId())
                    .collect(Collectors.toList());
            applyTicketWaysMap = ticketTemplateList.stream()
                    .map(it -> {
                        if (StringUtils.isEmpty(it.getApplyTicketWays())) {
                            it.setApplyTicketWays("");
                        }
                        return it;
                    }).collect(Collectors.toMap(TicketTemplate::getId, TicketTemplate::getApplyTicketWays, (existing, replacement) -> replacement));
            templateIdCodeMap = ticketTemplateList.stream()
                    .map(it -> {
                        if (StringUtils.isEmpty(it.getTicketTemplateCode())) {
                            it.setTicketTemplateCode("");
                        }
                        return it;
                    }).collect(Collectors.toMap(TicketTemplate::getId, TicketTemplate::getTicketTemplateCode, (existing, replacement) -> replacement));
        }
        if ((!ticketCategoryDto.isSupportBeyondApps() || StringUtils.isNotEmpty(ticketCategoryDto.getApplyTicketWaysStr())) && CollectionUtils.isNotEmpty(ticketTemplateIdList) ) {
            List<String> finalTicketTemplateIdList = ticketTemplateIdList;
            tcLambdaQueryWrapper.and(LambdaQueryWrapper ->{
                LambdaQueryWrapper.isNull(TicketCategory::getTemplateId);
                LambdaQueryWrapper.or();
                LambdaQueryWrapper.eq(TicketCategory::getTemplateId,"");
                LambdaQueryWrapper.or();
                LambdaQueryWrapper.in(TicketCategory::getTemplateId, finalTicketTemplateIdList);
            });
        }
        List<TicketCategory> tcList = this.getBaseMapper().selectList(tcLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(tcList)) {
            return Response.success(new ArrayList<>());
        }
        final Map<String,String> finalTemplateIdCodeMap = templateIdCodeMap;
        List<TicketCategoryDto> ticketCategoryDtoList = tcList.stream()
                .map(it -> new TicketCategoryDto(it, finalTemplateIdCodeMap))
                .collect(Collectors.toList());
        if (!applyTicketWaysMap.isEmpty()) {
            Map<String,String> finalApplyTicketWaysMap = applyTicketWaysMap;
            ticketCategoryDtoList.stream().map(it -> {
                if (StringUtils.isNotEmpty(it.getTemplateId())) {
                    it.setApplyTicketWaysStr(finalApplyTicketWaysMap.get(it.getTemplateId()));
                }
                return it;
            }).collect(Collectors.toList());
        }
        TicketCategoryDto root = new TicketCategoryDto(0, "虚拟首节点");
        List<TicketCategoryDto> treeList = buildTree(ticketCategoryDtoList);
        if (ObjectHelper.isNotEmpty(treeList)) {
            treeList.stream().forEach(it -> {
                root.add(it);
            });
        }
        root.sort();
        List<TicketCategoryDto> sortedTreeList = root.getChildren();
        return Response.success(sortedTreeList);
    }

    @Override
    public Response updateSort(List<TicketCategoryDto> ticketCategoryDtoList) {
        if (CollectionUtils.isEmpty(ticketCategoryDtoList)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "入参对象为空");
        }
        List<TicketCategory> ticketCategoryList = new ArrayList<>();
        for (TicketCategoryDto ticketCategoryDto : ticketCategoryDtoList) {
            if (Objects.isNull(ticketCategoryDto.getId()) || Objects.isNull(ticketCategoryDto.getSort())) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "该记录id或者顺序值为空");
            }
            TicketCategory ticketCategory = new TicketCategory();
            ticketCategory.setId(ticketCategoryDto.getId());
            ticketCategory.setSort(ticketCategoryDto.getSort());
            ticketCategoryList.add(ticketCategory);
        }
        if (!this.updateBatchById(ticketCategoryList)) {
            return Response.error(BizResponseEnums.UPDATE_ERROR,"批量更新顺序异常");
        }
        return Response.success();
    }

    @Override
    public Response<YESNOEnum> categoryEnabled(String appId) {
        if (StringUtils.isEmpty(appId)) {
            String errMsg = String.format("业务id为空");
            log.info(errMsg);
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, errMsg);
        }
        TicketApp ticketApp = ticketAppService.getById(appId);
        if (Objects.isNull(ticketApp)) {
            String errMsg = String.format("不存在的业务（id:%s)", appId);
            log.info(errMsg);
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, errMsg);
        }
        return Response.success(ticketApp.getCategoryEnabled());
    }

    public static List<TicketCategoryDto> buildTree(List<TicketCategoryDto> nodes) {
        Map<String, TicketCategoryDto> nodeMap = new HashMap<>();
        List<TicketCategoryDto> rootNodes = new ArrayList<>();
        // 首先，将所有节点存储到一个map中，便于后续按id查找
        for (TicketCategoryDto node : nodes) {
            nodeMap.put(node.getCode(), node);
        }
        // 遍历所有节点，构建树形结构
        for (TicketCategoryDto node : nodes) {
            if (StringUtils.isEmpty(node.getSuperiorCode())) {
                rootNodes.add(node); // 没有父节点的认为是根节点
            } else {
                TicketCategoryDto parentNode = nodeMap.get(node.getSuperiorCode());
                if (parentNode != null) {
                    parentNode.add(node);
                }
            }
        }
        return rootNodes; // 返回根节点列表
    }


}
