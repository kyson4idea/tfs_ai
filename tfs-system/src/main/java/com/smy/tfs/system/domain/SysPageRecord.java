package com.smy.tfs.system.domain;

import com.smy.tfs.common.annotation.Excel;
import com.smy.tfs.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 低代码页面配置修改记录对象 sys_page_record
 *
 * @author smy
 * @date 2023-07-28
 */
public class SysPageRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 记录表id主键
     */
    private Long recordId;

    /**
     * 页面配置标识
     */
    @Excel(name = "页面配置标识")
    private String pageKey;

    /**
     * JSON配置
     */
    @Excel(name = "JSON配置")
    private String paramJson;

    /**
     * 更新类型: 1-新增 | 2-修改 | 3-删除
     */
    @Excel(name = "更新类型: 1-新增 | 2-修改 | 3-删除")
    private Integer updateType;

    /**
     * 版本号
     */
    @Excel(name = "版本号")
    private Integer version;

    public SysPageRecord() {
    }

    public SysPageRecord(String pageKey, String paramJson, Integer updateType, Integer version) {
        this.pageKey = pageKey;
        this.paramJson = paramJson;
        this.updateType = updateType;
        this.version = version;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getPageKey() {
        return pageKey;
    }

    public void setPageKey(String pageKey) {
        this.pageKey = pageKey;
    }

    public String getParamJson() {
        return paramJson;
    }

    public void setParamJson(String paramJson) {
        this.paramJson = paramJson;
    }

    public Integer getUpdateType() {
        return updateType;
    }

    public void setUpdateType(Integer updateType) {
        this.updateType = updateType;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("recordId", getRecordId())
                .append("pageKey", getPageKey())
                .append("paramJson", getParamJson())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("updateType", getUpdateType())
                .append("version", getVersion())
                .toString();
    }
}
