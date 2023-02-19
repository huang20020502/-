package com.xin.yygh.hosp.controller.admin;

import com.xin.yygh.common.Result;
import com.xin.yygh.hosp.model.acl.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/user")
public class UserController {


    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        return Result.ok().data("token","admin-token");
    }

    @GetMapping("/info")
    public Result getUserInfo(String token) {
        return Result.ok().data("roles","[admin]")
                .data("introduction","I am a super administrator")
                .data("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif")
                .data("name","root");
    }

}
