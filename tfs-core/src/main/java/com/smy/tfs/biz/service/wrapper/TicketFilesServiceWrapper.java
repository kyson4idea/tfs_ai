package com.smy.tfs.biz.service.wrapper;

import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.service.ITicketFilesService;
import com.smy.tfs.openapi.service.ITicketFilesServiceWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.apidocs.annotations.ApiModule;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 工单文件对外数据服务
 * </p>
 *
 * @author yss
 * @since 2025-03-18
 */
@Slf4j
@Component("ticketFilesServiceWrapper")
@org.apache.dubbo.config.annotation.Service
@ApiModule(value = "工单文件对外数据服务", apiInterface = ITicketFilesServiceWrapper.class)
public class TicketFilesServiceWrapper implements ITicketFilesServiceWrapper {

    @Resource
    private ITicketFilesService ticketFilesService;

    @Override
    public Response<List<String>> uploadFilesToFsp(List<MultipartFile> files) {
        return ticketFilesService.uploadFilesToFsp(files);
    }
}