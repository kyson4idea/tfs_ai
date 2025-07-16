package com.smy.tfs.api.dbo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.dynamic.TicketDataStdDto;
import com.smy.tfs.api.dto.ticket_sla_service.NewTag;
import com.smy.tfs.api.enums.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.sql.Array;
import java.util.*;

/**
 * <p>
 * 工单数据表
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Getter
@Setter
@TableName("ticket_data")
public class TicketData extends TfsBaseEntity implements Serializable {


    private static final long serialVersionUID = -1848464036100695203L;
    /**
     * 工单ID
     */
    private String id;

    /**
     * 业务号，保障只有一个审批中的工单
     */
    private String ticketBusinessKey;

    /**
     * 工单模版ID
     */
    private String templateId;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 工单状态：
     * 草稿中
     * 审批中
     * 审批结束
     */
    private TicketDataStatusEnum ticketStatus;

    /**
     * 工单名称
     */
    private String ticketName;

    /**
     * 说明
     */
    private String description;
    /**
     * 工单模板标识
     */
    private String ticketTemplateCode;

    /**
     * 关联的应用
     */
    private String beyondApps;

    /**
     * 接口生成标识
     */
    private String interfaceKey;

    /**
     * 最近处理节点名称
     */
    private String currentNodeName;

    /**
     * 当前处理节点ID
     */
    private String currentNodeId;

    /**
     * 当前处理人
     * 示例：[{"accountId":"o02157","accountName":"Owen","accountType":"oms"}]
     */
    private String currentDealUsers;

    /**
     * 处理完成的人
     * 示例：[{"accountId":"o02157","accountName":"Owen","accountType":"oms"}]
     */
    private String currentDoneUsers;

    /**
     * 所有抄送人
     * 示例：[{"accountId":"o02157","accountName":"Owen","accountType":"oms"}]
     */
    private String currentCcUsers;

    /**
     * 工单完成时间
     */
    private Date ticketFinishTime;


    /**
     * 工单创建人
     *
     * @see AccountInfo
     */
    private String applyUser;


    private String wxChatGroupId;


    private TicketMsgBuildTypeEnum ticketMsgBuildType;

    /**
     * 通知触达方式
     */
    private TicketMsgArriveTypeEnum ticketMsgArriveType;

    /**
     * 工单支持修改标识
     */
    private YESNOEnum ticketFormChangeFlag;

    /**
     * 版本
     */
    private Integer version;

    /**
     * 基础流程 ["flowId-flowName"...]
     */
    private String baseFlow;

    //["已超时",""]
    private String tags;

    @TableField(exist = false)
    private String newTags;

    private String applyTicketWays;

    private String extend1;
    private String extend2;
    private String extend3;
    private String extend4;
    private String extend5;
    private String extend6;
    private String extend7;
    private String extend8;
    private String extend9;
    private String extend10;

    public void InitTicketData(String applyId, String appId, String templateId) {
        this.id = applyId;
        this.ticketStatus = TicketDataStatusEnum.INIT;
        this.appId = appId;
        if (templateId == null || templateId.isEmpty()) {
            templateId = "-1";
        }
        this.templateId = templateId;
        this.ticketName = "";
        this.description = "";
        this.currentNodeId = "";
        this.currentNodeName = "";
        this.currentDealUsers = "";
        this.currentDoneUsers = "";
        this.currentCcUsers = "";
        this.setCreateBy("");
        this.setCreateTime(new Date());
        this.setUpdateBy("");
        this.setUpdateTime(new Date());
    }


    public void InitTicketData(String applyUser,
                               TicketApp ticketApp,
                               TicketTemplate template,
                               TicketDataStdDto ticketDataStdDto,
                               TicketFlowData ticketFlowData,
                               TicketFlowNodeData currentNodeData,
                               List<TicketFlowNodeData> nodeDataList,
                               List<TicketFlowNodeExecutorData> currentNodeExecutors,
                               List<TicketFormItemData> formItemDataList
    ) {
        this.templateId = template.getId();
        this.ticketBusinessKey = ticketDataStdDto.getTicketBusinessKey();
        this.appId = template.getAppId();
        this.applyUser = applyUser;
        this.ticketStatus = TicketDataStatusEnum.APPLYING;
        this.ticketName = template.getTicketName();
        this.description = template.getDescription();
        this.ticketTemplateCode = template.getTicketTemplateCode();
        this.beyondApps = template.getBeyondApps();
        this.interfaceKey = template.getInterfaceKey();
        this.currentNodeId = currentNodeData.getId();
        this.currentNodeName = currentNodeData.getNodeName();
        this.ticketMsgBuildType = template.getTicketMsgBuildType();
        this.ticketMsgArriveType = template.getTicketMsgArriveType();
        this.version = template.getVersion();
        this.ticketFormChangeFlag = template.getTicketFormChangeFlag();
        this.applyTicketWays = template.getApplyTicketWays();
        this.tags = ticketDataStdDto.getTags();

        this.setCreateBy(applyUser);
        this.setUpdateTime(new Date());
        this.setUpdateBy("system");

        List<AccountInfo> currentDealUsers = new ArrayList<>();
        List<AccountInfo> existCcs = AccountInfo.ToAccountInfoList(this.getCurrentCcUsers());
        existCcs = existCcs == null ? new ArrayList<>() : existCcs;
        for (TicketFlowNodeExecutorData executor : currentNodeExecutors) {
            switch (executor.getExecutorType()) {
                case APPLY_MEMBER_LIST:
                case APPLY_GROUP:
                case APPLY_LEADER:
                case APPLY_SELF:
                case APPLY_POINT:
                case APPLY_DEPT_MANAGERS:
                case APPLY_DEPT_POINT:
                case APPLY_EXTERNAL_APPROVER:
                    List<AccountInfo> dealUsers = AccountInfo.ToAccountInfoList(executor.getExecutorList());
                    currentDealUsers.addAll(dealUsers);
                    break;
                case CA_SELF:
                case CA_MEMBER_LIST:
                case CA_GROUP:
                case CA_LEADER:
                case CA_DEPT_MANAGERS:
                case CA_EXTERNAL_APPROVER:
                    List<AccountInfo> ccUsers = AccountInfo.ToAccountInfoList(executor.getExecutorList());
                    existCcs.addAll(ccUsers);
                    break;
                default:
            }
        }

        existCcs = AccountInfo.Distinct(existCcs);
        currentDealUsers = AccountInfo.Distinct(currentDealUsers);
        this.setCurrentCcUsers(AccountInfo.ToAccountInfoListStr(existCcs));
        this.setCurrentDealUsers(AccountInfo.ToAccountInfoListStr(currentDealUsers));
        if (CollectionUtils.isNotEmpty(nodeDataList)) {
            List<String> baseFlow = new ArrayList<>();
            for (TicketFlowNodeData nodeData : nodeDataList) {
                if (!Arrays.asList("开始", "结束").contains(nodeData.getNodeName())) {
                    baseFlow.add(nodeData.getId() + "-" + nodeData.getNodeName());
                }
            }
            this.baseFlow = JSON.toJSONString(baseFlow);
        }
        if (ticketApp != null && YESNOEnum.YES.equals(ticketApp.getExtendEnabled()) && CollectionUtils.isNotEmpty(formItemDataList)) {
            for (TicketFormItemData itemData : formItemDataList) {
                String itemConfig = itemData.getItemConfigExt();
                itemData.EqConfig();
                if (StringUtils.isNotEmpty(itemConfig)) {
                    JSONObject itemConfigJsonObj = JSONObject.parseObject(itemConfig);
                    if (itemConfigJsonObj != null
                            && itemConfigJsonObj.containsKey("extendKey")
                            && StringUtils.isNotEmpty(itemConfigJsonObj.getString("extendKey"))) {
                        if ("extend1".equals(itemConfigJsonObj.getString("extendKey"))) {
                            this.extend1 = getItemValue(itemData.getItemType(), itemData.getItemValue(), itemConfigJsonObj);
                        } else if ("extend2".equals(itemConfigJsonObj.getString("extendKey"))) {
                            this.extend2 = getItemValue(itemData.getItemType(), itemData.getItemValue(), itemConfigJsonObj);
                        } else if ("extend3".equals(itemConfigJsonObj.getString("extendKey"))) {
                            this.extend3 = getItemValue(itemData.getItemType(), itemData.getItemValue(), itemConfigJsonObj);
                        } else if ("extend4".equals(itemConfigJsonObj.getString("extendKey"))) {
                            this.extend4 = getItemValue(itemData.getItemType(), itemData.getItemValue(), itemConfigJsonObj);
                        } else if ("extend5".equals(itemConfigJsonObj.getString("extendKey"))) {
                            this.extend5 = getItemValue(itemData.getItemType(), itemData.getItemValue(), itemConfigJsonObj);
                        } else if ("extend6".equals(itemConfigJsonObj.getString("extendKey"))) {
                            this.extend6 = getItemValue(itemData.getItemType(), itemData.getItemValue(), itemConfigJsonObj);
                        } else if ("extend7".equals(itemConfigJsonObj.getString("extendKey"))) {
                            this.extend7 = getItemValue(itemData.getItemType(), itemData.getItemValue(), itemConfigJsonObj);
                        } else if ("extend8".equals(itemConfigJsonObj.getString("extendKey"))) {
                            this.extend8 = getItemValue(itemData.getItemType(), itemData.getItemValue(), itemConfigJsonObj);
                        } else if ("extend9".equals(itemConfigJsonObj.getString("extendKey"))) {
                            this.extend9 = getItemValue(itemData.getItemType(), itemData.getItemValue(), itemConfigJsonObj);
                        } else if ("extend10".equals(itemConfigJsonObj.getString("extendKey"))) {
                            this.extend10 = getItemValue(itemData.getItemType(), itemData.getItemValue(), itemConfigJsonObj);
                        } else {
                            throw new RuntimeException("extendKey配置错误");
                        }
                    }
                }
            }
        }
    }

    private String getItemValue(FormItemTypeEnum itemType, String itemValue, JSONObject itemConfigJsonObj){
        if (Objects.nonNull(itemType) && itemType == FormItemTypeEnum.CASCADER) {
            return itemConfigJsonObj.getString("displayValue");
        }
        return itemValue;

    }

}
