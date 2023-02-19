package com.xin.yygh;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@MapperScan("com.xin.yygh.hosp.mapper")
@Slf4j
public class HospMain8201 {

    public static void main(String[] args) {
        SpringApplication.run(HospMain8201.class,args);
        log.info("**************** HospMain8201 启动成功 ****************");
    }

}
