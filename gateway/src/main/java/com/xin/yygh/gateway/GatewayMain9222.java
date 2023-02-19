package com.xin.yygh.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayMain9222 {

    public static void main(String[] args) {
        SpringApplication.run(GatewayMain9222.class, args);
        System.out.println("************ 9222, 启动成功 ****************");
    }
}
