package com.xin.yygh.order.config;

import com.xin.yygh.mq.MqConst;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 普通方式创建交换机、队列和绑定
//@Configuration
public class MQConfig {

    @Bean
    public Exchange getExchange() {
        return ExchangeBuilder.directExchange(MqConst.EXCHANGE_DIRECT_ORDER).durable(true).build();
    }

    @Bean
    public Queue getQueue() {
        return QueueBuilder.durable(MqConst.QUEUE_ORDER).build();
    }

    @Bean
    public Binding getBinding(@Qualifier("getExchange") Exchange exchange, @Qualifier("getQueue") Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with(MqConst.ROUTING_ORDER).noargs();
    }
}
