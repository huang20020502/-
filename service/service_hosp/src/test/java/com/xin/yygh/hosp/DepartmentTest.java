package com.xin.yygh.hosp;

import com.xin.yygh.hosp.service.DepartmentService;
import com.xin.yygh.hosp.vo.hosp.DepartmentVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class DepartmentTest {

    @Autowired
    private DepartmentService departmentService;

    @Test
    public void getDepartmentListByHoscodeTest() {
        List<DepartmentVo> list = departmentService.getDepartmentListByHoscode("10000");
        for (DepartmentVo departmentVo : list) {
            System.out.println(departmentVo);
        }
    }
}
