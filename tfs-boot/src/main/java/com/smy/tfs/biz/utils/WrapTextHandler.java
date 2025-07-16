package com.smy.tfs.biz.utils;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

public class WrapTextHandler implements CellWriteHandler {
    private CellStyle wrapTextStyle;
    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        // 初始化样式（仅创建一次）
        if (wrapTextStyle == null) {
            Workbook workbook = writeSheetHolder.getSheet().getWorkbook();
            wrapTextStyle = workbook.createCellStyle();
            wrapTextStyle.setWrapText(true); // 设置自动换行
        }
        cell.setCellStyle(wrapTextStyle);
    }

}