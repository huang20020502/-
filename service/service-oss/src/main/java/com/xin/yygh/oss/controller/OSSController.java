package com.xin.yygh.oss.controller;

import com.xin.yygh.common.Result;
import com.xin.yygh.oss.service.OSSService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "上传文件操作")
@RestController
@RequestMapping("/user/oss/file")
public class OSSController {

    @Autowired
    private OSSService ossService;

    @PostMapping("/upload")
    public Result upload(MultipartFile file) {
        String url = ossService.uploadFile(file);
        return Result.ok().data("url", url);
    }
}
