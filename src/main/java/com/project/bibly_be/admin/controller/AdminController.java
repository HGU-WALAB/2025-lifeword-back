package com.project.bibly_be.admin.controller;

import com.project.bibly_be.user.dto.UserResponseDTO;
import com.project.bibly_be.user.entity.User;
import com.project.bibly_be.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    /**
     * 모든 사용자 조회
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * 사용자 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 사용자 이름으로 검색
     */
    @GetMapping("/search/by-name")
    public ResponseEntity<List<User>> searchUsersByName(@RequestParam String name) {
        List<User> users = userService.searchUsersByName(name);
        return ResponseEntity.ok(users);
    }

    /**
     * 사용자 직업으로 검색
     */
    @GetMapping("/search/by-job")
    public ResponseEntity<List<User>> searchUsersByJob(@RequestParam String job) {
        List<User> users = userService.searchUsersByJob(job);
        return ResponseEntity.ok(users);
    }

    /**
     * 사용자 교회로 검색
     */
    @GetMapping("/search/by-church")
    public ResponseEntity<List<User>> searchUsersByChurch(@RequestParam String church) {
        List<User> users = userService.searchUsersByChurch(church);
        return ResponseEntity.ok(users);
    }
}
