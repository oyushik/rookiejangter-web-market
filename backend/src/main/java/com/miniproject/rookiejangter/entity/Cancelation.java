package com.miniproject.rookiejangter.entity;

import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "cancelations")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
@EqualsAndHashCode
public class Cancelation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cancelation_id")
    private Long cancelationId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancelation_reason_id")
    private CancelationReason cancelationReason;

    @Column(name = "cancelation_detail", length = 255)
    private String cancelationDetail;

    /**
     * 취소 정보를 업데이트합니다.
     * @param newCancelationReason 취소 사유
     * @param newCancelationDetail 취소 상세 정보
     */
    public void updateCancelationInfo(CancelationReason newCancelationReason, String newCancelationDetail) {
        if (newCancelationReason == null) {
            throw new BusinessException(ErrorCode.CANCELATION_REASON_EMPTY);
        }
        if (newCancelationDetail != null && newCancelationDetail.length() > 255) {
            throw new BusinessException(ErrorCode.CANCELATION_REASON_TOO_LONG);
        }

        this.cancelationReason = newCancelationReason;
        this.cancelationDetail = newCancelationDetail;
    }
}