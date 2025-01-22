package com.project.bibly_be.user.service;

import com.project.bibly_be.user.dto.UserRequestDTO;
import com.project.bibly_be.user.dto.UserResponseDTO;
import com.project.bibly_be.user.entity.User;
import com.project.bibly_be.user.repository.UserRepository;
import com.project.bibly_be.user.util.OauthProviderUtil;
import com.project.bibly_be.user.util.OauthUidUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

    // 사용자 생성
    public UserResponseDTO createUser(UserRequestDTO request) {
        boolean isBibly = false;

        // 1) 필수값 체크 로직
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

        // 2) 이메일 중복 체크
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            // ---- (A) 이미 존재하는 사용자 ----
            User user = existingUser.get();

            // ---- (B) 기존 oauthProvider를 JSON 배열로 관리 ----
            List<String> providerList = OauthProviderUtil.jsonToList(user.getOauthProvider());
            int idx = OauthProviderUtil.getProviderIndex(request.getOauthProvider());
            providerList.set(idx, request.getOauthProvider());
            user.setOauthProvider(OauthProviderUtil.listToJson(providerList));

            // ---- (C) Provider별 UID를 JSON으로 관리 ----
            List<String> uidList = OauthUidUtil.jsonToList(user.getOauthUid());
            uidList.set(idx,request.getOauthUid());
            user.setOauthUid(OauthUidUtil.listToJson(uidList));

            userRepository.save(user);
            return UserResponseDTO.from(user);

        } else {
            // ---- (D) 새 사용자 생성 ----
            // Provider 리스트 초기화
            List<String> providerList = OauthProviderUtil.initProviderList();
            int idx = OauthProviderUtil.getProviderIndex(request.getOauthProvider());
            providerList.set(idx, request.getOauthProvider());

            // UID 맵 초기화
            List<String> uidList = OauthUidUtil.initUidList();
            uidList.set(idx, request.getOauthUid());

            User user = User.builder()
                    .oauthProvider(OauthProviderUtil.listToJson(providerList)) // JSON 배열 저장
                    .oauthUid(OauthUidUtil.listToJson(uidList))                  // JSON 객체 저장
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
    }



    // 사용자 존재 여부 확인 (kakao/google case only)
    @Transactional(readOnly = true)
    public UserResponseDTO.VerifyResponse verifyUserSns(String oauthUid) { //String oauthProvider
        User user = userRepository.findByOauthUid(oauthUid)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없음요"));

        return UserResponseDTO.VerifyResponse.builder()
                .exists(true)
                .userId(user.getId())
                .job(user.getJob())
                .isAdmin(user.getIsAdmin())
                .build();
    }


    @Transactional(readOnly = true)
    public UserResponseDTO.VerifyResponse verifyUserBibly(String email, String password) {
        // 1) bibly로 가입한 사용자를 찾는다
        Optional<User> biblyUserOpt = userRepository.findUsersByEmailAndOauthProvider(email, "bibly");

        if (biblyUserOpt.isPresent()) {
            // --- (A) 이미 bibly로 가입된 경우 ---
            User biblyUser = biblyUserOpt.get();

            // (A-1) 패스워드 검증
            if (!biblyUser.getPassword().equals(password)) {
                throw new InputMismatchException("비밀번호 틀림요");
            }

            // (A-2) 로그인 성공 → 응답 반환
            return UserResponseDTO.VerifyResponse.builder()
                    .exists(true)
                    .userId(biblyUser.getId())
                    .job(biblyUser.getJob())
                    .isAdmin(biblyUser.getIsAdmin())
                    .build();

        } else {
            // --- (B) bibly로 가입된 레코드가 없음 ---
            // (B-1) 혹시 다른 Provider(kakao/google)로 가입된 사용자 존재하는지 확인
            Optional<User> anyUserOpt = userRepository.findByEmail(email);

            if (anyUserOpt.isPresent()) {
                // (B-2) 이미 kakao/google 등으로 가입된 이메일

                // ===== 미리 주석 처리해둔 "미래 로직" =====
                // 나중에 카카오/구글로 가입한 사용자도 마이페이지에서 비밀번호를 설정하면
                // bibly 로그인 가능하게 하고 싶을 때, 아래 주석을 해제하고 로직을 완성합니다.
            /*
            User anyUser = anyUserOpt.get();
            // Password가 이미 설정되어 있고, 그게 입력한 password와 같다면 bibly로도 로그인 허용
            if (anyUser.getPassword() != null && anyUser.getPassword().equals(password)) {
                return UserResponseDTO.VerifyResponse.builder()
                        .exists(true)
                        .userId(anyUser.getId())
                        .job(anyUser.getJob())
                        .isAdmin(anyUser.getIsAdmin())
                        .build();
            }
            */

                // 주석 해제 전까지는 "이미 가입된 이메일" 예외로 처리
                throw new IllegalStateException("이미 다른 Provider로 가입된 이메일이므로 bibly 로그인 불가");
            } else {
                // (B-3) 완전히 없는 이메일 → 일반 "사용자 없음" 처리
                throw new UsernameNotFoundException("해당 사용자를 찾을 수 없음요");
            }
        }
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
