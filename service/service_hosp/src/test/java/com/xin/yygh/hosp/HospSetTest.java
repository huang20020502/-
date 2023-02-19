package com.xin.yygh.hosp;


import com.xin.yygh.hosp.entities.HospitalSet;
import com.xin.yygh.hosp.service.HospitalSetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class HospSetTest {

    @Autowired
    private HospitalSetService hospitalSetService;

    @Test
    public void testFindAll() {
        List<HospitalSet> list = hospitalSetService.list();
        for (HospitalSet hospitalSet : list) {
            System.out.println(hospitalSet);
        }
    }
}
