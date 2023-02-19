package com.xin.yygh.hosp.controller.user;

import com.xin.yygh.common.Result;
import com.xin.yygh.hosp.model.hosp.Hospital;
import com.xin.yygh.hosp.service.HospitalService;
import com.xin.yygh.hosp.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/hospital")
public class UserHospitalController {

    @Autowired
    private HospitalService hospitalService;

    // 根据医院编号来获取信息
    @GetMapping("/detail/{hoscode}")
    public Result getDetailByHoscode(@PathVariable String hoscode) {
        Hospital hospital = hospitalService.getDetailByHoscode(hoscode);
        return Result.ok().data("hospital", hospital);
    }


    // 根据省、市、区编号来查询对应的医院信息
    @GetMapping
    public Result getListByAddress(String hosType, String districtCode) {
        List<Hospital> hospitals = hospitalService.getListByHosTypeAndDistrictCode(hosType, districtCode);
        return Result.ok().data("list",hospitals);
    }

    // 返回全部医院
    @ApiOperation(value = "返回全部医院信息")
    @GetMapping("/list")
    public Result getHospitalList() {
        List<Hospital> hospitals = hospitalService.findAll();
        return Result.ok().data("list",hospitals);
    }

    // 前台首页查询方法
    @ApiOperation(value = "根据医院名进行模糊查询")
    @GetMapping("/search/{hosname}")
    public Result getListByHosname(@PathVariable String hosname) {
        List<Hospital> hospitals = hospitalService.getListByHosname(hosname);
        return Result.ok().data("list",hospitals);
    }
}
