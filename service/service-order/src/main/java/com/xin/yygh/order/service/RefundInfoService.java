package com.xin.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xin.yygh.hosp.model.order.PaymentInfo;
import com.xin.yygh.hosp.model.order.RefundInfo;

/**
* @author xin
* @description 针对表【refund_info(退款信息表)】的数据库操作Service
* @createDate 2023-02-19 09:38:04
*/
public interface RefundInfoService extends IService<RefundInfo> {

    RefundInfo saveRefundInfo(PaymentInfo paymentInfo);
}
