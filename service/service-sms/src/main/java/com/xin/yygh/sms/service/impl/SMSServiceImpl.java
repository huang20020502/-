package com.xin.yygh.sms.service.impl;

import com.xin.yygh.sms.service.SMSService;
import com.xin.yygh.sms.util.HttpUtils;
import com.xin.yygh.sms.util.RandomUtil;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SMSServiceImpl implements SMSService {

    @Autowired
    private RedisTemplate redisTemplate;

    private final static String HOST = "http://dingxin.market.alicloudapi.com";
    private final static String PATH = "/dx/sendSms";
    private final static String METHOD = "POST";
    private final static String APPCODE = "a318e21759cf4945bf2fc1a9e50bbefd";

    @Override
    public boolean sendCode(String phone) {
        // 为了避免短信次数的消耗，判断redis中是否有验证码。有就不用发送
        String code = (String)redisTemplate.opsForValue().get(phone);
        if (!StringUtils.isEmpty(code)) {
            return true;
        }

        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + APPCODE);

        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", phone);

        String fourBitRandom = RandomUtil.getFourBitRandom();
        querys.put("param", "code:" + fourBitRandom);
        querys.put("tpl_id", "TP1711063");
        Map<String, String> bodys = new HashMap<String, String>();
        try {
            HttpResponse response = HttpUtils.doPost(HOST, PATH, METHOD, headers, querys, bodys);
            System.out.println(response.toString());

            // 将生成的验证码添加到redis中
            redisTemplate.opsForValue().set(phone, fourBitRandom, 20, TimeUnit.DAYS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
