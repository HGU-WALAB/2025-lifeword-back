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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID Id; //오류 생기면 String으로 받아오기.

    @Column(name = "oauth_provider")
    private String oauthProvider;

    // 소셜 로그인 식별자 (kakaoUid, googleUid 등 통합)
    @Column(name = "oauth_uid", unique = true)
    private String oauthUid;


    @Column(name = "email")
    private String email;

    @Column(name="church")
    private String church;

    @Column(name="job")
    private String job;

    @Column(name="place")
    private String place;

    @Column(name = "name")
    private String name;

    @Column(name = "contact")
    private String contact;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public User(String oauthProvider, String oauthUid, String email, String name, String contact,String church, String job, String place) {
        this.Id = UUID.randomUUID();
        this.oauthProvider = oauthProvider;
        this.oauthUid = oauthUid;
        this.email = email;
        this.church= church;
        this.job=job;
        this.place=place;
        this.name = name;
        this.contact = contact;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}