package com.project.bibly_be.user.service;

import com.project.bibly_be.user.dto.UserRequestDTO;
import com.project.bibly_be.user.dto.UserResponseDTO;
import com.project.bibly_be.user.entity.User;
import com.project.bibly_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.InputMismatchException;
import java.util.List;
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
        if(request.getOauthProvider()!=null) {
            //  Bibly case
            if(request.getOauthProvider().equals("bibly")){ // password ==null check included
                // isBibly -> true
                isBibly = true;
                if (request.getPassword()==null||request.getEmail() == null || request.getName() == null ||
                        request.getContact() == null || request.getChurch() == null || request.getJob() == null || request.getPlace() == null) {
                    throw new IllegalArgumentException("필수 입력 값이 누락되었습니다. bibly"); // uid 제외 필드 값중 뭔가가 빠졌으면 익셉션 쓰로
                }
            }
            // kakao Google case]
            else { // OauthUid ==null check
                if (request.getOauthUid() == null || request.getEmail() == null || request.getName() == null ||
                        request.getContact() == null || request.getChurch() == null || request.getJob() == null || request.getPlace() == null) {
                    throw new IllegalArgumentException("필수 입력 값이 누락되었습니다. kakao/google "); // password 제외 필드 값중 뭔가가 빠졌으면 익셉션 쓰로
                }
            }

        }
        else  throw new IllegalArgumentException("OauthProvider 값 누락됨유");
        // bibly sign in 이 아닐때민 체크 ( bibly sign in  중복 유저는 이매일 중복 확인으로 이미 확인함> verify api
        if(!isBibly) {
            boolean exists = userRepository.findByOauthUid(request.getOauthUid()).isPresent();
            if (exists) {
                throw new IllegalArgumentException("이미 존재하는 사용자에유");    // 나중에 id 중복확일 할때 더 추가 할거임유
            }
        }
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
                .build();

        User savedUser = userRepository.save(user);
        return UserResponseDTO.from(savedUser);
    }

    // 사용자 존재 여부 확인 (kakao/google case only)
    @Transactional(readOnly = true)
    public UserResponseDTO.VerifyResponse verifyUserSns(String oauthUid) {
        User user = userRepository.findByOauthUid(oauthUid).orElse(null);
                //.orElseThrow(()->new UsernameNotFoundException("해당 사용자를 찾을 수 없음요"));

        return UserResponseDTO.VerifyResponse.builder()
                .exists(user != null) // exists(true)
                .userId(user != null ? user.getId() : null)
                .job(  user!=null ?  user.getJob() : null)
                .isAdmin(user != null ? user.getIsAdmin(): false)
                .build();
    }

    // 사용자 존재 여부 확인 (biblycase only)
    @Transactional(readOnly = true)
    public UserResponseDTO.VerifyResponse verifyUserBibly(String email, String password) {
        User user = userRepository.findUsersByEmailAndOauthProvider(email,"bibly")//.orElse(null);
                .orElseThrow(()->new UsernameNotFoundException("해당 사용자를 찾을 수 없음요"));
        if(!user.getPassword().equals(password)) throw new InputMismatchException("비밀 번호 틀림요"); // security 문제 있을까?
        return UserResponseDTO.VerifyResponse.builder()
                .exists(true) // exists(true)
                .userId( user.getId() )//.userId(user != null ? user.getId() : null)
                .job(  user.getJob())
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
        List<User> users = userRepository.findByJob(job);
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
        List<User> users = userRepository.findByChurch(church);
        if (users.isEmpty()) {
            throw new IllegalArgumentException("'" + church + "' 교회를 가진 사용자가 없습니다.");
        }
        return users;
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
