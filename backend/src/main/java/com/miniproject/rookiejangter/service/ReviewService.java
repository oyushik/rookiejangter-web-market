package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.dto.ReviewDTO;
import com.miniproject.rookiejangter.entity.Complete;
import com.miniproject.rookiejangter.entity.Review;
import com.miniproject.rookiejangter.entity.User;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.repository.CompleteRepository;
import com.miniproject.rookiejangter.repository.ReviewRepository;
import com.miniproject.rookiejangter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CompleteRepository completeRepository;
    private final UserRepository userRepository;

    /**
     * 리뷰를 생성합니다.
     *
     * @param completeId 거래 완료 ID
     * @param userId    리뷰 작성자 ID
     * @param request   리뷰 요청 정보
     * @return 생성된 리뷰 정보
     */
    @Transactional
    public ReviewDTO.Response createReview(Long completeId, Long userId, ReviewDTO.Request request) {
        // 1. 해당 거래가 완료되었는지 확인
        Complete complete = completeRepository.findById(completeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRADE_NOT_FOUND, completeId));

        // 2. 리뷰를 작성하는 사용자(구매자) 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));

        // 3. 이미 리뷰를 작성했는지 확인 (1개의 거래에 1개의 리뷰만 허용)
        if (reviewRepository.findByComplete_CompleteIdAndBuyer_UserId(completeId, userId).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_REVIEW);
        }

        // 4. 리뷰 생성 및 저장
        Review review = Review.builder()
                .complete(complete)
                .buyer(complete.getBuyer())
                .seller(complete.getSeller())
                .rating(request.getRating())
                .content(request.getContent())
                .build();

        Review savedReview = reviewRepository.save(review);

        // 5. Review 엔티티를 ReviewDTO.Response로 변환하여 반환
        return ReviewDTO.Response.fromEntity(savedReview);
    }

    /**
     * 리뷰를 수정합니다.
     *
     * @param reviewId 리뷰 ID
     * @param userId   리뷰 작성자 ID
     * @param request  수정할 리뷰 정보
     * @return 수정된 리뷰 정보
     */
    @Transactional
    public ReviewDTO.Response updateReview(Long reviewId, Long userId, ReviewDTO.Request request) {
        // 1. 해당 리뷰가 있는지 확인
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND, reviewId));

        // 2. 리뷰 수정 권한 확인 (리뷰 작성자와 수정자가 동일한지)
        if (!review.getBuyer().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.REVIEW_ACCESS_DENIED);
        }

        // 3. 리뷰 내용 업데이트
        review.updateReviewInfo(request.getRating(), request.getContent());

        return ReviewDTO.Response.fromEntity(review);
    }

    /**
     * 리뷰를 삭제합니다.
     *
     * @param reviewId 리뷰 ID
     * @param userId   리뷰 작성자 ID
     */
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        // 1. 삭제할 리뷰가 있는지 확인
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND, reviewId));

        // 2. 리뷰 삭제 권한 확인 (리뷰 작성자와 삭제자가 동일한지)
        if (!review.getBuyer().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.TRADE_UNAUTHORIZED);
        }

        // 3. 리뷰 삭제
        reviewRepository.delete(review);
    }

    /**
     * 특정 리뷰를 ID로 조회합니다.
     *
     * @param reviewId 리뷰 ID
     * @return 리뷰 정보
     */
    public ReviewDTO.Response getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND, reviewId));
        return ReviewDTO.Response.fromEntity(review);
    }

    /**
     * 특정 사용자가 작성한 모든 리뷰를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 해당 사용자가 작성한 리뷰 리스트
     */
    public List<ReviewDTO.Response> getReviewsBySellerId(Long userId) {
        List<Review> reviews = reviewRepository.findBySeller_UserId(userId);
        if (reviews.isEmpty()) {
            throw new BusinessException(ErrorCode.REVIEW_NOT_FOUND, userId);
        }
        return reviews.stream()
                .map(ReviewDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 특정 거래 완료 ID에 대한 리뷰를 조회합니다.
     *
     * @param completeId 거래 완료 ID
     * @return 해당 거래 완료 ID에 대한 리뷰 정보
     */
    public ReviewDTO.Response getReviewByCompleteId(Long completeId) {
        Optional<Review> reviewOptional = reviewRepository.findByComplete_CompleteId(completeId);
        return reviewOptional.map(ReviewDTO.Response::fromEntity)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND, completeId));
    }
}