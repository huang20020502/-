package com.xin.yygh;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.xin.yygh.order.mapper")
@EnableFeignClients
public class OrderService8207 {

    public static void main(String[] args) {
        SpringApplication.run(OrderService8207.class, args);
    }
}
