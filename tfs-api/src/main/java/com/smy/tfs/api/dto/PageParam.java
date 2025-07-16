package com.smy.tfs.api.dto;

import lombok.Data;
import org.apache.dubbo.apidocs.annotations.RequestParam;

import java.io.Serializable;

@Data
public class PageParam<T> implements Serializable {

    private static final long serialVersionUID = 7882351587194546996L;
    @RequestParam(value = "页面参数",example="{}",description = "页面参数")
    private T param;
    @RequestParam(value = "总条数",example="90",description = "总条数")
    private int total;
    @RequestParam(value = "总页数",example="9",description = "总页数")
    private int pageSize;
    @RequestParam(value = "第几页",example="1",description = "第几页")
    private int pageNum;

    //构造函数
    public PageParam(T parma, int total, int pageSize, int pageNum) {
        this.param = parma;
        this.total = total;
        this.pageSize = pageSize;
        this.pageNum = pageNum;
    }
}
