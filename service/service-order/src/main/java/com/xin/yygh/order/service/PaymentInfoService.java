package com.xin.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xin.yygh.hosp.model.order.OrderInfo;
import com.xin.yygh.hosp.model.order.PaymentInfo;

/**
* @author xin
* @description 针对表【payment_info(支付信息表)】的数据库操作Service
* @createDate 2023-02-17 12:59:00
*/
public interface PaymentInfoService extends IService<PaymentInfo> {
    void savePaymentInfo(OrderInfo orderInfo, Integer paymentType);

    void cancelBook(Long orderId);
}
