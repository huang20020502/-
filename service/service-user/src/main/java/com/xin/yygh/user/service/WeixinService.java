package com.xin.yygh.user.service;

public interface WeixinService {
    String getUserInfo(String accessToken, String openid) throws Exception;
}
