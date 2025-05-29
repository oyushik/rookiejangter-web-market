package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Area;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class AreaRepositoryTest {

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Area area1;
    private Area area2;

    @BeforeEach
    void setUp() {
        area1 = Area.builder()
                .areaName("서울")
                .build();
        entityManager.persist(area1);

        area2 = Area.builder()
                .areaName("부산")
                .build();
        entityManager.persist(area2);
        entityManager.flush();
    }

    @Test
    void saveArea() {
        Area newArea = Area.builder()
                .areaName("인천")
                .build();
        Area savedArea = areaRepository.save(newArea);

        Optional<Area> foundArea = areaRepository.findById(savedArea.getAreaId());
        assertThat(foundArea).isPresent();
        assertThat(foundArea.get().getAreaName()).isEqualTo("인천");
    }

    @Test
    void findByAreaId() {
        Optional<Area> foundArea = areaRepository.findById(area1.getAreaId());
        assertThat(foundArea).isPresent();
        assertThat(foundArea.get().getAreaName()).isEqualTo("서울");
    }
}