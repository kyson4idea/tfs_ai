package com.smy.tfs.api.dbo;

import com.alibaba.fastjson2.JSONObject;
import com.smy.framework.base.BaseElasticsearchEntity;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.common.utils.DateUtils;
import com.smy.tfs.common.utils.StringUtils;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Data
public class ESTicketData extends BaseElasticsearchEntity implements Serializable {

    private static final long serialVersionUID = 3051243751209258739L;

    public ESTicketData () {

    }

    //工单id
    private String id;

    //业务id
    private String app_id;

    //应用名称
    private String app_name;

    //工单名称
    private String ticket_name;

    //工单状态
    private String ticket_status;

    //申请人
    private String apply_user;

    //创建时间,格式"yyyy-MM-dd HH:mm:ss"
    private Date create_time;

    //更新时间,格式"yyyy-MM-dd HH:mm:ss"
    private Date update_time;

    //更新时间,格式"yyyy-MM-dd HH:mm:ss"
    private Date ticket_finish_time;

    //工单标签
    private String tags;

    //工单模版Id
    private String template_id;

    //工单模版名称
    private String template_name;

    //删除时间
    private Date delete_time;

    //当前处理人
    private String current_deal_users;

    //处理完成的人
    private String current_done_users;

    //抄送人
    private String current_cc_users;

    private Long ticket_data_ts;

    private String ticket_business_key;

    private String ticket_template_code;

    private String beyond_apps;

    //所属分类id
    private String beyond_category_id;

    //所属分类名称
    private String beyond_category_name;

    private String current_node_id;

    private String current_node_name;

    private String apply_ticket_ways;

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

    /**
     * 微信群聊Id
     */
    private String wx_chat_group_id;

    /**
     * 通知触达方式
     */
    private String ticket_msg_arrive_type;

    /**
     * 工单申请人的sameoriginid
     */
    private String apply_user_sameoriginid;

    /**
     * 其中一个当前处理人的sameoriginid
     */
    private String one_current_deal_user_sameoriginid;

    //工单全部信息
    private HashMap<String, String> all_ticket_data_info = new HashMap<>();

    public ESTicketData (String id) {

        this.id = id;
    }

    public ESTicketData (JSONObject jsonObject, List<TicketFormItemData> ticketFormItemDataList, Long ts, String beyondCategoryId) {

        Date createTime = null;
        if (Objects.nonNull(jsonObject.getString("create_time"))) {
            createTime = DateUtils.parseDate(jsonObject.getString("create_time"));
        }
        Date updateTime = null;
        if (Objects.nonNull(jsonObject.getString("update_time"))) {
            updateTime = DateUtils.parseDate(jsonObject.getString("update_time"));
        }
        Date ticketFinishTime = null;
        if (Objects.nonNull(jsonObject.getString("ticket_finish_time"))) {
            ticketFinishTime = DateUtils.parseDate(jsonObject.getString("ticket_finish_time"));
        }
        Date deleteTime = null;
        if (Objects.nonNull(jsonObject.getString("delete_time"))) {
            deleteTime = DateUtils.parseDate(jsonObject.getString("delete_time"));
        }
        this.id = jsonObject.getString("id");
        this.app_id = jsonObject.getString("app_id");
        this.ticket_name = jsonObject.getString("ticket_name");
        this.ticket_status = jsonObject.getString("ticket_status");
        this.apply_user = jsonObject.getString("apply_user");
        this.create_time = createTime;
        this.update_time = updateTime;
        this.ticket_finish_time = ticketFinishTime;
        this.tags = jsonObject.getString("tags");
        this.template_id = jsonObject.getString("template_id");
        this.delete_time = deleteTime;
        this.current_deal_users = jsonObject.getString("current_deal_users");
        this.current_done_users = jsonObject.getString("current_done_users");
        this.current_cc_users = jsonObject.getString("current_cc_users");
        this.ticket_business_key = jsonObject.getString("ticket_business_key");
        this.ticket_template_code = jsonObject.getString("ticket_template_code");
        this.beyond_apps = jsonObject.getString("beyond_apps");
        this.beyond_category_id = beyondCategoryId;
        this.current_node_id = jsonObject.getString("current_node_id");
        this.current_node_name = jsonObject.getString("current_node_name");
        this.apply_ticket_ways = jsonObject.getString("apply_ticket_ways");
        this.ticket_data_ts = ts;
        this.extend1 = jsonObject.getString("extend1");
        this.extend2 = jsonObject.getString("extend2");
        this.extend3 = jsonObject.getString("extend3");
        this.extend4 = jsonObject.getString("extend4");
        this.extend5 = jsonObject.getString("extend5");
        this.extend6 = jsonObject.getString("extend6");
        this.extend7 = jsonObject.getString("extend7");
        this.extend8 = jsonObject.getString("extend8");
        this.extend9 = jsonObject.getString("extend9");
        this.extend10 = jsonObject.getString("extend10");
        this.wx_chat_group_id = jsonObject.getString("wx_chat_group_id");
        this.ticket_msg_arrive_type = jsonObject.getString("ticket_msg_arrive_type");
        this.all_ticket_data_info.put("id", jsonObject.getString("id"));
        this.all_ticket_data_info.put("app_id", jsonObject.getString("app_id"));
        this.all_ticket_data_info.put("ticket_name", jsonObject.getString("ticket_name"));
        this.all_ticket_data_info.put("ticket_status", jsonObject.getString("ticket_status"));
        this.all_ticket_data_info.put("apply_user", jsonObject.getString("apply_user"));
        if (Objects.nonNull(jsonObject.getJSONObject("apply_user")) && jsonObject.getJSONObject("apply_user").containsKey("sameOriginId")) {
            String applyUserSameoriginid = jsonObject.getJSONObject("apply_user").getString("sameOriginId");
            this.all_ticket_data_info.put("apply_user_sameoriginid", applyUserSameoriginid);
            this.apply_user_sameoriginid = applyUserSameoriginid;
        }
        this.all_ticket_data_info.put("tags", jsonObject.getString("tags"));
        this.all_ticket_data_info.put("template_id", jsonObject.getString("template_id"));
        this.all_ticket_data_info.put("current_deal_users", jsonObject.getString("current_deal_users"));
        if (StringUtils.isNotEmpty(jsonObject.getString("current_deal_users"))) {
            List<AccountInfo> accountInfoList = AccountInfo.ToAccountInfoList(jsonObject.getString("current_deal_users"));
            if (CollectionUtils.isNotEmpty(accountInfoList)) {
                AccountInfo accountInfo = accountInfoList.get(0);
                String firstSameoriginid = accountInfo.getSameOriginId();
                if (StringUtils.isNotEmpty(firstSameoriginid)) {
                    this.all_ticket_data_info.put("one_current_deal_user_sameoriginid", firstSameoriginid);
                    this.one_current_deal_user_sameoriginid = firstSameoriginid;
                }
            }
        } else {
            this.all_ticket_data_info.put("one_current_deal_user_sameoriginid", "");
            this.one_current_deal_user_sameoriginid = "";
        }
        this.all_ticket_data_info.put("current_done_users", jsonObject.getString("current_done_users"));
        this.all_ticket_data_info.put("current_cc_users", jsonObject.getString("current_cc_users"));
        this.all_ticket_data_info.put("apply_ticket_ways", jsonObject.getString("apply_ticket_ways"));
        this.all_ticket_data_info.put("ticket_business_key", jsonObject.getString("ticket_business_key"));
        this.all_ticket_data_info.put("ticket_template_code", jsonObject.getString("ticket_template_code"));
        this.all_ticket_data_info.put("beyond_apps", jsonObject.getString("beyond_apps"));
        this.all_ticket_data_info.put("beyond_category_id", beyondCategoryId);
        this.all_ticket_data_info.put("current_node_id", jsonObject.getString("current_node_id"));
        this.all_ticket_data_info.put("current_node_name", jsonObject.getString("current_node_name"));
        this.all_ticket_data_info.put("extend1", jsonObject.getString("extend1"));
        this.all_ticket_data_info.put("extend2", jsonObject.getString("extend2"));
        this.all_ticket_data_info.put("extend3", jsonObject.getString("extend3"));
        this.all_ticket_data_info.put("extend4", jsonObject.getString("extend4"));
        this.all_ticket_data_info.put("extend5", jsonObject.getString("extend5"));
        this.all_ticket_data_info.put("extend6", jsonObject.getString("extend6"));
        this.all_ticket_data_info.put("extend7", jsonObject.getString("extend7"));
        this.all_ticket_data_info.put("extend8", jsonObject.getString("extend8"));
        this.all_ticket_data_info.put("extend9", jsonObject.getString("extend9"));
        this.all_ticket_data_info.put("extend10", jsonObject.getString("extend10"));
        this.all_ticket_data_info.put("wx_chat_group_id", jsonObject.getString("wx_chat_group_id"));
        this.all_ticket_data_info.put("ticket_msg_arrive_type", jsonObject.getString("ticket_msg_arrive_type"));
        if (CollectionUtils.isNotEmpty(ticketFormItemDataList)) {
            ticketFormItemDataList.stream().forEach(it -> {
                if (StringUtils.isNotEmpty(it.getItemLabel())) {
                    this.all_ticket_data_info.put(it.getItemLabel(), it.getItemValue());
                }
            });
        }
    }


}
