package com.xin.yygh.oss.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "oss.file")
@PropertySource(value = {"classpath:oss.properties"})
@Component
@Data
public class OSSProperties {

    private String endpoint;
    private String keyid;
    private String keysecret;
    private String bucketname;
}
