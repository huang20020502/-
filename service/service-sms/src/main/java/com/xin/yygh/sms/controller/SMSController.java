package com.xin.yygh.sms.controller;

import com.xin.yygh.common.Result;
import com.xin.yygh.sms.service.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/sms")
public class SMSController {

    @Autowired
    private SMSService smsService;

    @PostMapping("/send/{phone}")
    public Result sendShortMessage(@PathVariable String phone) {
        boolean flag = smsService.sendCode(phone);
        if (flag) {
            return Result.ok();
        } else {
            return Result.error();
        }
    }
}
