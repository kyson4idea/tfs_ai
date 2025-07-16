package com.smy.tfs.web.controller.monitor;

import com.smy.tfs.common.core.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/monitor/healthCheck")
public class HealthCheckController {

    @GetMapping("/ping")
    public AjaxResult ping() {
        log.info("tfs-boot心跳检查：系统服务正常");
        return AjaxResult.success("tfs-boot系统服务正常");
    }
}
