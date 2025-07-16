package com.smy.tfs.api.service;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.smy.tfs.api.dbo.TicketConfig;
import com.smy.tfs.api.dto.ReqParam;
import com.smy.tfs.api.dto.TicketDataDto;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.dynamic.*;
import com.smy.tfs.api.enums.*;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * @author z01140
 * @Package: com.smy.tfs.api.service
 * @Description:
 * @CreateDate 2024/4/18 11:36
 * @UpdateDate 2024/4/18 11:36
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ITicketConfigServiceTest {

    @Autowired
    ITicketConfigService ticketConfigService;


    @Test
    public void getApplyIDTest() {
        for (int i = 0; i < 10; i++) { Map<String, Object> map = new HashMap<>();
            List<String> agreeTags = new ArrayList<>();
            agreeTags.add("符合事实");
            agreeTags.add("确认无误");
            agreeTags.add("通过");
            agreeTags.add("通过1");
            agreeTags.add("通过2");
            agreeTags.add("通过3");
            map.put("agreeTags", agreeTags);
            List<String> rejectTags = new ArrayList<>();
            rejectTags.add("不符合事实");
            rejectTags.add("确认有误");
            rejectTags.add("不通过");
            rejectTags.add("不通过1");
            rejectTags.add("不通过2");
            rejectTags.add("不通过3");
            map.put("rejectTags", rejectTags);
            List<String> addNodeTags = new ArrayList<>();
            addNodeTags.add("加签");
            addNodeTags.add("加签1");
            addNodeTags.add("加签2");
            addNodeTags.add("加签3");
            map.put("addNodeTags", addNodeTags);
            List<String> commentTags = new ArrayList<>();
            commentTags.add("评论2");
            commentTags.add("评论3");
            commentTags.add("评论4");
            commentTags.add("评论5");
            commentTags.add("评论6");
            map.put("commentTags", commentTags);

            String configStr= JSONUtil.toJsonStr(map);

            TicketConfig ticketConfig = new TicketConfig();
            ticketConfig.setTicketConfigStr(configStr);
            ticketConfig.setTicketTemplateId("10010");

            var saveRes=  ticketConfigService.createTicketConfig(ticketConfig,"ldap","o02157","owen");
            assert  saveRes.isSuccess();
        }

    }


}
