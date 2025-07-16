package com.smy.tfs.api.service;

import com.smy.tfs.api.dto.dynamic.TicketDataStdDto;
import com.smy.tfs.api.dto.dynamic.TicketFormItemStdDto;
import com.smy.tfs.biz.service.ITicketGenService;
import com.smy.tfs.common.core.domain.entity.SysUser;
import com.smy.tfs.common.core.domain.model.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TicketGenServiceTest {
    @Autowired
    ITicketGenService ticketGenService;

    @Test
    public void testPreviewCoreCode() {
        TicketDataStdDto ticketDataStdDto = new TicketDataStdDto();
        ticketDataStdDto.setApplyId("1002405210000320005");
        ticketDataStdDto.setTicketTemplateId("1182405150000840003");
        //ticketDataStdDto.setApplyUserId("admin");
        List<TicketFormItemStdDto> formItems = new ArrayList<>();
        formItems.add(new TicketFormItemStdDto("1162405150000850055", "2222"));
        formItems.add(new TicketFormItemStdDto("1162405150000850058", "111"));
        ticketDataStdDto.setFormItems(formItems);
        Map<String, String> resp = ticketGenService.previewCoreCode(ticketDataStdDto, new LoginUser(new SysUser(), new HashSet<>()));
        System.out.println(resp);
        assert resp != null && resp.get("vm/java/task.java.vm") != null;
    }
}
