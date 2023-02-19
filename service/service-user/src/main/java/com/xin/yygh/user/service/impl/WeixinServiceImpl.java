package com.xin.yygh.user.service.impl;

import com.xin.yygh.user.service.WeixinService;
import com.xin.yygh.user.util.HttpClientUtils;
import org.springframework.stereotype.Service;

@Service
public class WeixinServiceImpl implements WeixinService {

    @Override
    public String getUserInfo(String accessToken, String openid) throws Exception {
        // =ACCESS_TOKEN=OPENID
        // 拼接地址
        StringBuilder append = new StringBuilder().append("https://api.weixin.qq.com/sns/userinfo")
                                                  .append("?access_token=%s")
                                                  .append("&openid=%s");
        String url = String.format(append.toString(), accessToken, openid);

        return HttpClientUtils.get(url);
    }
}
