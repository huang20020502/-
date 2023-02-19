package com.xin.yygh.user.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xin.yygh.common.Result;
import com.xin.yygh.hosp.model.acl.User;
import com.xin.yygh.hosp.model.user.UserInfo;
import com.xin.yygh.hosp.vo.user.UserInfoQueryVo;
import com.xin.yygh.user.service.UserInfoService;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/userinfo")
public class AdminUserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @PutMapping("/auth/{id}/{status}")
    public Result updateAuthStatus(@PathVariable Long id,
                                   @PathVariable Integer status) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setAuthStatus(status);

        userInfoService.updateById(userInfo);

        return Result.ok();
    }

    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Long id) {
        Map map = userInfoService.getDetailById(id);
        return Result.ok().data(map);
    }

    @PutMapping("/lock/{id}/{status}")
    public Result updateStatus(@PathVariable Long id,
                               @PathVariable Integer status) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setStatus(status);
        userInfoService.updateById(userInfo);
        return Result.ok();
    }

    @GetMapping("/{pageNum}/{pageSize}")
    public Result getPageList(@PathVariable Integer pageNum,
                              @PathVariable Integer pageSize,
                              UserInfoQueryVo userInfoQueryVo) {
        Page<UserInfo> page = userInfoService.getPageList(pageNum, pageSize, userInfoQueryVo);
        return Result.ok().data("total", page.getTotal()).data("list", page.getRecords());
    }
}
