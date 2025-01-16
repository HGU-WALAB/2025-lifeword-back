package com.project.bibly_be.user.repository;

import com.project.bibly_be.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByOauthUid(String oauthUid); // google kakao case
    Optional<User> findByEmail(String email);

    Optional<User> findUsersByEmailAndOauthProvider(String email, String oauthProvider);


    // 관리자용 메서드
    Page<User> findAll(Pageable pageable); // 전체 사용자 페이징 조회
    List<User> findByNameContaining(String name); // 이름으로 검색
    List<User> findByJob(String job); // 직업으로 검색
    List<User> findByChurch(String church); // 교회로 검색

}