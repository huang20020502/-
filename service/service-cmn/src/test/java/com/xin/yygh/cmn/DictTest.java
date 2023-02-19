package com.xin.yygh.cmn;

import com.xin.yygh.cmn.service.DictService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DictTest {

    @Autowired
    private DictService dictService;

    @Test
    public void getNameByPidAndValueTest() {
        Long pid = dictService.getIdByDictCode("Hostype");
        String str = dictService.getNameByPidAndValue(pid, 1L);
        System.out.println(str);
    }

    @Test
    public void getNameByValue() {
        String str = dictService.getNameByValue(99103L);
        System.out.println(str);
    }
}
