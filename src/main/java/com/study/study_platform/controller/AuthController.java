package com.study.study_platform.controller;

import com.study.study_platform.dto.*;
import com.study.study_platform.entity.Member;
import com.study.study_platform.security.JwtTokenUtil;
import com.study.study_platform.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

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
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequestDto.getId(), loginRequestDto.getPassword());

            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwtToken = jwtTokenUtil.generateToken(authentication);

            Member member = (Member) authentication.getPrincipal();
            String role = member.getRole().name();

            LoginResponseDto loginResponseDto = new LoginResponseDto(jwtToken, member.getUsername(), role);
            return new ResponseDto(true, "로그인 성공", loginResponseDto);

        } catch (AuthenticationException e) {
            e.printStackTrace();
            return new ResponseDto(false, "아이디 또는 비밀번호가 잘못되었습니다.", null);
        }

    }
}
