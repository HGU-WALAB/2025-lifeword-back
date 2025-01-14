package com.project.bibly_be.user.dto;

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
    private String pastor;
    private String place;
    private String church;


}