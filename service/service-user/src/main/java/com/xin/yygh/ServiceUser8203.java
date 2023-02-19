package com.xin.yygh;

import com.xin.yygh.user.component.WeixinProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableDiscoveryClient
@EnableSwagger2
@EnableConfigurationProperties(WeixinProperties.class)
@MapperScan("com.xin.yygh.user.mapper")
@EnableFeignClients
public class ServiceUser8203 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceUser8203.class, args);
    }
}
