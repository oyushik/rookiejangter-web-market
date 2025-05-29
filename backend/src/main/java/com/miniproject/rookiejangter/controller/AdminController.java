package com.miniproject.rookiejangter.controller;

import org.springframework.http.HttpStatus;
// 사용자 관리 컨트롤러
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    // 관리자의 유저 목록 조회
    @GetMapping
    public ResponseEntity<List<UserDTO.Response>> getAllUsers() {
        
    }

    // 관리자의 특정 유저 조회
    @GetMapping("users/{id}")
    public ResponseEntity<UserDTO.Response> getUserById(@PathVariable Long id) {

    }
}
