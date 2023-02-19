package com.xin.yygh.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xin.yygh.hosp.model.order.OrderInfo;
import com.xin.yygh.hosp.vo.order.OrderCountQueryVo;
import com.xin.yygh.hosp.vo.order.OrderCountVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author xin
* @description 针对表【order_info(订单表)】的数据库操作Mapper
* @createDate 2023-02-16 09:54:56
* @Entity com.xin.yygh.order.OrderInfo
*/
@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    List<OrderCountVo> statistic(OrderCountQueryVo orderCountQueryVo);
}




