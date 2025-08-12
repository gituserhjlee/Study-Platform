package com.study.study_platform.security;

import com.study.study_platform.exception.TokenBlacklistedException;
import com.study.study_platform.service.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // 로그인/회원가입/로그아웃 요청에는 필터를 적용하지 않음
        String uri = request.getRequestURI();
        if ("/api/auth/login".equals(uri) || "/api/auth/signup".equals(uri) || "/api/auth/logout".equals(uri)) {
            filterChain.doFilter(request, response);  // 인증 필요 없는 엔드포인트는 필터 건너뜀
            return;
        }

        // 요청 헤더에서 JWT 토큰 추출
        String token = getJwtFromRequest(request);

        if (token != null && jwtTokenUtil.validateToken(token)) {
            // Redis 블랙리스트 확인
            if (redisService.isBlacklisted(token)) {
                // 블랙리스트된 토큰이면 인증하지 않고 다음 필터로 전달
                filterChain.doFilter(request, response);
                return;
            }
            
            String username = jwtTokenUtil.getUsernameFromToken(token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username); //사용자정보 조회

            // 인증 객체 생성
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            // Spring Security Context에 인증 정보 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    // 요청에서 JWT 추출하는 메서드
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
