package com.xin.yygh.task.job;


import com.xin.yygh.mq.MqConst;
import com.xin.yygh.mq.RabbitMQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PatientRemindJob {

    @Autowired
    private RabbitMQService rabbitMQService;

    @Scheduled(cron = "*/10 * * * * ?")
    public void PatientRemind() {
        rabbitMQService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK, MqConst.ROUTING_TASK_8, "");
    }
}
