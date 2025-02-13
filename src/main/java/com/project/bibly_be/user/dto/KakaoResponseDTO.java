package com.project.bibly_be.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KakaoResponseDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class KakaoAccount {
        private String email;
        private KakaoProfile profile;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class KakaoProfile {
        private String nickname;
    }
}
