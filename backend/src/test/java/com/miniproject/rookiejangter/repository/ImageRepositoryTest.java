package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Image;
import com.miniproject.rookiejangter.entity.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ImageRepositoryTest {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private TestEntityManager entityManager; // 테스트용 Entity Manager

    private Post testPost;
    private Image testImage1, testImage2;

    @BeforeEach // 각 테스트 메서드 실행 전에 실행
    void setUp() {
        // 테스트에 필요한 Post 객체 생성 및 저장
        testPost = Post.builder()
                .title("Test Post")
                .content("Test Content")
                .price(10000)
                .build();
        entityManager.persist(testPost);

        // 테스트에 필요한 Image 객체 생성 및 저장
        testImage1 = Image.builder()
                .post(testPost)
                .imageUrl("http://example.com/image1.jpg")
                .build();
        testImage2 = Image.builder()
                .post(testPost)
                .imageUrl("http://example.com/image2.jpg")
                .build();
        entityManager.persist(testImage1);
        entityManager.persist(testImage2);
    }

    @Test
    void findByPost_PostId_ShouldReturnImagesForGivenPostId() {
        // When
        List<Image> images = imageRepository.findByPost_PostId(testPost.getPostId());

        // Then
        assertThat(images).hasSize(2);
        assertThat(images).containsExactly(testImage1, testImage2);
    }

    @Test
    void findByImageUrl_ShouldReturnImageForGivenImageUrl() {
        // When
        Optional<Image> image = imageRepository.findByImageUrl("http://example.com/image1.jpg");

        // Then
        assertThat(image).isPresent();
        assertThat(image.get()).isEqualTo(testImage1);
    }

    @Test
    void countByPost_PostId_ShouldReturnCorrectCountForGivenPostId() {
        // When
        long count = imageRepository.countByPost_PostId(testPost.getPostId());

        // Then
        assertThat(count).isEqualTo(2);
    }
}