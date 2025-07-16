package com.smy.tfs.system.service.impl;

import com.smy.tfs.common.utils.DateUtils;
import com.smy.tfs.system.domain.SysPageRecord;
import com.smy.tfs.system.mapper.SysPageRecordMapper;
import com.smy.tfs.system.service.ISysPageRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 低代码页面配置修改记录Service业务层处理
 *
 * @author smy
 * @date 2023-07-28
 */
@Service
public class SysPageRecordServiceImpl implements ISysPageRecordService {
    @Autowired
    private SysPageRecordMapper sysPageRecordMapper;

    /**
     * 查询低代码页面配置修改记录
     *
     * @param recordId 低代码页面配置修改记录主键
     * @return 低代码页面配置修改记录
     */
    @Override
    public SysPageRecord selectSysPageRecordByRecordId(Long recordId) {
        return sysPageRecordMapper.selectSysPageRecordByRecordId(recordId);
    }

    /**
     * 查询低代码页面配置修改记录列表
     *
     * @param sysPageRecord 低代码页面配置修改记录
     * @return 低代码页面配置修改记录
     */
    @Override
    public List<SysPageRecord> selectSysPageRecordList(SysPageRecord sysPageRecord) {
        return sysPageRecordMapper.selectSysPageRecordList(sysPageRecord);
    }

    /**
     * 新增低代码页面配置修改记录
     *
     * @param sysPageRecord 低代码页面配置修改记录
     * @return 结果
     */
    @Override
    public int insertSysPageRecord(SysPageRecord sysPageRecord) {
        String paramJson = sysPageRecord.getParamJson();
        if (paramJson == null) {
            paramJson = "{}";
        }
        sysPageRecord.setParamJson(paramJson);
        return sysPageRecordMapper.insertSysPageRecord(sysPageRecord);
    }

    /**
     * 修改低代码页面配置修改记录
     *
     * @param sysPageRecord 低代码页面配置修改记录
     * @return 结果
     */
    @Override
    public int updateSysPageRecord(SysPageRecord sysPageRecord) {
        String paramJson = sysPageRecord.getParamJson();
        if (paramJson == null) {
            paramJson = "{}";
        }
        sysPageRecord.setParamJson(paramJson);
        sysPageRecord.setUpdateTime(DateUtils.getNowDate());
        return sysPageRecordMapper.updateSysPageRecord(sysPageRecord);
    }

    /**
     * 批量删除低代码页面配置修改记录
     *
     * @param recordIds 需要删除的低代码页面配置修改记录主键
     * @return 结果
     */
    @Override
    public int deleteSysPageRecordByRecordIds(Long[] recordIds) {
        return sysPageRecordMapper.deleteSysPageRecordByRecordIds(recordIds);
    }

    /**
     * 删除低代码页面配置修改记录信息
     *
     * @param recordId 低代码页面配置修改记录主键
     * @return 结果
     */
    @Override
    public int deleteSysPageRecordByRecordId(Long recordId) {
        return sysPageRecordMapper.deleteSysPageRecordByRecordId(recordId);
    }

    /**
     * 删除低代码页面配置修改记录信息，通过pageKey
     *
     * @param pageKey 配置标识
     * @return 结果
     */
    public int deleteSysPageRecordByPageKey(String pageKey) {
        return sysPageRecordMapper.deleteSysPageRecordByPageKey(pageKey);
    }
}
