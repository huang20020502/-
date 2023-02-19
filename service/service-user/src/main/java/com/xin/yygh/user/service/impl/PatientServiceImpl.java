package com.xin.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xin.yygh.client.DictService;
import com.xin.yygh.hosp.model.user.Patient;
import com.xin.yygh.user.service.PatientService;
import com.xin.yygh.user.mapper.PatientMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author xin
* @description 针对表【patient(就诊人表)】的数据库操作Service实现
* @createDate 2023-02-14 11:20:28
*/
@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient>
    implements PatientService{

    @Autowired
    private PatientMapper patientMapper;

    @Resource
    private DictService dictService;

    @Override
    public List<Patient> findAll(Long userId) {
        QueryWrapper<Patient> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<Patient> list = patientMapper.selectList(queryWrapper);

        list.stream().forEach(patient -> packagePatient(patient));
        return list;
    }

    @Override
    public Patient getPatientInfoById(Long id) {
        QueryWrapper<Patient> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        Patient patient = patientMapper.selectOne(queryWrapper);

        this.packagePatient(patient);
        return patient;
    }


    public void packagePatient(Patient patient) {
        String certificatesTypeString = dictService.getNameByValue(Long.parseLong(patient.getCertificatesType()));
        String province = dictService.getNameByValue(Long.parseLong(patient.getProvinceCode()));
        String  city = dictService.getNameByValue(Long.parseLong(patient.getCityCode()));
        String district = dictService.getNameByValue(Long.parseLong(patient.getDistrictCode()));

        Map map = new HashMap();
        map.put("certificatesTypeString", certificatesTypeString);
        map.put("provinceString", province);
        map.put("cityString", city);
        map.put("cityString", city);
        map.put("districtString", district);
        String fullAddress = province + city + district;
        map.put("fullAddress", fullAddress);
        patient.setParam(map);
    }
}




