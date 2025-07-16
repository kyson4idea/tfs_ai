package com.smy.tfs.biz.service.impl;

import com.smy.fsp.client.FileUtil;
import com.smy.tfs.api.constants.TfsBaseConstant;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.service.ITicketFilesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 工单文件服务实现类
 * </p>
 *
 * @author yss
 * @since 2025-05-22
 */
@Slf4j
@Component("ticketFilesServiceImpl")
public class TicketFilesServiceImpl implements ITicketFilesService {

    @Value("${app.env}")
    private String env;

    public Response<List<String>> uploadFilesToFsp(List<MultipartFile> files) {
        List<String> uploadUrlList = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                String fspFileUrl = FileUtil.uploadBuilder()
                        .fileName(file.getOriginalFilename())
                        .fileByte(file.getBytes())
                        .sceneType(TfsBaseConstant.FSP_UPLOAD_SCENE_TYPE)
                        .upload();
                // 返回https地址
                if ("prd".equals(env) || "pre".equals(env)) {
                    fspFileUrl = fspFileUrl.replaceAll("http://", "https://");
                }
                uploadUrlList.add(fspFileUrl);
            }
        } catch (Exception e) {
            log.error("文件上传异常:{}", e);
            return Response.error(BizResponseEnums.SYSTEM_ERROR, "文件上传异常：" + e.getMessage());
        }
        return  Response.success(uploadUrlList);
    }
}