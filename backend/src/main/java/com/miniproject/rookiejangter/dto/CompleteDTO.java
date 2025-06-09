package com.miniproject.rookiejangter.dto;

import com.miniproject.rookiejangter.entity.Complete;
import lombok.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class CompleteDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long productId;
        private Long buyerId;
        private Long sellerId;
        private OffsetDateTime completedAt;

        public static Response fromEntity(Complete complete) {
            return Response.builder()
                    .productId(complete.getProduct().getProductId())
                    .buyerId(complete.getBuyer().getUserId())
                    .sellerId(complete.getSeller().getUserId())
                    .completedAt(complete.getCompletedAt() != null ? complete.getCompletedAt().atOffset(ZoneOffset.UTC) : null)
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