package com.miniproject.rookiejangter.controller;

import com.miniproject.rookiejangter.controller.dto.UserDTO;
import com.miniproject.rookiejangter.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 사용자 관리 컨트롤러
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    // 관리자의 유저 목록 조회
    @GetMapping
    public ResponseEntity<UserDTO.UserListData> getAllUsers(@RequestParam Pageable pageable) {
        UserDTO.UserListData allUsers = userService.getAllUsers(pageable);
        return ResponseEntity.ok(allUsers);
    }

    // 관리자의 특정 유저 조회
    @GetMapping("users/{id}")
    public ResponseEntity<UserDTO.Response> getUserById(@PathVariable Long id) {
        UserDTO.Response user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    // 관리자의 특정 유저 상태 수정
    @PutMapping("users/{id}/status")
    public ResponseEntity<UserDTO.Response> updateUser(@PathVariable Long id, @RequestBody UserDTO.StatusUpdateRequest request) {
        UserDTO.Response response = userService.updateUserStatus(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
