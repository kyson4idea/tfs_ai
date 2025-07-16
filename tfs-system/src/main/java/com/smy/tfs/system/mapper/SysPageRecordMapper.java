package com.smy.tfs.system.mapper;

import com.smy.tfs.system.domain.SysPageRecord;

import java.util.List;

/**
 * 低代码页面配置修改记录Mapper接口
 *
 * @author smy
 * @date 2023-07-28
 */
public interface SysPageRecordMapper {
    /**
     * 查询低代码页面配置修改记录
     *
     * @param recordId 低代码页面配置修改记录主键
     * @return 低代码页面配置修改记录
     */
    public SysPageRecord selectSysPageRecordByRecordId(Long recordId);

    /**
     * 查询低代码页面配置修改记录列表
     *
     * @param sysPageRecord 低代码页面配置修改记录
     * @return 低代码页面配置修改记录集合
     */
    public List<SysPageRecord> selectSysPageRecordList(SysPageRecord sysPageRecord);

    /**
     * 新增低代码页面配置修改记录
     *
     * @param sysPageRecord 低代码页面配置修改记录
     * @return 结果
     */
    public int insertSysPageRecord(SysPageRecord sysPageRecord);

    /**
     * 修改低代码页面配置修改记录
     *
     * @param sysPageRecord 低代码页面配置修改记录
     * @return 结果
     */
    public int updateSysPageRecord(SysPageRecord sysPageRecord);

    /**
     * 删除低代码页面配置修改记录
     *
     * @param recordId 低代码页面配置修改记录主键
     * @return 结果
     */
    public int deleteSysPageRecordByRecordId(Long recordId);

    /**
     * 批量删除低代码页面配置修改记录
     *
     * @param recordIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysPageRecordByRecordIds(Long[] recordIds);

    /**
     * 删除低代码页面配置修改记录信息，通过pageKey
     *
     * @param pageKey 配置标识
     * @return 结果
     */
    public int deleteSysPageRecordByPageKey(String pageKey);
}
