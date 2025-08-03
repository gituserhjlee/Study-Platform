package com.study.study_platform.service;

import com.study.study_platform.dto.*;
import com.study.study_platform.entity.Member;
import com.study.study_platform.exception.DuplicateIdException;
import com.study.study_platform.repository.MemberRepository;
import com.study.study_platform.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenUtil jwtTokenUtil;

    public SignUpResponseDto signup(SignUpRequestDto signupRequestDto) {
        String id  = signupRequestDto.getId();
        String username = signupRequestDto.getUsername();
        String password = passwordEncoder.encode(signupRequestDto.getPassword()); // 비밀번호 암호화

        if (memberRepository.existsById(id)) {
            throw new DuplicateIdException("이미 사용 중인 ID입니다.");
        }

        Member member = new Member(id, username, password, Member.Role.ROLE_USER);
        memberRepository.save(member);

        return new SignUpResponseDto(member.getId(), member.getUsername(), member.getRole());
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto){
        // Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequestDto.getId(), passwordEncoder.encode(loginRequestDto.getPassword()));

        // 인증
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 인증이 성공하면 SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT 토큰 생성
        String jwtToken = jwtTokenUtil.generateToken(authentication);

        // 인증된 사용자 정보
        Member member = (Member) authentication.getPrincipal();
        String role = member.getRole().name();  // 사용자의 Role 정보

        // 응답으로 JWT와 사용자 정보 (이름, Role 등) 제공
        return new LoginResponseDto(jwtToken, member.getUsername(), role);
    }

}
