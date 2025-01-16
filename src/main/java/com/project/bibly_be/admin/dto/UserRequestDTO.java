package com.project.bibly_be.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRequestDTO {
    private String oauthProvider;  // 예: "kakao", "google", "apple"
    private String oauthUid;
    private String name;
    private String contact;
    private String email;
    private String password;
    private String job;
    private String place;
    private String church;


}