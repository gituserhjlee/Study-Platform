package com.study.study_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SignUpRequestDto   {
    private String id;
    private String username;
    private String password;
}
