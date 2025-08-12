package com.study.study_platform.controller;

import com.study.study_platform.dto.MemberInfoDto;
import com.study.study_platform.dto.ResponseDto;
import com.study.study_platform.service.MemberService;
import com.study.study_platform.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final RedisService redisService;

    @GetMapping("/info")
    public ResponseDto getMemberInfo() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            MemberInfoDto memberInfoDto = memberService.getMemberInfo(id);
            return new ResponseDto(true, "회원 정보 조회 성공", memberInfoDto);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto(false, "회원 정보 조회 실패", null);
        }
    }

    @GetMapping("/online-count")
    public ResponseDto getOnlineUsersCount() {
        try {
            Long count = redisService.getOnlineUsersCount();
            return new ResponseDto(true, "온라인 사용자 수 조회 성공", count);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto(false, "온라인 사용자 수 조회 실패", null);
        }
    }
}
