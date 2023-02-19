package com.xin.yygh.hosp.controller.api;

import com.xin.yygh.common.Result;
import com.xin.yygh.hosp.model.hosp.Department;
import com.xin.yygh.hosp.service.DepartmentService;
import com.xin.yygh.hosp.utils.HttpRequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp")
public class ApiDepartmentController {

    @Autowired
    private DepartmentService departmentService;


    @PostMapping("/department/remove")
    public Result delete(HttpServletRequest request) {
        Map<String, Object> map = HttpRequestUtils.switchMap(request.getParameterMap());
        departmentService.delete(map);
        return Result.ok().code(200);
    }

    @PostMapping("/department/list")
    public Result getPage(HttpServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        Page<Department> page = departmentService.getPage(map);
        return Result.ok().code(200).data("totalElements",page.getTotalElements()).data("content",page.toList());
    }

    @PostMapping("/saveDepartment")
    public Result saveDepartment(HttpServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();

        Map<String, Object> resultMap = HttpRequestUtils.switchMap(map);
        departmentService.saveDepartment(resultMap);
        return Result.ok().code(200);
    }
}
