package com.smy.tfs.web.controller.test;

import com.smy.framework.elasticsearch.factory.ClusterEsClientFactory;
import com.smy.tfs.api.dto.SyncTicketFormItemValuesDto;
import com.smy.tfs.biz.service.ITicketFormItemValuesService;
import com.smy.tfs.common.core.controller.BaseController;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.utils.DateUtils;
import com.smy.tfs.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.Timestamp;

/**
 * 高级查询补单控制器
 *
 * @author smy
 */
@RestController
@RequestMapping("/accuratequery/test")
@Slf4j
public class AccurateQueryTestController extends BaseController {

    @Resource
    private ITicketFormItemValuesService ticketFormItemValuesService;
    @PostMapping("/syncTicketData")
    public AjaxResult syncTicketData(@RequestBody SyncTicketFormItemValuesDto syncTicketFormItemValuesDto)  {
        log.info("补单开始：开始同步ticket_data表数据到ticket_form_item_values：{}",syncTicketFormItemValuesDto);

        if (null != syncTicketFormItemValuesDto.getStartTime()
                && null != syncTicketFormItemValuesDto.getEndTime()) {
            Timestamp startTimestamp = new Timestamp(DateUtils.parseDate(syncTicketFormItemValuesDto.getStartTime()).getTime());
            Timestamp endTimestamp = new Timestamp(DateUtils.parseDate(syncTicketFormItemValuesDto.getEndTime()).getTime());
            log.info("补单开始：补固定时间范围的单：{}",syncTicketFormItemValuesDto);
            ticketFormItemValuesService.syncTimeRangeTicketData(startTimestamp, endTimestamp);
        }
        if (StringUtils.isNotEmpty(syncTicketFormItemValuesDto.getTicketDataId())) {
            ticketFormItemValuesService.syncTicketData(syncTicketFormItemValuesDto.getTicketDataId());
        }

        log.info("补单结束：同步ticket_data表数据到ticket_form_item_values");

        return AjaxResult.success();
    }

}

