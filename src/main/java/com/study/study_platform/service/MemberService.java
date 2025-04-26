package com.study.study_platform.service;

import com.study.study_platform.dto.SignUpRequestDto;
import com.study.study_platform.dto.SignUpResponseDto;
import com.study.study_platform.entity.Member;
import com.study.study_platform.exception.DuplicateIdException;
import com.study.study_platform.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
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

}
