package com.xin.yygh.order.service;

import com.xin.yygh.hosp.vo.order.OrderCountQueryVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient("service-order")
public interface OrderService {

    @PostMapping("/api/order/orderInfo/statistic")
    Map<String, Object> statistic(@RequestBody OrderCountQueryVo orderCountQueryVo);
}
