package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Area;
import com.miniproject.rookiejangter.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Area area;

    @BeforeEach
    void setUp() {
        // Given
        area = Area.builder()
                .areaName("Test Area")
                .build();
        areaRepository.save(area);
        entityManager.persist(area);
    }

    @Test
    void createUser() {
        // Given
        User user = User.builder()
                .area(area)
                .loginId("testId")
                .password("testPassword")
                .userName("Test User")
                .phone("010-1234-5678")
                .isBanned(false)
                .isAdmin(false)
                .build();

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertThat(savedUser.getUserId()).isNotNull();
    }

    @Test
    void getUserById() {
        // Given
        User user = User.builder()
                .area(area)
                .loginId("testId")
                .password("testPassword")
                .userName("Test User")
                .phone("010-1234-5678")
                .isBanned(false)
                .isAdmin(false)
                .build();
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getUserId();

        // When
        User foundUser = userRepository.findById(userId).orElse(null);

        // Then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getLoginId()).isEqualTo("testId");
    }

    @Test
    void getUserByLoginId() {
        // Given
        User user = User.builder()
                .area(area)
                .loginId("testId")
                .password("testPassword")
                .userName("Test User")
                .phone("010-1234-5678")
                .isBanned(false)
                .isAdmin(false)
                .build();
        userRepository.save(user);

        // When
        User foundUser = userRepository.findByLoginId("testId").orElse(null);

        // Then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUserName()).isEqualTo("Test User");
    }

    @Test
    void updateUser() {
        // Given
        User user = User.builder()
                .area(area)
                .loginId("testId")
                .password("testPassword")
                .userName("Test User")
                .phone("010-1234-5678")
                .isBanned(false)
                .isAdmin(false)
                .build();
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getUserId();

        // When
        User foundUser = userRepository.findById(userId).orElse(null);
        foundUser.setUserName("Updated User");
        User updatedUser = userRepository.save(foundUser);

        // Then
        assertThat(updatedUser.getUserName()).isEqualTo("Updated User");
    }

    @Test
    void deleteUser() {
        // Given
        User user = User.builder()
                .area(area)
                .loginId("testId")
                .password("testPassword")
                .userName("Test User")
                .phone("010-1234-5678")
                .isBanned(false)
                .isAdmin(false)
                .build();
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getUserId();

        // When
        userRepository.deleteById(userId);
        User deletedUser = userRepository.findById(userId).orElse(null);

        // Then
        assertThat(deletedUser).isNull();
    }
}