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
public class BumpTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void createBump() {
        // given
        Bump bump = Bump.builder()
                .bumpedAt(LocalDateTime.now())
                .bumpCount(1)
                .build();

        // when
        entityManager.persist(bump);
        entityManager.flush();
        entityManager.clear();
        Bump savedBump = entityManager.find(Bump.class, bump.getBumpId());

        // then
        assertThat(savedBump).isNotNull();
        assertThat(savedBump.getBumpId()).isNotNull();
        assertThat(savedBump.getBumpedAt()).isNotNull();
        assertThat(savedBump.getBumpCount()).isEqualTo(1);
    }

    @Test
    void checkBumpProductAssociation() {
        // given
        User seller = User.builder()
                .loginId("seller")
                .password("pwd")
                .userName("판매자")
                .phone("010-1234-5678")
                .build();
        entityManager.persist(seller);

        Category category = Category.builder()
                .categoryName("디지털")
                .build();
        entityManager.persist(category);

        Product product = Product.builder()
                .user(seller)
                .category(category)
                .title("스마트폰")
                .content("최신 스마트폰")
                .price(1200000)
                .isBumped(false)
                .build();
        entityManager.persist(product);

        Bump bump = Bump.builder()
                .product(product)
                .bumpedAt(LocalDateTime.now())
                .bumpCount(1)
                .build();
        entityManager.persist(bump);

        entityManager.flush();
        entityManager.clear();

        // when
        Bump foundBump = entityManager.find(Bump.class, bump.getBumpId());

        // then
        assertThat(foundBump).isNotNull();
        assertThat(foundBump.getProduct()).isNotNull();
        assertThat(foundBump.getProduct().getTitle()).isEqualTo("스마트폰");
        assertThat(foundBump.getBumpedAt()).isNotNull();
        assertThat(foundBump.getBumpCount()).isEqualTo(1);
    }
}