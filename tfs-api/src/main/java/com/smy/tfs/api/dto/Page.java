package com.smy.tfs.api.dto;

import java.io.Serializable;
import java.util.List;

public class Page<T> implements Serializable {

    private static final long serialVersionUID = -8401696166017977171L;
    private List<T> dataList;

    private int total;

    private int pageSize;

    private int pageNum;

    public Page(List<T> dataList, int total, int pageSize, int pageNum) {
        this.dataList = dataList;
    }
}
