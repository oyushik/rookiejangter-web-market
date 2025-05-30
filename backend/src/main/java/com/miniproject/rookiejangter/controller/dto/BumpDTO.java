package com.miniproject.rookiejangter.controller.dto;

import com.miniproject.rookiejangter.entity.Bump;
import lombok.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class BumpDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long bumpId;
        private Long postId;
        private OffsetDateTime bumpedAt;
        private Integer bumpCount;

        public static Response fromEntity(Bump bump) {
            return Response.builder()
                    .bumpId(bump.getBumpId())
                    .postId(bump.getPost().getPostId())
                    .bumpedAt(bump.getBumpedAt() != null ? bump.getBumpedAt().atOffset(ZoneOffset.UTC) : null)
                    .bumpCount(bump.getBumpCount())
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