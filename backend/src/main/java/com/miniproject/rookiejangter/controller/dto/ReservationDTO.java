package com.miniproject.rookiejangter.controller.dto;

import com.miniproject.rookiejangter.entity.Reservation;
import lombok.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class ReservationDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private Long productId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatusUpdateRequest {
        private String status; // ["ACCEPTED","DECLINED","CANCELLED","COMPLETED"] - Reservation 엔티티에는 status 없음
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long tradeId; // API 응답에는 tradeId로 되어 있음 (ReservationId?)
        private Long productId;
        private Long buyerId;
        private String status; // API 응답에 있음 (Reservation 엔티티에는 없음)
        private OffsetDateTime requestedAt; // BaseEntity의 createdAt 활용 가능
        private OffsetDateTime updatedAt; // BaseEntity의 updatedAt 활용 가능

        public static Response fromEntity(Reservation reservation) {
            return Response.builder()
                    .tradeId(reservation.getReservationId())
                    .productId(reservation.getPost().getPostId())
                    .buyerId(reservation.getBuyer().getUserId())
                    // status 정보는 Reservation 엔티티에 없으므로, 별도로 설정해야 함
                    .requestedAt(reservation.getCreatedAt() != null ? reservation.getCreatedAt().atOffset(ZoneOffset.UTC) : null)
                    .updatedAt(reservation.getUpdatedAt() != null ? reservation.getUpdatedAt().atOffset(ZoneOffset.UTC) : null)
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