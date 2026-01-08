package com.officeflow.backend.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;    // 状态码：200 成功, 401 认证失败, 500 系统错误
    private String message;  // 提示信息
    private T data;          // 数据主体

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("Success");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}