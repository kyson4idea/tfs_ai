package com.smy.tfs;

import com.smy.scm.spring.annotation.EnableApolloConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * 启动程序
 *
 * @author ruoyi
 */
@MapperScan(basePackages = "com.smy.tfs.**.mapper")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class},scanBasePackages = {"com.smy"})
@EnableApolloConfig
@EnableWebMvc
@ImportResource("classpath*:beans/beans-dubbo-*.xml")
@org.apache.dubbo.apidocs.EnableDubboApiDocs
public class TfsApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(TfsApplication.class, args);

        System.out.println("-------------------------------------------server start finished!----------------------------------------------");

    }
}
