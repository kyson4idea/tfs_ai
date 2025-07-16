package com.smy.tfs.openapi.service;

import com.smy.tfs.api.dto.base.Response;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * <p>
 * 工单文件对外数据服务
 * </p>
 *
 * @author yss
 * @since 2024-05-28
 */
public interface ITicketFilesServiceWrapper {

    // 查询工单模版列表，不分页
    Response<List<String>> uploadFilesToFsp(List<MultipartFile> files);






}
