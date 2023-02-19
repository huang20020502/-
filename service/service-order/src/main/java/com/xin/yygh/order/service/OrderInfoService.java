package com.xin.yygh.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xin.yygh.hosp.model.order.OrderInfo;
import com.xin.yygh.hosp.vo.order.OrderCountQueryVo;
import com.xin.yygh.hosp.vo.order.OrderQueryVo;

import java.util.Map;

/**
* @author xin
* @description 针对表【order_info(订单表)】的数据库操作Service
* @createDate 2023-02-16 09:54:56
*/
public interface OrderInfoService extends IService<OrderInfo> {

    Page<OrderInfo> getPage(Integer pageNum, Integer pageSize, OrderQueryVo orderQueryVo, String token);

    OrderInfo getDetailById(Long id);

    Map<String, Object> statistic(OrderCountQueryVo orderCountQueryVo);
}
