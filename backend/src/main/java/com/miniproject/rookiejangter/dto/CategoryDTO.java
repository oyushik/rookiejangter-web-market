package com.miniproject.rookiejangter.dto;

import com.miniproject.rookiejangter.entity.Category;
import lombok.*;

public class CategoryDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Integer categoryId;
        private String categoryName;

        public static Response fromEntity(Category category) {
            return Response.builder()
                    .categoryId(category.getCategoryId())
                    .categoryName(category.getCategoryName())
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