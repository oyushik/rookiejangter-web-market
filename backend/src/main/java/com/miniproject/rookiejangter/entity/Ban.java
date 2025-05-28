package com.miniproject.rookiejangter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "bans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ban extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ban_id")
    private Long banId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @Size(max = 50, message = "제재 사유는 최대 50자까지 가능합니다.")
    @Column(name = "ban_reason", length = 50)
    private String banReason;

    @Override
    public String toString() {
        return "Ban{" +
                "banId=" + banId +
                ", banReason='" + banReason + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ban ban = (Ban) o;
        return banId != null && banId.equals(ban.banId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}