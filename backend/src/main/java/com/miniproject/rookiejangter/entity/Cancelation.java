package com.miniproject.rookiejangter.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "cancelations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class Cancelation {

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

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

}