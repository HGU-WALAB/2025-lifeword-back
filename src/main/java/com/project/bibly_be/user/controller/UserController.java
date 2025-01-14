package com.project.bibly_be.user.controller;

import com.project.bibly_be.user.dto.UserRequestDTO;
import com.project.bibly_be.bible.dto.ApiResponseDTO;
import com.project.bibly_be.user.dto.UserResponseDTO;
import com.project.bibly_be.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ApiResponseDTO<UserResponseDTO> createUser(@RequestBody UserRequestDTO request) {
        UserResponseDTO user = userService.createUser(request);
        return ApiResponseDTO.success("사용자 등록 완료", user);
    }

    @GetMapping("/verify")
    public ApiResponseDTO<UserResponseDTO.VerifyResponse> verifyUser(
            //@RequestParam("kakaoUid") String kakaoUid) {  // 헤더에서 파라미터로 변경
            @RequestParam("oauthUid") String oauthUid){
        UserResponseDTO.VerifyResponse response = userService.verifyUser(oauthUid);
        return ApiResponseDTO.success("사용자 확인 완료", response);
    }
}