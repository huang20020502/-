package com.xin.yygh.user.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xin.yygh.hosp.model.user.UserInfo;
import com.xin.yygh.hosp.vo.user.LoginVo;
import com.xin.yygh.hosp.vo.user.UserInfoQueryVo;

import java.util.Map;

/**
* @author xin
* @description 针对表【user_info(用户表)】的数据库操作Service
* @createDate 2023-02-11 17:02:01
*/
public interface UserInfoService extends IService<UserInfo> {

    Map<String, Object> login(LoginVo loginVo);

    UserInfo getUserInfoById(String token);

    Page<UserInfo> getPageList(Integer pageNum, Integer pageSize, UserInfoQueryVo userInfoQueryVo);

    Map getDetailById(Long id);
}
