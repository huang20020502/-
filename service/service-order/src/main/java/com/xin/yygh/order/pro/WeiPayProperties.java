package com.xin.yygh.order.pro;

import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@PropertySource(value = "classpath:weipay.properties")
@ConfigurationProperties(prefix = "weipay")
@Component
@Data
public class WeiPayProperties {
    private String appid;
    private String partner;
    private String partnerkey;
}
