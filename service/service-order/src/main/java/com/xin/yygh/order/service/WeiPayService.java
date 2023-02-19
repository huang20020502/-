package com.xin.yygh.order.service;

import com.xin.yygh.hosp.model.order.PaymentInfo;
import com.xin.yygh.hosp.model.order.RefundInfo;

import java.util.Map;

public interface WeiPayService {
    String creatNative(Long orderId);

    Map<String, String> queryStatus(Long orderId);

    boolean refund(PaymentInfo paymentInfo);

}
