package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Category;
import com.miniproject.rookiejangter.entity.Post;
import com.miniproject.rookiejangter.entity.User;
import com.miniproject.rookiejangter.repository.CategoryRepository;
import com.miniproject.rookiejangter.repository.PostRepository;
import com.miniproject.rookiejangter.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Category testCategory;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Given
        testCategory = Category.builder()
                .categoryName("Test Category")
                .build();
        testCategory = categoryRepository.save(testCategory);

        testUser = User.builder()
                .loginId("testId")
                .password("testPassword")
                .userName("Test User")
                .phone("01012345678")
                .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    void createPost() {
        // Given
        Post post = Post.builder()
                .category(testCategory)
                .user(testUser)
                .title("Test Post Title")
                .content("Test Post Content")
                .viewCount(0)
                .isBumped(false)
                .isReserved(false)
                .isCompleted(false)
                .build();

        // When
        Post savedPost = postRepository.save(post);

        // Then
        assertThat(savedPost.getPostId()).isNotNull();
        assertThat(savedPost.getTitle()).isEqualTo("Test Post Title");
    }

    @Test
    void getPostById() {
        // Given
        Post post = Post.builder()
                .category(testCategory)
                .user(testUser)
                .title("Test Post Title")
                .content("Test Post Content")
                .viewCount(0)
                .isBumped(false)
                .isReserved(false)
                .isCompleted(false)
                .build();
        post = postRepository.save(post);

        // When
        Post foundPost = postRepository.findById(post.getPostId()).orElse(null);

        // Then
        assertThat(foundPost).isNotNull();
        assertThat(foundPost.getTitle()).isEqualTo("Test Post Title");
    }

    @Test
    void updatePost() {
        // Given
        Post post = Post.builder()
                .category(testCategory)
                .user(testUser)
                .title("Test Post Title")
                .content("Test Post Content")
                .viewCount(0)
                .isBumped(false)
                .isReserved(false)
                .isCompleted(false)
                .build();
        post = postRepository.save(post);

        // When
        post.setTitle("Updated Post Title");
        Post updatedPost = postRepository.save(post);

        // Then
        assertThat(updatedPost.getTitle()).isEqualTo("Updated Post Title");
    }

    @Test
    void deletePost() {
        // Given
        Post post = Post.builder()
                .category(testCategory)
                .user(testUser)
                .title("Test Post Title")
                .content("Test Post Content")
                .viewCount(0)
                .isBumped(false)
                .isReserved(false)
                .isCompleted(false)
                .build();
        post = postRepository.save(post);

        // When
        postRepository.delete(post);
        Post deletedPost = postRepository.findById(post.getPostId()).orElse(null);

        // Then
        assertThat(deletedPost).isNull();
    }

    @Test
    void getPostsByCategory() {
        // Given
        Post post1 = Post.builder()
                .category(testCategory)
                .user(testUser)
                .title("Test Post Title 1")
                .content("Test Post Content 1")
                .viewCount(0)
                .isBumped(false)
                .isReserved(false)
                .isCompleted(false)
                .build();
        postRepository.save(post1);

        Post post2 = Post.builder()
                .category(testCategory)
                .user(testUser)
                .title("Test Post Title 2")
                .content("Test Post Content 2")
                .viewCount(0)
                .isBumped(false)
                .isReserved(false)
                .isCompleted(false)
                .build();
        postRepository.save(post2);

        // When
        List<Post> posts = postRepository.findByCategory(testCategory);

        // Then
        assertThat(posts).hasSize(2);
        assertThat(posts.get(0).getTitle()).isEqualTo("Test Post Title 1");
        assertThat(posts.get(1).getTitle()).isEqualTo("Test Post Title 2");
    }
}