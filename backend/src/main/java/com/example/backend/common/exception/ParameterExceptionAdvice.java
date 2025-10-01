package com.example.backend.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ParameterExceptionAdvice {

    @ExceptionHandler(CommonExceptionTemplate.class)
    public ResponseEntity<Map<String, Object>> handlerMemberException(MissingServletRequestParameterException ex) {
        String message = ex.getMessage();

        return ResponseEntity.badRequest().body(Map.of("message", message));

    }
}
