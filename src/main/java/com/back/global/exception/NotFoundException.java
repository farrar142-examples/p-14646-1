package com.back.global.exception;

public class NotFoundException extends DomainException {
    public NotFoundException(String message) {
        super("404", message);
    }
}
