package com.project.bibly_be.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GoogleUserInfo {
    private String email;

    @JsonProperty("name")
    private String name;

    @JsonProperty("id")
    private String id; // 구글에서 제공하는 고유 ID
}
