package com.study.study_platform.security;

import io.jsonwebtoken.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenUtil {

    private String secretKey = "secret-key"; // 보안 키 (환경 변수나 별도의 설정 파일에서 관리해야 합니다)

    // JWT 생성
    public String generateToken(Authentication authentication) {
        System.out.println("JWT 생성");

        String username = authentication.getName();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 만료 시간 1일
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    // JWT에서 사용자 이름 추출
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // JWT 토큰의 유효성 검사
    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    // 만료된 토큰인지 검사
    private boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    // 토큰의 만료일 추출
    private Date getExpirationDateFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
}
