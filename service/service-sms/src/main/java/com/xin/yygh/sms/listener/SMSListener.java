package com.xin.yygh.sms.listener;

import com.rabbitmq.client.Channel;
import com.xin.yygh.hosp.vo.msm.MsmVo;
import com.xin.yygh.mq.MqConst;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class SMSListener {

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(name = MqConst.QUEUE_MSM_ITEM),
                    exchange = @Exchange(name = MqConst.EXCHANGE_DIRECT_MSM),
                    key = MqConst.ROUTING_MSM_ITEM
            )
    })
    public void consumer(MsmVo msmVo, Message message, Channel channel) {
        this.sendMessage(msmVo);
    }

    private void sendMessage(MsmVo msmVo) {
        String phone = msmVo.getPhone();
        System.out.println("已经给" + phone + "发送了通知短信");
    }
}
