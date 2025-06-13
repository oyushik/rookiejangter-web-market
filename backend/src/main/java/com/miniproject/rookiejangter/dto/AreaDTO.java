package com.miniproject.rookiejangter.dto;

import com.miniproject.rookiejangter.entity.Area;
import lombok.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class AreaDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Integer areaId;
        private String areaName;

        public static Response fromEntity(Area area) {
            return Response.builder()
                    .areaId(area.getAreaId())
                    .areaName(area.getAreaName())
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