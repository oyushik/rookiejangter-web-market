package com.miniproject.rookiejangter.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bumps")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
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

}
