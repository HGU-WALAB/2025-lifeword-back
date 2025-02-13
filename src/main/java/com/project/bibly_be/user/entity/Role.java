package com.project.bibly_be.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("ROLE_USER", "일반 사용자"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String key;       // Spring Security에서 권한을 사용할 키
    private final String title;     // 권한의 설명

    @Override
    public String toString() {
        return this.key;
    }
}
