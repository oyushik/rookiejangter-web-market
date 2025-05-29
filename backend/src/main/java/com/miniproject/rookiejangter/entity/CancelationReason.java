package com.miniproject.rookiejangter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "cancelation_reasons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelationReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cancelation_reason_id")
    private Integer cancelationReasonId;

    @Size(max = 50, message = "취소 사유 타입은 최대 50자까지 가능합니다.")
    @Column(name = "cancelation_reason_type", length = 50)
    private String cancelationReasonType;

    @Override
    public String toString() {
        return "CancelationReason{" +
                "cancelationReasonId=" + cancelationReasonId +
                ", cancelationReasonType='" + cancelationReasonType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CancelationReason cancelationReason = (CancelationReason) o;
        return cancelationReasonId != null && cancelationReasonId.equals(cancelationReason.cancelationReasonId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
