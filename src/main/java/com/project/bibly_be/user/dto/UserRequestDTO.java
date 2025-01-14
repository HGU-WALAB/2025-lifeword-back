package com.project.bibly_be.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRequestDTO {
    private String oauthProvider;  // 예: "kakao", "google", "apple"
    private String oauthUid;
    private String name;
    private String contact;
    private String email;


}