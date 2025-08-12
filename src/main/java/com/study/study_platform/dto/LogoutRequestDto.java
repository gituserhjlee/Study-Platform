package com.study.study_platform.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequestDto {
    private String token; // 로그아웃할 JWT 토큰
}
