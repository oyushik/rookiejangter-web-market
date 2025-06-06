package com.miniproject.rookiejangter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Size(max = 255, message = "이미지 URL은 최대 255자까지 가능합니다.")
    @Column(name = "image_url", length = 255, nullable = false)
    private String imageUrl;

    @Override
    public String toString() {
        return "Image{" +
                "imageId=" + imageId +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return imageId != null && imageId.equals(image.imageId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
