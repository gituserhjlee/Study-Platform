package com.study.study_platform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)  // HTTP 409 상태 코드로 반환
public class DuplicateIdException extends RuntimeException {
    public DuplicateIdException(String message) {
        super(message);
    }
}
