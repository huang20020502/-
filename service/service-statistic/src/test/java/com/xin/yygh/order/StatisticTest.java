package com.xin.yygh.order;

import com.xin.yygh.hosp.vo.order.OrderCountQueryVo;
import com.xin.yygh.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
public class StatisticTest {

    @Autowired
    private OrderService orderService;
    @Test
    public void test01() {
        OrderCountQueryVo orderCountQueryVo = new OrderCountQueryVo();
        orderCountQueryVo.setHosname("北");
        orderCountQueryVo.setReserveDateBegin("2023-02-17");
        orderCountQueryVo.setReserveDateEnd("2023-02-24");
        Map<String, Object> statistic = orderService.statistic(orderCountQueryVo);
        for (Map.Entry<String, Object> entry : statistic.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}
