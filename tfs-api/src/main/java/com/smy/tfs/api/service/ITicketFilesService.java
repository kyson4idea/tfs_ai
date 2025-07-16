package com.smy.tfs.api.service;

import com.smy.tfs.api.dto.base.Response;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 工单文件服务类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
public interface ITicketFilesService {
    Response<List<String>> uploadFilesToFsp(List<MultipartFile> files);

}
