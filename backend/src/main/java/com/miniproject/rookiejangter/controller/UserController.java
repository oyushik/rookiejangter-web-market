package com.miniproject.rookiejangter.controller;

import com.miniproject.rookiejangter.controller.dto.ProductDTO;
import com.miniproject.rookiejangter.controller.dto.UserDTO;
import com.miniproject.rookiejangter.service.ProductService;
import com.miniproject.rookiejangter.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ProductService productService;

    // 현재 사용자 프로필 조회
    @GetMapping("/profile")
    public ResponseEntity<UserDTO.Response> getCurrentUserProfile(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        UserDTO.Response user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    // 현재 사용자 프로필 수정
    @PutMapping("/profile")
    public ResponseEntity<UserDTO.Response> updateCurrentUserProfile(
            @Valid @RequestBody UserDTO.UpdateRequest request,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        UserDTO.Response user = userService.updateUser(userId, request);
        return ResponseEntity.ok(user);
    }

    // 현재 사용자 계정 삭제
    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteCurrentUser(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    // 상품 등록
    @PostMapping("/products")
    public ResponseEntity<ProductDTO.Response> createProduct(
            @Valid @RequestBody ProductDTO.Request request,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        ProductDTO.Response response = productService.createProduct(request, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 현재 유저가 등록한 모든 상품 조회
    @GetMapping("/products")
    public ResponseEntity<ProductDTO.ProductListData> getUserProducts(
            Pageable pageable,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        ProductDTO.ProductListData response = productService.getProductsByUser(userId, pageable, userId);
        return ResponseEntity.ok(response);
    }

    // 현재 유저가 등록한 특정 상품 상세 조회
    @GetMapping("/products/{product_id}")
    public ResponseEntity<ProductDTO.Response> getUserProduct(
            @PathVariable("product_id") Long productId,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        ProductDTO.Response response = productService.getUserProductById(productId, userId);
        return ResponseEntity.ok(response);
    }

    // 현재 유저가 등록한 상품 수정
    @PutMapping("/products/{product_id}")
    public ResponseEntity<ProductDTO.Response> updateUserProduct(
            @PathVariable("product_id") Long productId,
            @Valid @RequestBody ProductDTO.UpdateRequest request,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        ProductDTO.Response response = productService.updateProduct(productId, request, userId);
        return ResponseEntity.ok(response);
    }

    // 현재 유저가 등록한 상품 삭제
    @DeleteMapping("/products/{product_id}")
    public ResponseEntity<Void> deleteUserProduct(
            @PathVariable("product_id") Long productId,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        productService.deleteProduct(productId, userId);
        return ResponseEntity.noContent().build();
    }

    // 현재 유저가 등록한 상품 상태 변경 (예약중/판매완료)
    @PutMapping("/products/{product_id}/status")
    public ResponseEntity<Void> updateProductStatus(
            @PathVariable("product_id") Long productId,
            @RequestBody ProductDTO.StatusUpdateRequest request,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        productService.updateProductStatus(productId, request.getIsReserved(), request.getIsCompleted(), userId);
        return ResponseEntity.ok().build();
    }
}