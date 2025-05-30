package com.miniproject.rookiejangter.controller.dto;

import com.miniproject.rookiejangter.entity.Dibs;
import com.miniproject.rookiejangter.entity.Post;
import lombok.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class DibsDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        // 찜하기 추가/제거 API의 요청 데이터는 path parameter {post_id} 뿐이므로
        // 별도의 Request Body DTO는 필요하지 않습니다.
        private Long postId; // Path parameter로 받음
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long postId;
        private Boolean isLiked;  // 요청하는 사용자 ID와 상품 ID를 기반으로 Dibs 테이블에 해당 레코드가 존재하는지 확인합니다.
        private OffsetDateTime likedAt;

        public static Response fromEntity(Dibs dibs, boolean liked) {
            return Response.builder()
                    .postId(dibs.getPost().getPostId())
                    .isLiked(liked)
                    .likedAt(dibs.getAddedAt() != null ? dibs.getAddedAt().atOffset(ZoneOffset.UTC) : null)
                    .build();
        }

        // 찜한 상품 목록 조회를 위한 응답
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class DibbedPost {
            private Long postId;
            private String title;
            private Integer price;
            private String thumbnail; // Post 엔티티에 thumbnail 필드가 없으므로 필요시 추가해야 함
            private OffsetDateTime likedAt;

            public static DibbedPost fromEntity(Dibs dibs) {
                Post post = dibs.getPost();
                return DibbedPost.builder()
                        .postId(post.getPostId())
                        .title(post.getTitle())
                        .price(post.getPrice())
                        .thumbnail(null) // Post 엔티티에 thumbnail 필드가 없음
                        .likedAt(dibs.getAddedAt() != null ? dibs.getAddedAt().atOffset(ZoneOffset.UTC) : null)
                        .build();
            }
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DibsListResponse {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean first;
        private boolean last;
        private List<Response.DibbedPost> content;
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