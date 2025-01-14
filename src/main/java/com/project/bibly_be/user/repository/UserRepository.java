package com.project.bibly_be.user.repository;

import com.project.bibly_be.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByOauthUid(String oauthUid);
}