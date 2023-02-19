package com.xin.yygh.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xin.yygh.hosp.model.user.Patient;
import org.apache.ibatis.annotations.Mapper;

/**
* @author xin
* @description 针对表【patient(就诊人表)】的数据库操作Mapper
* @createDate 2023-02-14 11:20:28
* @Entity com.xin.yygh.user.Patient
*/
@Mapper
public interface PatientMapper extends BaseMapper<Patient> {

}




