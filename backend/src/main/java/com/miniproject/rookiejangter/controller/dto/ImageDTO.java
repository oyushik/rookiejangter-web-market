package com.miniproject.rookiejangter.controller.dto;

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

        public static Response fromEntity(Image image) {
            return Response.builder()
                    .imageId(image.getImageId())
                    .imageUrl(image.getImageUrl())
                    .build();
        }
    }
}