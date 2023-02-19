package com.xin.yygh.statistic.controller;

import com.xin.yygh.common.Result;
import com.xin.yygh.hosp.vo.order.OrderCountQueryVo;
import com.xin.yygh.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin/statistic")
public class StatisticController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public Result statistic(OrderCountQueryVo orderCountQueryVo) {
        Map<String, Object> resultMap = orderService.statistic(orderCountQueryVo);
        return Result.ok().data(resultMap);
    }
}
