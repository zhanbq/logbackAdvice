package com.rmxc.longdemo.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class WebApp {
    private static final Logger log = LoggerFactory.getLogger("webapp");
    public static void main(String[] args) {
        new SpringApplicationBuilder(WebApp.class).run(args);

        log.info("test1111111");
    }

    @GetMapping("/test")
    public String test(String a){

        if(a.equals("1")){
            RuntimeException e = new RuntimeException("错误");
            log.error("error :{}",e);
            return "error";
        }
        log.info("test======");
        return "hello";
    }

}
