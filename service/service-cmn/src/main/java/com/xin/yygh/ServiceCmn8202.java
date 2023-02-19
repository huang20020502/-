package com.xin.yygh;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@Slf4j
@EnableDiscoveryClient
@MapperScan("com.xin.yygh.cmn.mapper")
public class ServiceCmn8202 {

    public static void main(String[] args) {

        SpringApplication.run(ServiceCmn8202.class,args);
        log.info("*********************** service8202, 启动成功 **************************");

    }
}
