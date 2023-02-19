package com.xin.yygh.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xin.yygh.hosp.model.order.RefundInfo;
import org.apache.ibatis.annotations.Mapper;

/**
* @author xin
* @description 针对表【refund_info(退款信息表)】的数据库操作Mapper
* @createDate 2023-02-19 09:38:04
* @Entity com.xin.yygh.order.RefundInfo
*/
@Mapper
public interface RefundInfoMapper extends BaseMapper<RefundInfo> {

}




