package com.project.bibly_be.user.entity;

import javax.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(name = "oauth_provider")
    private String oauthProvider;

    // 소셜 로그인 식별자 (kakaoUid, googleUid 등 통합)
    @Column(name = "oauth_uid", unique = true)
    private String oauthUid;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "contact")
    private String contact;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public User(String oauthProvider, String oauthUid, String email, String name, String contact) {
        this.oauthProvider = oauthProvider;
        this.oauthUid = oauthUid;
        this.email = email;
        this.name = name;
        this.contact = contact;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}