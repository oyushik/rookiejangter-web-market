package com.miniproject.rookiejangter.dto;

import com.miniproject.rookiejangter.entity.Image;
import lombok.*;

public class ImageDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long imageId;
        private String imageUrl;
        private Long productId;

        public static Response fromEntity(Image image) {
            return Response.builder()
                    .imageId(image.getImageId())
                    .imageUrl(image.getImageUrl())
                    .productId(image.getProduct().getProductId())
                    .build();
        }
    }
}