package com.study.study_platform.service;

import com.study.study_platform.dto.*;
import com.study.study_platform.entity.Member;
import com.study.study_platform.exception.DuplicateIdException;
import com.study.study_platform.repository.MemberRepository;
import com.study.study_platform.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        return memberRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }
}
