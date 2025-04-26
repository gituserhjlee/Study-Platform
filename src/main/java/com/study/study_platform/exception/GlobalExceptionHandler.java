package com.study.study_platform.exception;

import com.study.study_platform.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateIdException.class)
    public ResponseEntity<ResponseDto<?>> handleDuplicateId(DuplicateIdException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ResponseDto<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<?>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseDto<>(false, "서버 오류", null));
    }
}
