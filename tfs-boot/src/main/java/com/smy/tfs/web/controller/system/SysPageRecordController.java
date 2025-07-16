package com.smy.tfs.web.controller.system;

import com.smy.tfs.common.annotation.Log;
import com.smy.tfs.common.core.controller.BaseController;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.core.page.TableDataInfo;
import com.smy.tfs.common.enums.BusinessType;
import com.smy.tfs.common.utils.poi.ExcelUtil;
import com.smy.tfs.system.domain.SysPageRecord;
import com.smy.tfs.system.service.ISysPageRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 低代码页面配置修改记录Controller
 *
 * @author ruoyi
 * @date 2023-07-28
 */
@RestController
@RequestMapping("/system/page/record")
public class SysPageRecordController extends BaseController {
    @Autowired
    private ISysPageRecordService sysPageRecordService;

    /**
     * 查询低代码页面配置修改记录列表
     */
    @GetMapping("/list")
    public TableDataInfo list(SysPageRecord sysPageRecord) {
        startPage();
        List<SysPageRecord> list = sysPageRecordService.selectSysPageRecordList(sysPageRecord);
        return getDataTable(list);
    }

    /**
     * 导出低代码页面配置修改记录列表
     */
    @Log(title = "低代码页面配置修改记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysPageRecord sysPageRecord) {
        List<SysPageRecord> list = sysPageRecordService.selectSysPageRecordList(sysPageRecord);
        ExcelUtil<SysPageRecord> util = new ExcelUtil<SysPageRecord>(SysPageRecord.class);
        util.exportExcel(response, list, "低代码页面配置修改记录数据");
    }

    /**
     * 获取低代码页面配置修改记录详细信息
     */
    @GetMapping(value = "/{recordId}")
    public AjaxResult getInfo(@PathVariable("recordId") Long recordId) {
        return success(sysPageRecordService.selectSysPageRecordByRecordId(recordId));
    }

    /**
     * 新增低代码页面配置修改记录
     */
    @Log(title = "低代码页面配置修改记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysPageRecord sysPageRecord) {
        return toAjax(sysPageRecordService.insertSysPageRecord(sysPageRecord));
    }

    /**
     * 修改低代码页面配置修改记录
     */
    @Log(title = "低代码页面配置修改记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysPageRecord sysPageRecord) {
        return toAjax(sysPageRecordService.updateSysPageRecord(sysPageRecord));
    }

    /**
     * 删除低代码页面配置修改记录
     */
    @Log(title = "低代码页面配置修改记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds) {
        return toAjax(sysPageRecordService.deleteSysPageRecordByRecordIds(recordIds));
    }
}
