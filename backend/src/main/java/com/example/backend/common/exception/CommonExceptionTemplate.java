package com.example.backend.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CommonExceptionTemplate extends RuntimeException {

    private int code;
    private String message;
}
