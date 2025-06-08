package com.miniproject.rookiejangter.entity;

import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
@EqualsAndHashCode
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

    // 비즈니스 메서드: 리뷰 내용 업데이트
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
