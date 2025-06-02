package com.miniproject.rookiejangter.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import jakarta.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ImageTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void createImage() {
        // given
        Image image = Image.builder()
                .imageUrl("http://example.com/test.jpg")
                .build();

        // when
        entityManager.persist(image);
        entityManager.flush();
        entityManager.clear();
        Image savedImage = entityManager.find(Image.class, image.getImageId());

        // then
        assertThat(savedImage).isNotNull();
        assertThat(savedImage.getImageId()).isNotNull();
        assertThat(savedImage.getImageUrl()).isEqualTo("http://example.com/test.jpg");
    }

    @Test
    void checkImageProductAssociation() {
        // given
        User user = User.builder()
                .loginId("seller2")
                .password("pwd")
                .userName("판매자2")
                .phone("010-3333-4444")
                .build();
        entityManager.persist(user);

        Category category = Category.builder()
                .categoryName("가전")
                .build();
        entityManager.persist(category);

        Product product = Product.builder()
                .user(user)
                .category(category)
                .title("냉장고")
                .content("새 냉장고 판매")
                .price(500000)
                .build();
        entityManager.persist(product);

        Image image = Image.builder()
                .product(product)
                .imageUrl("http://example.com/fridge.jpg")
                .build();
        entityManager.persist(image);

        entityManager.flush();
        entityManager.clear();

        // when
        Image foundImage = entityManager.find(Image.class, image.getImageId());

        // then
        assertThat(foundImage).isNotNull();
        assertThat(foundImage.getProduct()).isNotNull();
        assertThat(foundImage.getProduct().getTitle()).isEqualTo("냉장고");
    }
}