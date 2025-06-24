package com.miniproject.rookiejangter.dto;

import com.miniproject.rookiejangter.entity.Cancelation;
import com.miniproject.rookiejangter.entity.Product;
import com.miniproject.rookiejangter.entity.User;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class CancelationDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private Integer cancelationReasonId;
        private CancelationReasonInfo cancelationReason;
        private Long productId;
        private ProductInfo product;
        private Long buyerId;
        private UserInfo buyer;
        private Long sellerId;
        private UserInfo seller;
        private Boolean isCanceledByBuyer;

        @Size(max = 255, message = "취소 상세는 최대 255자까지 가능합니다.")
        private String cancelationDetail;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long cancelationId;
        private Integer cancelationReasonId;
        private CancelationReasonInfo cancelationReason;
        private Long productId;
        private ProductInfo product;
        private Long buyerId;
        private UserInfo buyer;
        private Long sellerId;
        private UserInfo seller;
        private Boolean isCanceledByBuyer;
        private String cancelationDetail;
        private LocalDateTime canceledAt;

        public static Response fromEntity(Cancelation cancelation) {
            return Response.builder()
                    .cancelationId(cancelation.getCancelationId())
                    .cancelationReasonId(cancelation.getCancelationReason().getCancelationReasonId())
                    .cancelationReason(CancelationReasonInfo.fromEntity(cancelation.getCancelationReason()))
                    .productId(cancelation.getProduct().getProductId())
                    .product(ProductInfo.fromEntity(cancelation.getProduct()))
                    .buyerId(cancelation.getBuyer().getUserId())
                    .buyer(UserInfo.fromEntity(cancelation.getBuyer()))
                    .sellerId(cancelation.getSeller().getUserId())
                    .seller(UserInfo.fromEntity(cancelation.getSeller()))
                    .isCanceledByBuyer(cancelation.getIsCanceledByBuyer())
                    .cancelationDetail(cancelation.getCancelationDetail())
                    .canceledAt(cancelation.getCreatedAt())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CancelationReasonInfo {
        private Integer cancelationReasonId;
        private String cancelationReasonType;

        // CancelationReasonDTO.Response가 아닌 entity.CancelationReason을 인자로 받도록 수정
        public static CancelationReasonInfo fromEntity(com.miniproject.rookiejangter.entity.CancelationReason cancelationReason) {
            return CancelationReasonInfo.builder()
                    .cancelationReasonId(cancelationReason.getCancelationReasonId())
                    .cancelationReasonType(cancelationReason.getCancelationReasonType())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductInfo {
        private Long productId;
        private String title;

        public static ProductInfo fromEntity(Product product) {
            if (product == null) { // null 체크 추가
                return null;
            }
            return ProductInfo.builder()
                    .productId(product.getProductId())
                    .title(product.getTitle())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private Long id;
        private String userName;

        // 반환 타입을 UserInfo로 수정
        public static UserInfo fromEntity(User user) {
            if (user == null) { // null 체크 추가
                return null;
            }
            return UserInfo.builder() // ProductDTO.SellerInfo 대신 UserInfo.builder() 사용
                    .id(user.getUserId())
                    .userName(user.getUserName())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApiResponseWrapper<T> {
        private boolean success;
        private T data;
        private Object error;
        private String message;
        private OffsetDateTime timestamp = OffsetDateTime.now(ZoneOffset.UTC);
        private String requestId;
    }
}