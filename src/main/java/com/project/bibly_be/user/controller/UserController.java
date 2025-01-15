package com.project.bibly_be.user.controller;

import com.project.bibly_be.user.dto.UserRequestDTO;
import com.project.bibly_be.user.dto.UserResponseDTO;
import com.project.bibly_be.user.dto.ApiResponseDTO;
import com.project.bibly_be.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 사용자 생성 API
     */
    @Operation(summary = "새로운 사용자 등록 (CreateUser)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "사용자 등록 완료"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping
    public ApiResponseDTO<UserResponseDTO> createUser(@RequestBody UserRequestDTO request) {
        try {
            UserResponseDTO user = userService.createUser(request);
            return ApiResponseDTO.success("사용자 등록 완료", user);
        } catch (IllegalArgumentException e) {
            return ApiResponseDTO.error("필수 입력 값이 누락되었습니다.", HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            return ApiResponseDTO.error("서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    /**
     * 사용자 존재 여부 확인 API
     */
    @Operation(summary = "사용자 존재 여부 확인 (VerifyUser)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 확인 완료"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/verify")
    public ApiResponseDTO<UserResponseDTO.VerifyResponse> verifyUser(
            @RequestParam("oauthUid") String oauthUid) {
        try {
            UserResponseDTO.VerifyResponse response = userService.verifyUser(oauthUid);
            return ApiResponseDTO.success("사용자 확인 완료", response);
        } catch (UsernameNotFoundException e) {
            return ApiResponseDTO.error("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value());
        } catch (Exception e) {
            return ApiResponseDTO.error("서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
