package com.miniproject.rookiejangter.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "completes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Complete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "complete_id")
    private Long completeId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Override
    public String toString() {
        return "Complete{" +
                "completeId=" + completeId +
                ", completedAt=" + completedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Complete complete = (Complete) o;
        return completeId != null && completeId.equals(complete.completeId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
