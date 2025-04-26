package com.study.study_platform.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LoginResponseDto {
    private String jwtToken;
    private String username;
    private String role;
}
