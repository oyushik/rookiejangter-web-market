package com.miniproject.rookiejangter.controller;

import com.miniproject.rookiejangter.controller.dto.UserDTO;
import com.miniproject.rookiejangter.service.ProductService;
import com.miniproject.rookiejangter.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // URL에서 ID 제거하고 토큰에서만 사용자 식별
    @GetMapping("/profile")  // /{id} 제거
    public ResponseEntity<UserDTO.Response> getCurrentUserProfile(Authentication authentication) {
        String username = authentication.getName();
        UserDTO.Response user = userService.getUserByUserName(username);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")  // /{id} 제거
    public ResponseEntity<UserDTO.Response> updateCurrentUserProfile(@Valid @RequestBody UserDTO.UpdateRequest request, Authentication authentication) {
        String username = authentication.getName();
        UserDTO.Response user = userService.updateUser(username, request);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/profile")  // /{id} 제거
    public ResponseEntity<Void> deleteCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }


//    // 유저 상품 등록
//    @PostMapping("/{user_id}/products") {
//
//    }
//
//    // 유저가 등록한 모든 상품 조회
//    @GetMapping("/{user_id}/products")
//
//    // 유저가 등록한 특정 상품 조회
//    @GetMapping("/{user_id}/products/{product_id}")
//
//    // 유저가 등록한 상품 수정
//    @PutMapping("/{user_id}/products/{product_id}")
//
//    // 유저가 등록한 상품 삭제
//    @DeleteMapping("/{user_id}/products/{product_id}")
//
//    // 유저의 찜한 상품 조회
//    @GetMapping("/{user_id}/dibs")
}
