package com.taskflow.common.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    AUTH_FAILED(40100, "认证失败，用户名或密码错误"),
    VALIDATION_FAILED(40000, "参数校验失败");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
