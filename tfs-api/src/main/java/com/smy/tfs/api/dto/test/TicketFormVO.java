package com.smy.tfs.api.dto.test;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.fastjson.JSON;
import com.smy.tfs.api.dbo.TicketFormItemData;
import com.smy.tfs.common.utils.StringUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
public class TicketFormVO implements Serializable {
    private static final long serialVersionUID = -5708061877311325328L;

    @ExcelProperty(value = "工单编号", index = 0)
    @ColumnWidth(25)
    private String ticketDataId ;

    @ExcelProperty(value = "申请人", index = 1)
    @ColumnWidth(25)
    private String c1 ;

    @ExcelProperty(value = "申请人部门", index = 2)
    @ColumnWidth(25)
    private String c2 ;

    @ExcelProperty(value = "订单号", index = 3)
    @ColumnWidth(30)
    private String c3 ;

    @ExcelProperty(value = "客户号", index = 4)
    @ColumnWidth(30)
    private String c4 ;

    @ExcelProperty(value = "总期数", index = 5)
    @ColumnWidth(10)
    private String c5 ;

    @ExcelProperty(value = "期数", index = 6)
    @ColumnWidth(10)
    private String c6 ;

    @ExcelProperty(value = "调帐说明", index = 7)
    @ColumnWidth(30)
    private String c7 ;

    @ExcelProperty(value = "应还手续费原为", index = 8)
    @ColumnWidth(10)
    private String c8 ;

    @ExcelProperty(value = "应还手续费变更为", index = 9)
    @ColumnWidth(10)
    private String c9 ;

    @ExcelProperty(value = "应还逾期利息原为", index = 10)
    @ColumnWidth(10)
    private String c10 ;

    @ExcelProperty(value = "应还逾期利息变更为", index = 11)
    @ColumnWidth(10)
    private String c11 ;

    @ExcelProperty(value = "应还滞纳金原为", index = 12)
    @ColumnWidth(10)
    private String c12 ;

    @ExcelProperty(value = "应还滞纳金变更为", index = 13)
    @ColumnWidth(10)
    private String c13 ;

    @ExcelProperty(value = "调整金额", index = 14)
    @ColumnWidth(10)
    private String c14 ;

    @ExcelProperty(value = "调整后年化比例", index = 15)
    @ColumnWidth(10)
    private String c15 ;

    @ExcelProperty(value = "说明", index = 16)
    @ColumnWidth(30)
    private String c16 ;

    @ExcelProperty(value = "申请内容", index = 17)
    @ColumnWidth(30)
    private String c17 ;

    @ExcelProperty(value = "业务类型名称", index = 18)
    @ColumnWidth(10)
    private String c18 ;

    @ExcelProperty(value = "系统码", index = 19)
    @ColumnWidth(10)
    private String c19 ;

    @ExcelProperty(value = "业务类型", index = 20)
    @ColumnWidth(10)
    private String c20 ;

    @ExcelProperty(value = "标题", index = 21)
    @ColumnWidth(10)
    private String c21 ;

    @ExcelProperty(value = "业务KEY", index = 22)
    @ColumnWidth(10)
    private String c22 ;

    @ExcelProperty(value = "应还手续费", index = 23)
    @ColumnWidth(10)
    private String c23 ;

    public TicketFormVO (String ticketDataId, List<TicketFormItemData> ticketFormItemDataList) {
        this.ticketDataId = ticketDataId;
        if (CollectionUtils.isEmpty(ticketFormItemDataList)){
            return;
        }
        for (TicketFormItemData ticketFormItemData : ticketFormItemDataList) {
            setVals(ticketFormItemData.getItemLabel(), ticketFormItemData.getItemValue(), this);
        }
    }

    public void setVals(String label, String val, TicketFormVO ticketFormVO) {
        // 获取 TicketFormVO 类的所有字段
        Field[] fields = TicketFormVO.class.getDeclaredFields();
        // 遍历字段
        for (Field field : fields) {
            // 获取字段上的 ExcelProperty 注解
            ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
            if (null != excelProperty) {
                // 获取 ExcelProperty 注解的 value 值
                String[] values = excelProperty.value();
                if (values.length > 0) {
                    String value = values[0];
                    System.out.println("Field: " + field.getName() + ", ExcelProperty Value: " + value);
                    // 这里可以根据需要进行赋值操作
                    // 例如，将 value 赋值给字段
                    try {
                        if (StringUtils.isNotEmpty(value) && value.equals(label)) {
                            field.setAccessible(true); // 设置可访问私有字段
                            field.set(ticketFormVO, val); // 将 value 赋值给字段
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        List<TicketFormItemData> ticketFormItemDataList = new ArrayList<>();
        TicketFormItemData ticketFormItemData1 = new TicketFormItemData();
        ticketFormItemData1.setItemLabel("应还手续费");
        ticketFormItemData1.setItemValue("0.01");
        ticketFormItemDataList.add(ticketFormItemData1);
        TicketFormItemData ticketFormItemData2 = new TicketFormItemData();
        ticketFormItemData2.setItemLabel("说明");
        ticketFormItemData2.setItemValue("说明1");
        ticketFormItemDataList.add(ticketFormItemData2);
        TicketFormVO ticketFormVO = new TicketFormVO("", ticketFormItemDataList);
        System.out.println(JSON.toJSONString(ticketFormVO));

    }

}
