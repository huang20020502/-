package com.xin.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.xin.yygh.hosp.model.hosp.Department;
import com.xin.yygh.hosp.repository.DepartmentRepository;
import com.xin.yygh.hosp.service.DepartmentService;
import com.xin.yygh.hosp.utils.HttpRequestUtils;
import com.xin.yygh.hosp.vo.hosp.DepartmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void saveDepartment(Map<String, Object> map) {
        // 1. 转换成department对象
        String str = JSONObject.toJSONString(map);
        Department department = JSONObject.parseObject(str, Department.class);

        // 2. 判断mongodb中是否存储
        String hoscode = department.getHoscode();
        String depcode = department.getDepcode();
        Department result = departmentRepository.findByHoscodeAndDepcode(hoscode,depcode);

        if (result == null) {
            // 添加操作
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
        } else {
            // 修改操作
            department.setId(result.getId());
            department.setCreateTime(result.getCreateTime());
            department.setIsDeleted(result.getIsDeleted());
            department.setUpdateTime(new Date());
        }
        departmentRepository.save(department);
    }

    @Override
    public Page getPage(Map<String, String[]> map) {
        Map<String, Object> resultMap = HttpRequestUtils.switchMap(map);

        // 获取当前页数 和 显示页数
        Integer pageNum = Integer.parseInt((String) resultMap.get("page"));
        Integer pageSize = Integer.parseInt((String) resultMap.get("limit"));

        // 构造分页条件
        Pageable pageable = PageRequest.of(pageNum - 1,pageSize);
        Department department = new Department();
        Example<Department> example =  Example.of(department);
        Page<Department> page = departmentRepository.findAll(example, pageable);

        return page;
    }

    @Override
    public void delete(Map<String, Object> map) {
        String hoscode  =(String) map.get("hoscode");
        String depcode =(String) map.get("depcode");

        Department department = departmentRepository.findByHoscodeAndDepcode(hoscode, depcode);

        departmentRepository.delete(department);
    }

    @Override
    public List<DepartmentVo> getDepartmentListByHoscode(String hoscode) {
        // 查出对应医院的科室信息
        List<Department> departments = departmentRepository.findByHoscode(hoscode);

        // 封装返回值
        List<DepartmentVo> bigDepartmentList = new ArrayList<>();

        // 根据大科室来进行分组
        Map<String, List<Department>> map = departments.stream().collect(Collectors.groupingBy(Department::getBigcode));
        for (Map.Entry<String, List<Department>> entry : map.entrySet()) {
            // 小科室的信息
            List<DepartmentVo> childDepartmentList = new ArrayList<>();

            // 封装大科室下的所有小科室信息
            List<Department> departmentList = entry.getValue();
            for (Department department : departmentList) {
                DepartmentVo childDepartment = new DepartmentVo();

                childDepartment.setDepname(department.getDepname());
                childDepartment.setDepcode(department.getDepcode());

                childDepartmentList.add(childDepartment);
            }

            // key : 大科室的code
            // value : 小科室
            DepartmentVo bigDepartment = new DepartmentVo();
            bigDepartment.setDepcode(entry.getKey());
            bigDepartment.setDepname(departmentList.get(0).getBigname());
            bigDepartment.setChildren(childDepartmentList);

           bigDepartmentList.add(bigDepartment);
        }
        return bigDepartmentList;
    }
}
