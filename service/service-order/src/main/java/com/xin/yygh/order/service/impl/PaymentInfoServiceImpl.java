package com.xin.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xin.yygh.common.exception.YyghException;
import com.xin.yygh.hosp.enums.OrderStatusEnum;
import com.xin.yygh.hosp.enums.PaymentStatusEnum;
import com.xin.yygh.hosp.model.order.OrderInfo;
import com.xin.yygh.hosp.model.order.PaymentInfo;
import com.xin.yygh.hosp.model.order.RefundInfo;
import com.xin.yygh.hosp.vo.msm.MsmVo;
import com.xin.yygh.hosp.vo.order.OrderMqVo;
import com.xin.yygh.mq.MqConst;
import com.xin.yygh.mq.RabbitMQService;
import com.xin.yygh.order.service.OrderInfoService;
import com.xin.yygh.order.service.PaymentInfoService;
import com.xin.yygh.order.mapper.PaymentInfoMapper;
import com.xin.yygh.order.service.WeiPayService;
import com.xin.yygh.order.utils.HttpRequestHelper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
* @author xin
* @description 针对表【payment_info(支付信息表)】的数据库操作Service实现
* @createDate 2023-02-17 12:59:00
*/
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo>
    implements PaymentInfoService{

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

    @Autowired
    private WeiPayService weiPayService;

    @Autowired
    private RabbitMQService rabbitMQService;

    @Override
    public void savePaymentInfo(OrderInfo orderInfo, Integer paymentType) {
        // 1.判断当前订单是否被创建支付信息
        QueryWrapper<PaymentInfo> paymentInfoQueryWrapper = new QueryWrapper<>();
        paymentInfoQueryWrapper.eq("order_id", orderInfo.getId());
        PaymentInfo paymentInfo = paymentInfoMapper.selectOne(paymentInfoQueryWrapper);

        if (paymentInfo != null) {
            return;
        }

        // 2.当没有被创建支付信息，添加支付信息
        paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
        paymentInfo.setOrderId(orderInfo.getId());
        //paymentInfo.setTradeNo();  微信返回
        paymentInfo.setTotalAmount(orderInfo.getAmount());
        String subject = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd")+"|"+orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle();
        paymentInfo.setSubject(subject);
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setPaymentStatus(PaymentStatusEnum.UNPAID.getStatus());

        paymentInfoMapper.insert(paymentInfo);
    }

    @Override
    public void cancelBook(Long orderId) {
        // 0.判断当前是否已经超过了取消预约的时间
        OrderInfo orderInfo = orderInfoService.getById(orderId);
        DateTime quitTime = new DateTime(orderInfo.getQuitTime());
        if (quitTime.isBeforeNow()) {
            // 当前已经超过了规定的取消预约时间
            throw new YyghException(20001, "超过取消预约时间");

        }
        // 1.请求第三方医院，来取消预约
        //    1.1 第三方医院同意取消
        //    1.2 第三方医院不同意取消, 抛出异常
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("hoscode", orderInfo.getHoscode());
        paramMap.put("hosRecordId", orderInfo.getHosRecordId());
        JSONObject result = HttpRequestHelper.sendRequest(paramMap, "http://localhost:9998/order/updateCancelStatus");
        // 判断第三方医院是否取消成功
        if (result == null || result.getIntValue("code") != 200) {
            throw new YyghException(20001, "取消预约失败");
        }

        // 2.判断当前订单是否被支付
        //    2.1 支付过，请求微信服务器来继续退款
        // 查询支付信息
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        // 使用其中一个来查询
        queryWrapper.eq("out_trade_no", orderInfo.getOutTradeNo());
        //queryWrapper.eq("order_id", orderInfo.getId());
        PaymentInfo paymentInfo = paymentInfoMapper.selectOne(queryWrapper);
        if (paymentInfo.getPaymentStatus() == PaymentStatusEnum.PAID.getStatus()) {
            // 退款操作
            boolean flag = weiPayService.refund(paymentInfo);
            if (!flag) {
                throw new YyghException(20001, "退款失败");
            }

        }
        // 3.修改订单的信息
        orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());
        orderInfoService.updateById(orderInfo);

        // 修改支付的信息
        OrderMqVo orderMqVo = new OrderMqVo();
        orderMqVo.setScheduleId(orderInfo.getScheduleId());
        MsmVo msmVo = new MsmVo();
        msmVo.setPhone(orderInfo.getPatientPhone());
        orderMqVo.setMsmVo(msmVo);
        rabbitMQService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);
    }
}




