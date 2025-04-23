package com.study.study_platform.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig  {
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtTokenFilter jwtTokenFilter;

    public SecurityConfig(JwtTokenUtil jwtTokenUtil, JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")) // H2 콘솔은 CSRF 예외
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())) // 프레임 허용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll() // 누구나 허용
                        .anyRequest().authenticated()) // 나머지는 인증 필요
                .formLogin(Customizer.withDefaults()); // 기본 로그인 페이지

        return http.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/login", "/signup").permitAll()  // 로그인, 회원가입은 모두 접근 허용
                                .anyRequest().authenticated()  // 그 외 요청은 인증 필요
                )
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class); // JWT 필터를 기본 UsernamePasswordAuthenticationFilter 전에 추가
        return http.build();
    }

    // AuthenticationManager 설정 (필요 시)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
