package com.xin.yygh.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xin.yygh.common.Result;
import com.xin.yygh.common.exception.YyghException;
import com.xin.yygh.common.jwt.JWTHelper;
import com.xin.yygh.hosp.model.user.UserInfo;
import com.xin.yygh.user.component.WeixinProperties;
import com.xin.yygh.user.service.UserInfoService;
import com.xin.yygh.user.service.WeixinService;
import com.xin.yygh.user.util.HttpClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user/userinfo/wx")
public class WeixinController {
    @Autowired
    private WeixinProperties properties;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private WeixinService weixinService;


    @ResponseBody
    @GetMapping("/login")
    public Result wxLogin() throws UnsupportedEncodingException {
        HashMap<String, Object> map = new HashMap<>();
        String url = URLEncoder.encode(properties.getRedirectUri(), "UTF-8");
        map.put("appid", properties.getAppid());
        map.put("redirect_uri", url);
        map.put("scope", properties.getScope());
        map.put("state", System.currentTimeMillis() + "");
        return Result.ok().data(map);
    }

   @RequestMapping("/callback")
    public String callBack(String code, String state) throws Exception {
        // 请求微信服务器获取 open_id
       // SECRETCODE

       // 拼接微信获取open_id的接口
       StringBuilder append = new StringBuilder().append("https://api.weixin.qq.com/sns/oauth2/access_token")
                                                 .append("?appid=%s")
                                                 .append("&secret=%s")
                                                 .append("&code=%s")
                                                 .append("&grant_type=authorization_code");
       // 填充占位符
       String url = String.format(append.toString(), properties.getAppid(), properties.getSecret(), code);
       System.out.println(url);

       // 发送请求
       String result = HttpClientUtils.get(url);

       // 格式化返回结果
       JSONObject jsonObject = JSONObject.parseObject(result);
       String openid = jsonObject.getString("openid");
       String accessToken = jsonObject.getString("access_token");

       System.out.println("openid = " + openid);
       System.out.println("accessToken = " + accessToken);

       // 查询数据库中是否有当前用户
       QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
       queryWrapper.eq("openid", openid);
       UserInfo userInfo = userInfoService.getOne(queryWrapper);

       if (userInfo == null) {
           // 第一次登录
           userInfo = new UserInfo();
           userInfo.setOpenid(openid);
           userInfo.setStatus(1);

           // 获取微信的用户信息
           String weixinUserInfo = weixinService.getUserInfo(accessToken, openid);

           // 解析信息
           Map map = JSONObject.parseObject(weixinUserInfo, Map.class);
           String nickname = (String)map.get("nickname");
           userInfo.setNickName(nickname);

           userInfoService.save(userInfo);
       }

       // 判断用户是否为异常状态
       if (userInfo.getStatus() != 1) {
           throw new YyghException(20001,"账号异常");
       }

       // 将用户名和token返回给前端
       Map<String, Object> map = new HashMap<>();
       map.put("name", "");
       if (!StringUtils.isEmpty(userInfo.getName()) && StringUtils.isEmpty(map.get("name"))) {
           map.put("name", userInfo.getName());
       }
       if (!StringUtils.isEmpty(userInfo.getNickName()) && StringUtils.isEmpty(map.get("name"))) {
           map.put("name", userInfo.getNickName());
       }
       if ( StringUtils.isEmpty(map.get("name"))) {
           map.put("name", userInfo.getPhone());
       }

       if (StringUtils.isEmpty(userInfo.getPhone())) {
           map.put("openid", openid);
       } else  {
           map.put("openid", "");
       }

       // 创建token
       String token = JWTHelper.createToken(userInfo.getId(), (String) map.get("name"));
       map.put("token", token);
       return "redirect:http://localhost:3000/weixin/callback?token="+map.get("token")+ "&openid="+map.get("openid")+"&name="+URLEncoder.encode((String)map.get("name"),"utf-8");
    }
}
