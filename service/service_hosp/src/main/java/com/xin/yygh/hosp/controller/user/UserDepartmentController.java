package com.xin.yygh.hosp.controller.user;


import com.xin.yygh.common.Result;
import com.xin.yygh.hosp.model.hosp.Hospital;
import com.xin.yygh.hosp.service.DepartmentService;
import com.xin.yygh.hosp.service.HospitalService;
import com.xin.yygh.hosp.vo.hosp.DepartmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/hospital/department")
public class UserDepartmentController{

    @Autowired
    private DepartmentService departmentService;


    @GetMapping("/all/{hoscode}")
    public Result getDepartmentList(@PathVariable String hoscode) {
        List<DepartmentVo> list = departmentService.getDepartmentListByHoscode(hoscode);
        return Result.ok().data("list",list);
    }


}
