package com.smy.tfs.framework.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smy.tfs.framework.web.converter.RawDataHttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author smy
 */
@Configuration
public class RawDataHttpMessageConfig {
    @Bean
    public RawDataHttpMessageConverter encryptHttpMessageConverter(ObjectMapper objectMapper) {
        return new RawDataHttpMessageConverter(objectMapper);
    }
}
