package com.back.global.exception;

public class DomainException extends RuntimeException {
    String resultCode;
    public DomainException(String resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }
}
