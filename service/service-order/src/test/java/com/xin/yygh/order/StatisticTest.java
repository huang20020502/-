package com.xin.yygh.order;

import com.xin.yygh.hosp.vo.order.OrderCountQueryVo;
import com.xin.yygh.hosp.vo.order.OrderCountVo;
import com.xin.yygh.order.mapper.OrderInfoMapper;
import com.xin.yygh.order.service.OrderInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class StatisticTest {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderInfoService orderInfoService;
    @Test
    public void test01() {
        OrderCountQueryVo orderCountQueryVo = new OrderCountQueryVo();
        orderCountQueryVo.setHosname("北");
        orderCountQueryVo.setReserveDateBegin("2023-02-17");
        orderCountQueryVo.setReserveDateEnd("2023-02-24");
        List<OrderCountVo> statistic = orderInfoMapper.statistic(orderCountQueryVo);
        for (OrderCountVo orderCountVo : statistic) {
            System.out.println(orderCountVo);
        }
    }

    @Test
    public void test02() {
        OrderCountQueryVo orderCountQueryVo = new OrderCountQueryVo();
        orderCountQueryVo.setHosname("北");
        orderCountQueryVo.setReserveDateBegin("2023-02-17");
        orderCountQueryVo.setReserveDateEnd("2023-02-24");
        Map<String, Object> statistic = orderInfoService.statistic(orderCountQueryVo);

        for (Map.Entry<String, Object> entry : statistic.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

}
