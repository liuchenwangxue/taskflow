package com.taskflow.common.exception;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {
    private final int code;

    public AuthException(String message, int code) {
        super(message);
        this.code = code;
    }
}
