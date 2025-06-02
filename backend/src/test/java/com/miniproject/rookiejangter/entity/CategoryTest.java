package com.miniproject.rookiejangter.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import jakarta.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void createCategory() {
        // given
        Category category = Category.builder()
                .categoryName("전자제품")
                .build();

        // when
        entityManager.persist(category);
        entityManager.flush();
        entityManager.clear();
        Category savedCategory = entityManager.find(Category.class, category.getCategoryId());

        // then
        assertThat(savedCategory).isNotNull();
        assertThat(savedCategory.getCategoryId()).isNotNull();
        assertThat(savedCategory.getCategoryName()).isEqualTo("전자제품");
    }

    @Test
    void checkCategoryProductAssociation() {
        // given
        Category category = Category.builder()
                .categoryName("도서")
                .build();
        entityManager.persist(category);

        User user = User.builder()
                .loginId("seller1")
                .password("pwd")
                .userName("판매자1")
                .phone("010-1111-2222")
                .build();
        entityManager.persist(user);

        Product product1 = Product.builder()
                .category(category)
                .user(user)
                .title("자바의 정석")
                .content("자바 기본 서적")
                .price(30000)
                .build();
        entityManager.persist(product1);

        Product product2 = Product.builder()
                .category(category)
                .user(user)
                .title("스프링 부트")
                .content("스프링 부트 학습 서적")
                .price(40000)
                .build();
        entityManager.persist(product2);

        entityManager.flush();
        entityManager.clear();

        // when
        Category foundCategory = entityManager.find(Category.class, category.getCategoryId());

        // then
        assertThat(foundCategory).isNotNull();
        assertThat(foundCategory.getProducts()).hasSize(2);
        assertThat(foundCategory.getProducts()).extracting("title").containsExactly("자바의 정석", "스프링 부트");
        assertThat(product1.getCategory()).isEqualTo(foundCategory);
        assertThat(product2.getCategory()).isEqualTo(foundCategory);
    }
}