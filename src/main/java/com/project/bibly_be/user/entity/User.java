package com.project.bibly_be.user.entity;

import javax.persistence.*;

import com.project.bibly_be.bookmark.entity.Bookmark;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; //오류 생기면 String으로 받아오기.

   /* @Column(name = "oauth_provider")
    private String oauthProvider;*/
    @Column(name = "oauth_provider", columnDefinition = "JSON")
    private String oauthProvider;


    // 소셜 로그인 식별자 (kakaoUid, googleUid 등 통합)
    @Column(name = "oauth_uid", columnDefinition = "JSON")
    private String oauthUid;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "is_admin")
    private Boolean isAdmin;

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


    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Bookmark> bookmarks;

    @Builder
    public User(String oauthProvider, String oauthUid, Boolean isAdmin, String password, String email, String name, String contact,String church, String job, String place) {
        this.id = UUID.randomUUID();
        this.oauthProvider = oauthProvider;
        this.oauthUid = oauthUid;
        this.email = email;
        this.password =password;
        this.isAdmin=isAdmin;
        this.church= church;
        this.job=job;
        this.place=place;
        this.name = name;
        this.contact = contact;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}