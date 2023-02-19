package com.xin.yygh.hosp;

import com.xin.yygh.hosp.service.ScheduleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.Map;

@SpringBootTest
public class ScheduleTest {

    @Autowired
    private ScheduleService scheduleService;

    @Test
    public void getScheduleByHoscodeAndDepcodeAndWorkDateTest() {
    }

    @Test
    public void pageTest() {

        Map<String, Object> page = scheduleService.page(1, 10, "10000", null);

        System.out.println(page);
    }
}
