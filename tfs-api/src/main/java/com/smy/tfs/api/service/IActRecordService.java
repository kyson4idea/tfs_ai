package com.smy.tfs.api.service;

import com.smy.tfs.api.dbo.ActRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 操作记录表，数据同步夜莺 服务类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
public interface IActRecordService extends IService<ActRecord> {
    void addTest();
}
