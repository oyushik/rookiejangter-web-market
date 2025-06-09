package com.miniproject.rookiejangter.dto;

import com.miniproject.rookiejangter.entity.Report;
import lombok.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class ReportDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private Integer reportReasonId;
        private Long targetId;
        private String targetType;
        private String reportDetail;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long reportId;
        private Integer reportReasonId;
        private String reportReasonType;
        private Long reporterId;
        private Long targetId;
        private String targetType;
        private String reportDetail;
        private Boolean isProcessed;
        private OffsetDateTime createdAt;

        public static Response fromEntity(Report report) {
            return Response.builder()
                    .reportId(report.getReportId())
                    .reportReasonId(report.getReportReason().getReportReasonId())
                    .reportReasonType(report.getReportReason().getReportReasonType())
                    .reporterId(report.getUser().getUserId())
                    .targetId(report.getTargetId())
                    .targetType(report.getTargetType())
                    .reportDetail(report.getReportDetail())
                    .isProcessed(report.getIsProcessed())
                    .createdAt(report.getCreatedAt() != null ? report.getCreatedAt().atOffset(ZoneOffset.UTC) : null)
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