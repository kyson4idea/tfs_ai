package com.smy.tfs.system.mapper;

import com.smy.tfs.system.domain.SysPage;

import java.util.List;

/**
 * 页面配置 数据层
 *
 * @author smy
 */
public interface SysPageMapper {
    /**
     * 查询页面配置列表分页数据
     *
     * @param page 页面配置信息对象
     * @return 配置集合
     */
    public List<SysPage> selectPageList(SysPage page);

    /**
     * 查询页面配置列表所有数据
     *
     * @return 配置集合
     */
    public List<SysPage> selectAllList();

    /**
     * 根据配置标识查询页面配置对象
     *
     * @param pageKey 页面配置标识
     * @return 配置信息对象
     */
    public SysPage selectPageByKey(String pageKey);

    /**
     * 根据配置ID查询页面配置对象
     *
     * @param pageId 页面配置ID
     * @return 配置信息对象
     */
    public SysPage selectPageById(Long pageId);

    /**
     * 新增页面配置
     *
     * @param page 页面配置信息对象
     * @return 结果
     */
    public int insertPage(SysPage page);

    /**
     * 修改页面配置
     *
     * @param page 页面配置信息对象
     * @return 结果
     */
    public int updatePage(SysPage page);

    /**
     * 根据配置ID删除页面配置
     *
     * @param pageId 页面配置ID
     * @return 结果
     */
    public int deletePageById(Long pageId);

    /**
     * 根据配置ID列表批量删除页面配置
     *
     * @param pageIds 页面配置ID列表
     * @return 结果
     */
    public int deletePageByIds(Long[] pageIds);
}
