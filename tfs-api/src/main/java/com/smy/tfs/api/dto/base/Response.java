package com.smy.tfs.api.dto.base;

import com.smy.tfs.api.enums.BizResponseEnums;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Objects;

@Data
@Slf4j
public class Response<T> implements Serializable {


    private static final long serialVersionUID = -1074350137371773215L;
    private T data;
    // 0：成功  99：接口幂等值
    private String code;
    private String msg;

    public Response() {
    }

    /**
     * 初始化一个新创建的 Response 对象,包含T
     *
     * @param data
     * @param bizResponseEnums
     * @param msg
     */
    public Response(T data, BizResponseEnums bizResponseEnums, String msg) {
        this.data = data;
        this.code = bizResponseEnums.getCode();
        this.msg = msg;
    }

    /**
     * 成功响应
     *
     * @return
     */
    public static <T> Response success() {
        return new Response(null, BizResponseEnums.SUCCESS, BizResponseEnums.SUCCESS.getMsg());
    }

    public static <T> Response success(T data) {
        return new Response(data, BizResponseEnums.SUCCESS, BizResponseEnums.SUCCESS.getMsg());
    }

    /**
     * 错误响应
     *
     * @param bizResponseEnums
     * @param message
     * @return
     */
    public static Response error(BizResponseEnums bizResponseEnums, String message) {
        log.error(message);
        return new Response(null, bizResponseEnums, message);
    }

    public boolean isSuccess() {
        return Objects.equals(code, BizResponseEnums.IDEMPOTENT.getCode()) || Objects.equals(code, BizResponseEnums.SUCCESS.getCode());
    }

    public BizResponseEnums getEnum() {
        return BizResponseEnums.getEnumByCode(code);
    }
}