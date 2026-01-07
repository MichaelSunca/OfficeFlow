package com.officeflow.backend.common;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 这个类会自动捕获 Spring Security 抛出的异常。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 捕获：用户名或密码错误
    @ExceptionHandler(BadCredentialsException.class)
    public Result<?> handleBadCredentialsException(BadCredentialsException e) {
        return Result.error(401, "用户名或密码错误，请重试");
    }

    // 捕获：用户不存在
    @ExceptionHandler(UsernameNotFoundException.class)
    public Result<?> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return Result.error(401, "该用户不存在");
    }

    // 捕获：其他所有未预料的异常
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        e.printStackTrace(); // 在控制台打印日志，方便你调试
        return Result.error(500, "服务器开小差了: " + e.getMessage());
    }
}