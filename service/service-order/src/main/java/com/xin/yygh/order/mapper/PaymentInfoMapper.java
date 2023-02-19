package com.xin.yygh.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xin.yygh.hosp.model.order.PaymentInfo;
import org.apache.ibatis.annotations.Mapper;

/**
* @author xin
* @description 针对表【payment_info(支付信息表)】的数据库操作Mapper
* @createDate 2023-02-17 12:59:00
* @Entity com.xin.yygh.order.PaymentInfo
*/
@Mapper
public interface PaymentInfoMapper extends BaseMapper<PaymentInfo> {

}




