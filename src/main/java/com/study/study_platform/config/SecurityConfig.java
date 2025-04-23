package com.study.study_platform.config; // <-- 여러분의 패키지에 맞게 수정하세요

import com.study.study_platform.security.JwtTokenFilter;
import com.study.study_platform.security.JwtTokenUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class) // JWT 필터를 기본 UsernamePasswordAuthenticationFilter 전에 추가

                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/login")  // 로그인 페이지 URL 지정
                                .loginProcessingUrl("/perform_login")  // 로그인 처리 URL
                                .permitAll()  // 로그인 페이지는 모든 사용자에게 허용
                )
                .httpBasic(httpBasic ->
                        httpBasic
                                .realmName("MyApp")  // HTTP Basic 인증에서 사용할 realm 이름
                );

        return http.build();
    }

    // AuthenticationManager 설정 (필요 시)
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .build();
    }
}
