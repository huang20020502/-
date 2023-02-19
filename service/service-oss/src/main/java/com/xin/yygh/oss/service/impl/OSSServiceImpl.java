package com.xin.yygh.oss.service.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.xin.yygh.oss.prop.OSSProperties;
import com.xin.yygh.oss.service.OSSService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@Service
public class OSSServiceImpl implements OSSService {

    @Autowired
    private OSSProperties ossProperties;

    @Override
    public String uploadFile(MultipartFile file) {

        String endpoint = ossProperties.getEndpoint();
        String accessKeyId = ossProperties.getKeyid();
        String accessKeySecret = ossProperties.getKeysecret();
        String bucketName = ossProperties.getBucketname();

        String fileName = (new DateTime().toString("yyyy/MM/dd") + UUID.randomUUID() + file.getOriginalFilename()).replace("-", "");

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            InputStream inputStream = file.getInputStream();
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, inputStream);
            // 设置该属性可以返回response。如果不设置，则返回的response为空。
            putObjectRequest.setProcess("true");
            // 创建PutObject请求。
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            // 如果上传成功，则返回200。
            System.out.println(result.getResponse().getStatusCode());
            // 返回上传成功的图片路径
            String url = "https://"+bucketName+"."+endpoint+"/"+fileName;
            return url;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return null;
    }
}
