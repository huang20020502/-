package com.xin.yygh.user.controller;

import com.xin.yygh.common.Result;
import com.xin.yygh.common.jwt.JWTHelper;
import com.xin.yygh.hosp.model.user.Patient;
import com.xin.yygh.user.service.PatientService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/user/userinfo/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping("{patientId}")
    public Patient getById(@PathVariable(value = "patientId") Long patientId) {
        Patient patient = patientService.getById(patientId);
        return patient;
    }


    // 增
    @PostMapping("/save")
    public Result save(@RequestHeader String token, @RequestBody Patient patient) {
        Long userId = JWTHelper.getUserId(token);
        patient.setUserId(userId);
        patientService.save(patient);
        Date date = new DateTime(patient.getBirthdate()).toDate();
        patient.setBirthdate(date);
        return Result.ok();
    }

    // 删除
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Long id) {
        patientService.removeById(id);
        return Result.ok();
    }

    // 根据id回显信息
    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Long id) {
        Patient patient = patientService.getPatientInfoById(id);
        return Result.ok().data("patient", patient);
    }

    // 修改
    @PutMapping("/update")
    public Result update(@RequestBody Patient patient) {
        patientService.updateById(patient);
        return Result.ok();
    }

    // 查询
    @GetMapping("/all")
    public Result findAll(@RequestHeader String token) {
        Long userId = JWTHelper.getUserId(token);
        List<Patient> list = patientService.findAll(userId);
        return Result.ok().data("list", list);
    }
}
