package com.miniproject.rookiejangter.dto;

import com.miniproject.rookiejangter.entity.Notification;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class NotificationDTO {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long notificationId;
        private Long userId;
        private Long entityId;
        private String entityType;
        private String message;
        private LocalDateTime sentAt;
        private Boolean isRead;

        public static Response fromEntity(Notification notification) {
            return Response.builder()
                    .notificationId(notification.getNotificationId())
                    .userId(notification.getUser().getUserId())
                    .entityId(notification.getEntityId())
                    .entityType(notification.getEntityType())
                    .message(notification.getMessage())
                    .sentAt(notification.getCreatedAt())
                    .isRead(notification.getIsRead())
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