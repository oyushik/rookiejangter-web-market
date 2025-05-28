package com.miniproject.rookiejangter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "goods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Goods {

    @Id
    @Column(name = "post_id")
    private Long postId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "post_id")
    private Post post;

    @Size(max = 50, message = "상품명은 최대 50자까지 가능합니다.")
    @Column(name = "goods_name", length = 50)
    private String goodsName;

    @NotNull(message = "가격은 필수입니다.")
    @Column(name = "price", nullable = false)
    private Integer price;

    @Override
    public String toString() {
        return "Goods{" +
                "postId=" + postId +
                ", goodsName='" + goodsName + '\'' +
                ", price=" + price +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goods goods = (Goods) o;
        return postId != null && postId.equals(goods.postId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
