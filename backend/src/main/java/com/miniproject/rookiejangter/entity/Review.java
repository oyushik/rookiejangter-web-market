package com.miniproject.rookiejangter.entity;

import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complete_id")
    private Complete complete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "content", length = 255)
    private String content;

    /**
     * 리뷰를 업데이트합니다.
     * 
     * @param newRating 새로운 평점 (1~5 사이의 정수)
     * @param newContent 새로운 리뷰 내용 (최대 255자)
     * @throws BusinessException 평점이 유효하지 않거나 리뷰 내용이 너무 긴 경우 예외 발생
     */
    public void updateReviewInfo(Integer newRating, String newContent) {
        if (newRating == null || newRating < 1 || newRating > 5) {
            throw new BusinessException(ErrorCode.INVALID_REVIEW_RATING);
        }
        if (newContent != null && newContent.length() > 255) {
            throw new BusinessException(ErrorCode.REVIEW_CONTENT_TOO_LONG);
        }
        this.rating = newRating;
        this.content = newContent;
    }

}
