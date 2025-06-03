package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.*;
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

    private Category category;
    private Product product1;
    private Product product2;
    private Complete complete1;
    private Complete complete2;
    private User buyer;
    private User seller1;
    private User seller2;
    private Review review1;
    private Review review2;

    @BeforeEach
    public void setUp() {
        category = Category.builder()
                .categoryName("Test Category")
                .build();
        entityManager.persist(category);
        entityManager.flush();

        buyer = User.builder()
                .loginId("testuser1")
                .password("password")
                .userName("Test User1")
                .phone("010-1234-5678")
                .build();
        entityManager.persist(buyer);
        entityManager.flush();

        seller1 = User.builder()
                .loginId("testuser2")
                .password("password")
                .userName("Test User2")
                .phone("010-3434-3434")
                .build();
        entityManager.persist(seller1);
        entityManager.flush();

        seller2 = User.builder()
                .loginId("testuser3")
                .password("password")
                .userName("Test User3")
                .phone("010-5656-5656")
                .build();
        entityManager.persist(seller2);
        entityManager.flush();

        product1 = Product.builder()
                .category(category)
                .user(seller1)
                .title("test title1")
                .content("test content1")
                .price(10000)
                .build();
        entityManager.persist(product1);
        entityManager.flush();

        product2 = Product.builder()
                .category(category)
                .user(seller2)
                .title("test title2")
                .content("test content2")
                .price(10000)
                .build();
        entityManager.persist(product2);
        entityManager.flush();

        complete1 = Complete.builder()
                .product(product1)
                .buyer(buyer)
                .seller(seller1)
                .build();
        entityManager.persist(complete1);
        entityManager.flush();

        complete2 = Complete.builder()
                .product(product2)
                .buyer(buyer)
                .seller(seller2)
                .build();
        entityManager.persist(complete2);
        entityManager.flush();

        review1 = Review.builder()
                .complete(complete1)
                .buyer(buyer)
                .seller(seller1)
                .rating(5)
                .content("Excellent service!")
                .build();
        entityManager.persist(review1);
        entityManager.flush();

        review2 = Review.builder()
                .complete(complete2)
                .buyer(buyer)
                .seller(seller2)
                .rating(4)
                .content("Good product.")
                .build();
        entityManager.persist(review2);
        entityManager.flush();

    }

    @Test
    public void findByReviewId() {
        Optional<Review> foundReview = reviewRepository.findByReviewId(review1.getReviewId());
        assertThat(foundReview.get().getContent()).isEqualTo(review1.getContent());
    }

    @Test
    public void findByComplete_CompleteId() {
        Optional<Review> foundReview = reviewRepository.findByComplete_CompleteId(complete1.getCompleteId());
        assertThat(foundReview.get().getContent()).isEqualTo(review1.getContent());
    }

    @Test
    public void findByBuyer_UserId() {
        List<Review> foundReviews = reviewRepository.findByBuyer_UserId(buyer.getUserId());
        assertThat(foundReviews).hasSize(2).contains(review1, review2);
    }

    @Test
    public void findBySeller_UserId() {
        List<Review> foundReviews = reviewRepository.findBySeller_UserId(seller1.getUserId());
        assertThat(foundReviews).hasSize(1).contains(review1);
    }


    @Test
    public void whenFindByRating_thenReturnReviews() {
        List<Review> foundReviews = reviewRepository.findByRating(4);
        assertThat(foundReviews).hasSize(1).contains(review2);
    }

    @Test
    public void findByComplete_CompleteIdAndBuyer_UserId() {
        Optional<Review> foundReview = reviewRepository.
                findByComplete_CompleteIdAndBuyer_UserId
                        (complete1.getCompleteId(), complete1.getBuyer().getUserId());
        assertThat(foundReview).isPresent();
        assertThat(foundReview.get().getReviewId()).isEqualTo(review1.getReviewId());
    }
}