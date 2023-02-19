package com.xin.yygh.cmn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xin.yygh.hosp.model.cmn.Dict;
import org.apache.ibatis.annotations.Mapper;

/**
* @author xin
* @description 针对表【dict(组织架构表)】的数据库操作Mapper
* @createDate 2023-02-01 19:18:43
* @Entity com.xin.yygh.cmn.entities.Dict
*/
@Mapper
public interface DictMapper extends BaseMapper<Dict> {

}




