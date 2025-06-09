package com.miniproject.rookiejangter.dto;

import com.miniproject.rookiejangter.entity.Ban;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class BanDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {

        @NotNull(message = "제재할 사용자 ID는 필수입니다.")
        private Long userId;
        @NotNull(message = "근거가 되는 신고 ID는 필수입니다.")
        private Long reportId;

        @Size(max = 50, message = "제재 사유는 최대 50자까지 가능합니다.")
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