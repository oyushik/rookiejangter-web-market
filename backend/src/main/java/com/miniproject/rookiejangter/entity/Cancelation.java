package com.miniproject.rookiejangter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cancelations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cancelation {

    @Id
    @Column(name = "reservation_id")
    private Long reservationId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancelation_reason_id")
    private CancelationReason cancelationReason;

    @Column(name = "cancelation_detail", length = 255)
    private String cancelationDetail;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Override
    public String toString() {
        return "Cancelation{" +
                "reservationId=" + reservationId +
                ", reservation=" + reservation +
                ", cancelationReason=" + cancelationReason +
                ", cancelationDetail='" + cancelationDetail + '\'' +
                ", canceledAt=" + canceledAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cancelation cancelation = (Cancelation) o;
        return reservationId != null && reservationId.equals(cancelation.reservationId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
