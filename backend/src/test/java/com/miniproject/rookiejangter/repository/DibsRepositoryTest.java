package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Dibs;
import com.miniproject.rookiejangter.entity.Post;
import com.miniproject.rookiejangter.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // JPA 관련 테스트 설정
public class DibsRepositoryTest {

    @Autowired
    private DibsRepository dibsRepository;

    @Autowired
    private TestEntityManager entityManager; // 테스트용 Entity Manager

    private User user;
    private Post post;
    private Dibs dibs;

    @BeforeEach // 각 테스트 메서드 실행 전 실행
    public void setUp() {
        // User, Post, Dibs 객체 생성 및 저장
        user = User.builder()
                .loginId("testUser")
                .password("password")
                .userName("테스트유저")
                .phone("010-1234-5678")
                .build();
        entityManager.persist(user);

        post = Post.builder()
                .title("테스트 게시글")
                .content("테스트 내용")
                .user(user)
                .build();
        entityManager.persist(post);

        dibs = Dibs.builder()
                .user(user)
                .post(post)
                .addedAt(LocalDateTime.now())
                .build();
        entityManager.persist(dibs);
        entityManager.flush(); // 영속성 컨텍스트 변경내용 DB에 즉시 반영
    }

    @Test
    public void findByUser_UserId() {
        // 특정 User의 찜 목록 조회 테스트
        List<Dibs> dibsList = dibsRepository.findByUser_UserId(user.getUserId());
        assertThat(dibsList).isNotEmpty();
        assertThat(dibsList).contains(dibs);
    }

    @Test
    public void findByPost_PostId() {
        // 특정 Post를 찜한 목록 조회 테스트
        List<Dibs> dibsList = dibsRepository.findByPost_PostId(post.getPostId());
        assertThat(dibsList).isNotEmpty();
        assertThat(dibsList).contains(dibs);
    }

    @Test
    public void existsByUser_UserIdAndPost_PostId() {
        // 특정 User가 특정 Post를 찜했는지 확인하는 테스트
        boolean exists = dibsRepository.existsByUser_UserIdAndPost_PostId(user.getUserId(), post.getPostId());
        assertThat(exists).isTrue();
    }

    @Test
    public void deleteByUser_UserIdAndPost_PostId() {
        // 특정 User가 특정 Post를 찜한 것을 삭제하는 테스트
        dibsRepository.deleteByUser_UserIdAndPost_PostId(user.getUserId(), post.getPostId());
        boolean exists = dibsRepository.existsByUser_UserIdAndPost_PostId(user.getUserId(), post.getPostId());
        assertThat(exists).isFalse();
    }
}