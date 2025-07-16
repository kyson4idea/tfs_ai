package com.smy.tfs.system.domain;

import com.smy.tfs.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 页面配置表 sys_page
 *
 * @author smy
 */
public class SysPage extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 页面配置ID
     */
    private Long pageId;

    /**
     * 页面配置标识
     */
    private String pageKey;

    /**
     * 配置参数内容
     */
    private String paramJson;

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("pageId", getPageId())
                .append("pageKey", getPageKey())
                .append("remark", getRemark())
                .append("paramJson", getParamJson())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .toString();
    }
}
