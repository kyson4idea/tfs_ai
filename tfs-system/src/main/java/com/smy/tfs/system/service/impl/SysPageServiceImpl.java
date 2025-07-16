package com.smy.tfs.system.service.impl;

import com.smy.tfs.system.domain.SysPage;
import com.smy.tfs.system.mapper.SysPageMapper;
import com.smy.tfs.system.service.ISysPageService;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 页面配置 服务层实现
 *
 * @author smy
 */
@Service
public class SysPageServiceImpl implements ISysPageService {
    @Autowired
    private SysPageMapper pageMapper;

    /**
     * 查询页面配置列表分页数据
     *
     * @param page 页面配置信息对象
     * @return 配置集合
     */
    @Override
    public List<SysPage> selectPageList(SysPage page) {
        return pageMapper.selectPageList(page);
    }

    /**
     * 查询页面配置列表所有数据
     *
     * @return 配置集合
     */
    public List<SysPage> selectAllList() {
        return pageMapper.selectAllList();
    }

    /**
     * 根据配置标识查询页面配置
     *
     * @param pageKey 页面配置标识
     * @return 配置信息
     */
    @Override
    public String selectPageByKey(String pageKey) {
        SysPage ret = pageMapper.selectPageByKey(pageKey);
        if (ret != null) {
            return ret.getParamJson();
        }
        return StringUtil.EMPTY_STRING;
    }

    /**
     * 根据配置ID查询页面配置
     *
     * @param pageId 页面配置ID
     * @return 配置信息
     */
    @Override
    public SysPage selectPageById(Long pageId) {
        return pageMapper.selectPageById(pageId);
    }

    /**
     * 新增页面配置
     *
     * @param page 页面配置信息对象
     * @return 结果
     */
    @Override
    public int insertPage(SysPage page) {
        String paramJson = page.getParamJson();
        if (paramJson == null) {
            paramJson = "{}";
        }
        page.setParamJson(paramJson);
        int row = pageMapper.insertPage(page);
        return row;
    }

    /**
     * 修改页面配置
     *
     * @param page 页面配置信息对象
     * @return 结果
     */
    @Override
    public int updatePage(SysPage page) {
        String paramJson = page.getParamJson();
        if (paramJson == null) {
            paramJson = "{}";
        }
        page.setParamJson(paramJson);
        int row = pageMapper.updatePage(page);
        return row;
    }

    /**
     * 根据配置ID删除页面配置
     *
     * @param pageId 页面配置ID
     */
    @Override
    public int deletePageById(Long pageId) {
        return pageMapper.deletePageById(pageId);
    }

    /**
     * 根据配置ID列表批量删除页面配置
     *
     * @param pageId 页面配置ID列表
     * @return 结果
     */
    public int deletePageByIds(Long[] pageIds) {
        return pageMapper.deletePageByIds(pageIds);
    }
}
