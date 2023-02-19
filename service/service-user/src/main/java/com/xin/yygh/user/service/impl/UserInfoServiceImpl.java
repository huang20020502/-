package com.xin.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xin.yygh.client.DictService;
import com.xin.yygh.common.exception.YyghException;
import com.xin.yygh.common.jwt.JWTHelper;
import com.xin.yygh.hosp.enums.AuthStatusEnum;
import com.xin.yygh.hosp.model.acl.User;
import com.xin.yygh.hosp.model.user.Patient;
import com.xin.yygh.hosp.model.user.UserInfo;
import com.xin.yygh.hosp.vo.user.LoginVo;
import com.xin.yygh.hosp.vo.user.UserInfoQueryVo;
import com.xin.yygh.user.service.PatientService;
import com.xin.yygh.user.service.UserInfoService;
import com.xin.yygh.user.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author xin
* @description 针对表【user_info(用户表)】的数据库操作Service实现
* @createDate 2023-02-11 17:02:01
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService{

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PatientService patientService;



    // 用户登录逻辑
    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();

        // 判断手机号 和 验证码是否为空
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new YyghException(20001,"手机号或验证码错误");
        }

        // 比较验证码
        String redisCode = (String)redisTemplate.opsForValue().get(phone);
        if (StringUtils.isEmpty(redisCode) || !code.equals(redisCode)) {
            throw new YyghException(20001,"验证码错误");
        }

        String openid = loginVo.getOpenid();

        // 判断是否为绑定手机号
        if (StringUtils.isEmpty(openid)) {
            // 表示手机号登录
            // 构建查询条件
            QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("phone", phone);
            UserInfo userInfo = userInfoMapper.selectOne(queryWrapper);

            // 判断是否存在当前用户
            if (userInfo == null) {
                userInfo = new UserInfo();
                // 不存在，添加到用户
                userInfo.setPhone(phone);
                userInfo.setStatus(1);

                this.save(userInfo);
            }
        } else {
            // 绑定手机号

            // 1. 查询之前是否使用过手机号登录过
            QueryWrapper<UserInfo> phoneQueryWrapper = new QueryWrapper<>();
            phoneQueryWrapper.eq("phone", phone);
            UserInfo phoneUserInfo = userInfoMapper.selectOne(phoneQueryWrapper);


            QueryWrapper<UserInfo> wxQueryWrapper = new QueryWrapper<>();
            wxQueryWrapper.eq("openid", openid);
            UserInfo userInfo1 = userInfoMapper.selectOne(wxQueryWrapper);
            if (phoneUserInfo == null) {
                userInfo1.setPhone(phone);
                userInfoMapper.updateById(userInfo1);
            } else {
                // 之前用过手机号登录
                phoneUserInfo.setOpenid(userInfo1.getOpenid());
                phoneUserInfo.setNickName(userInfo1.getNickName());
                userInfoMapper.updateById(phoneUserInfo);
                userInfoMapper.deleteById(userInfo1.getId());
            }
        }

        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        UserInfo userInfo = userInfoMapper.selectOne(queryWrapper);

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

        // 生成token
        String token = JWTHelper.createToken(userInfo.getId(), (String) map.get("username"));
        map.put("token", token);

        return map;
    }

    @Override
    public UserInfo getUserInfoById(String token) {
        // 解析token
        Long userId = JWTHelper.getUserId(token);
        // 查询
        UserInfo userInfo = userInfoMapper.selectById(userId);
        String status = AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus());
        Map map = new HashMap();
        map.put("authStatusString", status);
        userInfo.setParam(map);
        return userInfo;
    }

    @Override
    public Page<UserInfo> getPageList(Integer pageNum, Integer pageSize, UserInfoQueryVo userInfoQueryVo) {
        Page page = new Page(pageNum, pageSize);

        String keyword = userInfoQueryVo.getKeyword();
        Integer status = userInfoQueryVo.getStatus();
        Integer authStatus = userInfoQueryVo.getAuthStatus();
        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin();
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd();

        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();

        if (!StringUtils.isEmpty(keyword)) {
            queryWrapper.like("name", keyword).or().eq("phone", keyword);
        }
        if (!StringUtils.isEmpty(status)) {
            queryWrapper.eq("status", status);
        }
        if (!StringUtils.isEmpty(authStatus)) {
            queryWrapper.eq("auth_status", authStatus);
        }
        if (!StringUtils.isEmpty(createTimeBegin)) {
            queryWrapper.ge("create_time", createTimeBegin);
        }
        if (!StringUtils.isEmpty(createTimeEnd)) {
            queryWrapper.ge("update_time", createTimeEnd);
        }

        Page pageInfo = this.page(page, queryWrapper);

        List<UserInfo> records = pageInfo.getRecords();
        records.stream().forEach(userInfo -> this.packageUserInfo(userInfo));
        return pageInfo;
    }

    @Override
    public Map getDetailById(Long id) {
        // 获取用户信息
        UserInfo userInfo = userInfoMapper.selectById(id);

        // 获取用户下的就诊人信息
        QueryWrapper<Patient> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", id);
        List<Patient> list = patientService.list(queryWrapper);

        // 封装就诊人信息
        list.stream().forEach(patient -> patientService.packagePatient(patient));

        Map map = new HashMap();
        map.put("userInfo", userInfo);
        map.put("patients", list);
        return map;
    }

    private void packageUserInfo(UserInfo userInfo) {
        Integer authStatus = userInfo.getAuthStatus();
        String  authStatusString = AuthStatusEnum.getStatusNameByStatus(authStatus);
        Map map = new HashMap();
        map.put("authStatusString", authStatusString);
        map.put("statusString", userInfo.getStatus() == 1 ? "正常" : "锁定");
        userInfo.setParam(map);
    }
}




