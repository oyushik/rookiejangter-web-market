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
            private String lastMessage;
//            private Integer unreadCount;
            private LocalDateTime createdAt;

            public static ChatInfo fromEntity(Chat chat, String lastMessage) {
                return ChatInfo.builder()
                        .chatId(chat.getChatId())
                        .productId(chat.getProduct().getProductId())
                        .lastMessage(lastMessage)
//                        .unreadCount(unreadCount)
                        .createdAt(chat.getCreatedAt())
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