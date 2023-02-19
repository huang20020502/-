package com.xin.yygh.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xin.yygh.hosp.enums.PaymentTypeEnum;
import com.xin.yygh.hosp.enums.RefundStatusEnum;
import com.xin.yygh.hosp.model.order.PaymentInfo;
import com.xin.yygh.hosp.model.order.RefundInfo;
import com.xin.yygh.order.service.RefundInfoService;
import com.xin.yygh.order.mapper.RefundInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author xin
* @description 针对表【refund_info(退款信息表)】的数据库操作Service实现
* @createDate 2023-02-19 09:38:04
*/
@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo>
    implements RefundInfoService{

    @Autowired
    private RefundInfoMapper refundInfoMapper;

    @Override
    public RefundInfo saveRefundInfo(PaymentInfo paymentInfo) {
        // 判断当前的订单是否在退款中
        QueryWrapper<RefundInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("out_trade_no", paymentInfo.getOutTradeNo());
        RefundInfo refundInfo = refundInfoMapper.selectOne(queryWrapper);

        if (refundInfo != null) {
            return refundInfo;
        }
        refundInfo = new RefundInfo();
        refundInfo.setOutTradeNo(paymentInfo.getOutTradeNo());
        refundInfo.setOrderId(paymentInfo.getOrderId());
        refundInfo.setPaymentType(PaymentTypeEnum.WEIXIN.getStatus());
        refundInfo.setTradeNo(paymentInfo.getTradeNo());
        refundInfo.setTotalAmount(paymentInfo.getTotalAmount());
        refundInfo.setSubject("退款中....");
        refundInfo.setRefundStatus(RefundStatusEnum.UNREFUND.getStatus());

        // 添加退款信息
        refundInfoMapper.insert(refundInfo);
        return refundInfo;
    }
}




