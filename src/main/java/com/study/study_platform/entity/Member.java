package com.study.study_platform.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 추가
public class Member {
    @Id
    private String id;
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role{
        ROLE_USER,
        ROLE_ADMIN
    }

    public Member(String id, String username, String password, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
