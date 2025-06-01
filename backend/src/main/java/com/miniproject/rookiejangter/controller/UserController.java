package com.miniproject.rookiejangter.controller;

import com.miniproject.rookiejangter.controller.dto.UserDTO;
import com.miniproject.rookiejangter.service.ProductService;
import com.miniproject.rookiejangter.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ProductService productService;

    // 유저 상세 정보 조회
    @GetMapping("/{id}/profile")
    public ResponseEntity<UserDTO.Response> getUserById(@PathVariable Long id) {
        UserDTO.Response users = userService.getUserById(id);
        return ResponseEntity.ok(users);
    }

    // 유저 상세 정보 수정
    @PutMapping("/{id}/profile")
    public ResponseEntity<UserDTO.Response> updateUserById(@PathVariable Long id, @Valid @RequestBody UserDTO.UpdateRequest request) {
        UserDTO.Response users = userService.updateUser(id, request);
        return ResponseEntity.ok(users);
    }

    // 유저 정보 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
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
