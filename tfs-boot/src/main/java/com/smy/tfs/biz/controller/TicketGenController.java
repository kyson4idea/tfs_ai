package com.smy.tfs.biz.controller;


import com.smy.tfs.api.dto.dynamic.TicketDataStdDto;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.biz.service.ITicketGenService;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.core.domain.model.LoginUser;
import com.smy.tfs.common.utils.SecurityUtils;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import com.smy.tfs.generator.config.TicketGenConfig;
import com.smy.tfs.generator.service.IGenTableService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import scala.runtime.StringFormat;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/ticketGen")
public class TicketGenController {

    @Resource
    private ITicketGenService ticketGenService;

    @PostMapping("/preview")
    public AjaxResult preview(@RequestBody TicketDataStdDto ticketDataStdDto) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Map<String, String> data = ticketGenService.previewCoreCode(ticketDataStdDto, loginUser);
        return AjaxResult.success(data);
    }

    @PostMapping("/api/project")
    public void download(HttpServletResponse response, @RequestBody TicketDataStdDto ticketDataStdDto) throws IOException {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        byte[] data = ticketGenService.downloadCode(ticketDataStdDto, loginUser);
        genCode(response, data);
    }

    /**
     * 生成zip文件
     */
    private void genCode(HttpServletResponse response, byte[] data) throws IOException {
        response.reset();
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", String.format("attachment; filename=%s.zip", TicketGenConfig.getProjectName()));
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        IOUtils.write(data, response.getOutputStream());
    }
}
