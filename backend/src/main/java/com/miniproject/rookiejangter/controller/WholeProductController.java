package com.miniproject.rookiejangter.controller;

import com.miniproject.rookiejangter.controller.dto.ProductDTO;
import com.miniproject.rookiejangter.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products") // API 엔드포인트 경로
@RequiredArgsConstructor
public class WholeProductController {

    private final ProductService productService;

    @GetMapping // GET /api/products
    public ResponseEntity<ProductDTO.ApiResponseWrapper<ProductDTO.ProductListData>> getAllProducts(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestHeader(value = "X-USER-ID", required = false) Long currentUserId) {
        ProductDTO.ProductListData productListData = productService.getAllProducts(pageable, currentUserId);
        return ResponseEntity.ok(ProductDTO.ApiResponseWrapper.<ProductDTO.ProductListData>builder()
                .success(true)
                .data(productListData)
                .message("모든 상품 목록이 성공적으로 조회되었습니다.")
                .build());
    }
}