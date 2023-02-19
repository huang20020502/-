package com.xin.yygh.common.handler;


import com.xin.yygh.common.Result;
import com.xin.yygh.common.exception.YyghException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;

// 同一异常处理
@ControllerAdvice // 开启全局异常处理
@Slf4j
public class GlobalExceptionHandler {



    @ExceptionHandler(value = Exception.class) // 指定处理异常类型
    public Result error(Exception exception) {
        // 打印错误日志
        log.error(exception.getMessage());
        return Result.error().message("服务器发生异常");
    }
    
    // 当设置了父级异常 和 子级异常 子级异常处理
    @ExceptionHandler(value = SQLException.class)
    public Result SqlExceptionHandler(SQLException sqlException) {
        log.error(sqlException.getMessage());
        return Result.error().message("sql语句异常");
    }

    // 当设置了父级异常 和 子级异常 子级异常处理
    @ExceptionHandler(value = YyghException.class)
    public Result SqlExceptionHandler(YyghException ex) {
        log.error(ex.getMessage());
        return Result.error().code(ex.getCode()).message(ex.getMessage());
    }

}
