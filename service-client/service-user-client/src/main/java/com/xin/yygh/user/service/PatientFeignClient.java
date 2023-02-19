package com.xin.yygh.user.service;

import com.xin.yygh.hosp.model.user.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-user")
public interface PatientFeignClient {

    @GetMapping("/user/userinfo/patient/{patientId}")
    Patient getById(@PathVariable(value = "patientId") Long patientId);
}
