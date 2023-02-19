package com.xin.yygh.hosp.service;

import com.xin.yygh.hosp.model.hosp.Schedule;
import com.xin.yygh.hosp.vo.hosp.ScheduleOrderVo;
import org.springframework.data.domain.Page;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ScheduleService {
    void save(Map<String, Object> stringObjectMap);


    Page<Schedule> findPage(Map<String, Object> stringObjectMap);

    void delete(Map<String, Object> stringObjectMap);

    Map<String, Object> page(Integer pageNum, Integer pageSize, String hoscode, String depcode);

    List<Schedule> getScheduleByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, String workDate);

    Map<String, Object> getPageList(Integer pageNum, Integer pageSize, String hoscode, String depcode);

    Schedule getScheduleDetailById(String scheduleId);

    ScheduleOrderVo getById(String scheduleId);

    boolean updateAvailableNumber(String scheduleId, Integer availableNumber);
}
