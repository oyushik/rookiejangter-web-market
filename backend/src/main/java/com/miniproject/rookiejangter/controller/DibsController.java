//package com.miniproject.rookiejangter.controller;
//
//import com.miniproject.rookiejangter.controller.dto.DibsDTO;
//import com.miniproject.rookiejangter.controller.dto.ProductDTO; // 공용 ApiResponseWrapper를 위해 (또는 DibsDTO에 정의된 것 사용)
//import com.miniproject.rookiejangter.exception.BusinessException;
//import com.miniproject.rookiejangter.exception.ErrorCode;
//import com.miniproject.rookiejangter.service.DibsService;
//import com.miniproject.rookiejangter.entity.User;
//import com.miniproject.rookiejangter.repository.UserRepository;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.OffsetDateTime;
//import java.util.UUID;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/wishlist") // RESTAPI.md 명세에 따른 기본 경로
//@RequiredArgsConstructor
//public class DibsController {
//
//    private final DibsService dibsService;
//    private final UserRepository userRepository;
//
//    // 사용자 ID를 가져오는 헬퍼 메소드
//    private Long getCurrentUserId() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
//            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
//        }
//
//        Object principal = authentication.getPrincipal();
//        if (principal instanceof UserDetails) {
//            String username = ((UserDetails) principal).getUsername();
//            try {
//                return Long.parseLong(username);
//            } catch (NumberFormatException e) {
//                User user = userRepository.findByLoginId(username)
//                        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_BY_LOGIN_ID, username));
//                return user.getUserId();
//            }
//        }
//        throw new BusinessException(ErrorCode.INVALID_AUTHENTICATION_PRINCIPAL);
//    }
//
//    /**
//     * 찜하기 추가/제거 (토글)
//     * PUT /api/wishlist/{product_id}
//     */
//    @PutMapping("/{productId}")
//    public ResponseEntity<ProductDTO.ApiResponseWrapper<DibsDTO.Response>> toggleWishlist(
//            @PathVariable Long productId) {
//        Long userId = getCurrentUserId();
//        DibsDTO.Response toggleResult = dibsService.getDibsStatus(userId, productId); // 서비스 메소드명 및 반환 DTO 수정 가정
//
//        String message = toggleResult.isLiked() ? "찜 목록에 추가되었습니다." : "찜 목록에서 제거되었습니다.";
//
//        ProductDTO.ApiResponseWrapper<DibsDTO.Response> response = ProductDTO.ApiResponseWrapper.<DibsDTO.Response>builder()
//                .success(true)
//                .data(toggleResult)
//                .message(message)
//                .timestamp(OffsetDateTime.now())
//                .requestId(UUID.randomUUID().toString())
//                .build();
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * 찜한 상품 목록 조회
//     * GET /api/wishlist
//     */
//    @GetMapping
//    public ResponseEntity<ProductDTO.ApiResponseWrapper<DibsDTO.DibsListResponse>> getWishlist(
//        @PageableDefault(size = 20, page = 0) Pageable pageable){
//        Long userId = getCurrentUserId();
//        DibsDTO.DibsListResponse dibsListResponse = dibsService.getUserDibsList(userId, pageable);
//
//        ProductDTO.ApiResponseWrapper<DibsDTO.DibsListResponse> response = ProductDTO.ApiResponseWrapper.<DibsDTO.DibsListResponse>builder()
//                .success(true)
//                .data(dibsListResponse)
//                .message("찜 목록 조회가 성공했습니다.")
//                .timestamp(OffsetDateTime.now())
//                .requestId(UUID.randomUUID().toString())
//                .build();
//        return ResponseEntity.ok(response);
//    }
//    @GetMapping("/{productId}/my-status")
//    public ResponseEntity<ProductDTO.ApiResponseWrapper<DibsDTO.Response>> getMyDibsStatusForProduct(@PathVariable Long productId) {
//        Long userId = getCurrentUserId();
//        DibsDTO.Response dibsStatusData = dibsService.getDibsStatus(userId, productId);
//
//        ProductDTO.ApiResponseWrapper<DibsDTO.Response> response = ProductDTO.ApiResponseWrapper.<DibsDTO.Response>builder()
//                .success(true)
//                .data(dibsStatusData)
//                .message("사용자의 상품 찜 상태 조회 성공")
//                .timestamp(OffsetDateTime.now())
//                .requestId(UUID.randomUUID().toString())
//                .build();
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/{productId}/total-count")
//    public ResponseEntity<ProductDTO.ApiResponseWrapper<Long>> getDibsCountForProduct(@PathVariable Long productId) {
//        long count = dibsService.getDibsCountForProduct(productId);
//
//         ProductDTO.ApiResponseWrapper<Long> response = ProductDTO.ApiResponseWrapper.<Long>builder()
//                .success(true)
//                .data(count)
//                .message("상품 총 찜 수 조회 성공")
//                .timestamp(OffsetDateTime.now())
//                .requestId(UUID.randomUUID().toString())
//                .build();
//        return ResponseEntity.ok(response);
//    }
//}
