package com.study.study_platform.controller;

import com.study.study_platform.dto.*;
import com.study.study_platform.entity.Member;
import com.study.study_platform.security.JwtTokenUtil;
import com.study.study_platform.service.MemberService;
import com.study.study_platform.service.RedisService;
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
    private final RedisService redisService;

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

            Authentication authentication = authenticationManager.authenticate(authenticationToken);//인증수행. memberService에서 정의한 loadUserByUsername 메서드 호출
            SecurityContextHolder.getContext().setAuthentication(authentication);//인증성공시 인증정보를 저장

            String jwtToken = jwtTokenUtil.generateToken(authentication);//jwt토큰생성

            Member member = (Member) authentication.getPrincipal();
            String role = member.getRole().name();

            // 온라인 사용자 수 증가
            redisService.incrementOnlineUsers();

            LoginResponseDto loginResponseDto = new LoginResponseDto(jwtToken, member.getUsername(), role);
            return new ResponseDto(true, "로그인 성공", loginResponseDto);

        } catch (AuthenticationException e) {
            e.printStackTrace();
            return new ResponseDto(false, "아이디 또는 비밀번호가 잘못되었습니다.", null);
        }

    }

    @PostMapping("/logout")
    public ResponseDto logout(@RequestBody LogoutRequestDto logoutRequestDto) {
        try {
            String token = logoutRequestDto.getToken();
            
            // 토큰 유효성 검사
            if (token == null || !jwtTokenUtil.validateToken(token)) {
                return new ResponseDto(false, "유효하지 않은 토큰입니다.", null);
            }
            
            // 토큰 만료 시간 계산 (현재 시간부터 토큰 만료까지)
            long expirationTime = jwtTokenUtil.getExpirationDateFromToken(token).getTime() - System.currentTimeMillis();
            
            // Redis 블랙리스트에 토큰 추가
            redisService.addToBlacklist(token, expirationTime);
            
            // 온라인 사용자 수 감소
            redisService.decrementOnlineUsers();
            
            // Spring Security Context에서 인증 정보 제거
            SecurityContextHolder.clearContext();
            
            return new ResponseDto(true, "로그아웃 성공", null);
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto(false, "로그아웃 실패", null);
        }
    }
}
