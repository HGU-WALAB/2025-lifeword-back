package com.project.bibly_be.user.service;

import com.project.bibly_be.user.dto.UserRequestDTO;
import com.project.bibly_be.user.dto.UserResponseDTO;
import com.project.bibly_be.user.entity.User;
import com.project.bibly_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

    // 사용자 생성
    public UserResponseDTO createUser(UserRequestDTO request) {
        User user = User.builder()
                .oauthProvider(request.getOauthProvider())
                .oauthUid(request.getOauthUid())
                .email(request.getEmail())
                .name(request.getName())
                .contact(request.getContact())
                .build();

        User savedUser = userRepository.save(user);
        return UserResponseDTO.from(savedUser);
    }

    // 사용자 존재 여부 확인
    @Transactional(readOnly = true)
    public UserResponseDTO.VerifyResponse verifyUser(String oauthUid) {
        User user = userRepository.findByOauthUid(oauthUid)
                .orElse(null);

        return UserResponseDTO.VerifyResponse.builder()
                .exists(user != null)
                // userId = UUID
                .userId(user != null ? user.getId() : null)
                .oauthUid(oauthUid)
                .build();
    }
}