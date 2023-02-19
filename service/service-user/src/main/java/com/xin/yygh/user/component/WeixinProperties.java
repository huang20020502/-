package com.xin.yygh.user.component;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "weixin")
@Data
public class WeixinProperties {

    private String appid;
    private String redirectUri;
    private String scope;
    private String secret;
}
