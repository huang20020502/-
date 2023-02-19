package com.xin.yygh.order.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xin.yygh.common.Result;
import com.xin.yygh.common.exception.YyghException;
import com.xin.yygh.hosp.enums.OrderStatusEnum;
import com.xin.yygh.hosp.model.order.OrderInfo;
import com.xin.yygh.hosp.model.user.Patient;
import com.xin.yygh.hosp.service.ScheduleFeignClient;
import com.xin.yygh.hosp.vo.hosp.ScheduleOrderVo;
import com.xin.yygh.hosp.vo.msm.MsmVo;
import com.xin.yygh.hosp.vo.order.OrderCountQueryVo;
import com.xin.yygh.hosp.vo.order.OrderMqVo;
import com.xin.yygh.hosp.vo.order.OrderQueryVo;
import com.xin.yygh.mq.MqConst;
import com.xin.yygh.mq.RabbitMQService;
import com.xin.yygh.order.service.OrderInfoService;
import com.xin.yygh.order.utils.HttpRequestHelper;
import com.xin.yygh.user.service.PatientFeignClient;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/order/orderInfo")
public class OrderInfoController {

    @Autowired
    private ScheduleFeignClient scheduleFeignClient;

    @Autowired
    private PatientFeignClient patientFeignClient;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private RabbitMQService rabbitMQService;

    @PostMapping("/statistic")
    public Map<String, Object> statistic(@RequestBody OrderCountQueryVo orderCountQueryVo) {
       Map<String, Object> resultMap =  orderInfoService.statistic(orderCountQueryVo);
        return resultMap;
    }


    @GetMapping("/detail/{id}")
    public Result getDetailById(@PathVariable Long id) {
        OrderInfo orderInfo = orderInfoService.getDetailById(id);
        return Result.ok().data("orderInfo", orderInfo);
    }

    @GetMapping("/statusList")
    public Result getStatusList() {
        List<Map<String, Object>> statusList = OrderStatusEnum.getStatusList();
        return Result.ok().data("statusList", statusList);
    }

    @GetMapping("/{pageNum}/{pageSize}")
    public Result getPage(@PathVariable Integer pageNum,
                          @PathVariable Integer pageSize,
                          OrderQueryVo orderQueryVo,
                          @RequestHeader String token) {

        Page<OrderInfo> page = orderInfoService.getPage(pageNum, pageSize, orderQueryVo, token);
        return Result.ok().data("total", page.getTotal()).data("list", page.getRecords());
    }


    @PostMapping("/{scheduleId}/{patientId}")
    public Result creatOrder(@PathVariable String scheduleId,
                             @PathVariable Long patientId) {
        // 1.根据scheduleId查询出排班信息和医院信息
        ScheduleOrderVo scheduleOrderVo = scheduleFeignClient.getById(scheduleId);
        // 2.根据patientId查询出就走人信息
        Patient patient = patientFeignClient.getById(patientId);

        // 判断当前时间是否超过了规定预约时间
        Date stopTime = scheduleOrderVo.getStopTime();
        if (new DateTime(stopTime).isBeforeNow()) {
            // 超过了规定的预约时间
            throw new YyghException(20001, "超过预约时间");
        }

        // 3.访问第三方医院，如果能创建订单返回订单号，不能直接抛出异常
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("hoscode", scheduleOrderVo.getHoscode());
        paramMap.put("depcode", scheduleOrderVo.getDepcode());
        paramMap.put("hosScheduleId", scheduleOrderVo.getHosScheduleId());
        paramMap.put("reserveDate", scheduleOrderVo.getReserveDate());
        paramMap.put("reserveTime", scheduleOrderVo.getReserveTime());
        paramMap.put("amount", scheduleOrderVo.getAmount());

        JSONObject jsonObject = HttpRequestHelper.sendRequest(paramMap, "http://localhost:9998/order/submitOrder");

        OrderInfo orderInfo = new OrderInfo();
        JSONObject data = jsonObject.getJSONObject("data");
        if (jsonObject != null && jsonObject.getInteger("code") == 200) {

            // 创建OrderInfo对象并添加到数据库中
            orderInfo.setUserId(patient.getUserId());
            orderInfo.setHoscode(scheduleOrderVo.getHoscode());
            orderInfo.setHosname(scheduleOrderVo.getHosname());
            orderInfo.setDepcode(scheduleOrderVo.getDepcode());
            orderInfo.setDepname(scheduleOrderVo.getDepname());
            orderInfo.setTitle(scheduleOrderVo.getTitle());
            orderInfo.setScheduleId(scheduleId);
            orderInfo.setReserveDate(scheduleOrderVo.getReserveDate());
            orderInfo.setReserveTime(scheduleOrderVo.getReserveTime());

            orderInfo.setPatientId(patient.getId());
            orderInfo.setPatientName(patient.getName());
            orderInfo.setPatientPhone(patient.getPhone());

            orderInfo.setHosRecordId(data.getString("hosRecordId"));
            orderInfo.setNumber(data.getInteger("number"));
            orderInfo.setFetchTime(data.getString("fetchTime"));
            orderInfo.setFetchAddress(data.getString("fetchAddress"));

            orderInfo.setAmount(scheduleOrderVo.getAmount());
            orderInfo.setQuitTime(scheduleOrderVo.getQuitTime());
            orderInfo.setOrderStatus(0);

            String bookTradeNo = UUID.randomUUID().toString().replace("-", "").substring(10);
            orderInfo.setOutTradeNo(bookTradeNo);

        } else {
            throw new YyghException(20001, "预定失败");
        }
        // 插入数据
        orderInfoService.save(orderInfo);

        // 发送消息
        OrderMqVo orderMqVo = new OrderMqVo();
        orderMqVo.setReservedNumber(data.getInteger("reservedNumber"));
        orderMqVo.setAvailableNumber(data.getInteger("availableNumber"));
        orderMqVo.setScheduleId(scheduleId);

        //短信提示
        MsmVo msmVo = new MsmVo();
        msmVo.setPhone(orderInfo.getPatientPhone());
        String reserveDate =
                new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd")
                        + (orderInfo.getReserveTime()==0 ? "上午": "下午");
        Map<String,Object> param = new HashMap<String,Object>(){{
            put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
            put("amount", orderInfo.getAmount());
            put("reserveDate", reserveDate);
            put("name", orderInfo.getPatientName());
            put("quitTime", new DateTime(orderInfo.getQuitTime()).toString("yyyy-MM-dd HH:mm"));
        }};
        msmVo.setParam(param);

        orderMqVo.setMsmVo(msmVo);

        rabbitMQService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);
        return Result.ok().data("orderId", orderInfo.getId());
    }

}
