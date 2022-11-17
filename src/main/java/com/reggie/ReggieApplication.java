package com.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
//这样这些web servlet的这些注解才会被扫描
public class ReggieApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class, args);
        log.info("reggie 启动成功");
    }

}
