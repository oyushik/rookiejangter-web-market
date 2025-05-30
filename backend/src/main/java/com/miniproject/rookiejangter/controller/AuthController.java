package com.miniproject.rookiejangter.controller;

import com.miniproject.rookiejangter.controller.dto.UserDTO;
import com.miniproject.rookiejangter.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<UserDTO.Response> registerUser(@Valid @RequestBody UserDTO.Request request) {
        UserDTO.Response user = userService.createUser(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<UserDTO.Response> loginUser(@RequestBody UserDTO.LoginRequest request) {
        boolean isAuthenticated = userService.authenticate(request);
        if (isAuthenticated) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}

