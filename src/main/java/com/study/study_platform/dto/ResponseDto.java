package com.study.study_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.processing.Generated;

@Getter
@AllArgsConstructor
public class ResponseDto<T> {
    private boolean status;
    private String message;
    private T data;
}
