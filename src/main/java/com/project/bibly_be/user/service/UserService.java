package com.project.bibly_be.user.service;

import com.project.bibly_be.user.dto.UserRequestDTO;
import com.project.bibly_be.user.dto.UserResponseDTO;
import com.project.bibly_be.user.entity.User;
import com.project.bibly_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        if(request.getOauthProvider()!=null) {
            //  Bibly case
            if(request.getOauthProvider().equals("bibly")){ // password ==null check included
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
        boolean exists = userRepository.findByOauthUid(request.getOauthUid()).isPresent();
        if(exists){
            throw new IllegalArgumentException("이미 존재하는 사용자에유");    // 나중에 id 중복확일 할때 더 추가 할거임유
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

    // 사용자 존재 여부 확인
    @Transactional(readOnly = true)
    public UserResponseDTO.VerifyResponse verifyUser(String oauthUid) {
        User user = userRepository.findByOauthUid(oauthUid)
                .orElseThrow(()->new UsernameNotFoundException("해당 사용자를 찾을 수 없음요"));

        return UserResponseDTO.VerifyResponse.builder()
                .exists(user != null) // exists(true)
                .userId(user != null ? user.getId() : null)
                .build();
    }
}
