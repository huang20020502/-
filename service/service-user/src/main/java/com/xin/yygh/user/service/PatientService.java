package com.xin.yygh.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xin.yygh.hosp.model.user.Patient;

import java.util.List;

/**
* @author xin
* @description 针对表【patient(就诊人表)】的数据库操作Service
* @createDate 2023-02-14 11:20:28
*/
public interface PatientService extends IService<Patient> {

    List<Patient> findAll(Long userId);

    Patient getPatientInfoById(Long id);

    void packagePatient(Patient patient);
}
