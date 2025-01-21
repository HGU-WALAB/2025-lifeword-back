package com.project.bibly_be.user.service;

import com.project.bibly_be.user.dto.UserRequestDTO;
import com.project.bibly_be.user.dto.UserResponseDTO;
import com.project.bibly_be.user.entity.User;
import com.project.bibly_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.minidev.json.JSONArray; //JsonArray

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

    // 사용자 생성
    public UserResponseDTO createUser(UserRequestDTO request) {
        boolean isBibly = false;

        if (request.getOauthProvider() != null) {
            // Check if it's a Bibly user
            if (request.getOauthProvider().equals("bibly")) {
                isBibly = true;

                if (request.getPassword() == null || request.getEmail() == null || request.getName() == null ||
                        request.getContact() == null || request.getChurch() == null || request.getJob() == null || request.getPlace() == null) {
                    throw new IllegalArgumentException("필수 입력 값이 누락되었습니다. bibly");
                }
            } else {
                // Check for Kakao or Google case
                if (request.getOauthUid() == null || request.getEmail() == null || request.getName() == null ||
                        request.getContact() == null || request.getChurch() == null || request.getJob() == null || request.getPlace() == null) {
                    throw new IllegalArgumentException("필수 입력 값이 누락되었습니다. kakao/google");
                }
            }
        } else {
            throw new IllegalArgumentException("OauthProvider 값 누락됨유");
        }

        // Check if the email already exists
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            User user = existingUser.get();

            // Add new provider if it doesn't already exist
            if (!request.getOauthProvider().equals(user.getOauthProvider())) {
                user.setOauthProvider(request.getOauthProvider());
                user.setOauthUid(request.getOauthUid());
                userRepository.save(user);
            }

            return UserResponseDTO.from(user);
        }

        // Create a new user if no existing email is found
        User user = User.builder()
                .oauthProvider(request.getOauthProvider())
                .oauthUid(request.getOauthUid())
                .email(request.getEmail())
                .password(request.getPassword())
                .name(request.getName())
                .contact(request.getContact())
                .church(request.getChurch())
                .job(request.getJob())
                .place(request.getPlace())
                .isAdmin(false)
                .build();

        User savedUser = userRepository.save(user);
        return UserResponseDTO.from(savedUser);
    }


    // 사용자 존재 여부 확인 (kakao/google case only)
    @Transactional(readOnly = true)
    public UserResponseDTO.VerifyResponse verifyUserSns(String oauthUid) {
        User user = userRepository.findByOauthUid(oauthUid)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없음요"));

        return UserResponseDTO.VerifyResponse.builder()
                .exists(true)
                .userId(user.getId())
                .job(user.getJob())
                .isAdmin(user.getIsAdmin())
                .build();
    }


    // 사용자 존재 여부 확인 (biblycase only)
    @Transactional(readOnly = true)
    public UserResponseDTO.VerifyResponse verifyUserBibly(String email, String password) {
        User user = userRepository.findUsersByEmailAndOauthProvider(email, "bibly")
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없음요"));

        if (!user.getPassword().equals(password)) {
            throw new InputMismatchException("비밀번호 틀림요");
        }

        return UserResponseDTO.VerifyResponse.builder()
                .exists(true)
                .userId(user.getId())
                .job(user.getJob())
                .isAdmin(user.getIsAdmin())
                .build();
    }


    /**
     * 모든 사용자 조회 (Admin 전용)
     */
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponseDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * 사용자 삭제 (Admin 전용)
     */
    public void deleteUser(UUID userId) {
        // 존재하는지 체크
        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            throw new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. (id: " + userId + ")");
        }
        userRepository.deleteById(userId);
    }

    /**
     * 이름으로 사용자 검색 (Admin 전용)
     */
    @Transactional(readOnly = true)
    public List<User> searchUsersByName(String name) {
        List<User> users = userRepository.findByNameContaining(name);
        if (users.isEmpty()) {
            throw new IllegalArgumentException("해당 이름 '" + name + "'을(를) 가진 사용자가 없습니다.");
        }
        return users;
    }

    /**
     * 직업으로 사용자 검색 (Admin 전용)
     */
    @Transactional(readOnly = true)
    public List<User> searchUsersByJob(String job) {
        List<User> users = userRepository.findByJobContaining(job);
        if (users.isEmpty()) {
            throw new IllegalArgumentException("'" + job + "' 직업을 가진 사용자가 없습니다.");
        }
        return users;
    }

    /**
     * 교회로 사용자 검색 (Admin 전용)
     */
    @Transactional(readOnly = true)
    public List<User> searchUsersByChurch(String church) {
        List<User> users = userRepository.findByChurchContaining(church);
        if (users.isEmpty()) {
            throw new IllegalArgumentException("'" + church + "' 교회를 가진 사용자가 없습니다.");
        }
        return users;
    }

    /**
     * 이메일로 사용자 검색 (Admin 전용)
     */
    @Transactional(readOnly = true)
    public List<User> searchUsersByEmail(String email) {
        List<User> users = userRepository.findByEmailContaining(email);
        if (users.isEmpty()) {
            throw new IllegalArgumentException("해당 이메일 '" + email + "'을 가진 사용자가 없습니다.");
        }
        return users;
    }
    public UserResponseDTO updateUser(UUID userId, UserRequestDTO requestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setEmail(requestDTO.getEmail());
        user.setName(requestDTO.getName());
        user.setContact(requestDTO.getContact());
        user.setChurch(requestDTO.getChurch());
        user.setJob(requestDTO.getJob());
        user.setPlace(requestDTO.getPlace());

        User updatedUser = userRepository.save(user);
        return UserResponseDTO.from(updatedUser);
    }

    // bibly 사용자가 이미 이매을을 쓰는지 여부 확인 (biblycase only: Email)
    @Transactional(readOnly = true)
    public boolean verifyUserByEmail(String email) {
        boolean exists = userRepository.findUsersByEmailAndOauthProvider(email, "bibly").isPresent();
        if (!exists) {
            return false;
            //throw new UsernameNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다.");
        }
        return true;
    }
}
