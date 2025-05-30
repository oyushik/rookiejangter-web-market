package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.controller.dto.AreaDTO;
import com.miniproject.rookiejangter.entity.Area;
import com.miniproject.rookiejangter.repository.AreaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AreaService {

    private final AreaRepository areaRepository;

    @Transactional
    public AreaDTO.Response createArea(String areaName) {
        Area area = Area.builder()
                .areaName(areaName)
                .build();
        Area savedArea = areaRepository.save(area);
        return AreaDTO.Response.fromEntity(savedArea);
    }

    @Transactional(readOnly = true)
    public AreaDTO.Response getAreaById(Integer areaId) {
        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new EntityNotFoundException("ID에 해당하는 지역을 찾을 수 없습니다: " + areaId));
        return AreaDTO.Response.fromEntity(area);
    }

    @Transactional(readOnly = true)
    public List<AreaDTO.Response> getAllAreas() {
        return areaRepository.findAll().stream()
                .map(AreaDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public AreaDTO.Response updateArea(Integer areaId, String newAreaName) {
        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new EntityNotFoundException("ID에 해당하는 지역을 찾을 수 없습니다: " + areaId));

        area.setAreaName(newAreaName);
        Area updatedArea = areaRepository.save(area);
        return AreaDTO.Response.fromEntity(updatedArea);
    }

    @Transactional
    public void deleteArea(Integer areaId) {
        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new EntityNotFoundException("ID에 해당하는 지역을 찾을 수 없습니다: " + areaId));
        areaRepository.delete(area);
    }
}