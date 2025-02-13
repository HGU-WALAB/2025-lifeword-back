package com.project.bibly_be.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoUserInfo {
    private String email;
    private String name;
    private String id; // 카카오에서 제공하는 고유 ID
}
