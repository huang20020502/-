package com.xin.yygh.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 控制层同一返回对象
 */
@Getter
public class Result implements Serializable {

    private Integer code;

    private Boolean success;

    private String message;

    private Map<String,Object> data = new HashMap<>();

    // 对外不提供构造方法
    private Result() { }

    public static Result error() {
        Result result = new Result();
        result.code = ResultEnum.ERROR.getCode();
        result.message = ResultEnum.ERROR.getMessage();
        result.success = ResultEnum.ERROR.getFlag();

        return result;
    }

    public static Result ok() {
        Result result = new Result();
        result.code = ResultEnum.SUCCESS.getCode();
        result.message = ResultEnum.SUCCESS.getMessage();
        result.success = ResultEnum.SUCCESS.getFlag();
        return result;
    }

    public Result code(Integer code) {
        this.code = code;
        return this;
    }

    public Result message(String message) {
        this.message = message;
        return this;
    }

    public Result success(Boolean success) {
        this.success = success;
        return this;
    }

    public Result data(String key, Object value) {
        data.put(key,value);
        return this;
    }

    public Result data(Map<String,Object> data) {
        this.data = data;
        return this;
    }

}
