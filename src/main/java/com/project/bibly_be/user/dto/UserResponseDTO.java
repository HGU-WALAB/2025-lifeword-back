package com.project.bibly_be.user.dto;

import com.project.bibly_be.user.entity.Role;
import com.project.bibly_be.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class UserResponseDTO {
    private UUID id;
    private String email;
    private String name;
    private String contact;
    private String church;
    private String job;
    private String place;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 엔티티 -> DTO 변환 메서드
    public static UserResponseDTO from(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .contact(user.getContact())
                .church(user.getChurch())
                .job(user.getJob())
                .place(user.getPlace())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * 검증(확인) 용도로 쓰이는 서브 DTO
     */
    @Getter
    @Builder
    public static class VerifyResponse {
        private boolean exists;
        private Role role;
        private String job;
        private String name;
        private String email;
        private UUID userId;
    }
}