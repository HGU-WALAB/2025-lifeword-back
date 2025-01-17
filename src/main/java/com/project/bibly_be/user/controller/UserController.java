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
            return ApiResponseDTO.error(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            return ApiResponseDTO.error("서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    /**
     * 사용자 존재 여부 확인 API
     */
    @Operation(summary = "사용자 존재 여부 확인 (VerifyUser) [kakao/google] ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 확인 완료"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/verify/kakao-google") // kakao google case
    public ApiResponseDTO<UserResponseDTO.VerifyResponse> verifyUser(
            @RequestParam("oauthUid") String oauthUid
            )
    {
        try {
            UserResponseDTO.VerifyResponse response = userService.verifyUserSns(oauthUid);
            return ApiResponseDTO.success("사용자 확인 완료", response);
        } catch (UsernameNotFoundException e) {
            return ApiResponseDTO.error(e.getMessage(), HttpStatus.NOT_FOUND.value());
        } catch (Exception e) {
            return ApiResponseDTO.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }


    @Operation(summary = "사용자 존재 여부 확인 (VerifyUser) [bibly]")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 확인 완료"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/verify/bibly") // bibly case
    public ApiResponseDTO<UserResponseDTO.VerifyResponse> verifyUser(
            @RequestParam("email") String email, @RequestParam("password") String password
    )
    {
        try {
            UserResponseDTO.VerifyResponse response = userService.verifyUserBibly(email, password);
            return ApiResponseDTO.success("사용자 확인 완료", response);
        } catch (UsernameNotFoundException e) {
            return ApiResponseDTO.error(e.getMessage(), HttpStatus.NOT_FOUND.value());
        } catch (Exception e) {
            return ApiResponseDTO.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Operation(summary = "OauthProvider == bibly  중 email 중복 여부 확인 [bibly]")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이매일 검색 왼료 (OauthProvider 가 bibly 중 검색)"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/verify/bibly-emailCheck") // bibly case
    public boolean verifyUserByEmail(
            @RequestParam("email") String email
    )
    {
        try {
            return userService.verifyUserByEmail(email);
            //return userService.verifyUserByEmail(email);
        } catch (Exception e) {
            throw new RuntimeException("서버 오류 발생");
        }

    }
}
