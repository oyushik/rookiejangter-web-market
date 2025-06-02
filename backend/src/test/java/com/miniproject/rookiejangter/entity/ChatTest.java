package com.miniproject.rookiejangter.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import jakarta.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ChatTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void createChat() {
        // given
        Chat chat = Chat.builder()
                .build();

        // when
        entityManager.persist(chat);
        entityManager.flush();
        entityManager.clear();
        Chat savedChat = entityManager.find(Chat.class, chat.getChatId());

        // then
        assertThat(savedChat).isNotNull();
        assertThat(savedChat.getChatId()).isNotNull();
    }

    @Test
    void checkChatAssociations() {
        // given
        User buyer = User.builder()
                .loginId("buyer1")
                .password("pwd")
                .userName("구매자1")
                .phone("010-1111-1111")
                .build();
        entityManager.persist(buyer);

        User seller = User.builder()
                .loginId("seller1")
                .password("pwd")
                .userName("판매자1")
                .phone("010-2222-2222")
                .build();
        entityManager.persist(seller);

        Category category = Category.builder()
                .categoryName("도서")
                .build();
        entityManager.persist(category);

        Product product = Product.builder()
                .user(seller)
                .category(category)
                .title("스프링 부트")
                .content("스프링 부트 학습")
                .price(35000)
                .build();
        entityManager.persist(product);

        Chat chat = Chat.builder()
                .buyer(buyer)
                .seller(seller)
                .product(product)
                .build();
        entityManager.persist(chat);

        entityManager.flush();
        entityManager.clear();

        // when
        Chat foundChat = entityManager.find(Chat.class, chat.getChatId());

        // then
        assertThat(foundChat).isNotNull();
        assertThat(foundChat.getBuyer()).isNotNull();
        assertThat(foundChat.getBuyer().getUserName()).isEqualTo("구매자1");
        assertThat(foundChat.getSeller()).isNotNull();
        assertThat(foundChat.getSeller().getUserName()).isEqualTo("판매자1");
        assertThat(foundChat.getProduct()).isNotNull();
        assertThat(foundChat.getProduct().getTitle()).isEqualTo("스프링 부트");
    }
}