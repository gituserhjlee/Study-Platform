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
        SignUpResponseDto signUpResponseDto = memberService.signup(signupRequestDto);
        return new ResponseDto(true, "회원가입 성공", signUpResponseDto);

    }

    @PostMapping("/login")
    public ResponseDto login(@RequestBody LoginRequestDto loginRequestDto) {
        try {

            // Authentication 객체 생성
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequestDto.getId(), loginRequestDto.getPassword());

            // 인증
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // 인증이 성공하면 SecurityContext에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // JWT 토큰 생성
            String jwtToken = jwtTokenUtil.generateToken(authentication);

            // 인증된 사용자 정보
            Member member = (Member) authentication.getPrincipal();
            String role = member.getRole().name();  // 사용자의 Role 정보

            // 응답으로 JWT와 사용자 정보 (이름, Role 등) 제공
            return new ResponseDto(true, "로그인 성공", new LoginResponseDto(jwtToken, member.getUsername(), role));
        } catch (AuthenticationException e) {
            // 로그인 실패 시 에러 메시지 반환
            return new ResponseDto(false, "아이디 또는 비밀번호가 잘못되었습니다.", null);
        }

    }
}
