package com.smy.tfs.quartz.task;

import com.smy.tfs.api.constants.TfsBaseConstant;
import com.smy.tfs.api.service.IArkMiaoDaService;
import com.smy.tfs.api.service.ITicketAccountService;
import com.smy.tfs.api.service.ITicketDataActService;
import com.smy.tfs.common.utils.SecurityUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("arkMiaoDaTask")
public class ArkMiaoDaTask {
    @Resource
    private IArkMiaoDaService arkMiaoDaService;

    // 同步喵达新数据
    public void syncMiaoDaNewTicket(String status, String st, String et, String page, String page_size) {
        arkMiaoDaService.syncMiaoDaNewTicketCore(status, st, et, page, page_size);
    }


    // 同步喵达老数据
    public void updateMiaoDaTicketJob(String appId, String templateId) {
        arkMiaoDaService.updateMiaoDaTicketJob(appId, templateId);
    }
}