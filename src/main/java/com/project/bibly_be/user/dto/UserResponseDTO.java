package com.project.bibly_be.user.dto;

import com.project.bibly_be.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponseDTO {
    private Long id;
    private String kakao_uid;
    private LocalDateTime created_at;

    public static UserResponseDTO from(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .kakao_uid(user.getKakaoUid())
                .created_at(user.getCreatedAt())
                .build();
    }

    @Getter
    @Builder
    public static class VerifyResponse {
        private boolean exists;
        private Long user_id;
        private String kakao_uid;
    }
}