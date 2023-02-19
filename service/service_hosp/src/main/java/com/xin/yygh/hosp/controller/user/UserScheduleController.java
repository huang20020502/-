package com.xin.yygh.hosp.controller.user;

import com.xin.yygh.common.Result;
import com.xin.yygh.hosp.model.hosp.Schedule;
import com.xin.yygh.hosp.service.ScheduleService;
import com.xin.yygh.hosp.vo.hosp.ScheduleOrderVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/hospital/schedule")
public class UserScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/{scheduleId}")
    public ScheduleOrderVo getById(@PathVariable(value = "scheduleId") String scheduleId) {
       ScheduleOrderVo scheduleOrderVo =  scheduleService.getById(scheduleId);
        return scheduleOrderVo;
    }

    @GetMapping("/scheduleDetail/{scheduleId}")
    public Result getScheduleDetailById(@PathVariable String scheduleId) {
        Schedule schedule = scheduleService.getScheduleDetailById(scheduleId);
        return Result.ok().data("schedule", schedule);
    }

    @GetMapping("{pageNum}/{pageSize}/{hoscode}/{depcode}")
    public Result getPageList( @PathVariable Integer pageNum,
                               @PathVariable Integer pageSize,
                               @PathVariable String hoscode,
                               @PathVariable String depcode) {
        Map<String, Object> map = scheduleService.getPageList(pageNum, pageSize, hoscode, depcode);
        return Result.ok().data(map);
    }

    @GetMapping("/{hoscode}/{depcode}/{workDate}")
    public Result detail(@PathVariable String hoscode,
                         @PathVariable String depcode,
                         @PathVariable String workDate) {
        List<Schedule> details = scheduleService.getScheduleByHoscodeAndDepcodeAndWorkDate(hoscode, depcode, workDate);
        return Result.ok().data("details", details);
    }
}
