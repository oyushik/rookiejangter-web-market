package com.miniproject.rookiejangter.entity;

import com.miniproject.rookiejangter.dto.ProductDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
@EqualsAndHashCode
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "content", length = 255, nullable = false)
    private String content;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "view_count")
    private Integer viewCount;

    @Column(name = "is_bumped")
    private Boolean isBumped;

    @Column(name = "is_reserved")
    private Boolean isReserved;

    @Column(name = "is_completed")
    private Boolean isCompleted;

    // Product 업데이트를 위한 메서드
    public Product update(ProductDTO.UpdateRequest requestDto, Category category) {
        ProductBuilder builder = Product.builder()
                .productId(this.productId) // 기존 ID 유지
                .user(this.user); // 기존 User 유지

        // 각 필드에 대해 requestDto의 값이 null이 아니면 업데이트, 아니면 기존 값 유지
        builder.title(requestDto.getTitle() != null ? requestDto.getTitle() : this.title)
                .content(requestDto.getContent() != null ? requestDto.getContent() : this.content)
                .price(requestDto.getPrice() != null ? requestDto.getPrice() : this.price)
                .category(category != null ? category : this.category);

        return builder.build();
    }

    public void incrementViewCount() {
        // null 체크: viewCount가 초기화되지 않은 경우를 대비 (DB 기본값 0이 아닌 경우)
        if (this.viewCount == null) {
            this.viewCount = 0;
        }
        this.viewCount++;
    }
}