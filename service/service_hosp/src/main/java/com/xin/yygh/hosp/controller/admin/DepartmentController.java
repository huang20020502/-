package com.xin.yygh.hosp.controller.admin;

import com.xin.yygh.common.Result;
import com.xin.yygh.hosp.service.DepartmentService;
import com.xin.yygh.hosp.vo.hosp.DepartmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/hospital/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/{hoscode}")
    public Result getDepartmentListByHoscode(@PathVariable String hoscode) {
        List<DepartmentVo> list = departmentService.getDepartmentListByHoscode(hoscode);
        return Result.ok().data("list",list);
    }
}
