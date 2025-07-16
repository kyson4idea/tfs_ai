package com.smy.tfs.web.core.config;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.smy.tfs.api.dbo.TfsBaseEntity;

import java.util.Collections;

public class MyFastAutoGenerator {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://192.168.27.155:3306/tfs?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8", "dm_tfs", "tfs@sit")
                .globalConfig(builder -> {
                    builder.author("yss") // 设置作者
                            //.enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("D://tfs-code"); // 指定输出目录
                }).packageConfig(builder -> {
                    builder.parent("com.smy.tfs.api") // 设置父包名
                            .moduleName("system") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "D://tfs-code")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("ticket_form_item_values,ticket_form_item_id_col_mapping")
                            .entityBuilder()
                            .superClass(TfsBaseEntity.class)
                            .enableLombok()

                    ; // 设置需要生成的表名;
                    // 设置过滤表前缀
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}
