package com.xin.yygh.hosp.service;

import com.xin.yygh.hosp.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface DepartmentService {
    void saveDepartment(Map<String, Object> map);

    Page getPage(Map<String, String[]> map);

    void delete(Map<String, Object> map);

    List<DepartmentVo> getDepartmentListByHoscode(String hoscode);
}
