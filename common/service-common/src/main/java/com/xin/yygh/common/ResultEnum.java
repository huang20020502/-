package com.xin.yygh.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

;

// 响应类的枚举类
@AllArgsConstructor
@Getter
public enum ResultEnum {

    SUCCESS(20000,"成功",true),
    ERROR(20001,"失败",false);

    private Integer code;
    private String message;
    private Boolean flag;


}
