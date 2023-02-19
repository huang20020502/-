package com.xin.yygh.user.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xin.yygh.hosp.model.user.UserInfo;
import org.apache.ibatis.annotations.Mapper;

/**
* @author xin
* @description 针对表【user_info(用户表)】的数据库操作Mapper
* @createDate 2023-02-11 17:02:01
* @Entity com.xin.yygh.user.entity.UserInfo
*/
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {

}




