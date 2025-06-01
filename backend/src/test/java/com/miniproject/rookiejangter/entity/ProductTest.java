package com.miniproject.rookiejangter.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import jakarta.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void createProduct() {
        // given
        Product product = Product.builder()
                .title("테스트 상품")
                .content("테스트 상품 내용")
                .price(10000)
                .viewCount(0)
                .isBumped(false)
                .isReserved(false)
                .isCompleted(false)
                .build();

        // when
        entityManager.persist(product);
        entityManager.flush();
        entityManager.clear();
        Product savedProduct = entityManager.find(Product.class, product.getProductId());

        // then
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getProductId()).isNotNull();
        assertThat(savedProduct.getTitle()).isEqualTo("테스트 상품");
        assertThat(savedProduct.getPrice()).isEqualTo(10000);
        assertThat(savedProduct.getIsCompleted()).isFalse();
    }

    @Test
    void checkProductCategoryAssociation() {
        // given
        Category category = Category.builder()
                .categoryName("디지털 기기")
                .build();
        entityManager.persist(category);

        Product product = Product.builder()
                .category(category)
                .title("노트북")
                .content("최신형 노트북 판매")
                .price(1500000)
                .build();
        entityManager.persist(product);

        entityManager.flush();
        entityManager.clear();

        // when
        Product foundProduct = entityManager.find(Product.class, product.getProductId());

        // then
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getCategory()).isNotNull();
        assertThat(foundProduct.getCategory().getCategoryName()).isEqualTo("디지털 기기");
    }

    @Test
    void checkProductUserAssociation() {
        // given
        User user = User.builder()
                .loginId("seller")
                .password("pwd")
                .userName("판매자")
                .phone("010-1234-5678")
                .build();
        entityManager.persist(user);

        Product product = Product.builder()
                .user(user)
                .title("의류")
                .content("새 의류 판매합니다.")
                .price(30000)
                .build();
        entityManager.persist(product);

        entityManager.flush();
        entityManager.clear();

        // when
        Product foundProduct = entityManager.find(Product.class, product.getProductId());

        // then
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getUser()).isNotNull();
        assertThat(foundProduct.getUser().getUserName()).isEqualTo("판매자");
    }
}