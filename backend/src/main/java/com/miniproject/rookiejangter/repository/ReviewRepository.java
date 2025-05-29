package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByReviewId(Long reviewId);
    List<Review> findByComplete_CompleteId(Long completeCompleteId);
    List<Review> findByUser_UserId(Long userUserId);
    List<Review> findByRating(Integer rating);
}