package entity;

import com.miniproject.rookiejangter.entity.Product;
import com.miniproject.rookiejangter.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "dibs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dibs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dibs_id")
    private Long dibsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "added_at")
    private LocalDateTime addedAt;

    @Override
    public String toString() {
        return "Dibs{" +
                "dibsId=" + dibsId +
                ", addedAt=" + addedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dibs dibs = (Dibs) o;
        return dibsId != null && dibsId.equals(dibs.dibsId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
