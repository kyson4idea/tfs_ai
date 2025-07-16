package com.smy.tfs.framework.config;

import com.smy.framework.core.config.PropertyHotUpdateConfigurer;
import com.smy.framework.core.support.SpringContextHolder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class PropertyConfig  implements EnvironmentAware {

    private Environment environment;

    @Bean
    public BeanFactoryPostProcessor beanFactoryPostProcessor(){
        PropertyHotUpdateConfigurer configurer = new PropertyHotUpdateConfigurer();
        configurer.setAppId(environment.getProperty("app.id"));
        return beanFactory -> beanFactory.registerSingleton("propertyHotUpdateConfigurer",configurer);
    }

    @Bean
    public SpringContextHolder springContextHolder(){
        return new SpringContextHolder();
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}