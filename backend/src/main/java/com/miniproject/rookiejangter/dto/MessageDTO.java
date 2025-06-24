package com.miniproject.rookiejangter.dto;

import com.miniproject.rookiejangter.entity.Message;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class MessageDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "내용은 필수입니다.")
        @Size(max = 255, message = "내용은 최대 255자까지 가능합니다.")
        private String content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long messageId;
        private Long chatId;
        private Long senderId;
        private String content;
        private Boolean isRead;
        private LocalDateTime createdAt;

        public static Response fromEntity(Message message, Long chatId) {
            return Response.builder()
                    .messageId(message.getMessageId())
                    .chatId(chatId)
                    .senderId(message.getSender() != null ? message.getSender().getUserId() : null) // sender가 null인 경우 처리
                    .content(message.getContent())
                    .isRead(message.getIsRead() != null ? message.getIsRead() : false) // Message 엔티티의 isRead 값 사용
                    .createdAt(message.getCreatedAt() != null ? message.getCreatedAt() : null)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MessageListResponse {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean first;
        private boolean last;
        private java.util.List<MessageResponse> content;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class MessageResponse {
            private Long messageId;
            private Long senderId;
            private String content;
            private Boolean isRead;
            private LocalDateTime createdAt;

            public static MessageResponse fromEntity(Message message) {
                return MessageResponse.builder()
                        .messageId(message.getMessageId())
                        .senderId(message.getSender() != null ? message.getSender().getUserId() : null)
                        .content(message.getContent())
                        .isRead(message.getIsRead() != null ? message.getIsRead() : false)
                        .createdAt(message.getCreatedAt() != null ? message.getCreatedAt() : null)
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
        private String content;
        private OffsetDateTime timestamp = OffsetDateTime.now(ZoneOffset.UTC);
        private String requestId;
    }
}