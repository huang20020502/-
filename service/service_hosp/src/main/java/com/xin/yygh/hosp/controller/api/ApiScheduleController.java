package com.xin.yygh.hosp.controller.api;

import com.xin.yygh.common.Result;
import com.xin.yygh.hosp.model.hosp.Schedule;
import com.xin.yygh.hosp.service.ScheduleService;
import com.xin.yygh.hosp.utils.HttpRequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp")
public class ApiScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @PostMapping("/schedule/remove")
    public Result delete(HttpServletRequest request) {
        Map<String, Object> stringObjectMap = HttpRequestUtils.switchMap(request.getParameterMap());
        scheduleService.delete(stringObjectMap);
        return Result.ok().code(200);
    }

    @PostMapping("/schedule/list")
    public Result findPage(HttpServletRequest request) {
        Map<String, Object> stringObjectMap = HttpRequestUtils.switchMap(request.getParameterMap());
        Page<Schedule> page = scheduleService.findPage(stringObjectMap);
        return Result.ok().code(200).data("totalElements",page.getTotalElements())
                                    .data("content",page.toList());
    }

    @PostMapping("/saveSchedule")
    public Result save(HttpServletRequest request) {
        Map<String, Object> stringObjectMap = HttpRequestUtils.switchMap(request.getParameterMap());
        scheduleService.save(stringObjectMap);
        return Result.ok().code(200);
    }
}
