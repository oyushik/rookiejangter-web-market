package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Goods;
import com.miniproject.rookiejangter.entity.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class GoodsRepositoryTest {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Post testPost;
    private Goods testGoods;

    @BeforeEach
    void setUp() {
        // Given
        testPost = Post.builder()
                .title("Test Post")
                .content("Test Content")
                .build();
        entityManager.persist(testPost);

        testGoods = Goods.builder()
                .post(testPost)
                .goodsName("Test Goods")
                .price(10000)
                .build();
        entityManager.persist(testGoods);
        entityManager.flush();
    }

    @Test
    void findByPost_PostId_shouldReturnGoods_whenPostIdExists() {
        // When
        Goods foundGoods = goodsRepository.findByPost_PostId(testPost.getPostId()).orElse(null);

        // Then
        assertThat(foundGoods).isNotNull();
        assertThat(foundGoods.getPrice()).isEqualTo(10000);
    }

    @Test
    void findByPost_PostId_shouldReturnEmptyOptional_whenPostIdDoesNotExist() {
        // When
        Goods foundGoods = goodsRepository.findByPost_PostId(999L).orElse(null); // Non-existent Post ID

        // Then
        assertThat(foundGoods).isNull();
    }
}