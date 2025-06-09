package com.miniproject.rookiejangter.entity;

import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
@EqualsAndHashCode
public class Reservation extends BaseEntity {

    public enum TradeStatus {
        REQUESTED, // 요청됨
        ACCEPTED,  // 수락됨 (판매자가 예약 요청을 수락)
        DECLINED,  // 거절됨 (판매자가 예약 요청을 거절)
        CANCELLED, // 취소됨 (구매자가 예약 요청을 취소 또는 판매자가 수락 후 취소)
        COMPLETED  // 완료됨 (거래 완료)
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long reservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "is_canceled")
    private Boolean isCanceled;

    @Setter(AccessLevel.PUBLIC)
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TradeStatus status;

    // 예약 상태를 요청됨으로 변경
    public void requestReservation() {
        if (this.status != null && this.status != TradeStatus.REQUESTED) {
            throw new BusinessException(ErrorCode.RESERVATION_ALREADY_EXISTS, this.product.getProductId());
        }
        this.status = TradeStatus.REQUESTED;
        this.isCanceled = false;
    }

    // 예약 상태를 수락됨으로 변경
    public void acceptReservation() {
        if (this.status != TradeStatus.REQUESTED) {
            throw new BusinessException(ErrorCode.RESERVATION_INVALID_STATE_FOR_ACTION, this.status, "수락");
        }
        this.status = TradeStatus.ACCEPTED;
        this.isCanceled = false;
    }

    // 예약 상태를 거절됨으로 변경
    public void declineReservation() {
        if (this.status != TradeStatus.REQUESTED) {
            throw new BusinessException(ErrorCode.RESERVATION_INVALID_STATE_FOR_ACTION, this.status, "거절");
        }
        this.status = TradeStatus.DECLINED;
        this.isCanceled = true; // 거절 시에는 예약이 취소됨
    }

    // 예약을 취소
    public void cancelReservation() {
        if (this.status == TradeStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.RESERVATION_CANNOT_CANCEL, this.status);
        }
        if (this.isCanceled) {
            throw new BusinessException(ErrorCode.RESERVATION_CANNOT_CANCEL, this.status);
        }
        this.status = TradeStatus.CANCELLED;
        this.isCanceled = true;
    }

    // 예약을 완료됨 상태로 변경 (거래가 성사되었을 때)
    public void completeReservation() {
        if (this.status != TradeStatus.ACCEPTED) {
            throw new BusinessException(ErrorCode.RESERVATION_INVALID_STATE_FOR_ACTION, this.status, "완료");
        }
        this.status = TradeStatus.COMPLETED;
        this.isCanceled = false;
    }

}