package com.xin.yygh.hosp.repository;

import com.xin.yygh.hosp.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface HospitalRepository extends MongoRepository<Hospital,String> {
    Hospital findByHoscode(String hoscode);

    List<Hospital> findByHosnameLike(String hosname);
}
