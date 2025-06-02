package com.miniproject.rookiejangter.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CompleteTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void createComplete() {
        // given
        User seller = User.builder()
                .loginId("seller")
                .password("pwd")
                .userName("판매자")
                .phone("010-1234-5678")
                .build();
        entityManager.persist(seller);

        Category category = Category.builder()
                .categoryName("기타")
                .build();
        entityManager.persist(category);

        Product product = Product.builder()
                .user(seller)
                .category(category)
                .title("완료 상품")
                .content("완료된 상품")
                .price(1000)
                .isCompleted(true)
                .build();
        entityManager.persist(product);

        User buyer = User.builder()
                .loginId("buyer")
                .password("pwd")
                .userName("구매자")
                .phone("010-1111-1111")
                .build();
        entityManager.persist(buyer);

        Complete complete = Complete.builder()
                .product(product)
                .buyer(buyer)
                .seller(seller)
                .completedAt(LocalDateTime.now())
                .build();

        // when
        entityManager.persist(complete);
        entityManager.flush();
        entityManager.clear();
        Complete savedComplete = entityManager.find(Complete.class, complete.getCompleteId());

        // then
        assertThat(savedComplete).isNotNull();
        assertThat(savedComplete.getCompleteId()).isNotNull();
        assertThat(savedComplete.getCompletedAt()).isNotNull();
        assertThat(savedComplete.getProduct()).isNotNull();
        assertThat(savedComplete.getProduct().getTitle()).isEqualTo("완료 상품");
        assertThat(savedComplete.getBuyer()).isNotNull();
        assertThat(savedComplete.getBuyer().getUserName()).isEqualTo("구매자");
        assertThat(savedComplete.getSeller()).isNotNull();
        assertThat(savedComplete.getSeller().getUserName()).isEqualTo("판매자");
    }

    @Test
    void checkCompleteAssociations() {
        // given
        User buyer = User.builder()
                .loginId("buyer2")
                .password("pwd")
                .userName("구매자2")
                .phone("010-3333-3333")
                .build();
        entityManager.persist(buyer);

        User seller = User.builder()
                .loginId("seller2")
                .password("pwd")
                .userName("판매자2")
                .phone("010-4444-4444")
                .build();
        entityManager.persist(seller);

        Category category = Category.builder()
                .categoryName("가전")
                .build();
        entityManager.persist(category);

        Product product = Product.builder()
                .user(seller)
                .category(category)
                .title("냉장고")
                .content("판매 완료된 냉장고")
                .price(500000)
                .isCompleted(true)
                .build();
        entityManager.persist(product);

        Complete complete = Complete.builder()
                .product(product)
                .buyer(buyer)
                .seller(seller)
                .completedAt(LocalDateTime.now())
                .build();
        entityManager.persist(complete);

        entityManager.flush();
        entityManager.clear();

        // when
        Complete foundComplete = entityManager.find(Complete.class, complete.getCompleteId());

        // then
        assertThat(foundComplete).isNotNull();
        assertThat(foundComplete.getCompleteId()).isNotNull();
        assertThat(foundComplete.getProduct()).isNotNull();
        assertThat(foundComplete.getProduct().getTitle()).isEqualTo("냉장고");
        assertThat(foundComplete.getBuyer()).isNotNull();
        assertThat(foundComplete.getBuyer().getUserName()).isEqualTo("구매자2");
        assertThat(foundComplete.getSeller()).isNotNull();
        assertThat(foundComplete.getSeller().getUserName()).isEqualTo("판매자2");
        assertThat(foundComplete.getCompletedAt()).isNotNull();
    }
}