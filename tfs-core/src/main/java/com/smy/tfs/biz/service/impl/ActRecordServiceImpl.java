package com.smy.tfs.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smy.tfs.api.dbo.ActRecord;
import com.smy.tfs.api.service.IActRecordService;
import com.smy.tfs.biz.mapper.ActRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.apidocs.annotations.ApiDoc;
import org.apache.dubbo.apidocs.annotations.ApiModule;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 操作记录表，数据同步夜莺 服务实现类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Slf4j
@Component("actRecordServiceImpl")
@org.apache.dubbo.config.annotation.Service
@ApiModule(value = "操作记录相关服务",apiInterface = IActRecordService.class)
public class ActRecordServiceImpl extends ServiceImpl<ActRecordMapper, ActRecord> implements IActRecordService {

    @Override
    @ApiDoc(value = "操作记录插入测试",description = "操作记录插入测试，可以删除")
    public void addTest() {
        log.info("插入测试，可以删除");
        ActRecord actRecord = new ActRecord();
        actRecord.setId("1");
        actRecord.setActType("1");
        actRecord.setActId("1");
        actRecord.setActBy("1");
        actRecord.setActContent("1");
        actRecord.setCreateBy("1");
        actRecord.setUpdateBy("1");
        actRecord.setCreateTime(new Date());
        actRecord.setUpdateTime(new Date());
        actRecord.setCreateBy("1");
        this.baseMapper.insert(actRecord);
    }
}
