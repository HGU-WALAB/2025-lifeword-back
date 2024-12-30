package com.project.bibly_be.controller;

import com.project.bibly_be.dto.request.UserRequestDTO;
import com.project.bibly_be.dto.response.ApiResponseDTO;
import com.project.bibly_be.dto.response.UserResponseDTO;
import com.project.bibly_be.service.UserService;
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
            @RequestParam("kakaoUid") String kakaoUid) {  // 헤더에서 파라미터로 변경
        UserResponseDTO.VerifyResponse response = userService.verifyUser(kakaoUid);
        return ApiResponseDTO.success("사용자 확인 완료", response);
    }
}