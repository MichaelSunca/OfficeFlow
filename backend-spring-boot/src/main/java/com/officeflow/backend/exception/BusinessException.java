package com.officeflow.backend.exception;

import lombok.Getter;

/**
 * 自定义业务异常，用于处理主动抛出的校验错误
 */
@Getter
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = 400; // 业务错误通常用 400
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}