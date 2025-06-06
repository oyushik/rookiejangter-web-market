package com.miniproject.rookiejangter.controller;

import com.miniproject.rookiejangter.controller.dto.DibsDTO;
import com.miniproject.rookiejangter.controller.dto.ProductDTO;
import com.miniproject.rookiejangter.controller.dto.UserDTO;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.service.DibsService;
import com.miniproject.rookiejangter.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 로깅 추가
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset; // UTC 명시를 위해 추가
import java.util.UUID;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class DibsController {

    private final DibsService dibsService;
    private final UserService userService;


    /**
     * 찜하기 추가/제거 (토글)
     * PUT /api/wishlist/{product_id}
     */
    @PutMapping("/{productId}")
    public ResponseEntity<ProductDTO.ApiResponseWrapper<DibsDTO.Response>> toggleWishlist(
            @PathVariable Long productId,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());

        DibsDTO.Response toggleResult = dibsService.toggleDibs(userId, productId);

        String message = toggleResult.isLiked() ? "찜 목록에 추가되었습니다." : "찜 목록에서 제거되었습니다.";

        ProductDTO.ApiResponseWrapper<DibsDTO.Response> response = ProductDTO.ApiResponseWrapper.<DibsDTO.Response>builder()
                .success(true)
                .data(toggleResult)
                .message(message)
                .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
                .requestId(UUID.randomUUID().toString())
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 찜한 상품 목록 조회
     * GET /api/wishlist
     */
    @GetMapping
    public ResponseEntity<ProductDTO.ApiResponseWrapper<DibsDTO.DibsListResponse>> getWishlist(
            @PageableDefault(size = 20, page = 0) Pageable pageable,
            Authentication authentication
    ){
        Long userId = Long.parseLong(authentication.getName());

        DibsDTO.DibsListResponse dibsListResponse = dibsService.getUserDibsList(userId, pageable);

        // if (dibsListResponse.getPagination() != null && dibsListResponse.getPagination().getContent() != null) {
        //     log.debug("DibsListResponse.pagination.content 중복 필드 제거");
        //     dibsListResponse.getPagination().setContent(null);
        // }


        ProductDTO.ApiResponseWrapper<DibsDTO.DibsListResponse> response = ProductDTO.ApiResponseWrapper.<DibsDTO.DibsListResponse>builder()
                .success(true)
                .data(dibsListResponse)
                .message("찜 목록 조회가 성공했습니다.")
                .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
                .requestId(UUID.randomUUID().toString())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}/my-status")
    public ResponseEntity<ProductDTO.ApiResponseWrapper<DibsDTO.Response>> getMyDibsStatusForProduct(
            @PathVariable Long productId,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());

        DibsDTO.Response dibsStatusData = dibsService.getDibsStatus(userId, productId);

        ProductDTO.ApiResponseWrapper<DibsDTO.Response> response = ProductDTO.ApiResponseWrapper.<DibsDTO.Response>builder()
                .success(true)
                .data(dibsStatusData)
                .message("사용자의 상품 찜 상태 조회 성공")
                .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
                .requestId(UUID.randomUUID().toString())
                .build();
        return ResponseEntity.ok(response);
    }
}


