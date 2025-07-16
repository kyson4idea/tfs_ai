package com.smy.tfs.biz.utils;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PropertyPlaceholderHelper;

import java.util.List;
import java.util.Properties;

public class EasyExcelTitleHandler implements CellWriteHandler {
    private List<Properties> headProperties;

    PropertyPlaceholderHelper placeholderHelper = new PropertyPlaceholderHelper("${", "}");

    public EasyExcelTitleHandler(List<Properties> headProperties) {
        this.headProperties = headProperties;
    }

    @Override
    public void beforeCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Head head, Integer columnIndex, Integer relativeRowIndex, Boolean isHead) {
        if (CollectionUtils.isEmpty(headProperties)) {
            return;
        }
        // 动态设置表头字段
        if (!ObjectUtils.isEmpty(head)) {
            List<String> headNameList = head.getHeadNameList();
            if (CollectionUtils.isNotEmpty(headNameList)) {
                for (int i = 0; i < headNameList.size(); i++) {
                    for (Properties properties : headProperties) {
                        //表头中如果有${}设置的单元格，则可以自定义赋值。
                        headNameList.set(i, placeholderHelper.replacePlaceholders(headNameList.get(i), properties));
                    }
                }
            }
        }
    }

}
