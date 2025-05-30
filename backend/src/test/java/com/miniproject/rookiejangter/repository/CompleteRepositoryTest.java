package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Complete;
import com.miniproject.rookiejangter.entity.Post;
import com.miniproject.rookiejangter.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CompleteRepositoryTest {

    @Autowired
    private CompleteRepository completeRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User buyer1;
    private User seller1;
    private Post post1;
    private Complete complete1;

    @BeforeEach
    void setUp() {
        buyer1 = User.builder()
                .userName("구매자1")
                .loginId("buyer1")
                .password("password")
                .phone("010-1111-1111")
                .build();
        entityManager.persist(buyer1);

        seller1 = User.builder()
                .userName("판매자1")
                .loginId("seller1")
                .password("password")
                .phone("010-2222-2222")
                .build();
        entityManager.persist(seller1);

        post1 = Post.builder()
                .title("Test Post 1")
                .content("Test Content 1")
                .price(10000)
                .user(seller1)
                .build();
        entityManager.persist(post1);
        entityManager.flush(); // Post ID를 얻기 위해 flush

        complete1 = Complete.builder()
                .post(post1)
                .buyer(buyer1)
                .seller(seller1)
                .completedAt(LocalDateTime.now())
                .build();
        entityManager.persist(complete1);
        entityManager.flush();
    }

    @Test
    void saveComplete() {
        Post newPost = Post.builder()
                .title("New Test Post")
                .content("New Test Content")
                .price(20000)
                .user(seller1)
                .build();
        entityManager.persist(newPost);
        entityManager.flush();

        Complete savedComplete = completeRepository.save(Complete.builder()
                .post(newPost)
                .buyer(User.builder().userId(buyer1.getUserId()).build())
                .seller(User.builder().userId(seller1.getUserId()).build())
                .completedAt(LocalDateTime.now().plusHours(1))
                .build());

        Optional<Complete> foundComplete = completeRepository.findById(savedComplete.getPost().getPostId());
        assertThat(foundComplete).isPresent();
        assertThat(foundComplete.get().getBuyer().getUserId()).isEqualTo(buyer1.getUserId());
        assertThat(foundComplete.get().getSeller().getUserId()).isEqualTo(seller1.getUserId());
    }

    @Test
    void findByPostId() {
        Optional<Complete> foundComplete = completeRepository.findById(post1.getPostId());
        assertThat(foundComplete).isPresent();
        assertThat(foundComplete.get().getBuyer().getUserId()).isEqualTo(buyer1.getUserId());
        assertThat(foundComplete.get().getSeller().getUserId()).isEqualTo(seller1.getUserId());
    }

    @Test
    void findByBuyerId() {
        // Complete 엔티티에 buyerId 필드가 있다면 테스트 가능
        // 현재 Complete 엔티티 구조상 buyer 객체를 통해 접근해야 함
        List<Complete> foundComplete = completeRepository.findByBuyer_UserId(buyer1.getUserId());
        assertThat(foundComplete).isNotNull();
        assertThat(foundComplete.get(0).getPost().getPostId()).isEqualTo(post1.getPostId());
    }

    @Test
    void findBySellerId() {
        // Complete 엔티티에 sellerId 필드가 있다면 테스트 가능
        // 현재 Complete 엔티티 구조상 seller 객체를 통해 접근해야 함
        List<Complete> foundComplete = completeRepository.findBySeller_UserId(seller1.getUserId());
        assertThat(foundComplete).isNotNull();
        assertThat(foundComplete.get(0).getPost().getPostId()).isEqualTo(post1.getPostId());
    }
}