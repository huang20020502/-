package com.xin.yygh.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.wxpay.sdk.WXPayUtil;
import com.xin.yygh.common.exception.YyghException;
import com.xin.yygh.hosp.enums.PaymentTypeEnum;
import com.xin.yygh.hosp.enums.RefundStatusEnum;
import com.xin.yygh.hosp.model.order.OrderInfo;
import com.xin.yygh.hosp.model.order.PaymentInfo;
import com.xin.yygh.hosp.model.order.RefundInfo;
import com.xin.yygh.order.pro.WeiPayProperties;
import com.xin.yygh.order.service.OrderInfoService;
import com.xin.yygh.order.service.PaymentInfoService;
import com.xin.yygh.order.service.RefundInfoService;
import com.xin.yygh.order.service.WeiPayService;
import com.xin.yygh.order.utils.HttpClient;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class WeiPayServiceImpl implements WeiPayService {

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private PaymentInfoService paymentInfoService;

    @Autowired
    private WeiPayProperties weiPayProperties;

    @Autowired
    private RefundInfoService refundInfoService;

    @Override
    public String creatNative(Long orderId) {
        // 1.根据orderId查询出订单信息
        OrderInfo orderInfo = orderInfoService.getById(orderId);
        // 2.添加支付记录
        Integer paymentType = PaymentTypeEnum.WEIXIN.getStatus();
        paymentInfoService.savePaymentInfo(orderInfo, paymentType);
        // 3.向微信服务器发起请求
        // 3.1 封装参数
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appid", weiPayProperties.getAppid());
        paramMap.put("mch_id", weiPayProperties.getPartner());
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        Date reserveDate = orderInfo.getReserveDate();
        String reserveDateString = new DateTime(reserveDate).toString("yyyy/MM/dd");
        String body = reserveDateString + "就诊"+ orderInfo.getDepname();
        paramMap.put("body", body);
        paramMap.put("out_trade_no", orderInfo.getOutTradeNo());
        paramMap.put("total_fee", "1"); // 单位是分
        paramMap.put("spbill_create_ip", "127.0.0.1");
        paramMap.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify");
        paramMap.put("trade_type", "NATIVE");

        // 发起请求
        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
        try {
            httpClient.setXmlParam(WXPayUtil.generateSignedXml(paramMap, weiPayProperties.getPartnerkey()));
            httpClient.setHttps(true);
            httpClient.post();

            // 获取微信服务器返回消息
            String content = httpClient.getContent();
            if (StringUtils.hasLength(content)) {
                Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
                System.out.println(resultMap);
                String resultUrl = resultMap.get("code_url");
                System.out.println(resultUrl);
                return resultUrl;
            }
            return "";
        }catch (Exception ex) {
            throw new YyghException(20001, "支付失败");
        }


    }

    /**
     * 向微信服务器发送请求来查询支付信息
     * @param orderId
     * @return
     */
    @Override
    public Map<String, String> queryStatus(Long orderId) {

        OrderInfo orderInfo = orderInfoService.getById(orderId);
        // 1.封装查询条件
        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("appid", weiPayProperties.getAppid());
        queryMap.put("mch_id", weiPayProperties.getPartner());
        queryMap.put("out_trade_no", orderInfo.getOutTradeNo());
        queryMap.put("nonce_str", WXPayUtil.generateNonceStr());

        // 2.转换成xml
        // 3.向微信服务器发送请求
        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
        try {
            httpClient.setXmlParam(WXPayUtil.generateSignedXml(queryMap, weiPayProperties.getPartnerkey()));
            httpClient.setHttps(true);
            httpClient.post();

            // 4.判断查询是否成功
            String content = httpClient.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            return resultMap;
        } catch (Exception exception) {
           throw new YyghException(20001, "查询支付状态失败");
        }
    }

    @Override
    public boolean refund(PaymentInfo paymentInfo) {

        RefundInfo refundInfo = refundInfoService.saveRefundInfo(paymentInfo);
        // 向微信返送请求退款
        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/secapi/pay/refund");
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("appid", weiPayProperties.getAppid());
        paramMap.put("mch_id", weiPayProperties.getPartner());
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paramMap.put("transaction_id", paymentInfo.getTradeNo());
        paramMap.put("out_refund_no", "tk" + paymentInfo.getOutTradeNo());
        paramMap.put("total_fee", "1");
        paramMap.put("refund_fee", "1");

        try {

            httpClient.setXmlParam(WXPayUtil.generateSignedXml(paramMap, weiPayProperties.getPartnerkey()));
            httpClient.setHttps(true);
            httpClient.setCert(true);
            httpClient.setCertPassword(weiPayProperties.getPartner());
            httpClient.post();

            // 判断是否成功
            String content = httpClient.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);

            if (resultMap == null || !"SUCCESS".equals(resultMap.get("return_code"))) {
                return false;
            }
            // 修改退款状态
            refundInfo.setRefundStatus(RefundStatusEnum.REFUND.getStatus());
            refundInfo.setSubject("退款成功");
            refundInfo.setCallbackTime(new Date());
            refundInfo.setCallbackContent(content);
            refundInfo.setUpdateTime(new Date());
            refundInfoService.updateById(refundInfo);
            return true;
        } catch (Exception exception) {
            return false;
        }

    }
}
