package com.xin.yygh.hosp.controller.admin;

import com.xin.yygh.common.Result;
import com.xin.yygh.hosp.model.hosp.Hospital;
import com.xin.yygh.hosp.service.HospitalService;
import com.xin.yygh.hosp.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/hospital")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    @GetMapping("/{id}")
    public Result getHospitalDetailById(@PathVariable String id) {
        Hospital hospital = hospitalService.getHospitalById(id);
        return Result.ok().data("hospital",hospital);
    }

    @PutMapping("/{id}/{status}")
    public Result updateStaus(@PathVariable(value = "id") String id,
                              @PathVariable(value = "status") Integer status) {
        hospitalService.updateStatusById(id, status);
        return Result.ok();
    }

    @ApiOperation(value = "显示医院的列表")
    @GetMapping("/{pageNum}/{pageSize}/{dictCode}")
    public Result getPageList(@PathVariable Integer pageNum,
                              HospitalQueryVo hospitalQueryVo,
                              @PathVariable Integer pageSize,
                              @PathVariable String dictCode) {

        Page<Hospital> page = hospitalService.getPageList(pageNum,pageSize,dictCode,hospitalQueryVo);
        return Result.ok().data("total",page.getTotalElements())
                          .data("rows",page.toList());
    }
}
