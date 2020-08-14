package com.rmxc.logconsumer.web.controller.monitor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Administrator
 */
@Slf4j
@RestController
@RequestMapping("/serv/")
public class ServerHealthController {

    /**
     * 用于代替spring actuator的健康检查简化版,
     * 只作为服务接口可用行的健康检查
     * @return
     */
    @GetMapping("ping")
    public String ping(){
        return "pong";
    }
}
