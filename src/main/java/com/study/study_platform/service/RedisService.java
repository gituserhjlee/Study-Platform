package com.study.study_platform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    
    // JWT 토큰 블랙리스트 관련 상수
    private static final String BLACKLIST_PREFIX = "blacklist:";
    private static final String SESSION_PREFIX = "session:";
    private static final String ONLINE_USERS_KEY = "online:users";
    
    /**
     * JWT 토큰을 블랙리스트에 추가 (로그아웃 시 사용)
     */
    public void addToBlacklist(String token, long expirationTime) {
        String key = BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, "blacklisted", expirationTime, TimeUnit.MILLISECONDS);
        log.info("토큰이 블랙리스트에 추가되었습니다: {}", token.substring(0, 20) + "...");
    }
    
    /**
     * JWT 토큰이 블랙리스트에 있는지 확인
     */
    public boolean isBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    
    /**
     * 사용자 세션 정보 저장
     */
    public void saveUserSession(String userId, Object userInfo, long expirationTime) {
        String key = SESSION_PREFIX + userId;
        redisTemplate.opsForValue().set(key, userInfo, expirationTime, TimeUnit.MILLISECONDS);
        log.info("사용자 세션이 저장되었습니다: {}", userId);
    }
    
    /**
     * 사용자 세션 정보 조회
     */
    public Object getUserSession(String userId) {
        String key = SESSION_PREFIX + userId;
        return redisTemplate.opsForValue().get(key);
    }
    
    /**
     * 사용자 세션 삭제
     */
    public void deleteUserSession(String userId) {
        String key = SESSION_PREFIX + userId;
        redisTemplate.delete(key);
        log.info("사용자 세션이 삭제되었습니다: {}", userId);
    }
    
    /**
     * 온라인 사용자 수 증가
     */
    public void incrementOnlineUsers() {
        redisTemplate.opsForValue().increment(ONLINE_USERS_KEY);
    }
    
    /**
     * 온라인 사용자 수 감소
     */
    public void decrementOnlineUsers() {
        redisTemplate.opsForValue().decrement(ONLINE_USERS_KEY);
    }
    
    /**
     * 현재 온라인 사용자 수 조회
     */
    public Long getOnlineUsersCount() {
        Object count = redisTemplate.opsForValue().get(ONLINE_USERS_KEY);
        return count != null ? Long.valueOf(count.toString()) : 0L;
    }
}
