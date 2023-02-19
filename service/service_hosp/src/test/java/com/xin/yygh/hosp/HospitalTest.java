package com.xin.yygh.hosp;

import com.xin.yygh.hosp.model.hosp.Hospital;
import com.xin.yygh.hosp.service.HospitalService;
import com.xin.yygh.hosp.vo.hosp.HospitalQueryVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.ldap.AutoConfigureDataLdap;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

@SpringBootTest
public class HospitalTest {

    @Autowired
    private HospitalService hospitalService;

    @Test
    public void testPage() {
        Page<Hospital> pageList = hospitalService.getPageList(1, 10, "Hostype", new HospitalQueryVo());
        for (Hospital hospital : pageList) {
            System.out.println(hospital);
        }
    }
}
