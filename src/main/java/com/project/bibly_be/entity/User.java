package com.project.bibly_be.entity;

import javax.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kakao_uid", unique = true)
    private String kakaoUid;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public User(String kakaoUid) {
        this.kakaoUid = kakaoUid;
        this.createdAt = LocalDateTime.now();
    }
}