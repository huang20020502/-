package com.xin.yygh.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.nio.file.Path;
import java.util.function.Predicate;

@Configuration
@EnableSwagger2 // 开启swagger
public class SwaggerConfig {

    // 一个docket对象就是一个组
    @Bean
    public Docket getDefaultDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(getDefaultApiInfo())
                .select()
                .paths(PathSelectors.none())
                .build();
    }


    public ApiInfo getDefaultApiInfo() {
        return new ApiInfoBuilder()
                .title("第三方api")
                .description("与医院对接的接口")
                .version("1.0v")

                .build();
    }

    @Bean
    public Docket getAdminDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(getAdminApiInfo())
                .groupName("admin")
                .select()
                .paths(PathSelectors.ant("/admin/**"))
                .build();
    }


    public ApiInfo getAdminApiInfo() {
        return new ApiInfoBuilder()
                .title("尚医通 - 后台管理员aip")
                .description("尚医通的后台管理员接口")
                .version("1.0v")
                .build();
    }

}
