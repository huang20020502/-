package com.xin.yygh.hosp.controller.admin;

import com.xin.yygh.common.Result;
import com.xin.yygh.hosp.model.hosp.Schedule;
import com.xin.yygh.hosp.service.ScheduleService;
import com.xin.yygh.hosp.vo.hosp.ScheduleQueryVo;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/hospital/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/{hoscode}/{depcode}/{workDate}")
    public Result getSchedule(@PathVariable("hoscode") String hoscode,
                              @PathVariable("depcode") String depcode,
                              @PathVariable("workDate") String workDate) {

        List<Schedule> list = scheduleService.getScheduleByHoscodeAndDepcodeAndWorkDate(hoscode, depcode, workDate);
        return Result.ok().data("list",list);
    }

    @GetMapping("/{pageNum}/{pageSize}/{hoscode}/{depcode}")
    public Result page(@PathVariable Integer pageNum,
                       @PathVariable Integer pageSize,
                       @PathVariable String hoscode,
                       @PathVariable String depcode) {
        Map<String,Object> map = scheduleService.page(pageNum,pageSize,hoscode,depcode);
        return Result.ok().data(map);
    }
}
