package com.study.study_platform.dto;

import com.study.study_platform.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SignUpResponseDto {
    private String id;
    private String username;
    private Member.Role role;
}
