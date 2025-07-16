package com.smy.tfs.generator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ticket-gen")
@PropertySource(value = {"classpath:generator.yml"})
public class TicketGenConfig {
    /**
     * 作者
     */
    private static String author;

    /**
     * 生成包路径
     */
    private static String packageName;


    /**
     * 应用名称
     */
    private static String appName;

    /**
     * 项目名称
     */
    private static String projectName;

    /**
     * 工单任务文件名称
     */
    private static String taskFileName;

    /**
     * dubbo配置文件路径
     */
    private static String dubboConfigPath;


    public static String getAuthor() {
        return author;
    }

    @Value("${author}")
    public void setAuthor(String author) {
        TicketGenConfig.author = author;
    }

    public static String getPackageName() {
        return packageName;
    }

    @Value("${packageName}")
    public void setPackageName(String packageName) {
        TicketGenConfig.packageName = packageName;
    }

    public static String getAppName() {
        return appName;
    }

    @Value("${apiAppName}")
    public void setAppName(String appName) {
        TicketGenConfig.appName = appName;
    }

    public static String getProjectName() {
        return projectName;
    }

    @Value("${projectName}")
    public void setProjectName(String projectName) {
        TicketGenConfig.projectName = projectName;
    }

    public static String getTaskFileName() {
        return taskFileName;
    }

    @Value("${taskFileName}")
    public void setTaskFileName(String taskFileName) {
        TicketGenConfig.taskFileName = taskFileName;
    }

    public static String getDubboConfigPath() {
        return dubboConfigPath;
    }

    @Value("${dubboConfigPath}")
    public void setDubboConfigPath(String dubboConfigPath) {
        TicketGenConfig.dubboConfigPath = dubboConfigPath;
    }
}
