package com.xin.yygh.order.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xin.yygh.common.Result;
import com.xin.yygh.hosp.enums.OrderStatusEnum;
import com.xin.yygh.hosp.enums.PaymentStatusEnum;
import com.xin.yygh.hosp.model.order.OrderInfo;
import com.xin.yygh.hosp.model.order.PaymentInfo;
import com.xin.yygh.order.service.OrderInfoService;
import com.xin.yygh.order.service.PaymentInfoService;
import com.xin.yygh.order.service.WeiPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/user/order/wei")
public class WeiPayController {

    @Autowired
    private WeiPayService weiPayService;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private PaymentInfoService paymentInfoService;

    @GetMapping("/cancelBook/{orderId}")
    public Result cancelBook(@PathVariable Long orderId) {
        paymentInfoService.cancelBook(orderId);
        return Result.ok();
    }

    @GetMapping("/queryStatus/{orderId}")
    public Result queryStatus(@PathVariable Long orderId) {
        Map<String, String> resultMap = weiPayService.queryStatus(orderId);
        if (resultMap != null) {
            // 获取支付状态
            String tradeState = resultMap.get("trade_state");

            if ("SUCCESS".equals(tradeState)) {
                // 更新订单
                OrderInfo orderInfo = new OrderInfo();
                orderInfo.setId(orderId);
                orderInfo.setOrderStatus(OrderStatusEnum.PAID.getStatus());
                orderInfoService.updateById(orderInfo);
                // 更新支付
                QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("order_id", orderInfo.getId());
                PaymentInfo paymentInfo = new PaymentInfo();

                paymentInfo.setPaymentStatus(PaymentStatusEnum.PAID.getStatus());
                paymentInfo.setTradeNo(resultMap.get("transaction_id"));
                paymentInfo.setCallbackTime(new Date());
                paymentInfo.setCallbackContent(JSONObject.toJSONString(resultMap));
                paymentInfoService.update(paymentInfo, queryWrapper);
                return Result.ok().message("支付成功");
            }

            return Result.ok().message(tradeState);
        }
        return Result.ok().message("查询支付状态失败");
    }

    @GetMapping("/{orderId}")
    public Result createNative(@PathVariable Long orderId) {
        String url = weiPayService.creatNative(orderId);
        return Result.ok().data("url", url);
    }
}
