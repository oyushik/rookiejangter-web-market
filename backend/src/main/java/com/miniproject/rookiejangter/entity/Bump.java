package com.miniproject.rookiejangter.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bumps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bump {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bump_id")
    private Long bumpId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "bumped_at")
    private LocalDateTime bumpedAt;

    @Column(name = "bump_count")
    private Integer bumpCount;

    @Override
    public String toString() {
        return "Bump{" +
                "bumpId=" + bumpId +
                ", bumpedAt=" + bumpedAt +
                ", bumpCount=" + bumpCount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bump bump = (Bump) o;
        return bumpId != null && bumpId.equals(bump.bumpId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
