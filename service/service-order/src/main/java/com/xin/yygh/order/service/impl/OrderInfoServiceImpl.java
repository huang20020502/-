package com.xin.yygh.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xin.yygh.common.jwt.JWTHelper;
import com.xin.yygh.hosp.enums.OrderStatusEnum;
import com.xin.yygh.hosp.model.order.OrderInfo;
import com.xin.yygh.hosp.vo.order.OrderCountQueryVo;
import com.xin.yygh.hosp.vo.order.OrderCountVo;
import com.xin.yygh.hosp.vo.order.OrderQueryVo;
import com.xin.yygh.order.service.OrderInfoService;
import com.xin.yygh.order.mapper.OrderInfoMapper;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author xin
* @description 针对表【order_info(订单表)】的数据库操作Service实现
* @createDate 2023-02-16 09:54:56
*/
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo>
    implements OrderInfoService{


    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Override
    public Page<OrderInfo> getPage(Integer pageNum, Integer pageSize, OrderQueryVo orderQueryVo, String token) {
        Page<OrderInfo> orderInfoPage = new Page<>(pageNum, pageSize);
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        // 封装查询条件
        Long userId = JWTHelper.getUserId(token);
        String outTradeNo = orderQueryVo.getOutTradeNo();
        Long patientId = orderQueryVo.getPatientId();
        String patientName = orderQueryVo.getPatientName();
        String keyword = orderQueryVo.getKeyword();
        String orderStatus = orderQueryVo.getOrderStatus();
        String reserveDate = orderQueryVo.getReserveDate();
        String createTimeBegin = orderQueryVo.getCreateTimeBegin();
        String createTimeEnd = orderQueryVo.getCreateTimeEnd();

        if (!StringUtils.isEmpty(userId)) {
            queryWrapper.eq("user_id", userId);
        }
        if (!StringUtils.isEmpty(outTradeNo)) {
            queryWrapper.eq("out_trade_no", outTradeNo);
        }
        if (!StringUtils.isEmpty(patientId)) {
            queryWrapper.eq("patient_id", patientId);
        }
        if (!StringUtils.isEmpty(patientName)) {
            queryWrapper.eq("patient_name", patientName);
        }
        if (!StringUtils.isEmpty(keyword)) {
            queryWrapper.like("hosname", keyword);
        }
        if (!StringUtils.isEmpty(orderStatus)) {
            queryWrapper.eq("order_status", orderStatus);
        }
        if (!StringUtils.isEmpty(reserveDate)) {
            queryWrapper.eq("reserve_date", reserveDate);
        }
        if (!StringUtils.isEmpty(createTimeBegin)) {
            queryWrapper.gt("create_time", createTimeBegin);
        }
        if (!StringUtils.isEmpty(createTimeEnd)) {
            queryWrapper.lt("create_time", createTimeEnd);
        }

        Page<OrderInfo> page = orderInfoMapper.selectPage(orderInfoPage, queryWrapper);

        page.getRecords().stream().forEach(orderInfo -> {
            this.packageOrderInfo(orderInfo);
        });
        return page;
    }

    @Override
    public OrderInfo getDetailById(Long id) {
        OrderInfo orderInfo = orderInfoMapper.selectById(id);
        this.packageOrderInfo(orderInfo);
        return orderInfo;
    }

    @Override
    public Map<String, Object> statistic(OrderCountQueryVo orderCountQueryVo) {
        List<OrderCountVo> orderCountVoList =  orderInfoMapper.statistic(orderCountQueryVo);

        List<String> reserveDateList = orderCountVoList.stream().map(OrderCountVo::getReserveDate).collect(Collectors.toCollection(ArrayList<String>::new));
        List<Integer> countList = orderCountVoList.stream().map(OrderCountVo::getCount).collect(Collectors.toCollection(ArrayList<Integer>::new));

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("dateList", reserveDateList);
        resultMap.put("countList", countList);
        return resultMap;
    }

    private void packageOrderInfo(OrderInfo orderInfo) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("orderStatusString", OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus()));
        orderInfo.setParam(map);

    }
}




