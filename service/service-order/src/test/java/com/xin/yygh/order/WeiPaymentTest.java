package com.xin.yygh.order;

import com.xin.yygh.order.service.WeiPayService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WeiPaymentTest {

    @Autowired
    private WeiPayService weiPayService;

    @Test
    public void testWeiPayTest() {
        weiPayService.creatNative(36L);
    }
}
