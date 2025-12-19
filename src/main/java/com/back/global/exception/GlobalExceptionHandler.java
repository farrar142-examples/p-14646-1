package com.back.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    record ErrorResponse(
            String message,
            String resultCode
    ){}
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException e){
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                e.resultCode
        );
        return ResponseEntity
                .badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e){
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                e.resultCode
        );
        return ResponseEntity
                .status(404)
                .body(errorResponse);
    }
}
