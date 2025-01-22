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
     * 사용자 존재 여부 확인 API (kakao/google 전용)
     */
    @Operation(summary = "사용자 존재 여부 확인 (VerifyUser) [kakao/google] ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 확인 완료"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/verify/kakao-google") // kakao google case
    public ApiResponseDTO<UserResponseDTO.VerifyResponse> verifyUserSns(
            @RequestParam("email") String email
            // @RequestParam("oauthProvider") String oauthProvider // 필요 시 추가
    ) {
        try {
            // 현재는 oauthUid로만 찾도록 구현된 예시
            UserResponseDTO.VerifyResponse response = userService.verifyUserSns(email);
            return ApiResponseDTO.success("사용자 확인 완료", response);
        } catch (UsernameNotFoundException e) {
            return ApiResponseDTO.error(e.getMessage(), HttpStatus.NOT_FOUND.value());
        } catch (Exception e) {
            return ApiResponseDTO.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    /**
     * 사용자 존재 여부 확인 (bibly 전용)
     */
    @Operation(summary = "사용자 존재 여부 확인 (VerifyUser) [bibly]")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 확인 완료"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/verify/bibly") // bibly case
    public ApiResponseDTO<UserResponseDTO.VerifyResponse> verifyUserBibly(
            @RequestParam("email") String email,
            @RequestParam("password") String password
    ) {
        try {
            UserResponseDTO.VerifyResponse response = userService.verifyUserBibly(email, password);
            return ApiResponseDTO.success("사용자 확인 완료", response);
        } catch (UsernameNotFoundException e) {
            return ApiResponseDTO.error(e.getMessage(), HttpStatus.NOT_FOUND.value());
        } catch (Exception e) {
            return ApiResponseDTO.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    /**
     * bibly 이메일 중복 여부 확인
     */
    @Operation(summary = "모든 OauthProvider 에서 email 중복 여부 확인 [ALL] // 수정 전 /verify/bibily-emailCheck")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이매일 검색 완료 (OauthProvider 가 bibly 중 검색)"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/verify/emailCheck") // bibly case
    public boolean verifyUserByEmail(@RequestParam("email") String email) {
        try {
            return userService.verifyUserByEmail(email);
        } catch (Exception e) {
            throw new RuntimeException("서버 오류 발생");
        }
    }

    // ======================================
    // ** 새로 추가할 메서드: updateProviderIfExists **
    // ======================================
    @Operation(summary = "기존 사용자 Provider/UID 업데이트 (kakao/google/bibly)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PatchMapping("/provider")
    public ApiResponseDTO<UserResponseDTO.VerifyResponse> updateProviderIfExists(
            @RequestParam("email") String email,
            @RequestParam("oauthProvider") String oauthProvider,
            @RequestParam("oauthUid") String oauthUid
    ) {
        try {
            UserResponseDTO.VerifyResponse response =
                    userService.updateProviderIfExists(email, oauthProvider, oauthUid);
            return ApiResponseDTO.success("Provider 업데이트 완료", response);
        } catch (UsernameNotFoundException e) {
            return ApiResponseDTO.error(e.getMessage(), HttpStatus.NOT_FOUND.value());
        } catch (Exception e) {
            return ApiResponseDTO.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }


    // 마이 페이지 비번 생성/ 바꾸기
    @Operation(summary = "사용자 마이페이지에서 비번 생성/ 바꾸기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PatchMapping("/setUserPassword")
    public ApiResponseDTO<UserResponseDTO.VerifyResponse> setUserPassword(
            @RequestParam("email") String email,
            @RequestParam("password") String password
    ){
        try {
            UserResponseDTO.VerifyResponse response =
                    userService.setUserPassword(email, password);
            return ApiResponseDTO.success("Provider 업데이트 완료", response);
        } catch (UsernameNotFoundException e) {
            return ApiResponseDTO.error(e.getMessage(), HttpStatus.NOT_FOUND.value());
        } catch (Exception e) {
            return ApiResponseDTO.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }

}
