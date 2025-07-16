package com.smy.tfs.biz.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class TfSJumpUrlProperties {
    @Value("${tfs.dashboard.url}")
    private String dashboardUrl;


    @Value("${tfs.ticketDetail.url}")
    private String ticketDetailUrl;

    @Value("${tfs.getUserToken.url}")
    private String getUserTokenUrl;


}
