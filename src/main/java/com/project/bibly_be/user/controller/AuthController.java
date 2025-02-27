package com.project.bibly_be.user.controller;

import com.project.bibly_be.admin.dto.ApiResponseDTO;
import com.project.bibly_be.user.dto.UserResponseDTO;
import com.project.bibly_be.user.security.CustomUserDetails;
import com.project.bibly_be.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.project.bibly_be.user.security.JwtAuthenticationFilter;
import com.project.bibly_be.user.security.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")

public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            UserService userService,
            JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> request, HttpServletResponse response) {
        String code = request.get("code");

        if (code == null || code.isEmpty()) {
            return ResponseEntity.badRequest().body("Google Authorization Code is required.");
        }

        // ✅ Google OAuth 토큰 요청
        String tokenUrl = "https://oauth2.googleapis.com/token";
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", "925011581212-dk3lgg6dgktsourut3vo3ef1qi2ofg1c.apps.googleusercontent.com");
        params.add("client_secret", "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");
        params.add("redirect_uri", "http://localhost:3000/eax9952/auth");
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(tokenUrl, HttpMethod.POST, requestEntity, Map.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = responseEntity.getBody();
                String accessToken = (String) responseBody.get("access_token");

                // ✅ 사용자 정보 가져오기
                String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";
                HttpHeaders authHeaders = new HttpHeaders();
                authHeaders.set("Authorization", "Bearer " + accessToken);
                HttpEntity<String> authRequest = new HttpEntity<>(authHeaders);

                ResponseEntity<Map> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, authRequest, Map.class);
                Map<String, Object> userInfo = userInfoResponse.getBody();
                String email = (String) userInfo.get("email");

                // 사용자 확인 (회원 여부 체크)
                UserResponseDTO.VerifyResponse userResponse = userService.verifyUserSns(email);

                // JWT 생성
                if (userResponse.isExists()) {
                    String jwt = jwtUtil.generateToken(email);

                    // JWT를 HttpOnly 쿠키에 저장
                    ResponseCookie jwtCookie = ResponseCookie.from("jwt", jwt)
                            .httpOnly(true)
                            .secure(false)
                            .sameSite("Strict")
                            .path("/")
                            .maxAge(60 * 60 * 24) // 1일 (초 단위)
                            .build();
                    response.setHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
                }
                System.out.println("✅ 응답 데이터: " + userResponse);

                return ResponseEntity.ok(userResponse);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Google 로그인 실패");
            }
        } catch (Exception e) {
            System.out.println("❌ Google 인증 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Google 인증 중 오류 발생: " + e.getMessage());
        }
    }




    /**
     * [2] 카카오 로그인 처리 (세션 유지 포함)
     */
    @PostMapping("/login/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestBody Map<String, String> request, HttpServletResponse response) {
        String code = request.get("code");

        if (code == null || code.isEmpty()) {
            return ResponseEntity.badRequest().body("카카오 Authorization Code is required.");
        }

        System.out.println("✅ 카카오 Authorization Code: " + code);

        // 카카오 OAuth 토큰 요청
        String tokenUrl = "https://kauth.kakao.com/oauth/token";
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "87ba1491a0bee12f60f85d9ad8caebd4");
        params.add("redirect_uri", "http://localhost:3000/eax9952/auth");
        params.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(tokenUrl, HttpMethod.POST, requestEntity, Map.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = responseEntity.getBody();
                String accessToken = (String) responseBody.get("access_token");
                System.out.println("✅ 카카오 Access Token: " + accessToken);

                // 사용자 정보 가져오기
                String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
                HttpHeaders authHeaders = new HttpHeaders();
                authHeaders.set("Authorization", "Bearer " + accessToken);
                HttpEntity<String> authRequest = new HttpEntity<>(authHeaders);

                ResponseEntity<Map> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, authRequest, Map.class);
                Map<String, Object> userInfo = userInfoResponse.getBody();
                Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");
                String email = (String) kakaoAccount.get("email");

                // 사용자 정보 확인 (회원 여부 체크)
                UserResponseDTO.VerifyResponse userResponse = userService.verifyUserSns(email);

                if (userResponse.isExists()) {
                    String jwt = jwtUtil.generateToken(email);

                    // JWT를 HttpOnly 쿠키에 저장
                    ResponseCookie jwtCookie = ResponseCookie.from("jwt", jwt)
                            .httpOnly(true)
                            .secure(false)
                            .sameSite("Strict")
                            .path("/")
                            .maxAge(60 * 60 * 24) // 1일 (초 단위)
                            .build();
                    response.setHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
                }

                System.out.println("✅ 응답 데이터: " + userResponse);

                return ResponseEntity.ok(userResponse);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("카카오 로그인 실패");
            }
        } catch (Exception e) {
            System.out.println("❌ 카카오 인증 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("카카오 인증 중 오류 발생: " + e.getMessage());
        }
    }




    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password, HttpServletResponse response) {
        try {
            // ✅ 사용자가 존재하는지 확인
            UserResponseDTO.VerifyResponse userResponse = userService.verifyUserBibly(email, password);

            // ✅ 사용자가 존재하면 JWT 생성
            if (userResponse.isExists()) {
                String token = jwtUtil.generateToken(email);

                // ✅ JWT를 HttpOnly 쿠키에 저장
                ResponseCookie jwtCookie = ResponseCookie.from("jwt", token)
                        .httpOnly(true)
                        .secure(false)  // HTTPS 환경에서만 사용 (로컬 테스트 시 false 가능)
                        .sameSite("Strict") // CORS 문제 방지
                        .path("/")
                        .maxAge(60 * 60 * 24) // 1일 (초 단위)
                        .build();
                response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
            }

            System.out.println("✅ 응답 데이터: " + userResponse);
            return ResponseEntity.ok(userResponse);

        } catch (Exception e) {
            System.out.println("❌ 로그인 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패: " + e.getMessage());
        }
    }


    /**
     * [3] 사용자 존재 여부 확인 (카카오 & 구글 로그인 후 호출됨)
     */
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용자 확인 완료"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    private ApiResponseDTO<UserResponseDTO.VerifyResponse> verifyUserSns(String email) {
        try {
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 확인 완료"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    private ApiResponseDTO<UserResponseDTO.VerifyResponse> verifyUserBibly(String email, String password) {
        try {
            UserResponseDTO.VerifyResponse response = userService.verifyUserBibly(email, password);
            return ApiResponseDTO.success("사용자 확인 완료", response);
        } catch (UsernameNotFoundException e) {
            return ApiResponseDTO.error(e.getMessage(), HttpStatus.NOT_FOUND.value());
        } catch (Exception e) {
            return ApiResponseDTO.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }


    @GetMapping("/check")
    public ResponseEntity<?> checkAuth(@CookieValue(value = "jwt", required = false) String jwt) {
        if (jwt == null || jwt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 없음");
        }

        if (!jwtUtil.validateToken(jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰");
        }

        String email = jwtUtil.validateAndGetEmail(jwt);
        UserResponseDTO.VerifyResponse user = userService.verifyUserSns(email);
        return ResponseEntity.ok(user);
    }




    //디버깅용 API (현재 인증 상태 출력)
    @GetMapping("/debug")
    public ResponseEntity<?> debugAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.ok("현재 인증되지 않은 상태입니다.");
        }

        return ResponseEntity.ok("현재 로그인된 사용자: " + authentication.getName() +
                " / 권한: " + authentication.getAuthorities());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        // 현재 세션 무효화
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        System.out.println("hidsfasdfafadsfdsfadsfdsafasdfassd");
        // SecurityContextHolder에서 인증 정보 제거
        SecurityContextHolder.clearContext();

        // JWT 쿠키 삭제
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/")
                // 쿠키 삭제 (0초로 설정)
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        return ResponseEntity.ok("로그아웃 성공");
    }


}

