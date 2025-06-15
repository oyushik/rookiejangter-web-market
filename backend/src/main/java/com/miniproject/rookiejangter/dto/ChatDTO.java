package com.miniproject.rookiejangter.dto;

import com.miniproject.rookiejangter.entity.Chat;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

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
        private Long buyerId;
        private Long sellerId;
        private LocalDateTime createdAt;

        public static Response fromEntity(Chat chat) {
            return Response.builder()
                    .chatId(chat.getChatId())
                    .productId(chat.getProduct().getProductId())
                    .buyerId(chat.getBuyer().getUserId())
                    .sellerId(chat.getSeller().getUserId())
                    .createdAt(chat.getCreatedAt())
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
        private List<ChatInfo> content;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class ChatInfo {
            private Long chatId;
            private Long productId;
            private Long buyerId;
            private Long sellerId;
            private String lastMessage;
            private String otherParticipantName;
            private Long otherParticipantId;
            private LocalDateTime createdAt;

            public static ChatInfo fromEntity(Chat chat, String lastMessage, Long currentUserId) {
                String otherName = null;
                Long otherId = null;

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