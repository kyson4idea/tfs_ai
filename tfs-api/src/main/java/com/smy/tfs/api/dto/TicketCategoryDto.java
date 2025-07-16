package com.smy.tfs.api.dto;

import com.smy.tfs.api.dbo.TicketCategory;
import com.smy.tfs.api.enums.CategoryLevelEnum;
import com.smy.tfs.api.enums.CategoryStatusEnum;
import com.smy.tfs.common.utils.StringUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

/**
 * <p>
 * 工单分类表
 * </p>
 *
 * @author yss
 * @since 2024-11-04
 */
@Getter
@Setter
@NoArgsConstructor
public class TicketCategoryDto implements Serializable,Comparable<TicketCategoryDto> {

    private static final long serialVersionUID = 1L;

    /**
     * 工单分类id
     */
    private Integer id;

    /**
     * 工单分类code
     */
    private String code;

    /**
     * 工单分类名称
     */
    private String name;

    /**
     * 工单分类上级id
     **/
    private String superiorCode;

    /**
     * 工单分类状态
     */
    private CategoryStatusEnum status;

    /**
     * 工单分类层级
     *
     */
    private CategoryLevelEnum categoryLevel;

    /**
     * 所属业务
     *
     */
    private String appId;

    /**
     * 排序
     *
     */
    private Integer sort;


    /**
     * 工单模版id
     */
    private String templateId;

    /**
     * 工单模版标识
     */
    private String ticketTemplateCode;

    /**
     * 工单创建方式
     */
    private String applyTicketWaysStr;

    /**
     * 是否要控制权限
     */
    private boolean needControl = true;

    /**
     * 是否要控制权限
     */
    private boolean categoryEnabledCheck = false;

    /**
     * 是否支持查询订阅应用
     */
    private boolean supportBeyondApps = true;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 删除时间
     */
    private Date deleteTime;

    /**
     * 子菜单
     */
    private List<TicketCategoryDto> children = new ArrayList<>();

    public TicketCategory toTicketCategory(TicketCategoryDto ticketCategoryDto){
        TicketCategory ticketCategory = new TicketCategory();
        ticketCategory.setId(ticketCategoryDto.getId());
        ticketCategory.setCode(ticketCategoryDto.getCode());
        ticketCategory.setName(ticketCategoryDto.getName());
        ticketCategory.setStatus(ticketCategoryDto.getStatus());
        ticketCategory.setAppId(ticketCategoryDto.getAppId());
        ticketCategory.setSuperiorCode(ticketCategoryDto.getSuperiorCode());
        ticketCategory.setCategoryLevel(ticketCategoryDto.getCategoryLevel());
        ticketCategory.setSort(ticketCategoryDto.getSort());
        ticketCategory.setCreateBy(ticketCategoryDto.getCreateBy());
        ticketCategory.setCreateTime(ticketCategoryDto.getCreateTime());
        ticketCategory.setUpdateBy(ticketCategoryDto.getUpdateBy());
        ticketCategory.setUpdateTime(ticketCategoryDto.getUpdateTime());
        ticketCategory.setDeleteTime(ticketCategoryDto.getDeleteTime());
        ticketCategory.setTemplateId(ticketCategoryDto.getTemplateId());
        return ticketCategory;
    }

    public TicketCategoryDto(TicketCategory ticketCategory, Map<String,String> templateIdCodeMap){
        this.id = ticketCategory.getId();
        this.code = ticketCategory.getCode();
        this.name = ticketCategory.getName();
        this.status = ticketCategory.getStatus();
        this.appId = ticketCategory.getAppId();
        this.superiorCode = ticketCategory.getSuperiorCode();
        this.categoryLevel = ticketCategory.getCategoryLevel();
        this.sort = ticketCategory.getSort();
        this.createBy = ticketCategory.getCreateBy();
        this.createTime = ticketCategory.getCreateTime();
        this.updateBy = ticketCategory.getUpdateBy();
        this.updateTime = ticketCategory.getUpdateTime();
        this.deleteTime = ticketCategory.getDeleteTime();
        this.templateId = ticketCategory.getTemplateId();
        if (StringUtils.isNotEmpty(ticketCategory.getTemplateId())) {
            this.ticketTemplateCode = templateIdCodeMap.get(ticketCategory.getTemplateId());
        }
    }

    public  TicketCategoryDto(Integer id, String name){
        this.id = id;
        this.name = name;
    }

    @Override
    public int compareTo(TicketCategoryDto other) {
        Integer thisId = this.sort;
        Integer otherId = other.sort ;
        return Integer.compare(thisId, otherId);
    }
    // 递归排序子节点
    public void sort() {
        Collections.sort(children);
        for (TicketCategoryDto ticketCategoryDto : children) {
            ticketCategoryDto.sort();
        }
    }
    // 添加子节点的方法
    public void add(TicketCategoryDto ticketCategoryDto) {
        this.children.add(ticketCategoryDto);
    }


}
