package com.xin.yygh.hosp.service;

import com.xin.yygh.hosp.model.hosp.Hospital;
import com.xin.yygh.hosp.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


public interface HospitalService {


    boolean saveHospital(HttpServletRequest request);

    Hospital getByHoscode(String hoscode);

    Page<Hospital> getPageList(Integer pageNum, Integer pageSize, String dictCode, HospitalQueryVo hospitalQueryVo);

    void updateStatusById(String id, Integer status);

    Hospital getHospitalById(String id);

    List<Hospital> getListByHosname(String hosname);

    List<Hospital> findAll();

    List<Hospital> getListByAddress(HospitalQueryVo hospitalQueryVo);

    List<Hospital> getListByHosTypeAndDistrictCode(String hosType, String districtCode);

    Hospital getDetailByHoscode(String hoscode);
}
