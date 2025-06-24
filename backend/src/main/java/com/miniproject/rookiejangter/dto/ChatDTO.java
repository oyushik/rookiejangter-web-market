package com.miniproject.rookiejangter.dto;

import com.miniproject.rookiejangter.entity.Chat;
import com.miniproject.rookiejangter.entity.Product; // Product 엔티티 import 추가
import com.miniproject.rookiejangter.entity.User; // User 엔티티 import 추가
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class ChatDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private Long sellerId;
        private Long productId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long chatId;
        private Long productId;
        private ProductInfo product;
        private Long buyerId;
        private UserInfo buyer;
        private Long sellerId;
        private UserInfo seller;
        private Long reservationId;
        private LocalDateTime createdAt;

        public static Response fromEntity(Chat chat) {
            Long reservationId = null;
            if (chat.getReservation() != null) { // reservation이 null이 아닐 때만 ID를 가져옴
                reservationId = chat.getReservation().getReservationId();
            }

            return Response.builder()
                    .chatId(chat.getChatId())
                    .productId(chat.getProduct().getProductId())
                    .product(ProductInfo.fromEntity(chat.getProduct()))
                    .buyerId(chat.getBuyer().getUserId())
                    .buyer(UserInfo.fromEntity(chat.getBuyer()))
                    .sellerId(chat.getSeller().getUserId())
                    .seller(UserInfo.fromEntity(chat.getSeller()))
                    .reservationId(reservationId)
                    .createdAt(chat.getCreatedAt())
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
        private String title; // 상품명
        private Boolean isReserved;
        // 필요하다면 다른 상품 정보도 추가 (예: imageUrl)

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
        private String userName; // 사용자 이름

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
    public static class ChatListResponse {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean first;
        private boolean last;
        private java.util.List<ChatInfo> content;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class ChatInfo {
            private Long chatId;
            private Long productId;
            private String productTitle; // 상품 제목 추가
            private String lastMessage;
            private LocalDateTime createdAt;
            private String otherParticipantName;
            private Long otherParticipantId;

            public static ChatInfo fromEntity(Chat chat, String lastMessage, Long currentUserId) {
                String otherName = null;
                Long otherId = null;
                String productTitle = chat.getProduct().getTitle(); // Product 엔티티에서 title 가져옴

                // 현재 사용자가 구매자라면 판매자 정보를, 판매자라면 구매자 정보를 설정
                if (chat.getBuyer().getUserId().equals(currentUserId)) {
                    otherName = chat.getSeller().getUserName(); // User 엔티티에 userName 필드가 있다고 가정
                    otherId = chat.getSeller().getUserId();
                } else if (chat.getSeller().getUserId().equals(currentUserId)) {
                    otherName = chat.getBuyer().getUserName(); // User 엔티티에 userName 필드가 있다고 가정
                    otherId = chat.getBuyer().getUserId();
                }

                return ChatInfo.builder()
                        .chatId(chat.getChatId())
                        .productId(chat.getProduct().getProductId())
                        .productTitle(productTitle) // DTO에 상품 제목 설정
                        .lastMessage(lastMessage)
                        .createdAt(chat.getCreatedAt())
                        .otherParticipantName(otherName)
                        .otherParticipantId(otherId)
                        .build();
            }
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