package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Bump;
import com.miniproject.rookiejangter.entity.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BumpRepositoryTest {

    @Autowired
    private BumpRepository bumpRepository;

    @Autowired
    private PostRepository postRepository;

    private Post testPost;
    private Bump testBump1;
    private Bump testBump2;

    @BeforeEach
    void setUp() {
        // Given
        testPost = Post.builder()
                .title("Test Post")
                .content("Test Content")
                .price(10000)
                .build();
        postRepository.save(testPost);

        testBump1 = Bump.builder()
                .post(testPost)
                .bumpedAt(LocalDateTime.now().minusDays(2))
                .bumpCount(1)
                .build();

        testBump2 = Bump.builder()
                .post(testPost)
                .bumpedAt(LocalDateTime.now())
                .bumpCount(2)
                .build();

        bumpRepository.save(testBump1);
        bumpRepository.save(testBump2);
    }

    @Test
    void findTopByPost_PostIdOrderByBumpedAtDesc() {
        // When
        Optional<Bump> latestBump = bumpRepository.findTopByPost_PostIdOrderByBumpedAtDesc(testPost.getPostId());

        // Then
        assertThat(latestBump).isPresent();
        assertThat(latestBump.get().getBumpCount()).isEqualTo(2);
    }

    @Test
    void findByPost_PostId() {
        // When
        List<Bump> bumps = bumpRepository.findByPost_PostId(testPost.getPostId());

        // Then
        assertThat(bumps).hasSize(2);
        assertThat(bumps.get(0).getPost().getPostId()).isEqualTo(testPost.getPostId());
    }

    @Test
    void countByPost_PostIdAndBumpedAtBetween() {
        // Given
        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        // When
        Long count = bumpRepository.countByPost_PostIdAndBumpedAtBetween(testPost.getPostId(), start, end);

        // Then
        assertThat(count).isEqualTo(2);
    }
}