package com.xin.yygh;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xin.yygh.hosp.model.user.UserInfo;
import com.xin.yygh.hosp.vo.user.UserInfoQueryVo;
import com.xin.yygh.user.service.UserInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AdminUserInfoTest {

    @Autowired
    private UserInfoService userInfoService;

    @Test
    public void getPageList() {
        Page<UserInfo> pageList = userInfoService.getPageList(1, 10, new UserInfoQueryVo());
        List<UserInfo> records = pageList.getRecords();
        for (UserInfo record : records) {
            System.out.println(record);
        }
    }
}
