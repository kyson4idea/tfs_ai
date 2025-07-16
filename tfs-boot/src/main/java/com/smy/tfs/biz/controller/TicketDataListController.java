package com.smy.tfs.biz.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.excel.write.handler.AbstractCellWriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.smy.tfs.api.dbo.TicketData;
import com.smy.tfs.api.dbo.TicketFormItemData;
import com.smy.tfs.api.dbo.TicketFormItemValues;
import com.smy.tfs.api.dto.*;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.dynamic.TicketBatchDto;
import com.smy.tfs.api.dto.test.DownloadTicketFormReqDto;
import com.smy.tfs.api.dto.test.TicketFormVO;
import com.smy.tfs.api.enums.ApproveDealTypeEnum;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.service.*;
import com.smy.tfs.biz.mapper.TicketDataMapper;
import com.smy.tfs.biz.utils.EasyExcelTitleHandler;
import com.smy.tfs.common.core.controller.BaseController;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.core.page.TableDataInfo;
import com.smy.tfs.common.exception.ServiceException;
import com.smy.tfs.common.utils.DateUtils;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import com.smy.tfs.quartz.task.TicketFinishTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@RestController
@ResponseBody
@Slf4j
public class TicketDataListController extends BaseController {
    @Resource
    private ITicketDataService ticketDataService;
    @Resource
    private ITicketDataQueryService ticketDataQueryService;
    @Resource
    private ITicketAppService ticketAppService;

    /**
     * 普通查询工单列表（PC，jssdk）
     * @param ticketDataListRequestDto
     * @return
     */
    @GetMapping({"/ticketDataList/queryTicketDataList","/outside/ticketDataList/queryTicketDataList"})
    public TableDataInfo queryTicketDataList(TicketDataListRequestDto ticketDataListRequestDto){
        Response<List<TicketDataListResponseDto>> response = ticketDataService.selectTicketDataList(ticketDataListRequestDto);
        TableDataInfo tableDataInfo = getDataTable(response.getData());
        return tableDataInfo;
    }

    /**
     * 分页查询工单列表:高级查询
     * @param advancedQueryDto
     * @return
     */
    @PostMapping({"/ticketDataList/advancedQueyTicketDataList","/outside/ticketDataList/advancedQueyTicketDataList"})
    public TableDataInfo advancedQueyTicketDataList(@RequestBody AdvancedQueryDto advancedQueryDto){
        Response<List<TicketFormItemValues>> response = ticketDataService.advancedSelectTicketDataList(advancedQueryDto);
        if (!response.isSuccess()) {
            Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
            return getErrorDataTable(code,response.getMsg());
        }
        List<TicketFormItemValues> ticketFormItemValuesList = response.getData();
        TableDataInfo tableDataInfo = getDataTable(ticketFormItemValuesList);

        Response<List<TicketDataListResponseDto>> ticketDataListResponseDtoListResp = ticketDataService.advancedQueryPostHandle(ticketFormItemValuesList);
        if (!ticketDataListResponseDtoListResp.isSuccess()) {
            Integer code = ObjectHelper.isNotEmpty(ticketDataListResponseDtoListResp.getCode()) ? Integer.valueOf(ticketDataListResponseDtoListResp.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
            return getErrorDataTable(code,ticketDataListResponseDtoListResp.getMsg());
        }
        tableDataInfo.setRows(ticketDataListResponseDtoListResp.getData());

        return tableDataInfo;
    }
    /**
     * 根据条件查询工单总数:高级查询
     * @param advancedQueryDto
     * @return
     */
    @PostMapping("/ticketDataList/advancedQueyTicketDataCount")
    public AjaxResult advancedQueyTicketDataCount(@RequestBody AdvancedQueryDto advancedQueryDto) {
        Response response = ticketDataService.advancedSelectTicketDataCount(advancedQueryDto);
        if (!response.isSuccess()) {
            Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
            return AjaxResult.error(code,response.getMsg());
        }
        return AjaxResult.success(response.getData());
//        return AjaxResult.success(1000);
    }

    /**
     * 查询工单数量
     * @param ticketDataListRequestDto
     * @return
     */
    @CrossOrigin(origins = "*")
    @GetMapping({"/ticketDataList/queryTicketDataCount","/outside/ticketDataList/queryTicketDataCount"})
    public AjaxResult queryTicketDataCount(TicketDataListRequestDto ticketDataListRequestDto){
        Integer count = ticketDataService.selectTicketDataCount(ticketDataListRequestDto);
        return AjaxResult.success(count);
//        return AjaxResult.success(1000);
    }

    @PostMapping({"/ticketDataList/downloadTicketDataList"})
    public void downloadTicketDataList(HttpServletResponse response, HttpServletRequest request, @RequestBody DownloadTicketDataReqDto downloadTicketDataReqDto) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("导出工单列表", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        //查询数据
        Response<List<DownloadTicketDataRespDto>> downloadTicketDataDtoListResponse = ticketDataQueryService.queryDownloadTicketDataList(downloadTicketDataReqDto);
        if (!downloadTicketDataDtoListResponse.isSuccess()) {
            throw new ServiceException(downloadTicketDataDtoListResponse.getMsg());
        }
        List<DownloadTicketDataRespDto> downloadTicketDataRespDtoList = downloadTicketDataDtoListResponse.getData();
        if (CollectionUtils.isEmpty(downloadTicketDataRespDtoList)) {
            throw new ServiceException("查询无数据，无需下载");
        }
        List<String>  appIdList = downloadTicketDataRespDtoList.stream()
                .map(it -> it.getAppId())
                .distinct()
                .collect(Collectors.toList());
        List<String> columnFiledsInclude = new ArrayList<>();
        //excel表头转换Properties
        List<Properties> headProperties = new ArrayList<>();
        List<TableColumnDto>  tableColumnsList = downloadTicketDataReqDto.getTableColumnsList();
        if(CollectionUtils.isEmpty(tableColumnsList)){
            throw new ServiceException("未勾选导出列");
        }
        columnFiledsInclude.addAll(
                tableColumnsList.stream()
                .filter(it -> StringUtils.isNotBlank(it.getFieldCode()))
                .map(it -> it.getFieldCode())
                .collect(Collectors.toList())
        );
        //如果只有一个业务，则需要移除非配置的扩展字段列
        if (1 == appIdList.size()) {
            String appId = appIdList.get(0);
            Response<ExtendFieldsMappingDto> efmResponse = ticketAppService.getExtendFieldsMapping(appId);
            if (!efmResponse.isSuccess()) {
                logger.error("获取业务扩展字段映射关系失败");
            }
            if (null != efmResponse.getData() && CollectionUtils.isNotEmpty(efmResponse.getData().getExtendFields())) {
                List<BusiTicketDataFieldsMappingDto> extendFieldsList = efmResponse.getData().getExtendFields();
                for (BusiTicketDataFieldsMappingDto extendField : extendFieldsList) {
                    if (StringUtils.isNotBlank(extendField.getFieldName())) {
                        Properties headProperty = new Properties();
                        headProperty.setProperty(extendField.getFieldCode(), extendField.getFieldName());
                        headProperties.add(headProperty);
                    }
                }
            }
        }
        EasyExcelTitleHandler easyExcelTitleHandler = new EasyExcelTitleHandler(headProperties);
        ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), DownloadTicketDataRespDto.class)
                .includeColumnFieldNames(columnFiledsInclude)
                .registerWriteHandler(easyExcelTitleHandler)
                .build();
        WriteSheet writeSheet = EasyExcel.writerSheet("导出工单").build();
        excelWriter.write(downloadTicketDataRespDtoList, writeSheet);
        excelWriter.finish();

    }

    @Resource
    private ITicketDataActService iTicketDataActService;

    /**
     * 批量关单
     * @param batchFinishTicketsDto
     * @return
     */
    @PostMapping({"/ticketDataList/finishTicket"})
    public AjaxResult finishTicket(@RequestBody BatchFinishTicketsDto batchFinishTicketsDto){
        log.info("开始批量关单");
        String userType = "ldap";
        String userId = "tfs_system";
        String userName = "tfs_system";
        if (StringUtils.isEmpty(batchFinishTicketsDto.getDealOpinion())) {
            batchFinishTicketsDto.setDealOpinion("系统关单");
        }
        Response response = iTicketDataActService.batchFinishTickets(batchFinishTicketsDto, userType, userId, userName);
        if (!response.isSuccess()) {
            Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
            return AjaxResult.error(code,response.getMsg());
        }
        log.info("结束批量关单");
        return AjaxResult.success(response.getData());
    }

    @Value("${ticket.finish.templateId}")
    private String templateId;

    @Resource
    private TicketDataMapper ticketDataMapper;

    @Resource
    private ITicketFormItemDataService ticketFormItemDataService;

    @PostMapping({"/ticketDataList/downloadTicketFormDataList"})
    public void downloadTicketFormDataList(HttpServletResponse response, HttpServletRequest request, @RequestBody DownloadTicketFormReqDto downloadTicketFormReqDto) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("导出工单表单数据"+DateUtils.dateTimeNow(), "UTF-8");
        response.setHeader( "Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), TicketFormVO.class).build();
        WriteSheet writeSheet = EasyExcel.writerSheet("工单表单数据").build();
        log.info("开始导数据...");
        //查询数据
        String startTime = downloadTicketFormReqDto.getStartTime();
        String endTime = downloadTicketFormReqDto.getEndTime();
        if (StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime)) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime twoMonthsAgo = now.minusMonths(2);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            startTime = twoMonthsAgo.format(formatter);
            endTime = now.format(formatter);
        }
        String userName = downloadTicketFormReqDto.getUserName();
        LambdaQueryWrapper<TicketData> tDLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(templateId)) {
            tDLambdaQueryWrapper.eq(TicketData::getTemplateId, templateId);
        }
        tDLambdaQueryWrapper.ge(TicketData::getCreateTime, startTime);
        tDLambdaQueryWrapper.le(TicketData::getCreateTime, endTime);
        if (StringUtils.isNotBlank(userName)) {
            tDLambdaQueryWrapper.and(LambdaQueryWrapper -> {
                LambdaQueryWrapper.like(TicketData::getCurrentDealUsers, userName);
                LambdaQueryWrapper.or();
                LambdaQueryWrapper.like(TicketData::getCurrentDoneUsers, userName);
            });
        }
        tDLambdaQueryWrapper.orderByDesc(TicketData::getCreateTime).orderByDesc(TicketData::getId);
        int pageNum = 1;
        int pageSize = 100;
        List<TicketData> ticketDataList ;
        do {
            PageInfo<TicketData> ticketDataPageInfo = PageHelper.<TicketData>startPage(pageNum, pageSize)
                    .doSelectPageInfo(() -> ticketDataMapper.selectList(tDLambdaQueryWrapper));
            if (ObjectHelper.isEmpty(ticketDataPageInfo) || CollectionUtils.isEmpty(ticketDataPageInfo.getList())) {
                log.info("查询工单数据为空：{},pageNum:{}", ticketDataPageInfo, pageNum);
                break;
            }
            ticketDataList = ticketDataPageInfo.getList();
            List<TicketFormVO> ticketFormVOList = new ArrayList<>();
            for (TicketData ticketData : ticketDataList) {
                // 获取表单数据
                String ticketDataId = ticketData.getId();
                List<TicketFormItemData> ticketFormItemDataList = ticketFormItemDataService.lambdaQuery()
                        .eq(TicketFormItemData::getTicketDataId, ticketDataId)
                        .list();
                if (CollectionUtils.isEmpty(ticketFormItemDataList)) {
                    log.info("查询表单数据为空,ticketDataId:{}", ticketDataId);
                }
                TicketFormVO ticketFormVO = new TicketFormVO(ticketDataId, ticketFormItemDataList);
                ticketFormVOList.add(ticketFormVO);
            }
            excelWriter.write(ticketFormVOList, writeSheet);
            pageNum = pageNum + 1 ;
        } while (CollectionUtils.isNotEmpty(ticketDataList));
        excelWriter.finish();
        log.info("结束导数据...");
    }

    @Resource
    TicketFinishTask ticketFinishTask;
    @PostMapping({"/ticketDataList/finishTicketSync"})
    public void finishTicketSync(){
        log.info("测试任务：开始关单...");

        ticketFinishTask.finishTicketSync();

        log.info("测试任务：结束关单...");
    }
}
