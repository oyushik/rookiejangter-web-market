package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Chat;
import com.miniproject.rookiejangter.entity.Post;
import com.miniproject.rookiejangter.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ChatRepositoryTest {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User buyer1;
    private User seller1;
    private User buyer2;
    private Post post1;
    private Post post2;
    private Chat chat1;
    private Chat chat2;
    private Chat chat3;

    @BeforeEach
    void setUp() {
        buyer1 = User.builder()
                .userName("구매자1")
                .loginId("buyer1")
                .password("password")
                .phone("010-1234-1234")
                .build();
        entityManager.persist(buyer1);

        seller1 = User.builder()
                .userName("판매자1")
                .loginId("seller1")
                .password("password")
                .phone("010-5678-5678")
                .build();
        entityManager.persist(seller1);

        buyer2 = User.builder()
                .userName("구매자2")
                .loginId("buyer2")
                .password("password")
                .phone("010-1414-6868")
                .build();
        entityManager.persist(buyer2);

        post1 = Post.builder()
                .title("Test Post 1")
                .content("Test Content 1")
                .user(seller1)
                .build();
        entityManager.persist(post1);

        post2 = Post.builder()
                .title("Test Post 2")
                .content("Test Content 2")
                .user(seller1)
                .build();
        entityManager.persist(post2);
        entityManager.flush();

        chat1 = Chat.builder()
                .buyer(buyer1)
                .seller(seller1)
                .post(post1)
                .build();
        entityManager.persist(chat1);

        chat2 = Chat.builder()
                .buyer(buyer2)
                .seller(seller1)
                .post(post1)
                .build();
        entityManager.persist(chat2);

        chat3 = Chat.builder()
                .buyer(buyer1)
                .seller(seller1)
                .post(post2)
                .build();
        entityManager.persist(chat3);
        entityManager.flush();
    }

    @Test
    void saveChat() {
        Chat newChat = Chat.builder()
                .buyer(buyer2)
                .seller(seller1)
                .post(post2)
                .build();
        Chat savedChat = chatRepository.save(newChat);

        Optional<Chat> foundChat = chatRepository.findById(savedChat.getChatId());
        assertThat(foundChat).isPresent();
        assertThat(foundChat.get().getBuyer().getUserId()).isEqualTo(buyer2.getUserId());
        assertThat(foundChat.get().getSeller().getUserId()).isEqualTo(seller1.getUserId());
        assertThat(foundChat.get().getPost().getPostId()).isEqualTo(post2.getPostId());
        assertThat(foundChat.get().getCreatedAt()).isNotNull(); // 자동 생성 확인
        assertThat(foundChat.get().getUpdatedAt()).isNotNull(); // 자동 생성 확인
    }

    @Test
    void findByChatId() {
        Optional<Chat> foundChat = chatRepository.findById(chat1.getChatId());
        assertThat(foundChat).isPresent();
        assertThat(foundChat.get().getBuyer().getUserId()).isEqualTo(buyer1.getUserId());
        assertThat(foundChat.get().getSeller().getUserId()).isEqualTo(seller1.getUserId());
        assertThat(foundChat.get().getPost().getPostId()).isEqualTo(post1.getPostId());
    }

    @Test
    void findByBuyerId() {
        List<Chat> foundChats = chatRepository.findByBuyer_UserId(buyer1.getUserId());
        assertThat(foundChats).hasSize(2);
        assertThat(foundChats).extracting(Chat::getPost).extracting(Post::getPostId)
                .containsExactlyInAnyOrder(post1.getPostId(), post2.getPostId());
    }

    @Test
    void findBySellerId() {
        List<Chat> foundChats = chatRepository.findBySeller_UserId(seller1.getUserId());
        assertThat(foundChats).hasSize(3);
        assertThat(foundChats).extracting(Chat::getPost).extracting(Post::getPostId)
                .containsExactlyInAnyOrder(post1.getPostId(), post1.getPostId(), post2.getPostId());
        assertThat(foundChats).extracting(Chat::getBuyer).extracting(User::getUserId)
                .containsExactlyInAnyOrder(buyer1.getUserId(), buyer2.getUserId(), buyer1.getUserId());
    }

    @Test
    void findByPostId() {
        List<Chat> foundChats = chatRepository.findByPost_PostId(post1.getPostId());
        assertThat(foundChats).hasSize(2);
        assertThat(foundChats).extracting(Chat::getBuyer).extracting(User::getUserId)
                .containsExactlyInAnyOrder(buyer1.getUserId(), buyer2.getUserId());
    }
}