package com.smy.tfs.biz.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import com.smy.tfs.api.dbo.TicketFormTemplate;
import com.smy.tfs.api.dbo.TicketTemplate;
import com.smy.tfs.api.dto.*;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.*;
import com.smy.tfs.api.service.ITicketTemplateService;
import com.smy.tfs.biz.mapper.TicketTemplateMapper;
import com.smy.tfs.quartz.task.TicketFinishTask;
import com.smy.tfs.quartz.task.TicketFormItemValuesSyncTask;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.TRUE;

/**
 * @author yss
 * @Package: com.smy.tfs.api.service.ITicketTemplateService
 * @Description:
 * @CreateDate 2024/4/18 11:36
 * @UpdateDate 2024/4/18 11:36
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TicketTemplateServiceImplTest {

    @Autowired
    ITicketTemplateService ticketTemplateService;

    @Resource
    TicketTemplateMapper ticketTemplateMapper;

    @Resource
    TicketFormItemValuesSyncTask ticketFormItemValuesSyncTask;

    @Test
    public void createTicketTemplateTest() {
        TicketTemplateDto ticketTemplateDto = new TicketTemplateDto();
        ticketTemplateDto.setId("");
        ticketTemplateDto.setAppId("1");
        ticketTemplateDto.setTicketName("yssGD24");
        ticketTemplateDto.setDescription("yss创建GD24");
        ticketTemplateDto.setTicketTemplateCode("ticketTemplateCode24");
        ticketTemplateDto.setBeyondApps("2,3,4");
        ticketTemplateDto.setInterfaceKey("interfaceKey");
        ticketTemplateDto.setTicketFormChangeFlag(YESNOEnum.NO.getCode());
        ticketTemplateDto.setTicketMsgArriveType(TicketMsgArriveTypeEnum.WECOM.getCode());
        ticketTemplateDto.setTicketMsgBuildType(TicketMsgBuildTypeEnum.CREATE_NONE.getCode());

        //TicketFormTemplate表单模板
        TicketFormTemplateDto ticketFormTemplateDto = new TicketFormTemplateDto();
        List<TicketFormItemTemplateDto> ticketFormItemTemplateDtoList = new ArrayList<>();
        TicketFormItemTemplateDto ticketFormItemTemplateDto1 = new TicketFormItemTemplateDto();
        ticketFormItemTemplateDto1.setId("f_1");
        ticketFormItemTemplateDto1.setItemParentId("");
        ticketFormItemTemplateDto1.setItemOrder(1);
        ticketFormItemTemplateDto1.setItemType(FormItemTypeEnum.INPUT.getCode());
        ticketFormItemTemplateDto1.setItemConfig("{}");
        ticketFormItemTemplateDto1.setItemLabel("性别");
        ticketFormItemTemplateDto1.setItemVisibleRule("[[]]");
        ticketFormItemTemplateDto1.setItemRequired(TRUE);
        ticketFormItemTemplateDto1.setItemTips("请输入性别");
        ticketFormItemTemplateDto1.setItemAdvancedSearch(FormItemAdvancedSearchEnum.TRUE.getBooleanCode());

        TicketFormItemTemplateDto ticketFormItemTemplateDto2 = new TicketFormItemTemplateDto();
        ticketFormItemTemplateDto2.setId("f_2");
        ticketFormItemTemplateDto2.setItemParentId("");
        ticketFormItemTemplateDto2.setItemOrder(2);
        ticketFormItemTemplateDto2.setItemType(FormItemTypeEnum.GROUP.getCode());
        ticketFormItemTemplateDto2.setItemConfig("{}");
        ticketFormItemTemplateDto2.setItemLabel("明细");
        ticketFormItemTemplateDto2.setItemVisibleRule("[[{\"compareId\":\"f_1\",\"compareType\":\"EQUAL\",\"compareValue\":\"女\"}],[{\"compareId\":\"f_3\",\"compareType\":\"EQUAL\",\"compareValue\":\"上海\"},{\"compareId\":\"f_1\",\"compareType\":\"EQUAL\",\"compareValue\":\"男\"},{\"compareId\":\"f_4\",\"compareType\":\"GREATER\",\"compareValue\":\"18\"}]]");
        ticketFormItemTemplateDto2.setItemRequired(TRUE);
        ticketFormItemTemplateDto2.setItemTips("请输入明细");
        ticketFormItemTemplateDto2.setItemAdvancedSearch(FormItemAdvancedSearchEnum.TRUE.getBooleanCode());

        TicketFormItemTemplateDto ticketFormItemTemplateDto3 = new TicketFormItemTemplateDto();
        ticketFormItemTemplateDto3.setId("f_3");
        ticketFormItemTemplateDto3.setItemParentId("f_2");
        ticketFormItemTemplateDto3.setItemOrder(3);
        ticketFormItemTemplateDto3.setItemType(FormItemTypeEnum.SELECT.getCode());
        ticketFormItemTemplateDto3.setItemConfig("{\"options\":[\"上海\",\"北京\",\"深圳\"]}");
        ticketFormItemTemplateDto3.setItemLabel("地区");
        ticketFormItemTemplateDto3.setItemVisibleRule("[[]]");
        ticketFormItemTemplateDto3.setItemRequired(TRUE);
        ticketFormItemTemplateDto3.setItemTips("请输入地区");
        ticketFormItemTemplateDto3.setItemAdvancedSearch(FormItemAdvancedSearchEnum.TRUE.getBooleanCode());

        TicketFormItemTemplateDto ticketFormItemTemplateDto4 = new TicketFormItemTemplateDto();
        ticketFormItemTemplateDto4.setId("f_4");
        ticketFormItemTemplateDto4.setItemParentId("f_2");
        ticketFormItemTemplateDto4.setItemOrder(4);
        ticketFormItemTemplateDto4.setItemType(FormItemTypeEnum.INPUTNUMBER.getCode());
        ticketFormItemTemplateDto4.setItemConfig("{}");
        ticketFormItemTemplateDto4.setItemLabel("年龄");
        ticketFormItemTemplateDto4.setItemVisibleRule("[[]]");
        ticketFormItemTemplateDto4.setItemRequired(TRUE);
        ticketFormItemTemplateDto4.setItemTips("请输入年龄");
        ticketFormItemTemplateDto4.setItemAdvancedSearch(FormItemAdvancedSearchEnum.TRUE.getBooleanCode());

        TicketFormItemTemplateDto ticketFormItemTemplateDto5 = new TicketFormItemTemplateDto();
        ticketFormItemTemplateDto5.setId("f_5");
        ticketFormItemTemplateDto5.setItemParentId("f_2");
        ticketFormItemTemplateDto5.setItemOrder(5);
        ticketFormItemTemplateDto5.setItemType(FormItemTypeEnum.INPUT.getCode());
        ticketFormItemTemplateDto5.setItemConfig("{}");
        ticketFormItemTemplateDto5.setItemLabel("住址");
        ticketFormItemTemplateDto5.setItemVisibleRule("[[]]");
        ticketFormItemTemplateDto5.setItemRequired(TRUE);
        ticketFormItemTemplateDto5.setItemTips("请输入住址");
        ticketFormItemTemplateDto5.setItemAdvancedSearch(FormItemAdvancedSearchEnum.TRUE.getBooleanCode());

        ticketFormItemTemplateDtoList.add(ticketFormItemTemplateDto1);
        ticketFormItemTemplateDtoList.add(ticketFormItemTemplateDto2);
        ticketFormItemTemplateDtoList.add(ticketFormItemTemplateDto3);
        ticketFormItemTemplateDtoList.add(ticketFormItemTemplateDto4);
        ticketFormItemTemplateDtoList.add(ticketFormItemTemplateDto5);
        ticketFormTemplateDto.setTicketFormItemTemplateDtoList(ticketFormItemTemplateDtoList);

        TicketFlowTemplateDto ticketFlowTemplateDto = new TicketFlowTemplateDto();
        ticketFlowTemplateDto.setId("");
        ticketFlowTemplateDto.setStartCc("[{\"accountType\":\"smy\",\"accountId\":\"y01781\",\"accountName\":\"殷沙沙\"},{\"accountType\":\"smy\",\"accountId\":\"o02157\",\"accountName\":\"欧文\"}]");
        ticketFlowTemplateDto.setEndCc("[{\"accountType\":\"smy\",\"accountId\":\"y01781\",\"accountName\":\"殷沙沙\"},{\"accountType\":\"smy\",\"accountId\":\"o02157\",\"accountName\":\"欧文\"}]");

        List<TicketFlowNodeTemplateDto> ticketFlowNodeTemplateDtoList = new ArrayList<>();
        for (int node = 1;node<4;node++) {
            TicketFlowNodeTemplateDto ticketFlowNodeTemplateDto = new TicketFlowNodeTemplateDto();
            ticketFlowNodeTemplateDto.setId("f_1" + node);
            ticketFlowNodeTemplateDto.setNodeName("nodeName" + node);
            if (node == 1) {
                ticketFlowNodeTemplateDto.setPreNodeId("-1");
            }else if (node == 2) {
                ticketFlowNodeTemplateDto.setPreNodeId("f_11");
            }else if (node == 3) {
                ticketFlowNodeTemplateDto.setPreNodeId("f_11,f_12");
            }
            ticketFlowNodeTemplateDto.setNodeOrder(node);
            ticketFlowNodeTemplateDto.setAuditedMethod(AuditedMethodEnum.OR.getCode());
            ticketFlowNodeTemplateDto.setAuditedType(AuditedType.BY_USER.getCode());

            List<TicketFlowEventTemplateDto> ticketFlowEventTemplateDtoList = new ArrayList<>();
            for (int event = 1; event < 3; event++) {
                TicketFlowEventTemplateDto ticketFlowEventTemplateDto = new TicketFlowEventTemplateDto();
                ticketFlowEventTemplateDto.setId("");
                ticketFlowEventTemplateDto.setEventTag("用户输入");
                ticketFlowEventTemplateDto.setExecuteStep(ExecuteStepEnum.BEFORE.getCode());
                ticketFlowEventTemplateDto.setEventType(EventTypeEnum.DUBBO_SERVICE.getCode());
                ticketFlowEventTemplateDto.setEventConfig("tfs-core:TicketFlowService.createTicketDynamic");
                ticketFlowEventTemplateDtoList.add(ticketFlowEventTemplateDto);
            }
            ticketFlowNodeTemplateDto.setTicketFlowEventTemplateDtoList(ticketFlowEventTemplateDtoList);

            List<TicketFlowNodeRuleTemplateDto> ticketFlowNodeRuleTemplateDtoList = new ArrayList<>();
            for (int rule = 1; rule < 3; rule++) {
                TicketFlowNodeRuleTemplateDto ticketFlowNodeRuleTemplateDto = new TicketFlowNodeRuleTemplateDto();
                ticketFlowNodeRuleTemplateDto.setId("");
                ticketFlowNodeRuleTemplateDto.setRuleInfoList("[[{\"compareId\":\"f_1\",\"compareType\":\"EQUAL\",\"compareValue\":\"女\"}],[{\"compareId\":\"f_3\",\"compareType\":\"EQUAL\",\"compareValue\":\"上海\"},{\"compareId\":\"f_1\",\"compareType\":\"EQUAL\",\"compareValue\":\"男\"},{\"compareId\":\"f_4\",\"compareType\":\"GREATER\",\"compareValue\":\"18\"}]]");
                ticketFlowNodeRuleTemplateDtoList.add(ticketFlowNodeRuleTemplateDto);
            }
            ticketFlowNodeTemplateDto.setTicketFlowNodeRuleTemplateDtoList(ticketFlowNodeRuleTemplateDtoList);

            List<TicketFlowNodeExecutorTemplateDto> ticketFlowNodeExecutorTemplateList = new ArrayList<>();
            for (int executor = 1; executor < 3; executor++) {

                if (executor == 1) {
                    TicketFlowNodeExecutorTemplateDto ticketFlowNodeExecutorTemplateDto = new TicketFlowNodeExecutorTemplateDto();
                    ticketFlowNodeExecutorTemplateDto.setId("");
                    ticketFlowNodeExecutorTemplateDto.setExecutorType(ExecutorTypeEnum.APPLY_GROUP.getCode());
                    ticketFlowNodeExecutorTemplateDto.setExecutorValue("[\"1784488508838711297\",\"1784114213646061570\"]");
                    ticketFlowNodeExecutorTemplateList.add(ticketFlowNodeExecutorTemplateDto);
                } else {
                    TicketFlowNodeExecutorTemplateDto ticketFlowNodeExecutorTemplateDto = new TicketFlowNodeExecutorTemplateDto();
                    ticketFlowNodeExecutorTemplateDto.setId("");
                    ticketFlowNodeExecutorTemplateDto.setExecutorType(ExecutorTypeEnum.APPLY_MEMBER_LIST.getCode());
                    ticketFlowNodeExecutorTemplateDto.setExecutorValue("[{\"accountType\":\"smy\",\"accountId\":\"y01781\",\"accountName\":\"殷沙沙\"},{\"accountType\":\"smy\",\"accountId\":\"o02157\",\"accountName\":\"欧文\"}]");
                    ticketFlowNodeExecutorTemplateList.add(ticketFlowNodeExecutorTemplateDto);
                }

            }
            ticketFlowNodeTemplateDto.setTicketFlowNodeExecutorTemplateDtoList(ticketFlowNodeExecutorTemplateList);
            ticketFlowNodeTemplateDtoList.add(ticketFlowNodeTemplateDto);

        }

        ticketTemplateDto.setTicketFormTemplateDto(ticketFormTemplateDto);

        ticketFlowTemplateDto.setTicketFlowNodeTemplateDtoList(ticketFlowNodeTemplateDtoList);
        ticketTemplateDto.setTicketFlowTemplateDto(ticketFlowTemplateDto);

        Response<String> ticketTemplateId = ticketTemplateService.save(ticketTemplateDto, "ldap", "y01781", "殷沙沙");


    }

    @Test
    public void updateTicketTemplateTest() {
        TicketTemplateDto ticketTemplateDto = new TicketTemplateDto();
        ticketTemplateDto.setId("1182405070000580001");
        ticketTemplateDto.setAppId("1");
        ticketTemplateDto.setTicketName("yssGD17");
        ticketTemplateDto.setDescription("yss创建GD17");
        ticketTemplateDto.setTicketTemplateCode("ticketTemplateCode17");
        ticketTemplateDto.setBeyondApps("2,3,4");
        ticketTemplateDto.setInterfaceKey("interfaceKey");
        ticketTemplateDto.setTicketFormChangeFlag(YESNOEnum.NO.getCode());
        ticketTemplateDto.setTicketMsgArriveType(TicketMsgArriveTypeEnum.WECOM.getCode());
        ticketTemplateDto.setTicketMsgBuildType(TicketMsgBuildTypeEnum.CREATE_NONE.getCode());

        //TicketFormTemplate表单模板
        TicketFormTemplateDto ticketFormTemplateDto = new TicketFormTemplateDto();
        List<TicketFormItemTemplateDto> ticketFormItemTemplateDtoList = new ArrayList<>();
        TicketFormItemTemplateDto ticketFormItemTemplateDto1 = new TicketFormItemTemplateDto();
        ticketFormItemTemplateDto1.setId("f_1");
        ticketFormItemTemplateDto1.setItemParentId("");
        ticketFormItemTemplateDto1.setItemOrder(1);
        ticketFormItemTemplateDto1.setItemType(FormItemTypeEnum.INPUT.getCode());
        ticketFormItemTemplateDto1.setItemConfig("{}");
        ticketFormItemTemplateDto1.setItemLabel("性别");
        ticketFormItemTemplateDto1.setItemVisibleRule("[[]]");
        ticketFormItemTemplateDto1.setItemRequired(TRUE);
        ticketFormItemTemplateDto1.setItemTips("请输入性别");
        ticketFormItemTemplateDto1.setItemAdvancedSearch(FormItemAdvancedSearchEnum.TRUE.getBooleanCode());

        TicketFormItemTemplateDto ticketFormItemTemplateDto2 = new TicketFormItemTemplateDto();
        ticketFormItemTemplateDto2.setId("f_2");
        ticketFormItemTemplateDto2.setItemParentId("");
        ticketFormItemTemplateDto2.setItemOrder(2);
        ticketFormItemTemplateDto2.setItemType(FormItemTypeEnum.GROUP.getCode());
        ticketFormItemTemplateDto2.setItemConfig("{}");
        ticketFormItemTemplateDto2.setItemLabel("明细");
        ticketFormItemTemplateDto2.setItemVisibleRule("[[{\"compareId\":\"f_1\",\"compareType\":\"EQUAL\",\"compareValue\":\"女\"}],[{\"compareId\":\"f_3\",\"compareType\":\"EQUAL\",\"compareValue\":\"上海\"},{\"compareId\":\"f_1\",\"compareType\":\"EQUAL\",\"compareValue\":\"男\"},{\"compareId\":\"f_4\",\"compareType\":\"GREATER\",\"compareValue\":\"18\"}]]");
        ticketFormItemTemplateDto2.setItemRequired(TRUE);
        ticketFormItemTemplateDto2.setItemTips("请输入明细");
        ticketFormItemTemplateDto2.setItemAdvancedSearch(FormItemAdvancedSearchEnum.TRUE.getBooleanCode());

        TicketFormItemTemplateDto ticketFormItemTemplateDto3 = new TicketFormItemTemplateDto();
        ticketFormItemTemplateDto3.setId("f_3");
        ticketFormItemTemplateDto3.setItemParentId("f_2");
        ticketFormItemTemplateDto3.setItemOrder(3);
        ticketFormItemTemplateDto3.setItemType(FormItemTypeEnum.SELECT.getCode());
        ticketFormItemTemplateDto3.setItemConfig("{\"options\":[\"上海\",\"北京\",\"深圳\"]}");
        ticketFormItemTemplateDto3.setItemLabel("地区");
        ticketFormItemTemplateDto3.setItemVisibleRule("[[]]");
        ticketFormItemTemplateDto3.setItemRequired(TRUE);
        ticketFormItemTemplateDto3.setItemTips("请输入地区");
        ticketFormItemTemplateDto3.setItemAdvancedSearch(FormItemAdvancedSearchEnum.TRUE.getBooleanCode());

        TicketFormItemTemplateDto ticketFormItemTemplateDto4 = new TicketFormItemTemplateDto();
        ticketFormItemTemplateDto4.setId("f_4");
        ticketFormItemTemplateDto4.setItemParentId("f_2");
        ticketFormItemTemplateDto4.setItemOrder(4);
        ticketFormItemTemplateDto4.setItemType(FormItemTypeEnum.INPUTNUMBER.getCode());
        ticketFormItemTemplateDto4.setItemConfig("{}");
        ticketFormItemTemplateDto4.setItemLabel("年龄");
        ticketFormItemTemplateDto4.setItemVisibleRule("[[]]");
        ticketFormItemTemplateDto4.setItemRequired(TRUE);
        ticketFormItemTemplateDto4.setItemTips("请输入年龄");
        ticketFormItemTemplateDto4.setItemAdvancedSearch(FormItemAdvancedSearchEnum.TRUE.getBooleanCode());

        TicketFormItemTemplateDto ticketFormItemTemplateDto5 = new TicketFormItemTemplateDto();
        ticketFormItemTemplateDto5.setId("f_5");
        ticketFormItemTemplateDto5.setItemParentId("f_2");
        ticketFormItemTemplateDto5.setItemOrder(5);
        ticketFormItemTemplateDto5.setItemType(FormItemTypeEnum.INPUT.getCode());
        ticketFormItemTemplateDto5.setItemConfig("{}");
        ticketFormItemTemplateDto5.setItemLabel("住址");
        ticketFormItemTemplateDto5.setItemVisibleRule("[[]]");
        ticketFormItemTemplateDto5.setItemRequired(TRUE);
        ticketFormItemTemplateDto5.setItemTips("请输入住址");
        ticketFormItemTemplateDto5.setItemAdvancedSearch(FormItemAdvancedSearchEnum.TRUE.getBooleanCode());

        ticketFormItemTemplateDtoList.add(ticketFormItemTemplateDto1);
        ticketFormItemTemplateDtoList.add(ticketFormItemTemplateDto2);
        ticketFormItemTemplateDtoList.add(ticketFormItemTemplateDto3);
        ticketFormItemTemplateDtoList.add(ticketFormItemTemplateDto4);
        ticketFormItemTemplateDtoList.add(ticketFormItemTemplateDto5);
        ticketFormTemplateDto.setTicketFormItemTemplateDtoList(ticketFormItemTemplateDtoList);

        TicketFlowTemplateDto ticketFlowTemplateDto = new TicketFlowTemplateDto();
        ticketFlowTemplateDto.setId("");
        ticketFlowTemplateDto.setStartCc("[{accountType:\"smy\",accountId:\"y01781\",accountName:\"殷沙沙\"},{accountType:\"smy\",accountId:\"o02157\",accountName:\"欧文\"}]");
        ticketFlowTemplateDto.setEndCc("[{accountType:\"smy\",accountId:\"y01781\",accountName:\"殷沙沙\"},{accountType:\"smy\",accountId:\"o02157\",accountName:\"欧文\"}]");

        List<TicketFlowNodeTemplateDto> ticketFlowNodeTemplateDtoList = new ArrayList<>();
        for (int node = 1;node<4;node++) {
            TicketFlowNodeTemplateDto ticketFlowNodeTemplateDto = new TicketFlowNodeTemplateDto();
            ticketFlowNodeTemplateDto.setId("f_1" + node);
            ticketFlowNodeTemplateDto.setNodeName("nodeName" + node);
            if (node == 1) {
                ticketFlowNodeTemplateDto.setPreNodeId("-1");
            }else if (node == 2) {
                ticketFlowNodeTemplateDto.setPreNodeId("f_11");
            }else if (node == 3) {
                ticketFlowNodeTemplateDto.setPreNodeId("f_11,f_12");
            }
            ticketFlowNodeTemplateDto.setNodeOrder(node);
            ticketFlowNodeTemplateDto.setAuditedMethod(AuditedMethodEnum.OR.getCode());
            ticketFlowNodeTemplateDto.setAuditedType(AuditedType.BY_USER.getCode());

            List<TicketFlowEventTemplateDto> ticketFlowEventTemplateDtoList = new ArrayList<>();
            for (int event = 1; event < 3; event++) {
                TicketFlowEventTemplateDto ticketFlowEventTemplateDto = new TicketFlowEventTemplateDto();
                ticketFlowEventTemplateDto.setId("");
                ticketFlowEventTemplateDto.setEventTag("用户输入");
                ticketFlowEventTemplateDto.setExecuteStep(ExecuteStepEnum.BEFORE.getCode());
                ticketFlowEventTemplateDto.setEventType(EventTypeEnum.DUBBO_SERVICE.getCode());
                ticketFlowEventTemplateDto.setEventConfig("tfs-core:TicketFlowService.createTicketDynamic");
                ticketFlowEventTemplateDtoList.add(ticketFlowEventTemplateDto);
            }
            ticketFlowNodeTemplateDto.setTicketFlowEventTemplateDtoList(ticketFlowEventTemplateDtoList);

            List<TicketFlowNodeRuleTemplateDto> ticketFlowNodeRuleTemplateDtoList = new ArrayList<>();
            for (int rule = 1; rule < 3; rule++) {
                TicketFlowNodeRuleTemplateDto ticketFlowNodeRuleTemplateDto = new TicketFlowNodeRuleTemplateDto();
                ticketFlowNodeRuleTemplateDto.setId("");
                ticketFlowNodeRuleTemplateDto.setRuleInfoList("[[{\"compareId\":\"f_1\",\"compareType\":\"EQUAL\",\"compareValue\":\"女\"}],[{\"compareId\":\"f_3\",\"compareType\":\"EQUAL\",\"compareValue\":\"上海\"},{\"compareId\":\"f_1\",\"compareType\":\"EQUAL\",\"compareValue\":\"男\"},{\"compareId\":\"f_4\",\"compareType\":\"GREATER\",\"compareValue\":\"18\"}]]");
                ticketFlowNodeRuleTemplateDtoList.add(ticketFlowNodeRuleTemplateDto);
            }
            ticketFlowNodeTemplateDto.setTicketFlowNodeRuleTemplateDtoList(ticketFlowNodeRuleTemplateDtoList);

            List<TicketFlowNodeExecutorTemplateDto> ticketFlowNodeExecutorTemplateList = new ArrayList<>();
            for (int executor = 1; executor < 3; executor++) {
                TicketFlowNodeExecutorTemplateDto ticketFlowNodeExecutorTemplateDto = new TicketFlowNodeExecutorTemplateDto();
                ticketFlowNodeExecutorTemplateDto.setId("");
                ticketFlowNodeExecutorTemplateDto.setExecutorType(ExecutorTypeEnum.APPLY_MEMBER_LIST.getCode());
                ticketFlowNodeExecutorTemplateDto.setExecutorValue("[{accountType:\"smy\",accountId:\"y01781\",accountName:\"殷沙沙\"},{accountType:\"smy\",accountId:\"o02157\",accountName:\"欧文\"}]");
                ticketFlowNodeExecutorTemplateList.add(ticketFlowNodeExecutorTemplateDto);
            }
            ticketFlowNodeTemplateDto.setTicketFlowNodeExecutorTemplateDtoList(ticketFlowNodeExecutorTemplateList);
            ticketFlowNodeTemplateDtoList.add(ticketFlowNodeTemplateDto);

        }

        ticketTemplateDto.setTicketFormTemplateDto(ticketFormTemplateDto);

        ticketFlowTemplateDto.setTicketFlowNodeTemplateDtoList(ticketFlowNodeTemplateDtoList);
        ticketTemplateDto.setTicketFlowTemplateDto(ticketFlowTemplateDto);

        System.out.println(JSONUtil.toJsonStr(ticketTemplateDto));
        Response<String> ticketTemplateId = ticketTemplateService.save(ticketTemplateDto, "ldap", "y01781", "殷沙沙");


    }

    @Test
    public void insertTicketAccountTest() {
        TicketTemplateDto ticketTemplateDto = new TicketTemplateDto();

        ticketTemplateService.initTicketTemplate("ldap", "y01781", "殷沙沙");
    }

    @Test
    public void save() {
        TicketTemplateFullDto ticketTemplateFullDto = new TicketTemplateFullDto();
        TicketTemplate ticketTemplate = new TicketTemplate();
        ticketTemplate.setId("1182404300000530001");
        ticketTemplate.setAppId("1");
        ticketTemplate.setTicketStatus(TicketTemplateStatusEnum.INIT);
        ticketTemplate.setTicketName("yssGD011");
        ticketTemplate.setDescription("yss创建GD011");
        ticketTemplate.setTicketTemplateCode("ticketTemplateCode11");
        ticketTemplate.setBeyondApps("2,3,4");
        ticketTemplate.setInterfaceKey("interfaceKey");
        ticketTemplate.setTicketFormChangeFlag(YESNOEnum.NO);
        ticketTemplate.setTicketMsgArriveType(TicketMsgArriveTypeEnum.WECOM);
        ticketTemplate.setTicketMsgBuildType(TicketMsgBuildTypeEnum.CREATE_NONE);

        ticketTemplateFullDto.setTicketTemplate(ticketTemplate);

        //TicketFormTemplate表单模板
        TicketFormTemplate ticketFormTemplate = new TicketFormTemplate();
        ticketFormTemplate.setId("2");
        ticketFormTemplate.setTicketTemplateId("12");
        ticketTemplateFullDto.setTicketFormTemplate(ticketFormTemplate);
        ticketTemplateService.saveOrUpdateTest(ticketTemplateFullDto);
    }

    @Test
    public void query() {

        Response<TicketTemplateDto> ticketTemplateDtoResp = ticketTemplateService.selectTicketTemplateFullById("1182404280000250001", "");
        System.out.println( JSONUtil.toJsonStr(ticketTemplateDtoResp));
    }

    @Test
    public void del() {
        System.out.println(ticketTemplateMapper.deleteById("1182405090000680001"));
    }

    @Test
    public void task() {

        ticketFormItemValuesSyncTask.ticketFormItemValuesSync(5L);
    }

    @Resource
    TicketFinishTask ticketFinishTask;
    @Test
    public void task1() {

        ticketFinishTask.finishTicketSync();
    }


}
