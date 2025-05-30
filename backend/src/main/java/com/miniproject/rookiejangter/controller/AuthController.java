package com.miniproject.rookiejangter.controller;

import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<UserDTO.Response> registerUser(@Valid @RequestBody UserDTO.Request request) {
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<UserDTO.LoginResponse> loginUser(@Valid @RequestBody UserDTO.LoginRequest request) {
    }
}

