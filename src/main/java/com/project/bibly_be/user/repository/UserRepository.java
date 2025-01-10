package com.project.bibly_be.user.repository;

import com.project.bibly_be.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKakaoUid(String kakaoUid);
    boolean existsByKakaoUid(String kakaoUid);
}