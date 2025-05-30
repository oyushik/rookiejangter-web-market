package com.miniproject.rookiejangter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    // 유저 상세 정보 조회
    @GetMapping("/{id}/profile")
    public ResponseEntity<UserDTO.Response> getUserById(@PathVariable Long id) {

    }

    // 유저 상세 정보 수정
    @PutMapping("/{id}/profile")
    public ResponseEntity<UserDTO.Response> getUserById(@PathVariable Long id) {

    }
}
