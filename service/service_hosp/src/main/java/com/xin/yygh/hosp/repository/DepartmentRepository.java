package com.xin.yygh.hosp.repository;


import com.xin.yygh.hosp.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DepartmentRepository extends MongoRepository<Department,String> {
    Department findByHoscodeAndDepcode(String hoscode, String depcode);


    List<Department> findByHoscode(String hoscode);
}
