package com.xin.yygh.user.controller;


import com.xin.yygh.common.Result;
import com.xin.yygh.common.jwt.JWTHelper;
import com.xin.yygh.hosp.enums.AuthStatusEnum;
import com.xin.yygh.hosp.model.user.UserInfo;
import com.xin.yygh.hosp.vo.user.LoginVo;
import com.xin.yygh.hosp.vo.user.UserAuthVo;
import com.xin.yygh.user.service.UserInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user/userinfo")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @PutMapping("/save")
    public Result updateUserInfo(@RequestHeader String token,  UserAuthVo userAuthVo) {
        Long userId = JWTHelper.getUserId(token);
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userAuthVo, userInfo);

        userInfo.setId(userId);
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());

        userInfoService.updateById(userInfo);
        return Result.ok();
    }

    @GetMapping("/auth/getUserInfo")
    public Result getUserInfo(@RequestHeader String token) {
        UserInfo userInfo = userInfoService.getUserInfoById(token);
        return Result.ok().data("userInfo", userInfo);
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginVo loginVo) {
        Map<String,Object> map = userInfoService.login(loginVo);
        return Result.ok().data(map);
    }



}
