package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaRepository extends JpaRepository<Area, Integer> {
    Area findByAreaId(Integer areaId);
    Area findByAreaName(String areaName);
}