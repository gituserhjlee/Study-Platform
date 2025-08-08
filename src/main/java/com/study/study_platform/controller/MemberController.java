package com.study.study_platform.controller;

import com.study.study_platform.dto.MemberInfoDto;
import com.study.study_platform.dto.ResponseDto;
import com.study.study_platform.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor

public class MemberController {

    @GetMapping("/me")
    public ResponseDto<MemberInfoDto> getMyInfo(@AuthenticationPrincipal Member member) {
        if (member == null) {
            return new ResponseDto<>(false, "로그인 정보가 없습니다.", null);
        }

        MemberInfoDto data = new MemberInfoDto(member.getId(), member.getDisplayUsername(), member.getRole());
        return new ResponseDto<>(true, "내 정보 조회 성공", data);
    }


}
