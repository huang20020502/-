package com.xin.yygh.order.listener;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rabbitmq.client.Channel;
import com.xin.yygh.hosp.model.order.OrderInfo;
import com.xin.yygh.hosp.vo.msm.MsmVo;
import com.xin.yygh.mq.MqConst;
import com.xin.yygh.mq.RabbitMQService;
import com.xin.yygh.order.service.OrderInfoService;
import org.joda.time.DateTime;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TaskListener {

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private RabbitMQService rabbitMQService;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(name = MqConst.QUEUE_TASK_8),
                    exchange = @Exchange(name = MqConst.EXCHANGE_DIRECT_TASK),
                    key = MqConst.ROUTING_TASK_8
            )
    })
    public void PatientRemind(Message message, Channel channel) {
        // 查询当天全部订单
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("reserve_date", new DateTime().toString("yyyy-MM-dd"));
        queryWrapper.ne("order_status", "-1");
        List<OrderInfo> list = orderInfoService.list(queryWrapper);

        for (OrderInfo orderInfo : list) {
            // 发送对象
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("reserveDate", orderInfo.getReserveDate());
                put("name", orderInfo.getPatientName());
            }};
            msmVo.setParam(param);

            rabbitMQService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM, MqConst.ROUTING_MSM_ITEM, msmVo);
        }
    }
}
