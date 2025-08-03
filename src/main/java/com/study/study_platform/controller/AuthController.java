package com.study.study_platform.controller;

import com.study.study_platform.dto.*;
import com.study.study_platform.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseDto signup(@RequestBody SignUpRequestDto signupRequestDto) {
        try {
            SignUpResponseDto signUpResponseDto = memberService.signup(signupRequestDto);
            return new ResponseDto(true, "회원가입 성공", signUpResponseDto);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto(false, "회원가입 실패", null);
        }
    }

    @PostMapping("/login")
    public ResponseDto login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            LoginResponseDto loginResponseDto = memberService.login(loginRequestDto);
            // 응답으로 JWT와 사용자 정보 (이름, Role 등) 제공
            return new ResponseDto(true, "로그인 성공", loginResponseDto);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return new ResponseDto(false, "아이디 또는 비밀번호가 잘못되었습니다.", null);
        }

    }
}
