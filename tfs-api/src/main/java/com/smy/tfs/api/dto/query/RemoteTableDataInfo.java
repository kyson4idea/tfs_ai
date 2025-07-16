package com.smy.tfs.api.dto.query;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 表格分页数据对象
 *
 * @author ruoyi
 */
public class RemoteTableDataInfo<T> implements Serializable {
    private static final long serialVersionUID = -460953490837376359L;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 列表数据
     */
    private List<T> rows;

    /**
     * 消息状态码
     */
    private int code;

    /**
     * 消息内容
     */
    private String msg;

    /**
     * 表格数据对象
     */
    public RemoteTableDataInfo() {
    }

    /**
     * 分页
     *
     * @param list  列表数据
     * @param total 总记录数
     */
    public RemoteTableDataInfo(List<T> list, int total) {
        this.rows = list;
        this.total = total;
    }

    public static RemoteTableDataInfo fail(int code, String msg) {
        RemoteTableDataInfo ret = new RemoteTableDataInfo();
        ret.setCode(code);
        ret.setMsg(msg);
        return ret;
    }

    public static RemoteTableDataInfo success(List<Map<String, Object>> rows, int total) {
        RemoteTableDataInfo ret = new RemoteTableDataInfo();
        ret.setCode(200);
        ret.setMsg("操作成功");
        ret.setRows(rows);
        ret.setTotal(total);
        return ret;
    }

    public Boolean isSuccess() {
        if (Objects.nonNull(this.getCode()) &&  200 == this.getCode()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }




}
