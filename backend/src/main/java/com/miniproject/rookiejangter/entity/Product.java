package com.miniproject.rookiejangter.entity;

import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
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

    /**
     * 생성자: ProductDTO를 사용하여 Product 정보를 업데이트합니다.
     * 
     * @param newCategory   새 카테고리 정보
     * @param newTitle      새 제목
     * @param newContent    새 내용
     * @param newPrice      새 가격
     */
    public void updateProductInfo(Category newCategory, String newTitle, String newContent, Integer newPrice) {
        if (newCategory == null) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND, "카테고리 정보가 필요합니다.");
        }
        if (newTitle == null || newTitle.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PRODUCT_NAME_EMPTY);
        }
        if (newTitle.length() > 50) {
            throw new BusinessException(ErrorCode.PRODUCT_NAME_TOO_LONG);
        }
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PRODUCT_CONTENT_EMPTY);
        }
        if (newContent.length() > 255) {
            throw new BusinessException(ErrorCode.PRODUCT_CONTENT_TOO_LONG);
        }
        if (newPrice == null || newPrice < 0) { // 가격은 0 이상이어야 함
            throw new BusinessException(ErrorCode.PRODUCT_PRICE_NEGATIVE);
        }

        this.category = newCategory;
        this.title = newTitle;
        this.content = newContent;
        this.price = newPrice;
    }

    // 조회수 증가
    public void incrementViewCount() {
        if (this.viewCount == null) {
            this.viewCount = 0;
        }
        this.viewCount++;
    }

    // 예약 상태 변경
    public void markAsReserved(boolean isReserved) {
        this.isReserved = isReserved;
    }

    // 거래 완료 상태 변경
    public void markAsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
}