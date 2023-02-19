package com.xin.yygh.hosp.service;

import com.xin.yygh.hosp.vo.hosp.ScheduleOrderVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-hosp")
public interface ScheduleFeignClient {

    @GetMapping("/user/hospital/schedule/{scheduleId}")
    ScheduleOrderVo getById(@PathVariable(value = "scheduleId") String scheduleId);
}
