package com.miniproject.rookiejangter.dto;

import com.miniproject.rookiejangter.entity.Product;
import com.miniproject.rookiejangter.entity.Reservation;
import com.miniproject.rookiejangter.entity.User;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class ReservationDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TradeRequest {
        private Long chatId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long reservationId;
        private Long productId;
        private ProductInfo product;
        private Long buyerId;
        private UserInfo buyer;
        private Long sellerId;
        private UserInfo seller;
        private Long chatId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response fromEntity(Reservation reservation) {
            return Response.builder()
                    .reservationId(reservation.getReservationId())
                    .productId(reservation.getProduct().getProductId() != null ? reservation.getProduct().getProductId() : null)
                    .product(ProductInfo.fromEntity(reservation.getProduct()) != null ? ProductInfo.fromEntity(reservation.getProduct()) : null)
                    .buyerId(reservation.getBuyer().getUserId() != null ? reservation.getBuyer().getUserId() : null)
                    .buyer(UserInfo.fromEntity(reservation.getBuyer()) != null ? UserInfo.fromEntity(reservation.getBuyer()) : null)
                    .sellerId(reservation.getSeller().getUserId() != null ? reservation.getSeller().getUserId() : null)
                    .seller(UserInfo.fromEntity(reservation.getSeller()) != null ? UserInfo.fromEntity(reservation.getSeller()) : null)
                    .chatId(reservation.getChat().getChatId() != null ? reservation.getChat().getChatId() : null)
                    .createdAt(reservation.getCreatedAt())
                    .updatedAt(reservation.getUpdatedAt())
                    .build();
        }
    }

    // Product 정보만 담는 내부 DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductInfo {
        private Long productId;
        private String title;
        private Boolean isReserved;

        public static ProductInfo fromEntity(Product product) {
            return ProductInfo.builder()
                    .productId(product.getProductId())
                    .title(product.getTitle())
                    .isReserved(product.getIsReserved())
                    .build();
        }
    }

    // User 정보만 담는 내부 DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private Long userId;
        private String userName;

        public static UserInfo fromEntity(User user) {
            return UserInfo.builder()
                    .userId(user.getUserId())
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