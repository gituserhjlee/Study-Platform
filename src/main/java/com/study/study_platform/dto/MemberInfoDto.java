package com.study.study_platform.dto;

import com.study.study_platform.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberInfoDto {
    private String id;
    private String username; // 표시용 이름
    private Member.Role role;
}


