package com.study.study_platform.controller;

import com.study.study_platform.dto.ResponseDto;
import com.study.study_platform.entity.Member;
import com.study.study_platform.repository.MemberRepository;
import com.study.study_platform.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor

public class MemberController {
    private final JwtTokenUtil jwtTokenUtil;
    private final MemberRepository memberRepository;

    @GetMapping("/me")
    public ResponseDto getMyInfo(@AuthenticationPrincipal Member member) {
        if (member == null) {
            return new ResponseDto(false, "로그인 정보가 없습니다.", null);
        }

        return new ResponseDto(true, "내 정보 조회 성공", member);
    }


}
