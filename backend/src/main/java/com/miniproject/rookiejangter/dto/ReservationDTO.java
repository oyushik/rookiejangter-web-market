package com.miniproject.rookiejangter.dto;

import com.miniproject.rookiejangter.entity.Reservation;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class ReservationDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TradeRequest {
        private Long productId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long reservationId;
        private Long productId;
        private Long buyerId;
        private Long sellerId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response fromEntity(Reservation reservation) {
            return Response.builder()
                    .reservationId(reservation.getReservationId())
                    .productId(reservation.getProduct().getProductId())
                    .buyerId(reservation.getBuyer().getUserId())
                    .sellerId(reservation.getSeller().getUserId())
                    .createdAt(reservation.getCreatedAt())
                    .updatedAt(reservation.getUpdatedAt())
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