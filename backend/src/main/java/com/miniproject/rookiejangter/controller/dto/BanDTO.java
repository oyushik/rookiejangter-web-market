package com.miniproject.rookiejangter.controller.dto;

import com.miniproject.rookiejangter.entity.Ban;
import lombok.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class BanDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String banReason;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long banId;
        private Long userId;
        private Long reportId;
        private String banReason;
        private OffsetDateTime bannedAt;

        public static Response fromEntity(Ban ban) {
            return Response.builder()
                    .banId(ban.getBanId())
                    .userId(ban.getUser().getUserId())
                    .reportId(ban.getReport() != null ? ban.getReport().getReportId() : null)
                    .banReason(ban.getBanReason())
                    .bannedAt(ban.getCreatedAt() != null ? ban.getCreatedAt().atOffset(ZoneOffset.UTC) : null)
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
        private java.time.OffsetDateTime timestamp = java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC);
        private String requestId;
    }
}