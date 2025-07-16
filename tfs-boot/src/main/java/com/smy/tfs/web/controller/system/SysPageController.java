package com.smy.tfs.web.controller.system;

import com.smy.tfs.common.annotation.Anonymous;
import com.smy.tfs.common.annotation.Log;
import com.smy.tfs.common.core.controller.BaseController;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.core.page.TableDataInfo;
import com.smy.tfs.common.enums.BusinessType;
import com.smy.tfs.common.exception.ServiceException;
import com.smy.tfs.system.domain.SysPage;
import com.smy.tfs.system.domain.SysPageRecord;
import com.smy.tfs.system.service.ISysPageRecordService;
import com.smy.tfs.system.service.ISysPageService;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 页面配置 信息操作处理
 *
 * @author smy
 */
@RestController
@RequestMapping("/system/page")
public class SysPageController extends BaseController {
    @Autowired
    private ISysPageService pageService;
    @Autowired
    private ISysPageRecordService pageRecordService;

    /**
     * 获取页面配置列表
     */
    @GetMapping("/list")
    public TableDataInfo list(SysPage page) {
        startPage();
        List<SysPage> list = pageService.selectPageList(page);
        return getDataTable(list);
    }

    /**
     * 获取页面配置列表（增加匿名注解，允许非认证post访问）
     */
    @Anonymous
    @PostMapping("/list-all")
    public TableDataInfo listAll() {
        List<SysPage> list = pageService.selectAllList();
        return getDataTable(list);
    }

    /**
     * 根据配置标识查询页面配置
     */
    @GetMapping(value = "/pageKey/{pageKey}")
    public AjaxResult getByPageKey(@PathVariable String pageKey) {
        return success((Object) pageService.selectPageByKey(pageKey));
    }

    /**
     * 根据配置ID查询页面配置
     */
    @GetMapping(value = "/{pageId}")
    public AjaxResult getByPageId(@PathVariable Long pageId) {
        return success((Object) pageService.selectPageById(pageId));
    }

    /**
     * 新增页面配置
     */
    @Log(title = "低代码配置管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysPage page) {
        String pageParamJson = pageService.selectPageByKey(page.getPageKey());
        if (pageParamJson != StringUtil.EMPTY_STRING) {
            throw new ServiceException("页面标识已存在");
        }

        page.setCreateBy(getUsername());
        int rowNum = pageService.insertPage(page);
        if (rowNum > 0) {
            // 插入一条新增类型的修改记录
            SysPageRecord record = new SysPageRecord(page.getPageKey(), page.getParamJson(), BusinessType.INSERT.ordinal(), 0);
            record.setUpdateBy(getUsername());
            pageRecordService.insertSysPageRecord(record);

            return success();
        }

        return error();
    }

    /**
     * 修改页面配置
     */
    @Log(title = "低代码配置管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysPage page) {
        int rowNum = pageService.updatePage(page);
        if (rowNum > 0) {
            // 插入一条修改类型的修改记录，先查询是否有修改记录，并在上一次修改的那条数据版本号上加1
            SysPageRecord record = new SysPageRecord();
            record.setPageKey(page.getPageKey());
            List<SysPageRecord> recordList = pageRecordService.selectSysPageRecordList(record);

            int version = 0;
            if (recordList.size() > 0) {
                record = recordList.get(0);
                version = record.getVersion();

                // 上一次记录的配置信息与本次修改的配置一样，则不做配置记录的新增
                if (record.getParamJson().equals(page.getParamJson())) {
                    return success();
                }
            }
            record.setParamJson(page.getParamJson());
            record.setUpdateType(BusinessType.UPDATE.ordinal());
            record.setVersion(version + 1);
            record.setUpdateBy(getUsername());
            pageRecordService.insertSysPageRecord(record);

            return success();
        }

        return error();
    }

    /**
     * 根据配置ID删除页面配置
     */
    @Log(title = "低代码配置管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{pageIds}")
    public AjaxResult remove(@PathVariable Long[] pageIds) {
        for (Long pageId : pageIds) {
            if (pageId == 1 || pageId == 2) {
                throw new ServiceException("不允许删除系统配置");
            }
        }

        // 获取对应的 pageKey，用于删除子表数据
        Map<Long, String> pageKeyMap = new HashMap<Long, String>();
        for (Long pageId : pageIds) {
            SysPage page = pageService.selectPageById(pageId);
            pageKeyMap.put(pageId, page.getPageKey());
        }

        // 删除页面配置主表
        int rowNum = pageService.deletePageByIds(pageIds);
        if (rowNum > 0) {
            for (Long pageId : pageIds) {
                // 删除页面配置修改记录子表
                pageRecordService.deleteSysPageRecordByPageKey(pageKeyMap.get(pageId));
            }
        }

        return success();
    }
}
