package com.project.bibly_be.user.dto;

import com.project.bibly_be.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class UserResponseDTO {
    private UUID id;
    private String oauthProvider;  // ex) "kakao", "google", "apple"
    private String oauthUid;       // 소셜에서 제공하는 식별자
    private String email;
    private String name;
    private String contact;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 엔티티 -> DTO 변환 메서드
    public static UserResponseDTO from(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .oauthProvider(user.getOauthProvider())
                .oauthUid(user.getOauthUid())
                .email(user.getEmail())
                .name(user.getName())
                .contact(user.getContact())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * 검증(확인) 용도로 쓰이는 서브 DTO라고 가정
     */
    @Getter
    @Builder
    public static class VerifyResponse {
        private boolean exists;
        //private Long userId; //
        private UUID userId;
        private String oauthUid;
    }
}