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
public class DibsTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void createDibs() {
        // given
        Dibs dibs = Dibs.builder()
                .addedAt(LocalDateTime.now())
                .build();

        // when
        entityManager.persist(dibs);
        entityManager.flush();
        entityManager.clear();
        Dibs savedDibs = entityManager.find(Dibs.class, dibs.getDibsId());

        // then
        assertThat(savedDibs).isNotNull();
        assertThat(savedDibs.getDibsId()).isNotNull();
        assertThat(savedDibs.getAddedAt()).isNotNull();
    }

    @Test
    void checkDibsAssociations() {
        // given
        User user = User.builder()
                .loginId("dibsuser")
                .password("pwd")
                .userName("찜유저")
                .phone("010-1111-1111")
                .build();
        entityManager.persist(user);

        Category category = Category.builder()
                .categoryName("가전")
                .build();
        entityManager.persist(category);

        User seller = User.builder()
                .loginId("seller")
                .password("pwd")
                .userName("판매자")
                .phone("010-2222-2222")
                .build();
        entityManager.persist(seller);

        Product product = Product.builder()
                .user(seller)
                .category(category)
                .title("TV")
                .content("스마트 TV 판매")
                .price(1000000)
                .build();
        entityManager.persist(product);

        Dibs dibs = Dibs.builder()
                .user(user)
                .product(product)
                .addedAt(LocalDateTime.now())
                .build();
        entityManager.persist(dibs);

        entityManager.flush();
        entityManager.clear();

        // when
        Dibs foundDibs = entityManager.find(Dibs.class, dibs.getDibsId());

        // then
        assertThat(foundDibs).isNotNull();
        assertThat(foundDibs.getUser()).isNotNull();
        assertThat(foundDibs.getUser().getUserName()).isEqualTo("찜유저");
        assertThat(foundDibs.getProduct()).isNotNull();
        assertThat(foundDibs.getProduct().getTitle()).isEqualTo("TV");
        assertThat(foundDibs.getAddedAt()).isNotNull();
    }
}