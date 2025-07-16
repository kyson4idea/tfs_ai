package com.smy.tfs.quartz.task;

import com.smy.tfs.biz.service.ITicketFormItemValuesService;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * 同步ticket_data表的数据到ticket_form_item_values表中
 */
@Slf4j
@Component("ticketFormItemValuesSyncTask")
public class TicketFormItemValuesSyncTask {

    @Resource
    private ITicketFormItemValuesService ticketFormItemValuesService;

    /**
     * 同步一小时之前的工单数据
     */
    public void ticketFormItemValuesSync(Long minutes){
        //当前时间
        LocalDateTime currentLocalDateTime  = LocalDateTime.now();
        //当前时间减去minutes分钟
        if (ObjectHelper.isEmpty(minutes)) minutes = 60L;
        LocalDateTime minutesAgo = currentLocalDateTime.minusMinutes(minutes);

        Timestamp startTimestamp = Timestamp.valueOf(minutesAgo);
        Timestamp endTimestamp = Timestamp.valueOf(currentLocalDateTime);
        //同步ticket_data表数据到ticket_form_item_values
        log.info("开始同步ticket_data表数据到ticket_form_item_values,startTimestamp:{},endTimestamp:{}", startTimestamp, endTimestamp);
        ticketFormItemValuesService.syncTimeRangeTicketData(startTimestamp, endTimestamp);
        log.info("结束同步ticket_data表数据到ticket_form_item_values");
    }


}
