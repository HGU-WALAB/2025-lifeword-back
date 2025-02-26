package com.project.bibly_be.user.service;

import com.project.bibly_be.user.dto.*;
import com.project.bibly_be.user.entity.Role;
import com.project.bibly_be.user.entity.User;
import com.project.bibly_be.user.repository.UserRepository;
import com.project.bibly_be.user.util.OauthProviderUtil;
import com.project.bibly_be.user.util.OauthUidUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate = new RestTemplate();


    /**
     * 📌 4. OAuth 로그인 공통 로직 (카카오 & 구글)
     */
    private UserResponseDTO processOAuthUser(String email, String name, String provider, String providerUid) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            // 기존 oauthProvider 업데이트
            List<String> providerList = OauthProviderUtil.jsonToList(user.getOauthProvider());
            int idx = OauthProviderUtil.getProviderIndex(provider);
            providerList.set(idx, provider);
            user.setOauthProvider(OauthProviderUtil.listToJson(providerList));

            // 기존 oauthUid 업데이트
            List<String> uidList = OauthUidUtil.jsonToList(user.getOauthUid());
            uidList.set(idx, providerUid);
            user.setOauthUid(OauthUidUtil.listToJson(uidList));

            userRepository.save(user);
            return UserResponseDTO.from(user);
        } else {
            // 새 사용자 생성
            List<String> providerList = OauthProviderUtil.initProviderList();
            int idx = OauthProviderUtil.getProviderIndex(provider);
            providerList.set(idx, provider);

            List<String> uidList = OauthUidUtil.initUidList();
            uidList.set(idx, providerUid);

            User newUser = User.builder()
                    .email(email)
                    .name(name)
                    .oauthProvider(OauthProviderUtil.listToJson(providerList))
                    .oauthUid(OauthUidUtil.listToJson(uidList))
                    .role(Role.USER)
                    .build();

            userRepository.save(newUser);
            return UserResponseDTO.from(newUser);
        }
    }

    /**
     * 📌 5. 카카오 사용자 정보 가져오기
     */
    private KakaoUserInfo getKakaoUserInfo(String accessToken) {
        String url = "https://kapi.kakao.com/v2/user/me";
        KakaoResponseDTO response = restTemplate.getForObject(url, KakaoResponseDTO.class);

        if (response == null || response.getKakaoAccount() == null) {
            throw new IllegalArgumentException("카카오 사용자 정보를 가져올 수 없습니다.");
        }

        return new KakaoUserInfo(response.getKakaoAccount().getEmail(), response.getKakaoAccount().getProfile().getNickname(), response.getId());
    }

    /**
     * 📌 6. 구글 사용자 정보 가져오기
     */
    private GoogleUserInfo getGoogleUserInfo(String accessToken) {
        String url = "https://www.googleapis.com/oauth2/v2/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);  // ✅ 올바른 accessToken 포함
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        System.out.println("🔍 Google API 요청 Authorization 헤더: " + headers);

        ResponseEntity<GoogleUserInfo> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, GoogleUserInfo.class
        );

        if (response.getBody() == null) {
            throw new IllegalArgumentException("❌ 구글 사용자 정보를 가져올 수 없습니다.");
        }

        return response.getBody();
    }


    /**
     * 1) 사용자 생성 (기존 로직 그대로)
     *    - 이메일 없으면 신규 가입
     *    - 이미 존재하면 provider/UID 업데이트
     */
    public UserResponseDTO createUser(UserRequestDTO request) {
        boolean isBibly = false;
        Role userRole = Role.USER;

        // 1) 필수값 체크 로직
        if (request.getOauthProvider() != null) {
            if (request.getOauthProvider().equals("bibly")) {
                isBibly = true;
                if (request.getPassword() == null || request.getEmail() == null || request.getName() == null ||
                        request.getContact() == null || request.getChurch() == null || request.getJob() == null ||
                        request.getPlace() == null) {
                    throw new IllegalArgumentException("필수 입력 값이 누락되었습니다. bibly");
                }
            } else {
                // kakao/google
                if (request.getOauthUid() == null || request.getEmail() == null || request.getName() == null ||
                        request.getContact() == null || request.getChurch() == null || request.getJob() == null ||
                        request.getPlace() == null) {
                    throw new IllegalArgumentException("필수 입력 값이 누락되었습니다. kakao/google");
                }
            }
        } else {
            throw new IllegalArgumentException("OauthProvider 값 누락됨유");
        }

        // 2) 이메일 중복 체크
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            // 이미 존재하는 사용자
            User user = existingUser.get();

            // (B) 기존 oauthProvider JSON 배열 업데이트
            List<String> providerList = OauthProviderUtil.jsonToList(user.getOauthProvider());
            int idx = OauthProviderUtil.getProviderIndex(request.getOauthProvider());
            providerList.set(idx, request.getOauthProvider());
            user.setOauthProvider(OauthProviderUtil.listToJson(providerList));

            // (C) Provider별 UID JSON 배열 업데이트
            List<String> uidList = OauthUidUtil.jsonToList(user.getOauthUid());
            uidList.set(idx, request.getOauthUid());
            user.setOauthUid(OauthUidUtil.listToJson(uidList));

            userRepository.save(user);
            return UserResponseDTO.from(user);

        } else {
            // 새 사용자 생성
            List<String> providerList = OauthProviderUtil.initProviderList();
            int idx = OauthProviderUtil.getProviderIndex(request.getOauthProvider());
            providerList.set(idx, request.getOauthProvider());

            List<String> uidList = OauthUidUtil.initUidList();
            uidList.set(idx, request.getOauthUid());

            User user = User.builder()
                    .oauthProvider(OauthProviderUtil.listToJson(providerList))
                    .oauthUid(OauthUidUtil.listToJson(uidList))
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword())) // 비밀번호 암호화 후 저장
                    .name(request.getName())
                    .contact(request.getContact())
                    .church(request.getChurch())
                    .job(request.getJob())
                    .place(request.getPlace())
                    .role(userRole)
                    .build();

            User savedUser = userRepository.save(user);
            return UserResponseDTO.from(savedUser);
        }
    }

    /**
     * 2) 사용자 존재 여부 확인 (kakao/google case only) - 읽기 전용
     */
    @Transactional(readOnly = true)
    public UserResponseDTO.VerifyResponse verifyUserSns(String email) {
        return userRepository.findByEmail(email)
                .map(user -> UserResponseDTO.VerifyResponse.builder()
                        .exists(true)
                        .userId(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .job(user.getJob())
                        .role(user.getRole())
                        .build())
                .orElseGet(() -> {
                    System.out.println("⚠️ 해당 이메일을 가진 사용자가 없음, 회원가입 필요: " + email);
                    return UserResponseDTO.VerifyResponse.builder()
                            .exists(false)  // 사용자가 없음을 표시
                            .userId(null)   // 신규 가입이므로 ID 없음
                            .name(null)     // 기본값
                            .email(email)   // 가입 시 사용할 이메일 전달
                            .job(null)
                            .role(null)
                            .build();
                });
    }

    /**
     * 3) 사용자 존재 시 Provider/UID 업데이트 (새 메서드)
     *    - 이미 가입된 이메일이면 provider/UID JSON 배열 업데이트
     *    - 없으면 예외 발생 (가입되어 있지 않음)
     */
    @Transactional
    public UserResponseDTO.VerifyResponse updateProviderIfExists(String email, String oauthProvider, String oauthUid) {
        // (A) 이메일로 사용자 조회 (없으면 예외)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없음요"));

        // (B) Provider 인덱스
        int idx = OauthProviderUtil.getProviderIndex(oauthProvider);

        // (C) 기존 oauthProvider JSON 배열 & uid JSON 배열
        List<String> providerList = OauthProviderUtil.jsonToList(user.getOauthProvider());
        List<String> uidList = OauthUidUtil.jsonToList(user.getOauthUid());

        // (D) 해당 인덱스에 이미 값이 있는지 확인
        String existingProvider = providerList.get(idx);
        String existingUid = uidList.get(idx);

        // (E) 만약 이미 값이 들어있다면 -> 예외 발생
        if (existingProvider != null && !existingProvider.isEmpty()) {
            throw new IllegalStateException(
                    String.format("이미 '%s' 값이 존재합니다. 새 Provider(%s)로 수정 불가!",
                            existingProvider, oauthProvider)
            );
        }

        // (F) 값이 없는 경우(null 또는 "")만 새로 채워넣기
        providerList.set(idx, oauthProvider);
        uidList.set(idx, oauthUid);

        user.setOauthProvider(OauthProviderUtil.listToJson(providerList));
        user.setOauthUid(OauthUidUtil.listToJson(uidList));

        // (G) DB 저장
        userRepository.save(user);

        // (H) 업데이트 후 응답 반환
        return UserResponseDTO.VerifyResponse.builder()
                .exists(true)
                .userId(user.getId())
                .job(user.getJob())
                .role(user.getRole())
                .build();
    }


    /**
     * 4) 사용자 존재 여부 확인 (bibly case only) - 기존 로직
     */
    @Transactional(readOnly = true)
    public UserResponseDTO.VerifyResponse verifyUserBibly(String email, String password) {
        Optional<User> biblyUserOpt = userRepository.findUsersByEmailAndOauthProvider(email, "bibly");

        if (biblyUserOpt.isPresent()) {
            User biblyUser = biblyUserOpt.get();


            if (!passwordEncoder.matches(password, biblyUser.getPassword())) {
                throw new InputMismatchException("비밀번호 틀림요");
            }
            System.out.println("✅ 비밀번호 일치!");

            return UserResponseDTO.VerifyResponse.builder()
                    .exists(true)
                    .userId(biblyUser.getId())
                    .name(biblyUser.getName())
                    .email(biblyUser.getEmail())
                    .job(biblyUser.getJob())
                    .role(biblyUser.getRole())
                    .build();

        } else {
            Optional<User> anyUserOpt = userRepository.findByEmail(email);
            if (anyUserOpt.isPresent()) {

                User anyUser = anyUserOpt.get();
                if (anyUser.getPassword() != null && passwordEncoder.matches(password, anyUser.getPassword())) { // 여기 패스워드 검증 확인해야함.
                    return UserResponseDTO.VerifyResponse.builder()
                            .exists(true)
                            .userId(anyUser.getId())
                            .name(anyUser.getName())
                            .email(anyUser.getEmail())
                            .job(anyUser.getJob())
                            .role(anyUser.getRole())
                            .build();
                }

                throw new IllegalStateException("테스트 에러. 501 error");
            } else {
                throw new UsernameNotFoundException("해당 사용자를 찾을 수 없음요");
            }
        }
    }

    // SetUserPassword  마이 패이지에서 유저 패스워드 생성
    @Transactional
    public UserResponseDTO.VerifyResponse setUserPassword(String email, String password) {
        Optional<User> users = userRepository.findByEmail(email);

        if (users.isPresent()) {
            User user = users.get();

            // 기존 비밀번호 확인 (디버깅용)
            System.out.println("🔍 기존 암호화된 비밀번호: " + user.getPassword());

            // Bibly 계정이 없는 경우, Provider 추가
            List<String> providerList = OauthProviderUtil.jsonToList(user.getOauthProvider());
            int idx = OauthProviderUtil.getProviderIndex("bibly");

            // **사용자 OAuth Provider 업데이트**
            if (providerList.get(idx) == null) {
                providerList.set(idx, "bibly");
                user.setOauthProvider(OauthProviderUtil.listToJson(providerList));
            }

            // 기존 비밀번호와 동일한지 확인
            if (passwordEncoder.matches(password, user.getPassword())) {
                throw new IllegalArgumentException("기존 비밀번호와 동일한 비밀번호입니다.");
            }

            //  비밀번호 암호화 후 저장
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);

            System.out.println("비밀번호 변경 완료!");

            return UserResponseDTO.VerifyResponse.builder()
                    .exists(true)
                    .userId(user.getId())
                    .job(user.getJob())
                    .role(user.getRole())
                    .build();

        } else {
            throw new UsernameNotFoundException("email과 일치하는 회원이 없습니다.");
        }
    }


    // -------------------- (이하 Admin / 기타 메서드 동일) -------------------- //

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponseDTO::from)
                .collect(Collectors.toList());
    }

    public void deleteUser(UUID userId) {
        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            throw new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. (id: " + userId + ")");
        }
        userRepository.deleteById(userId);
    }

    @Transactional(readOnly = true)
    public List<User> searchUsersByName(String name) {
        List<User> users = userRepository.findByNameContaining(name);
        if (users.isEmpty()) {
            throw new IllegalArgumentException("해당 이름 '" + name + "'을(를) 가진 사용자가 없습니다.");
        }
        return users;
    }

    @Transactional(readOnly = true)
    public List<User> searchUsersByJob(String job) {
        List<User> users = userRepository.findByJobContaining(job);
        if (users.isEmpty()) {
            throw new IllegalArgumentException("'" + job + "' 직업을 가진 사용자가 없습니다.");
        }
        return users;
    }

    @Transactional(readOnly = true)
    public List<User> searchUsersByChurch(String church) {
        List<User> users = userRepository.findByChurchContaining(church);
        if (users.isEmpty()) {
            throw new IllegalArgumentException("'" + church + "' 교회를 가진 사용자가 없습니다.");
        }
        return users;
    }

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

    @Transactional(readOnly = true)
    public boolean verifyUserByEmail(String email) {
        //boolean exists = userRepository.findUsersByEmailAndOauthProvider(email, "bibly").isPresent();
        boolean exists = userRepository.findByEmail(email).isPresent(); //"email" is unique key , so only one user will return or false,
        if(!exists) {
            return false;
        }
        return true;
    }

    //기존 db에 있는 password 암호화 하는 것. (일회성임)
    @Transactional
    public void updateUserPasswords() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            String currentPassword = user.getPassword();

            if (currentPassword == null || currentPassword.isEmpty()) {
                System.out.println("비밀번호가 없는 계정: " + user.getEmail() + " → 암호화 제외");
                continue; // 비밀번호가 없는 계정은 변경하지 않음
            }

            // 현재 비밀번호가 암호화되지 않은 경우 (평문인지 체크)
            // BCrypt 해싱된 비밀번호는 `$2a$`로 시작
            if (!currentPassword.startsWith("$2a$")) {
                String encryptedPassword = passwordEncoder.encode(currentPassword);
                user.setPassword(encryptedPassword);
                userRepository.save(user); // 업데이트 실행
                System.out.println("비밀번호 암호화 완료: " + user.getEmail());
            } else {
                System.out.println("이미 암호화된 비밀번호: " + user.getEmail());
            }
        }
    }

}


