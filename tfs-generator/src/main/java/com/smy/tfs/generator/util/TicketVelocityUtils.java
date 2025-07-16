package com.smy.tfs.generator.util;

import com.smy.tfs.common.utils.DateUtils;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.generator.config.TicketGenConfig;
import org.apache.velocity.VelocityContext;

import java.util.ArrayList;
import java.util.List;

public class TicketVelocityUtils {
    /**
     * 项目空间路径
     */
    private static final String PROJECT_PATH = "src/main/java";

    /**
     * 项目资源文件路径
     */
    private static final String PROJECT_RESOURCES_PATH = "src/main/resources";

    public static VelocityContext prepareInitContext() {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("author", TicketGenConfig.getAuthor());
        velocityContext.put("packageName", TicketGenConfig.getPackageName());
        velocityContext.put("appName", TicketGenConfig.getAppName());
        velocityContext.put("datetime", DateUtils.getDate());
        velocityContext.put("projectName", TicketGenConfig.getProjectName());
        velocityContext.put("taskFileName", TicketGenConfig.getTaskFileName());
        velocityContext.put("dubboConfigPath", TicketGenConfig.getDubboConfigPath());
        return velocityContext;
    }

    /**
     * 获取模板信息
     *
     * @return 模板列表
     */
    public static List<String> getTemplateList() {
        List<String> templates = new ArrayList<>();
        templates.add("vm/java/boot.java.vm");
        templates.add("vm/java/task.java.vm");
        templates.add("vm/md/README.md.vm");
        templates.add("vm/yml/application.yml.vm");
        return templates;
    }

    public static List<String> getStaticFileList() {
        List<String> fileNames = new ArrayList<>();
        fileNames.add("client-static/beans-dubbo-common.xml");
        fileNames.add("client-static/beans-dubbo-client.xml");
        fileNames.add("client-static/log4j2.xml");
        fileNames.add("client-static/pom.xml");
        return fileNames;
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template) {
        // 文件名称
        String fileName = "";
        // 包路径
        String packageName = TicketGenConfig.getPackageName();

        // 应用名称
        String appName = TicketGenConfig.getAppName();

        // 工单任务文件名称
        String taskFileName = TicketGenConfig.getTaskFileName();

        String javaPath = PROJECT_PATH + "/" + StringUtils.replace(packageName, ".", "/");

        if (template.contains("boot.java.vm")) {
            fileName = StringUtils.format("{}/{}Application.java", javaPath, appName);
        } else if (template.contains("task.java.vm")) {
            fileName = StringUtils.format("{}/{}.java", javaPath, taskFileName);
        } else if (template.contains("README.md.vm")) {
            fileName = "README.md";
        } else if (template.contains("application.yml.vm")) {
            fileName = StringUtils.format("{}/{}", PROJECT_RESOURCES_PATH, "application.yml");
        } else if (template.contains("beans-dubbo-common.xml")) {
            fileName = StringUtils.format("{}/beans/{}", PROJECT_RESOURCES_PATH, "beans-dubbo-common.xml");
        } else if (template.contains("beans-dubbo-client.xml")) {
            fileName = StringUtils.format("{}/beans/{}", PROJECT_RESOURCES_PATH, "beans-dubbo-client.xml");
        } else if (template.contains("log4j2.xml")) {
            fileName = StringUtils.format("{}/{}", PROJECT_RESOURCES_PATH, "log4j2.xml");
        } else if (template.contains("pom.xml")) {
            fileName = "pom.xml";
        }

        return fileName;
    }
}
