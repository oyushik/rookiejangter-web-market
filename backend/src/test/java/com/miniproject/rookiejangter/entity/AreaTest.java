package com.miniproject.rookiejangter.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import jakarta.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AreaTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void createArea() {
        // given
        Area area = Area.builder()
                .areaName("서울 강남구")
                .build();

        // when
        entityManager.persist(area);
        entityManager.flush();
        entityManager.clear();
        Area savedArea = entityManager.find(Area.class, area.getAreaId());

        // then
        assertThat(savedArea).isNotNull();
        assertThat(savedArea.getAreaId()).isNotNull();
        assertThat(savedArea.getAreaName()).isEqualTo("서울 강남구");
    }

    @Test
    void checkAreaUserAssociation() {
        // given
        Area area = Area.builder()
                .areaName("경기 성남시")
                .build();
        entityManager.persist(area);

        User user1 = User.builder()
                .area(area)
                .loginId("user3")
                .password("pwd")
                .userName("유저3")
                .phone("010-7777-8888")
                .build();
        entityManager.persist(user1);

        User user2 = User.builder()
                .area(area)
                .loginId("user4")
                .password("pwd")
                .userName("유저4")
                .phone("010-9999-0000")
                .build();
        entityManager.persist(user2);

        entityManager.flush();
        entityManager.clear();

        // when
        Area foundArea = entityManager.find(Area.class, area.getAreaId());

        // then
        assertThat(foundArea).isNotNull();
        assertThat(foundArea.getUsers()).hasSize(2);
        assertThat(foundArea.getUsers()).extracting("userName")
                .containsExactly("유저3", "유저4");
        assertThat(user1.getArea()).isEqualTo(foundArea);
        assertThat(user2.getArea()).isEqualTo(foundArea);
    }
}