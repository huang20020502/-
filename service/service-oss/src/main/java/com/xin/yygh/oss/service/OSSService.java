package com.xin.yygh.oss.service;

import org.springframework.web.multipart.MultipartFile;

public interface OSSService {


    String uploadFile(MultipartFile file);
}
