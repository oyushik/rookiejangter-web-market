package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Area;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AreaRepository extends JpaRepository<Area, Integer> {
    Optional<Area> findByAreaId(Integer areaId);
}