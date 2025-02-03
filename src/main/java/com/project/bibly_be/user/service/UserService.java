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

    /**
     * 1) 사용자 생성 (기존 로직 그대로)
     *    - 이메일 없으면 신규 가입
     *    - 이미 존재하면 provider/UID 업데이트
     */
    public UserResponseDTO createUser(UserRequestDTO request) {
        boolean isBibly = false;

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

    /**
     * 2) 사용자 존재 여부 확인 (kakao/google case only) - 읽기 전용
     */
    @Transactional(readOnly = true)
    public UserResponseDTO.VerifyResponse verifyUserSns(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없음요"));

        return UserResponseDTO.VerifyResponse.builder()
                .exists(true)
                .userId(user.getId())
                .job(user.getJob())
                .isAdmin(user.getIsAdmin())
                .build();
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
                .isAdmin(user.getIsAdmin())
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
            if (!biblyUser.getPassword().equals(password)) {
                throw new InputMismatchException("비밀번호 틀림요");
            }
            return UserResponseDTO.VerifyResponse.builder()
                    .exists(true)
                    .userId(biblyUser.getId())
                    .job(biblyUser.getJob())
                    .isAdmin(biblyUser.getIsAdmin())
                    .build();

        } else {
            Optional<User> anyUserOpt = userRepository.findByEmail(email);
            if (anyUserOpt.isPresent()) {

                User anyUser = anyUserOpt.get();
                if (anyUser.getPassword() != null && anyUser.getPassword().equals(password)) {
                    return UserResponseDTO.VerifyResponse.builder()
                            .exists(true)
                            .userId(anyUser.getId())
                            .job(anyUser.getJob())
                            .isAdmin(anyUser.getIsAdmin())
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

        if(users.isPresent()) {
            User user = users.get();


            // if user doesnt have bibily account yet,
           List<String> providerList = OauthProviderUtil.jsonToList(user.getOauthProvider());
           int idx = OauthProviderUtil.getProviderIndex("bibly");


            if (providerList.get(idx) == null ){ //"null" 이 아님 **, null 객채임
               //throw new IllegalStateException("providerList.get(idx): "+providerList.get(idx)+" idx"+idx); //debug
               // basically same as,
               // providerList.get(2)  is null value -> then put bibily in ouath provider.set(3)

               providerList.set(idx, "bibly");
               user.setOauthProvider(OauthProviderUtil.listToJson(providerList)); // setting user Oauth Provider[3] bibly


           }
            if(user.getPassword().equals(password)) {
                throw new IllegalArgumentException("기존 비밀번호와 동일한 비밀번호 입니다.");
            }

            user.setPassword(password);
            userRepository.save(user);

            return UserResponseDTO.VerifyResponse.builder()
                    .exists(true) //
                    .userId(user.getId())
                    .job(user.getJob())
                    .isAdmin(user.getIsAdmin())
                    .build();

        }else{
            throw  new UsernameNotFoundException("email과 일치하는 회원이 없습니다.");
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
}
