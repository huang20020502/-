package com.xin.yygh.hosp.listener;

import com.rabbitmq.client.Channel;
import com.xin.yygh.hosp.model.hosp.Schedule;
import com.xin.yygh.hosp.service.ScheduleService;
import com.xin.yygh.hosp.vo.hosp.ScheduleOrderVo;
import com.xin.yygh.hosp.vo.msm.MsmVo;
import com.xin.yygh.hosp.vo.order.OrderMqVo;
import com.xin.yygh.mq.MqConst;
import com.xin.yygh.mq.RabbitMQService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// 使用注解来创建 队列，交换机
@Component
public class ReduceAvailableNumberListener {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private RabbitMQService rabbitMQService;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(name = MqConst.QUEUE_ORDER), // 队列
                    exchange = @Exchange(name = MqConst.EXCHANGE_DIRECT_ORDER), // 交换机
                    key = MqConst.ROUTING_ORDER// bindingKey
            )
    })
    public void consumer(OrderMqVo orderMqVo, Message message, Channel channel) {
        // 获取返送过来的信息
        String scheduleId = orderMqVo.getScheduleId();
        Integer availableNumber = orderMqVo.getAvailableNumber();

        MsmVo msmVo = orderMqVo.getMsmVo();
        if (availableNumber != null) {
            // 执行更新方法
            scheduleService.updateAvailableNumber(scheduleId, availableNumber);
        } else {
            Schedule schedule = scheduleService.getScheduleDetailById(scheduleId);
            Integer number = schedule.getAvailableNumber() + 1;
            scheduleService.updateAvailableNumber(scheduleId, number );
        }

        if (msmVo != null) {
            // 向rabbit发送消息
            rabbitMQService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM, MqConst.ROUTING_MSM_ITEM, msmVo);
        }

    }
}
