package com.smy.tfs.api.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.smy.tfs.api.dto.ReqParam;
import com.smy.tfs.api.dto.TicketDataDto;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.dynamic.*;
import com.smy.tfs.api.enums.*;
import com.smy.tfs.openapi.service.ITicketDataServiceWrapper;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
public class ITicketDataServiceTest {

    @Autowired
    ITicketDataService ticketDataService;
    @Resource
    private ITicketDataServiceWrapper iTicketDataServiceWrapper;


    @Test
    public void getApplyIDTest() {

//       List<String> compareValueList = JSONArray.parseArray("[\"1\",\"2\"]", String.class);
//       List<String> templateCompareValueList =JSONArray.parseArray("[\"1\",\"2\",\"3\"]", String.class);
//       var  compareResult =  CollectionUtils.containsAny(templateCompareValueList, compareValueList);


        ticketDataService.getTicketApplyId("tfs");
    }


    //深圳20岁的男性
    @Test
    public void createTicketTest1() {
        Response<String> applyIDRes = ticketDataService.getTicketApplyId("tfs");
        assert applyIDRes != null && Objects.equals(applyIDRes.getCode(), BizResponseEnums.SUCCESS.getCode());
        String applyID = applyIDRes.getData();
        TicketDataStdDto ticketDataStdDto = new TicketDataStdDto();
        ticketDataStdDto.setApplyId(applyID);
        //ticketDataStdDto.setApplyUserId("o02157");




        ticketDataStdDto.setTicketTemplateId("owenTest");
        List<TicketFormItemStdDto> formItems = new ArrayList<>();
        formItems.add(new TicketFormItemStdDto("姓名", "2"));
        formItems.add(new TicketFormItemStdDto("详情", "https://www.tcpdump.org/", "LINK"));
        JSONArray formItems1 = new JSONArray();
        JSONObject item1 = new JSONObject();
        item1.put("姓名", "海龙");
        item1.put("性别", "男");
        item1.put("地区", "深圳");
        formItems1.add(item1);
        JSONObject item2 = new JSONObject();
        item2.put("姓名", "施悦");
        item2.put("性别", "男");
        item2.put("地区", "上海");
        formItems1.add(item2);
        JSONObject item3 = new JSONObject();
        item3.put("姓名", "owen");
        item3.put("性别", "男");
        item3.put("地区", "北京");
        formItems1.add(item3);
        formItems.add(new TicketFormItemStdDto("人员列表", JSONObject.toJSONString(formItems1), "TABLE"));

        ticketDataStdDto.setFormItems(formItems);
        var resp = ticketDataService.createTicket(ticketDataStdDto, "ldap", "o02157", "owen");
        assert resp != null && Objects.equals(resp.getCode(), BizResponseEnums.SUCCESS.getCode());
    }

    //深圳20岁的女性
    @Test
    public void createTicketTest2() {
        Response<String> applyIDRes = ticketDataService.getTicketApplyId("tfs");
        assert applyIDRes != null && Objects.equals(applyIDRes.getCode(), BizResponseEnums.SUCCESS.getCode());
        String applyID = applyIDRes.getData();
        TicketDataStdDto ticketDataStdDto = new TicketDataStdDto();
        ticketDataStdDto.setApplyId(applyID);
        ticketDataStdDto.setTicketTemplateId("1182404280000250001");
        List<TicketFormItemStdDto> formItems = new ArrayList<>();
        formItems.add(new TicketFormItemStdDto("1162404280000210001", "Owen"));
        formItems.add(new TicketFormItemStdDto("1162404280000210002", "女"));
        formItems.add(new TicketFormItemStdDto("1162404280000210003", "20"));
        formItems.add(new TicketFormItemStdDto("1162404280000210004", "深圳"));
        ticketDataStdDto.setFormItems(formItems);
        var resp = ticketDataService.createTicket(ticketDataStdDto, "ldap", "o02157", "owen");
        assert resp != null && Objects.equals(resp.getCode(), BizResponseEnums.SUCCESS.getCode());
    }

    @Test
    public void createTicketTest5() {
        // 创建工单
        TicketDataStdDto ticketDataStdDto = new TicketDataStdDto();
        // 工单号，通过申请工单号接口获取, ticketService.getTicketApplyId
        ticketDataStdDto.setApplyId(iTicketDataServiceWrapper.getTicketApplyId("tianxin").getData());
        // 工单模板ID
        ticketDataStdDto.setTicketTemplateId("1182405280000210001");
        // 工单表单内容
        List<TicketFormItemStdDto> formItems = new ArrayList<>();
        formItems.add(new TicketFormItemStdDto("1162405280000290001", "zjwapi发起053006"));
        formItems.add(new TicketFormItemStdDto("1162405280000290002", "个人使用"));
        formItems.add(new TicketFormItemStdDto("1162405280000290003", "为自己开通"));
        formItems.add(new TicketFormItemStdDto("1162405280000290004", "[\"标签管理\",\"客群管理\"]"));
        formItems.add(new TicketFormItemStdDto("1162405280000290008", "15"));
        formItems.add(new TicketFormItemStdDto("1162405280000290009", "2024-05-29 01:03:01"));
        ticketDataStdDto.setFormItems(formItems);
        Response<String> result = iTicketDataServiceWrapper.createTicket(ticketDataStdDto, "tianxin", "1002405200001200003", "郑吉伟1");
        System.out.println("Receive result ======> " + result);

    }


    //上海20岁的男性
    @Test
    public void createTicketTest3() {
        Response<String> applyIDRes = ticketDataService.getTicketApplyId("tfs");
        assert applyIDRes != null && Objects.equals(applyIDRes.getCode(), BizResponseEnums.SUCCESS.getCode());
        String applyID = applyIDRes.getData();
        TicketDataStdDto ticketDataStdDto = new TicketDataStdDto();
        ticketDataStdDto.setApplyId(applyID);
        ticketDataStdDto.setTicketTemplateId("1182404280000250001");
        List<TicketFormItemStdDto> formItems = new ArrayList<>();
        formItems.add(new TicketFormItemStdDto("1162404280000210001", "Owen"));
        formItems.add(new TicketFormItemStdDto("1162404280000210002", "男"));
        formItems.add(new TicketFormItemStdDto("1162404280000210003", "20"));
        formItems.add(new TicketFormItemStdDto("1162404280000210004", "上海"));
        ticketDataStdDto.setFormItems(formItems);
        var resp = ticketDataService.createTicket(ticketDataStdDto, "ldap", "o02157", "owen");
        assert resp != null && Objects.equals(resp.getCode(), BizResponseEnums.SUCCESS.getCode());
    }

    //执行人类型工单
    @Test
    public void createTicketTest4() {
        //步骤1：动态获取工单号
        Response<String> applyIdRes = ticketDataService.getTicketApplyId("tfs"); //需调整为实际业务appId
        if (applyIdRes.getEnum() != BizResponseEnums.SUCCESS) {
            System.out.println("get apply id failed");
            return;
        }
        System.out.println("Get ApplyId Success，Id： ======> " + applyIdRes.getData());

        //步骤2：创建工单
        TicketDataStdDto ticketDataStdDto = new TicketDataStdDto();
        //ticketDataStdDto.setApplyId("1002406030001820006");// 页面工单号
        ticketDataStdDto.setApplyId(applyIdRes.getData());// 动态工单号
        ticketDataStdDto.setTicketTemplateId("1182405270001210001");// 工单模板ID

        List<TicketFormItemStdDto> formItems = new ArrayList<>();// 工单表单内容
        formItems.add(new TicketFormItemStdDto("1162405270001590001", "1"));// 姓名
        formItems.add(new TicketFormItemStdDto("1162406030001660001", "选项一"));// 地区

        //formItems.add(new TicketFormItemStdDto("公司名称", "萨摩耶")); //中文例如“公司名称”，无需提前配置
        ticketDataStdDto.setFormItems(formItems);

        List<TicketFlowNodeStdDto> flowNodes = new ArrayList<>();// 工单流程内容
        flowNodes.add(new TicketFlowNodeStdDto("1122405270001510002", "ldap", "z00740", "张泽东"));// 审批人
        ticketDataStdDto.setFlowNodes(flowNodes);

        String userType = "ldap";//用户账号类型
        String userId = "o02157";//用户ID
        String userName = "owen";//用户名称
        Response<String> result = ticketDataService.createTicket(ticketDataStdDto, userType, userId, userName);// 调用创建工单接口
        if (result.getEnum() != BizResponseEnums.SUCCESS) {
            System.out.println("create ticket failed");
            return;
        }
        System.out.println("Create Ticket Success, Id: ======> " + result.getData());

    }


    @Test
    public void countFlowNodeTest3() {
        Response<String> applyIDRes = ticketDataService.getTicketApplyId("tfs");
        assert applyIDRes != null && Objects.equals(applyIDRes.getCode(), BizResponseEnums.SUCCESS.getCode());
        String applyID = applyIDRes.getData();
        TicketDataStdDto ticketDataStdDto = new TicketDataStdDto();
        ticketDataStdDto.setApplyId(applyID);
        ticketDataStdDto.setTicketTemplateId("1182404280000250001");
        List<TicketFormItemStdDto> formItems = new ArrayList<>();
        formItems.add(new TicketFormItemStdDto("1162404280000210001", "Owen"));
        formItems.add(new TicketFormItemStdDto("1162404280000210002", "男"));
        formItems.add(new TicketFormItemStdDto("1162404280000210003", "20"));
        formItems.add(new TicketFormItemStdDto("1162404280000210004", "上海"));
        ticketDataStdDto.setFormItems(formItems);
        var resp = ticketDataService.countFlowNode(ticketDataStdDto);
        assert resp != null && Objects.equals(resp.getCode(), BizResponseEnums.SUCCESS.getCode());
    }

    @Test
    public void createTicketDynamicTest() {
        Response<String> applyIDRes = ticketDataService.getTicketApplyId("tfs");
        assert applyIDRes != null && "200".equals(applyIDRes.getCode());
        String applyID = applyIDRes.getData();


        TicketDataDynamicDto dynamicDto = new TicketDataDynamicDto();
        dynamicDto.setAppId("tfs");
        dynamicDto.setId(applyID);
        dynamicDto.setTicketName(String.format("工单-%s", dynamicDto.getId()));
        //流程数据
        TicketFlowDataDynamicDto ticketFlowDataDynamicDto = new TicketFlowDataDynamicDto();
        ticketFlowDataDynamicDto.setStartCc("smy:o02157-owen");
        ticketFlowDataDynamicDto.setEndCc("smy:y01781-殷沙沙");
        //流程节点数据
        List<TicketFlowNodeDataDynamicDto> ticketFlowNodeDataDynamicDtoList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            TicketFlowNodeDataDynamicDto ticketFlowNodeDataDynamicDto = new TicketFlowNodeDataDynamicDto();
            ticketFlowNodeDataDynamicDto.setOrder(i);
            ticketFlowNodeDataDynamicDto.setAuditedMethod(AuditedMethodEnum.AND);
            ticketFlowNodeDataDynamicDto.setAuditedType(AuditedType.BY_USER);
            //审批人
            TicketFlowNodeExcutorDynamicDto ticketFlowNodeExcutorDynamicDto = new TicketFlowNodeExcutorDynamicDto();
            ticketFlowNodeExcutorDynamicDto.setExecutorType(ExecutorTypeEnum.APPLY_MEMBER_LIST);
            if (i == 0) {
                AccountInfo accountInfo = new AccountInfo("smy", "smy", "y01781", "殷沙沙");
                ticketFlowNodeExcutorDynamicDto.setExecutorValue(accountInfo.ToJsonString());
            }
            if (i == 1) {
                AccountInfo accountInfo = new AccountInfo("smy", "smy", "y01781", "殷沙沙");
                ticketFlowNodeExcutorDynamicDto.setExecutorValue(accountInfo.ToJsonString());
            }
            List<TicketFlowNodeExcutorDynamicDto> excutorDtoList = new ArrayList<>();
            excutorDtoList.add(ticketFlowNodeExcutorDynamicDto);
            ticketFlowNodeDataDynamicDto.setExcutorDtoList(excutorDtoList);
            ticketFlowNodeDataDynamicDtoList.add(ticketFlowNodeDataDynamicDto);
        }
        ticketFlowDataDynamicDto.setTicketFlowNodeDataDynamicDtoList(ticketFlowNodeDataDynamicDtoList);
        dynamicDto.setTicketFlowDataDynamicDto(ticketFlowDataDynamicDto);
        //表单数据
        TicketFormDataDynamicDto ticketFormDataDynamicDto = new TicketFormDataDynamicDto();
        //表单节点数据
        List<TicketFormItemDataDynamicDto> ticketFormItemDataDynamicDtos = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            TicketFormItemDataDynamicDto ticketFormItemDataDynamicDto = new TicketFormItemDataDynamicDto();
            ticketFormItemDataDynamicDto.setItemOrder(i);
            if (i == 0) {//输入框
                ticketFormItemDataDynamicDto.setItemType(FormItemTypeEnum.INPUT);
                ticketFormItemDataDynamicDto.setItemLabel("测试标题" + i);
                ticketFormItemDataDynamicDto.setItemValue("测试值" + i);
            }
            if (i == 1) {//选择框
                ticketFormItemDataDynamicDto.setItemType(FormItemTypeEnum.SELECT);
                ticketFormItemDataDynamicDto.setItemLabel("测试标题" + i);
                ticketFormItemDataDynamicDto.setItemValue("选择框测试值" + i);
            }
            if (i == 2) {//提示框
                ticketFormItemDataDynamicDto.setItemType(FormItemTypeEnum.TIP);
                ticketFormItemDataDynamicDto.setItemLabel("提示框的测试提示" + i);
            }
            ticketFormItemDataDynamicDtos.add(ticketFormItemDataDynamicDto);
        }
        ticketFormDataDynamicDto.setTicketFormItemDataDtoList(ticketFormItemDataDynamicDtos);
        dynamicDto.setTicketFormDataDynamicDto(ticketFormDataDynamicDto);
        var resp = ticketDataService.createTicketDynamic(dynamicDto, "ldap", "o02157", "owen");
        assert resp != null && "200".equals(resp.getCode());
    }

    @Test
    public void selectFullTicketDataByIdTest() {
        Response<TicketDataDto> response = ticketDataService.selectFullTicketDataById(new ReqParam("1002404240000330001"));
        //给response添加断言
        assert response.getCode() == "0";
    }

    @Resource
    private ITicketAccountService ticketAccountService;
    @Test
    public void syncTicketAccountGroup() {

        Response response = ticketAccountService.syncTicketAccountGroup("NCS","项目经理","11111116",null);
        //给response添加断言
        assert response.getCode() == "0";
    }


    @Test
    public void dealTicketDataByIdTest() {
        String ticketID = "1002404240000330001";
        String dealType = ApproveDealTypeEnum.PASS.getCode();
        String dealOpinion = "合理，通过";
        String dealUserType = "ldap";
        String dealUserId = "o02157";
        String dealUserName = "owen";
        try {
            Response<TicketDataDto> response = ticketDataService.dealTicketDataById(ticketID, dealType, dealOpinion, dealUserType, dealUserId, dealUserName, null);
            log.info("result1 = {}", response);

            response = ticketDataService.dealTicketDataById(ticketID, dealType, dealOpinion, dealUserType, dealUserId, dealUserName, null);
            log.info("result2 = {}", response);

            response = ticketDataService.dealTicketDataById(ticketID, dealType, dealOpinion, dealUserType, dealUserId, dealUserName, null);
            log.info("result3 = {}", response);
            //assert Objects.equals(response.getCode(), "0");
        } catch (Exception e) {
            log.error("", e);
            assert false;
        }
    }


    @Test
    public void createQWGroupByIdListTest() {
        List<String> isList = new ArrayList<>();
        isList.add("1002407230003140013");
        List<AccountInfo> userList = new ArrayList<>();
        userList.add(new AccountInfo("", "ldap", "o02157", "owen"));
        ticketDataService.createQWGroupByIdList(isList, userList);
    }


    @Test
    public void joinQWGroupTest() {
        String ticketDataId = "1002407230003140013";
        String userType = "ldap";
        String userID = "y01781";
        ticketDataService.joinQWGroup(ticketDataId, userType, userID);
    }


    @Test
    public void gotoFlowNodeTest() {
        String ticketDataId = "1002407300003440010";
        String currentNodeId = "1082407300005810072";
        String gotoNodeId = "1082407300005810070";
        String gotoNodeReason = "12";
        AccountInfo accountInfo = new AccountInfo("", "ldap", "o02157", "owen");
        ticketDataService.gotoFlowNode(ticketDataId, currentNodeId, gotoNodeId, gotoNodeReason, accountInfo);
    }
}
