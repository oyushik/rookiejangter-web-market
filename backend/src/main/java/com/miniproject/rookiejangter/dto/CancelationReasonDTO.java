package com.miniproject.rookiejangter.dto;

import com.miniproject.rookiejangter.entity.CancelationReason;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class CancelationReasonDTO {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @Size(max = 50, message = "취소 사유 타입은 최대 50자까지 가능합니다.")
        private String cancelationReasonType;
        private Boolean isCancelationReasonOfBuyer;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Integer cancelationReasonId;
        private String cancelationReasonType;
        private Boolean isCancelationReasonOfBuyer;

        public static Response fromEntity(CancelationReason cancelationReason) {
            return Response.builder()
                    .cancelationReasonId(cancelationReason.getCancelationReasonId())
                    .cancelationReasonType(cancelationReason.getCancelationReasonType())
                    .isCancelationReasonOfBuyer(cancelationReason.getIsCancelationReasonOfBuyer())
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