package com.project.bibly_be.user.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final com.project.bibly_be.user.security.JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    //  생성자
    public JwtAuthenticationFilter(com.project.bibly_be.user.security.JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    //@Override

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // 요청 헤더의 Cookie 값을 로그로 출력
        String cookieHeader = request.getHeader("Cookie");
        System.out.println("DEBUG: Cookie header: " + cookieHeader);

        String token = getTokenFromCookie(request);
        System.out.println("DEBUG: Extracted JWT from cookie: " + token);
        if (token == null) {
            System.out.println("DEBUG: No JWT found in cookies");
        }

        if (token != null && jwtUtil.validateToken(token)) {
            String email = jwtUtil.validateAndGetEmail(token);
            System.out.println("DEBUG: JWT validated successfully. Extracted email: " + email);

            // 사용자 정보 로딩 전, 사용자 존재 여부를 로그로 출력
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            System.out.println("DEBUG: Loaded user details: " + userDetails);

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
            );
        }
        chain.doFilter(request, response);
    }


    //  JWT를 쿠키에서 가져오는 메서드
    private String getTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("jwt")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
