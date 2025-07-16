package com.smy.tfs;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.smy.tfs.api.dbo.TicketFormItemData;
import com.smy.tfs.api.service.ITicketFormItemDataService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes= TfsApplication.class)
@RunWith(SpringRunner.class)
public abstract class SpringTestCase {

    @Autowired
    private ITicketFormItemDataService ticketFormItemDataService;

    String value = "";
    public void testItemValue() {
        // 使用 MyBatis-Plus 的 LambdaUpdateWrapper 进行更新
        LambdaUpdateWrapper<TicketFormItemData> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TicketFormItemData::getId, "testkkk")
                .set(TicketFormItemData::getTicketDataId, "aaa")
                .set(TicketFormItemData::getTicketFormDataId, "aaa")
                .set(TicketFormItemData::getItemOrder, "0")
                .set(TicketFormItemData::getItemValue, value)
                .set(TicketFormItemData::getItemType, "INPUT")
                .set(TicketFormItemData::getItemLabel, "域账号")
                .set(TicketFormItemData::getUpdateBy, "system")
                .set(TicketFormItemData::getTemplateId, "1162405200000100001")
                .set(TicketFormItemData::getItemConfigExt, "{}");

        ticketFormItemDataService.update(updateWrapper);
    }
}
