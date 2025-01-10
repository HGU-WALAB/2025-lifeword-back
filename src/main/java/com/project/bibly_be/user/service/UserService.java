package com.project.bibly_be.user.service;

import com.project.bibly_be.user.dto.UserRequestDTO;
import com.project.bibly_be.user.dto.UserResponseDTO;
import com.project.bibly_be.user.entity.User;
import com.project.bibly_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserResponseDTO createUser(UserRequestDTO request) {
        User user = User.builder()
                .kakaoUid(request.getKakaoUid())
                .build();
        User savedUser = userRepository.save(user);
        return UserResponseDTO.from(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO.VerifyResponse verifyUser(String kakaoUid) {
        User user = userRepository.findByKakaoUid(kakaoUid)
                .orElse(null);

        return UserResponseDTO.VerifyResponse.builder()
                .exists(user != null)
                .user_id(user != null ? user.getId() : null)
                .kakao_uid(kakaoUid)
                .build();
    }
}