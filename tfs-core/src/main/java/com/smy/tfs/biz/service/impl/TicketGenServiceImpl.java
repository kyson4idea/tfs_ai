package com.smy.tfs.biz.service.impl;

import com.smy.tfs.api.dbo.TicketFormItemTemplate;
import com.smy.tfs.api.dbo.TicketTemplate;
import com.smy.tfs.api.dto.dynamic.TicketDataStdDto;
import com.smy.tfs.api.dto.dynamic.TicketFormItemStdDto;
import com.smy.tfs.api.service.ITicketFormItemTemplateService;
import com.smy.tfs.api.service.ITicketTemplateService;
import com.smy.tfs.biz.service.ITicketGenService;
import com.smy.tfs.common.constant.Constants;
import com.smy.tfs.common.core.domain.model.LoginUser;
import com.smy.tfs.generator.dto.TicketFormItemDto;
import com.smy.tfs.generator.util.TicketVelocityUtils;
import com.smy.tfs.generator.util.VelocityInitializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class TicketGenServiceImpl implements ITicketGenService {

    @Value("${dubbo.registry.address:}")
    private String zkAddress;

    @Resource
    private ITicketFormItemTemplateService ticketFormItemTemplateService;

    @Resource
    private ITicketTemplateService ticketTemplateService;


    /**
     * 预览核心代码
     */
    @Override
    public Map<String, String> previewCoreCode(TicketDataStdDto ticketDataStdDto, LoginUser loginUser) {
        Map<String, String> dataMap = new LinkedHashMap<>();
        VelocityInitializer.initVelocity();
        VelocityContext ctx = TicketVelocityUtils.prepareInitContext();
        // 设置额外的参数（用户信息、表单内容信息）
        ctx.put("userType", loginUser.getUserType());
        ctx.put("userId", loginUser.getUsername());
        ctx.put("userName", loginUser.getNickName());

        ctx.put("applyId", ticketDataStdDto.getApplyId());
        String templateId = ticketDataStdDto.getTicketTemplateId();
        TicketTemplate ticketTemplate = ticketTemplateService.lambdaQuery().isNull(TicketTemplate::getDeleteTime).eq(TicketTemplate::getId, templateId).one();
        ctx.put("appId", ticketTemplate.getAppId());
        ctx.put("ticketTemplateId", ticketDataStdDto.getTicketTemplateId());
        ctx.put("formItems", genFormItems(ticketDataStdDto));

        StringWriter sw = new StringWriter();
        String templateName = "vm/java/task.java.vm" ;
        Template tpl = Velocity.getTemplate(templateName, Constants.UTF8);
        tpl.merge(ctx, sw);
        dataMap.put(templateName, sw.toString());

        return dataMap;
    }

    /**
     * 生成代码（下载方式）
     */
    @Override
    public byte[] downloadCode(TicketDataStdDto ticketDataStdDto, LoginUser loginUser) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        generatorCode(zip, ticketDataStdDto, loginUser);
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    private List<TicketFormItemDto> genFormItems(TicketDataStdDto ticketDataStdDto) {
        List<TicketFormItemTemplate> ticketFormItemTemplateList = ticketFormItemTemplateService.lambdaQuery().eq(TicketFormItemTemplate::getTicketTemplateId, ticketDataStdDto.getTicketTemplateId()).isNull(TicketFormItemTemplate::getDeleteTime).orderByAsc(TicketFormItemTemplate::getItemOrder).list();
        List<TicketFormItemDto> formItems = new ArrayList<>();
        Map<String, String> templateMap = ticketFormItemTemplateList.stream()
                .collect(Collectors.toMap(TicketFormItemTemplate::getId, TicketFormItemTemplate::getItemLabel));
        for (TicketFormItemStdDto item : ticketDataStdDto.getFormItems()) {
            formItems.add(new TicketFormItemDto(item.getTemplateId(), item.getValue().replace("\"", "\\\""), templateMap.getOrDefault(item.getTemplateId(), "")));
        }
        return formItems;
    }

    private void generatorCode(ZipOutputStream zip, TicketDataStdDto ticketDataStdDto, LoginUser loginUser) {
        VelocityInitializer.initVelocity();
        VelocityContext ctx = TicketVelocityUtils.prepareInitContext();
        // 设置额外的参数（zk地址、用户信息、表单内容信息）
        ctx.put("zkAddress", zkAddress);

        ctx.put("userType", loginUser.getUserType());
        ctx.put("userId", loginUser.getUserId());
        ctx.put("userName", loginUser.getUsername());

        ctx.put("applyId", ticketDataStdDto.getApplyId());
        String templateId = ticketDataStdDto.getTicketTemplateId();
        TicketTemplate ticketTemplate = ticketTemplateService.lambdaQuery().isNull(TicketTemplate::getDeleteTime).eq(TicketTemplate::getId, templateId).one();
        ctx.put("appId", ticketTemplate.getAppId());
        ctx.put("ticketTemplateId", ticketDataStdDto.getTicketTemplateId());
        ctx.put("formItems", genFormItems(ticketDataStdDto));

        List<String> templates = TicketVelocityUtils.getTemplateList();
        for (String template : templates) {
            // 渲染模版
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, Constants.UTF8);
            tpl.merge(ctx, sw);
            String tplStr = sw.toString();
            try {
                zip.putNextEntry(new ZipEntry(TicketVelocityUtils.getFileName(template)));
                IOUtils.write(tplStr, zip, Constants.UTF8);
                IOUtils.closeQuietly(sw);
                zip.flush();
                zip.closeEntry();
            } catch (IOException e) {
                log.error("渲染模板失败", e);
            }
        }

        List<String> files = TicketVelocityUtils.getStaticFileList();
        for (String file : files) {
            try (InputStream resourceStream = getClass().getResourceAsStream("/" + file)) {
                if (resourceStream != null) {
                    zip.putNextEntry(new ZipEntry(TicketVelocityUtils.getFileName(file)));
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = resourceStream.read(buffer)) > 0) {
                        zip.write(buffer, 0, length);
                    }
                    zip.closeEntry();
                } else {
                    log.error("文件 {} 未找到", file);
                }
            } catch (IOException e) {
                log.error("文件写入zip失败", e);
            }
        }
    }
}
