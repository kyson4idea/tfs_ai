package com.smy.tfs.api.dto.query;

import com.smy.tfs.api.dbo.TicketData;
import com.smy.tfs.api.enums.TicketDataStatusEnum;
import com.smy.tfs.api.enums.TicketMsgArriveTypeEnum;
import com.smy.tfs.api.enums.TicketMsgBuildTypeEnum;
import com.smy.tfs.api.enums.YESNOEnum;
import com.smy.tfs.common.utils.DateUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

@Data
@NoArgsConstructor
public class BusiCommonESQueryRspDto implements Serializable {
    private static final long serialVersionUID = 6737283450618148679L;

    /**
     * 工单对象
     */
    private TicketData ticketData;

    public BusiCommonESQueryRspDto(Map<String,Object> map){
        TicketData it = new TicketData();
        if (Objects.nonNull(map.get("create_time"))) {
            String createTimeStr = (String)map.get("create_time");
            it.setCreateTime(DateUtils.parseDate(createTimeStr));
        }
        if (Objects.nonNull(map.get("update_time"))) {
            String updateTimeStr = (String)map.get("update_time");
            it.setUpdateTime(DateUtils.parseDate(updateTimeStr));
        }
        if (Objects.nonNull(map.get("id"))) {
            it.setId((String)map.get("id"));
        }
        if (Objects.nonNull(map.get("ticket_business_key"))) {
            it.setTicketBusinessKey((String)map.get("ticket_business_key"));
        }
        if (Objects.nonNull(map.get("template_id"))) {
            it.setTemplateId((String)map.get("template_id"));
        }
        if (Objects.nonNull(map.get("app_id"))) {
            it.setAppId((String)map.get("app_id"));
        }
        if (Objects.nonNull(map.get("ticket_status"))) {
            it.setTicketStatus(TicketDataStatusEnum.getEnumByCode((String)map.get("ticket_status")));
        }
        if (Objects.nonNull(map.get("ticket_name"))) {
            it.setTicketName((String)map.get("ticket_name"));
        }
        if (Objects.nonNull(map.get("description"))) {
            it.setDescription((String)map.get("description"));
        }
        if (Objects.nonNull(map.get("ticket_template_code"))) {
            it.setTicketTemplateCode((String)map.get("ticket_template_code"));
        }
        if (Objects.nonNull(map.get("beyond_apps"))) {
            it.setBeyondApps((String)map.get("beyond_apps"));
        }
        if (Objects.nonNull(map.get("interface_key"))) {
            it.setInterfaceKey((String)map.get("interface_key"));
        }
        if (Objects.nonNull(map.get("current_node_name"))) {
            it.setCurrentNodeName((String)map.get("current_node_name"));
        }
        if (Objects.nonNull(map.get("current_node_id"))) {
            it.setCurrentNodeId((String)map.get("current_node_id"));
        }
        if (Objects.nonNull(map.get("current_deal_users"))) {
            it.setCurrentDealUsers((String)map.get("current_deal_users"));
        }
        if (Objects.nonNull(map.get("current_done_users"))) {
            it.setCurrentDoneUsers((String)map.get("current_done_users"));
        }
        if (Objects.nonNull(map.get("current_cc_users"))) {
            it.setCurrentCcUsers((String)map.get("current_cc_users"));
        }
        if (Objects.nonNull(map.get("ticket_finish_time"))) {
            String ticketFinishTimeStr = (String)map.get("ticket_finish_time");
            it.setTicketFinishTime(DateUtils.parseDate(ticketFinishTimeStr));
        }
        if (Objects.nonNull(map.get("apply_user"))) {
            it.setApplyUser((String)map.get("apply_user"));
        }
        if (Objects.nonNull(map.get("wx_chat_group_id"))) {
            it.setWxChatGroupId((String)map.get("wx_chat_group_id"));
        }
        if (Objects.nonNull(map.get("ticket_msg_build_type"))) {
            it.setTicketMsgBuildType(TicketMsgBuildTypeEnum.getEnumByCode((String)map.get("ticket_msg_build_type")));
        }
        if (Objects.nonNull(map.get("ticket_msg_arrive_type"))) {
            it.setTicketMsgArriveType(TicketMsgArriveTypeEnum.getEnumByCode((String)map.get("ticket_msg_arrive_type")));
        }
        if (Objects.nonNull(map.get("ticket_form_change_flag"))) {
            it.setTicketFormChangeFlag(YESNOEnum.getEnumByCode((String)map.get("ticket_form_change_flag")));
        }
        if (Objects.nonNull(map.get("version"))) {
            it.setVersion((Integer)map.get("version"));
        }
        if (Objects.nonNull(map.get("base_flow"))) {
            it.setBaseFlow((String)map.get("base_flow"));
        }
        if (Objects.nonNull(map.get("tags"))) {
            it.setTags((String)map.get("tags"));
        }
        if (Objects.nonNull(map.get("apply_ticket_ways"))) {
            it.setApplyTicketWays((String)map.get("apply_ticket_ways"));
        }
        if (Objects.nonNull(map.get("extend1"))) {
            it.setExtend2((String)map.get("extend1"));
        }
        if (Objects.nonNull(map.get("extend2"))) {
            it.setExtend2((String)map.get("extend2"));
        }
        if (Objects.nonNull(map.get("extend3"))) {
            it.setExtend3((String)map.get("extend3"));
        }
        if (Objects.nonNull(map.get("extend4"))) {
            it.setExtend4((String)map.get("extend4"));
        }
        if (Objects.nonNull(map.get("extend5"))) {
            it.setExtend5((String)map.get("extend5"));
        }
        if (Objects.nonNull(map.get("extend6"))) {
            it.setExtend6((String)map.get("extend6"));
        }
        if (Objects.nonNull(map.get("extend7"))) {
            it.setExtend7((String)map.get("extend7"));
        }
        if (Objects.nonNull(map.get("extend8"))) {
            it.setExtend8((String)map.get("extend8"));
        }
        if (Objects.nonNull(map.get("extend9"))) {
            it.setExtend9((String)map.get("extend9"));
        }
        if (Objects.nonNull(map.get("extend10"))) {
            it.setExtend10((String)map.get("extend10"));
        }
        this.ticketData = it;
    }
}
