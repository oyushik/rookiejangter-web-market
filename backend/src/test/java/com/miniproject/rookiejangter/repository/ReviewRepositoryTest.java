package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Complete;
import com.miniproject.rookiejangter.entity.Post;
import com.miniproject.rookiejangter.entity.Review;
import com.miniproject.rookiejangter.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ReviewRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReviewRepository reviewRepository;

    private Post post;
    private Complete complete;
    private User user;
    private Review review1, review2, review3;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .loginId("testuser")
                .password("password")
                .userName("Test User")
                .phone("01012345678")
                .build();
        entityManager.persist(user);

        post = Post.builder()
                .title("test title")
                .content("test content")
                .build();

        complete = Complete.builder()
                .post(post)
                .buyer(user)
                .seller(user)
                .build();
        entityManager.persist(complete);

        review1 = Review.builder()
                .complete(complete)
                .user(user)
                .rating(5)
                .content("Excellent service!")
                .build();
        entityManager.persist(review1);

        review2 = Review.builder()
                .complete(complete)
                .user(user)
                .rating(4)
                .content("Good product.")
                .build();
        entityManager.persist(review2);

        review3 = Review.builder()
                .complete(complete)
                .user(user)
                .rating(3)
                .content("Average.")
                .build();
        entityManager.persist(review3);

        entityManager.flush();
    }

    @Test
    public void whenFindByReviewId_thenReturnReview() {
        Optional<Review> foundReview = reviewRepository.findByReviewId(review1.getReviewId());
        assertThat(foundReview.get().getContent()).isEqualTo(review1.getContent());
    }

    @Test
    public void whenFindByComplete_CompleteId_thenReturnReviews() {
        List<Review> foundReviews = reviewRepository.findByComplete_CompleteId(complete.getCompleteId());
        assertThat(foundReviews).hasSize(3).contains(review1, review2, review3);
    }

    @Test
    public void whenFindByUser_UserId_thenReturnReviews() {
        List<Review> foundReviews = reviewRepository.findByUser_UserId(user.getUserId());
        assertThat(foundReviews).hasSize(3).contains(review1, review2, review3);
    }

    @Test
    public void whenFindByRating_thenReturnReviews() {
        List<Review> foundReviews = reviewRepository.findByRating(4);
        assertThat(foundReviews).hasSize(1).contains(review2);
    }
}