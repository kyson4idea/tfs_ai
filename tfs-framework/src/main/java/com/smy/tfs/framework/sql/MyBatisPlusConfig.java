//package com.smy.tfs.framework.sql;
//
//import com.baomidou.mybatisplus.core.MybatisConfiguration;
//import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
//import com.smy.tfs.framework.interceptor.impl.DeleteTimeInterceptor;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.sql.DataSource;
//
///**
// * Mybatis支持*匹配扫描包
// *
// * @author ruoyi
// */
//@Configuration
//public class MyBatisPlusConfig {
//
//    @Bean
//    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception{
//        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
//        factoryBean.setDataSource(dataSource);
//
//        MybatisConfiguration configuration = new MybatisConfiguration();
//        // 添加自定义拦截器
//        configuration.addInterceptor(new DeleteTimeInterceptor());
//        factoryBean.setConfiguration(configuration);
//
//        return factoryBean.getObject();
//    }
//
//}