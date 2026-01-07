package com.officeflow.backend.common;

import com.officeflow.backend.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器：统一处理业务、校验及系统级异常
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获自定义业务异常：如 SN 重复、资产状态不对等
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务规则限制: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 捕获 Bean Validation 校验异常 (针对 @NotBlank 等注解)
     * 优化：支持同时返回多个字段的校验错误信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining("; ")); // 多个错误用分号隔开

        log.warn("请求参数非法: {}", message);
        return Result.error(400, message);
    }

    /**
     * 捕获身份验证异常
     * 提示：Spring Security 在找不到用户时有时会抛出 InternalAuthenticationServiceException
     */
    @ExceptionHandler({BadCredentialsException.class, InternalAuthenticationServiceException.class})
    public Result<?> handleAuthException(Exception e) {
        log.warn("登录失败尝试: {}", e.getMessage());
        // 统一模糊提示，防止用户枚举攻击
        return Result.error(401, "用户名或密码错误，请重试");
    }

    /**
     * 捕获系统级的未知异常（最后一道防线）
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        // 只有这里需要打印详细的堆栈信息 e，用于排查 Bug
        log.error("【系统崩溃】核心链路异常: ", e);
        return Result.error(500, "服务器开小差了，请联系管理员");
    }
}