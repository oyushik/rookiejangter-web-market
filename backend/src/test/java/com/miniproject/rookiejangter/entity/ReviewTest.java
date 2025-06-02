package com.miniproject.rookiejangter.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import jakarta.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ReviewTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void createReview() {
        // given
        Review review = Review.builder()
                .rating(5)
                .content("친절하고 좋은 거래였습니다.")
                .build();

        // when
        entityManager.persist(review);
        entityManager.flush();
        entityManager.clear();
        Review savedReview = entityManager.find(Review.class, review.getReviewId());

        // then
        assertThat(savedReview).isNotNull();
        assertThat(savedReview.getReviewId()).isNotNull();
        assertThat(savedReview.getRating()).isEqualTo(5);
        assertThat(savedReview.getContent()).isEqualTo("친절하고 좋은 거래였습니다.");
    }

    @Test
    void checkReviewAssociations() {
        // given
        User buyer = User.builder()
                .loginId("buyer")
                .password("pwd")
                .userName("구매자")
                .phone("010-1111-1111")
                .build();
        entityManager.persist(buyer);

        User seller = User.builder()
                .loginId("seller")
                .password("pwd")
                .userName("판매자")
                .phone("010-2222-2222")
                .build();
        entityManager.persist(seller);

        Category category = Category.builder()
                .categoryName("잡화")
                .build();
        entityManager.persist(category);

        Product product = Product.builder()
                .user(seller)
                .category(category)
                .title("지갑")
                .content("가죽 지갑")
                .price(50000)
                .isCompleted(true)
                .build();
        entityManager.persist(product);

        Complete complete = Complete.builder()
                .product(product)
                .buyer(buyer)
                .seller(seller)
                .build();
        entityManager.persist(complete);

        Review review = Review.builder()
                .complete(complete)
                .buyer(buyer)
                .seller(seller)
                .rating(4)
                .content("배송이 조금 늦었지만 상품은 좋아요.")
                .build();
        entityManager.persist(review);

        entityManager.flush();
        entityManager.clear();

        // when
        Review foundReview = entityManager.find(Review.class, review.getReviewId());

        // then
        assertThat(foundReview).isNotNull();
        assertThat(foundReview.getComplete()).isNotNull();
        assertThat(foundReview.getComplete().getProduct().getTitle()).isEqualTo("지갑");
        assertThat(foundReview.getBuyer()).isNotNull();
        assertThat(foundReview.getBuyer().getUserName()).isEqualTo("구매자");
        assertThat(foundReview.getSeller()).isNotNull();
        assertThat(foundReview.getSeller().getUserName()).isEqualTo("판매자");
        assertThat(foundReview.getRating()).isEqualTo(4);
        assertThat(foundReview.getContent()).isEqualTo("배송이 조금 늦었지만 상품은 좋아요.");
    }
}