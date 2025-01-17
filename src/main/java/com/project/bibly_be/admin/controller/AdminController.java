package com.project.bibly_be.admin.controller;

import com.project.bibly_be.user.dto.UserRequestDTO;
import com.project.bibly_be.user.entity.User;
import com.project.bibly_be.user.dto.UserResponseDTO;
import com.project.bibly_be.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "모든 유저 다 불러오기")
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
     * 하나의 엔드포인트로 이름/직업/교회 검색
     * 예) /search?type=name&value=홍길동
     *     /search?type=job&value=teacher
     *     /search?type=church&value=seoul
     */
    @Operation(summary = "Admin 입장에서 User Search입니다요 type 파라미터 종류 : name, job, church, email")

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(
            @RequestParam("type") String type,
            @RequestParam("value") String value
    ) {
        List<User> users;

        switch (type.toLowerCase()) {
            case "name":
                users = userService.searchUsersByName(value);
                break;
            case "job":
                users = userService.searchUsersByJob(value);
                break;
            case "church":
                users = userService.searchUsersByChurch(value);
                break;
            case "email" :
                users = userService.searchUsersByEmail(value);
                break;
            default:
                throw new IllegalArgumentException("Invalid search type: " + type);
        }

        return ResponseEntity.ok(users);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable UUID id,
            @RequestBody UserRequestDTO updateRequestDTO
    ) {
        UserResponseDTO updatedUser = userService.updateUser(id, updateRequestDTO);
        return ResponseEntity.ok(updatedUser);
    }

}
