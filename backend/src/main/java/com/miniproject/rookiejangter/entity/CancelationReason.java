package com.miniproject.rookiejangter.entity;

import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "cancelation_reasons")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class CancelationReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cancelation_reason_id")
    private Integer cancelationReasonId;

    @Column(name = "cancelation_reason_type", length = 50)
    private String cancelationReasonType;

    /**
     * 취소 사유 내용을 변경합니다.
     *
     * @param newReasonType 취소 사유 타입
     */
    public void changeReasonType(String newReasonType) {
        if (newReasonType == null || newReasonType.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.CANCELATION_REASON_EMPTY);
        }
        if (newReasonType.length() > 50) {
            throw new BusinessException(ErrorCode.CANCELATION_REASON_TOO_LONG);
        }
        this.cancelationReasonType = newReasonType;
    }
}
