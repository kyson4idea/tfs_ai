package com.smy.tfs.biz.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSONArray;
import com.smy.tfs.api.dto.BusiTicketDataFieldsMappingDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.query.*;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.common.core.page.TableDataInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TicketDataQueryServiceInner {

    /**
     * 超管查询校验参数
     * @param superAdminQueryReqDto
     * @param sameOriginId
     * @param userType
     * @param userId
     * @param userName
     * @param extendFieldsStr
     * @return
     */
    public static Response checkSuperAdminQuery(SuperAdminQueryReqDto superAdminQueryReqDto, String sameOriginId, String userType, String userId, String userName, String extendFieldsStr) {
        if (StringUtils.isAnyEmpty(sameOriginId, userType, userId, userName)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("登入账号信息为空, sameOriginId:%s userType:%s userID:%s userName:%s",sameOriginId,userType,userId,userName));
        }
        //权限控制
        if (!"ldap".equals(userType) || !"admin".equals(userId)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("非超管账号，无法查看数据 userType:%s userID:%s", userType, userId));
        }
        if ((StringUtils.isEmpty(superAdminQueryReqDto.getFinishStartTime()) && StringUtils.isNotEmpty(superAdminQueryReqDto.getFinishEndTime())) || (StringUtils.isNotEmpty(superAdminQueryReqDto.getFinishStartTime()) && StringUtils.isEmpty(superAdminQueryReqDto.getFinishEndTime()))) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION,"结单开始时间和结束时间必须同时传值");
        }
        if ((StringUtils.isEmpty(superAdminQueryReqDto.getCreateStartTime()) && StringUtils.isNotEmpty(superAdminQueryReqDto.getCreateEndTime())) || (StringUtils.isNotEmpty(superAdminQueryReqDto.getCreateStartTime()) && StringUtils.isEmpty(superAdminQueryReqDto.getCreateEndTime()))) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION,"创建开始时间和结束时间必须同时传值");
        }
        String updateStartTime = superAdminQueryReqDto.getUpdateStartTime();
        String updateEndTime = superAdminQueryReqDto.getUpdateEndTime();
        if ((StringUtils.isEmpty(updateStartTime) && StringUtils.isNotEmpty(updateEndTime)) || (StringUtils.isNotEmpty(updateStartTime) && StringUtils.isEmpty(updateEndTime))) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION,"更新开始时间和结束时间必须同时传值");
        }
        if (StringUtils.isEmpty(updateStartTime) && StringUtils.isNotEmpty(updateEndTime)) {
            Date updateStartTimeDate = DateUtil.parse(updateStartTime, "yyyy-MM-dd HH:mm:ss");
            Date updateEndTimeDate = DateUtil.parse(updateEndTime, "yyyy-MM-dd HH:mm:ss");
            if ((updateStartTimeDate.after(updateEndTimeDate) || DateUtil.between(updateStartTimeDate, updateEndTimeDate, DateUnit.DAY) > 30)) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "更新时间的终止时间比开始时间最多晚30天");
            }
        }
        //更新时间、模糊查询字段、扩展字段1三选一
        if (Objects.isNull(updateStartTime) && Objects.isNull(updateEndTime)
                && StringUtils.isBlank(superAdminQueryReqDto.getSearchValue())
                && StringUtils.isBlank(superAdminQueryReqDto.getExtend1())
                ) {
            String extendName1 = "扩展字段1";
            if (StringUtils.isNotEmpty(extendFieldsStr)) {
                List<BusiTicketDataFieldsMappingDto> extendFieldsList = JSONArray.parseArray(extendFieldsStr, BusiTicketDataFieldsMappingDto.class);
                if (CollectionUtils.isNotEmpty(extendFieldsList)) {
                    List<BusiTicketDataFieldsMappingDto> extendList = extendFieldsList.stream().filter(it -> "extend1".equals(it.getFieldCode())).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(extendList)) {
                        BusiTicketDataFieldsMappingDto busiTicketDataFieldsMappingDto = extendList.get(0);
                        extendName1 = busiTicketDataFieldsMappingDto.getFieldName();
                    }
                }
            }
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("更新时间、模糊查询字段、%s不能都为空", extendName1));
        }
        return Response.success();

    }

    /**
     * 业管查询校验参数
     * @param busiAdminQueryReqDto
     * @param sameOriginId
     * @param userType
     * @param userId
     * @param userName
     * @param extendFieldsStr
     * @return
     */
    public static Response checkBusiAdminQuery(BusiAdminQueryReqDto busiAdminQueryReqDto, String sameOriginId, String userType, String userId, String userName, String extendFieldsStr) {
        if ((StringUtils.isEmpty(busiAdminQueryReqDto.getFinishStartTime()) && StringUtils.isNotEmpty(busiAdminQueryReqDto.getFinishEndTime())) || (StringUtils.isNotEmpty(busiAdminQueryReqDto.getFinishStartTime()) && StringUtils.isEmpty(busiAdminQueryReqDto.getFinishEndTime()))) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION,"结单开始时间和结束时间必须同时传值");
        }
        if ((StringUtils.isEmpty(busiAdminQueryReqDto.getCreateStartTime()) && StringUtils.isNotEmpty(busiAdminQueryReqDto.getCreateEndTime())) || (StringUtils.isNotEmpty(busiAdminQueryReqDto.getCreateStartTime()) && StringUtils.isEmpty(busiAdminQueryReqDto.getCreateEndTime()))) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION,"创建开始时间和结束时间必须同时传值");
        }
        String updateStartTime = busiAdminQueryReqDto.getUpdateStartTime();
        String updateEndTime = busiAdminQueryReqDto.getUpdateEndTime();
        if ((StringUtils.isEmpty(updateStartTime) && StringUtils.isNotEmpty(updateEndTime)) || (StringUtils.isNotEmpty(updateStartTime) && StringUtils.isEmpty(updateEndTime))) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION,"更新开始时间和结束时间必须同时传值");
        }
        if (StringUtils.isEmpty(updateStartTime) && StringUtils.isNotEmpty(updateEndTime)) {
            Date updateStartTimeDate = DateUtil.parse(updateStartTime, "yyyy-MM-dd HH:mm:ss");
            Date updateEndTimeDate = DateUtil.parse(updateEndTime, "yyyy-MM-dd HH:mm:ss");
            if ((updateStartTimeDate.after(updateEndTimeDate) || DateUtil.between(updateStartTimeDate, updateEndTimeDate, DateUnit.DAY) > 30)) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "更新时间的终止时间比开始时间最多晚30天");
            }
        }
        //更新时间、模糊查询字段、扩展字段1三选一
        if (Objects.isNull(updateStartTime) && Objects.isNull(updateEndTime)
                && StringUtils.isBlank(busiAdminQueryReqDto.getSearchValue())
                && StringUtils.isBlank(busiAdminQueryReqDto.getExtend1())
        ) {
            String extendName1 = "扩展字段1";
            if (StringUtils.isNotEmpty(extendFieldsStr)) {
                List<BusiTicketDataFieldsMappingDto> extendFieldsList = JSONArray.parseArray(extendFieldsStr, BusiTicketDataFieldsMappingDto.class);
                if (CollectionUtils.isNotEmpty(extendFieldsList)) {
                    List<BusiTicketDataFieldsMappingDto> extendList = extendFieldsList.stream().filter(it -> "extend1".equals(it.getFieldCode())).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(extendList)) {
                        BusiTicketDataFieldsMappingDto busiTicketDataFieldsMappingDto = extendList.get(0);
                        extendName1 = busiTicketDataFieldsMappingDto.getFieldName();
                    }
                }
            }
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("更新时间、模糊查询字段、%s不能都为空", extendName1));
        }
        return Response.success();

    }

    /**
     * 业管查询校验参数
     * @param busiQueryReqDto
     * @param userType
     * @param userId
     * @param userName
     * @return
     */
    public static Response checkBusiQuery(BusiQueryReqDto busiQueryReqDto, String userType, String userId, String userName) {
        if (Objects.isNull(busiQueryReqDto.getUserDealType())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "用户处理类型为空");
        }
        if (StringUtils.isAnyEmpty(userType, userId, userName)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "登入账号信息为空");
        }
        if (CollectionUtils.isEmpty(busiQueryReqDto.getAppIdList())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "业务id不能为空");
        }
        if ((StringUtils.isEmpty(busiQueryReqDto.getFinishStartTime()) && StringUtils.isNotEmpty(busiQueryReqDto.getFinishEndTime()))
                || (StringUtils.isNotEmpty(busiQueryReqDto.getFinishStartTime()) && StringUtils.isEmpty(busiQueryReqDto.getFinishEndTime()))) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION,"结单开始时间和结束时间必须同时传值");
        }
        if ((StringUtils.isEmpty(busiQueryReqDto.getCreateStartTime()) && StringUtils.isNotEmpty(busiQueryReqDto.getCreateEndTime()))
                || (StringUtils.isNotEmpty(busiQueryReqDto.getCreateStartTime()) && StringUtils.isEmpty(busiQueryReqDto.getCreateEndTime()))) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION,"创建开始时间和结束时间必须同时传值");
        }
        String updateStartTime = busiQueryReqDto.getUpdateStartTime();
        String updateEndTime = busiQueryReqDto.getUpdateEndTime();
        if ((StringUtils.isEmpty(updateStartTime) || StringUtils.isEmpty(updateEndTime))) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION,"更新开始时间和更新结束时间必须同时传值");
        }
        if (StringUtils.isNotEmpty(updateStartTime) && StringUtils.isNotEmpty(updateEndTime)) {
            Date updateStartTimeDate = DateUtil.parse(updateStartTime, "yyyy-MM-dd HH:mm:ss");
            Date updateEndTimeDate = DateUtil.parse(updateEndTime, "yyyy-MM-dd HH:mm:ss");
            if ((updateStartTimeDate.after(updateEndTimeDate) || DateUtil.between(updateStartTimeDate, updateEndTimeDate, DateUnit.DAY) > 365)) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "更新时间的终止时间比开始时间最多晚1年");
            }
        }
        //更新时间、模糊查询字段二选一
        if (Objects.isNull(updateStartTime) && Objects.isNull(updateEndTime)
                && StringUtils.isBlank(busiQueryReqDto.getSearchValue())
        ) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "更新时间、模糊查询字段不能都为空");
        }
        return Response.success();

    }

    /**
     * 业管查询校验参数
     * @param slaTagsQueryReqDto
     * @param userType
     * @param userId
     * @param userName
     * @return
     */
    public static Response checkSlaTagsQuery(SlaTagsQueryReqDto slaTagsQueryReqDto, String userType, String userId, String userName) {
        if (StringUtils.isAnyEmpty(userType, userId, userName)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "登入账号信息为空");
        }

        String updateStartTime = slaTagsQueryReqDto.getUpdateStartTime();
        String updateEndTime = slaTagsQueryReqDto.getUpdateEndTime();
        if ((StringUtils.isEmpty(updateStartTime) || StringUtils.isEmpty(updateEndTime))) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION,"更新开始时间和更新结束时间必须同时传值");
        }
        if (StringUtils.isNotEmpty(updateStartTime) && StringUtils.isNotEmpty(updateEndTime)) {
            Date updateStartTimeDate = DateUtil.parse(updateStartTime, "yyyy-MM-dd HH:mm:ss");
            Date updateEndTimeDate = DateUtil.parse(updateEndTime, "yyyy-MM-dd HH:mm:ss");
            if ((updateStartTimeDate.after(updateEndTimeDate) || DateUtil.between(updateStartTimeDate, updateEndTimeDate, DateUnit.DAY) > 365)) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "更新时间的终止时间比开始时间最多晚1年");
            }
        }
        if (CollectionUtils.isEmpty(slaTagsQueryReqDto.getTagList())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单tags筛选条件不能为空");
        }
        return Response.success();

    }

    /**
     * 业管查询校验参数
     * @param ownQueryReqDto
     * @param sameOriginId
     * @param userType
     * @param userId
     * @param userName
     * @param extendFieldsStr
     * @return
     */
    public static Response checkOwnQuery(OwnQueryReqDto ownQueryReqDto, String sameOriginId, String userType, String userId, String userName, String extendFieldsStr) {
        if (StringUtils.isAnyEmpty(sameOriginId, userType, userId, userName)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "登入账号信息为空");
        }
        if (Objects.isNull(ownQueryReqDto.getUserDealType())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "处理类型不能为空");
        }
        if ((StringUtils.isEmpty(ownQueryReqDto.getFinishStartTime()) && StringUtils.isNotEmpty(ownQueryReqDto.getFinishEndTime())) || (StringUtils.isNotEmpty(ownQueryReqDto.getFinishStartTime()) && StringUtils.isEmpty(ownQueryReqDto.getFinishEndTime()))) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION,"结单开始时间和结束时间必须同时传值");
        }
        if ((StringUtils.isEmpty(ownQueryReqDto.getCreateStartTime()) && StringUtils.isNotEmpty(ownQueryReqDto.getCreateEndTime())) || (StringUtils.isNotEmpty(ownQueryReqDto.getCreateStartTime()) && StringUtils.isEmpty(ownQueryReqDto.getCreateEndTime()))) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION,"创建开始时间和结束时间必须同时传值");
        }
        String updateStartTime = ownQueryReqDto.getUpdateStartTime();
        String updateEndTime = ownQueryReqDto.getUpdateEndTime();
        if ((StringUtils.isEmpty(updateStartTime) && StringUtils.isNotEmpty(updateEndTime)) || (StringUtils.isNotEmpty(updateStartTime) && StringUtils.isEmpty(updateEndTime))) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION,"更新开始时间和结束时间必须同时传值");
        }
        if (StringUtils.isEmpty(updateStartTime) && StringUtils.isNotEmpty(updateEndTime)) {
            Date updateStartTimeDate = DateUtil.parse(updateStartTime, "yyyy-MM-dd HH:mm:ss");
            Date updateEndTimeDate = DateUtil.parse(updateEndTime, "yyyy-MM-dd HH:mm:ss");
            if ((updateStartTimeDate.after(updateEndTimeDate) || DateUtil.between(updateStartTimeDate, updateEndTimeDate, DateUnit.DAY) > 30)) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "更新时间的终止时间比开始时间最多晚30天");
            }
        }
        //更新时间、模糊查询字段、扩展字段1三选一
        if (Objects.isNull(updateStartTime) && Objects.isNull(updateEndTime)
                && StringUtils.isBlank(ownQueryReqDto.getSearchValue())
                && StringUtils.isBlank(ownQueryReqDto.getExtend1())
        ) {
            String extendName1 = "扩展字段1";
            if (StringUtils.isNotEmpty(extendFieldsStr)) {
                List<BusiTicketDataFieldsMappingDto> extendFieldsList = JSONArray.parseArray(extendFieldsStr, BusiTicketDataFieldsMappingDto.class);
                if (CollectionUtils.isNotEmpty(extendFieldsList)) {
                    List<BusiTicketDataFieldsMappingDto> extendList = extendFieldsList.stream().filter(it -> "extend1".equals(it.getFieldCode())).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(extendList)) {
                        BusiTicketDataFieldsMappingDto busiTicketDataFieldsMappingDto = extendList.get(0);
                        extendName1 = busiTicketDataFieldsMappingDto.getFieldName();
                    }
                }
            }
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("更新时间、模糊查询字段、%s不能都为空", extendName1));
        }
        return Response.success();

    }
}
