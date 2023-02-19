package com.xin.yygh.mq;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // rabbitMQ消息转换器
    // 当提供者向rabbitMQ发送消息时，会将pojo对象转换成json发送
    // 当消费者消息rabbitMQ中的消息时，将json对象转换成pojo对象
    @Bean
    public MessageConverter getMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
