package com.miniproject.rookiejangter.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MessageTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void createMessage() {
        // given
        Message message = Message.builder()
                .content("안녕하세요!")
                .sentAt(LocalDateTime.now())
                .isRead(false)
                .build();

        // when
        entityManager.persist(message);
        entityManager.flush();
        entityManager.clear();
        Message savedMessage = entityManager.find(Message.class, message.getMessageId());

        // then
        assertThat(savedMessage).isNotNull();
        assertThat(savedMessage.getMessageId()).isNotNull();
        assertThat(savedMessage.getContent()).isEqualTo("안녕하세요!");
        assertThat(savedMessage.getIsRead()).isFalse();
    }

    @Test
    void checkMessageAssociations() {
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
                .categoryName("음반")
                .build();
        entityManager.persist(category);

        Product product = Product.builder()
                .user(seller)
                .category(category)
                .title("LP")
                .content("희귀 LP 판매")
                .price(50000)
                .build();
        entityManager.persist(product);

        Chat chat = Chat.builder()
                .buyer(buyer)
                .seller(seller)
                .product(product)
                .build();
        entityManager.persist(chat);

        Message message = Message.builder()
                .chat(chat)
                .user(buyer) // sender is buyer
                .content("구매 문의드립니다.")
                .sentAt(LocalDateTime.now())
                .isRead(false)
                .build();
        entityManager.persist(message);

        entityManager.flush();
        entityManager.clear();

        // when
        Message foundMessage = entityManager.find(Message.class, message.getMessageId());

        // then
        assertThat(foundMessage).isNotNull();
        assertThat(foundMessage.getChat()).isNotNull();
        assertThat(foundMessage.getChat().getChatId()).isEqualTo(chat.getChatId());
        assertThat(foundMessage.getUser()).isNotNull();
        assertThat(foundMessage.getUser().getUserName()).isEqualTo("구매자1");
        assertThat(foundMessage.getContent()).isEqualTo("구매 문의드립니다.");
    }
}