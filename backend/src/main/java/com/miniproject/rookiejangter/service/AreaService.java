package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.dto.AreaDTO;
import com.miniproject.rookiejangter.entity.Area;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AreaService {

    private final AreaRepository areaRepository;

    /** 특정 지역 정보 생성
     * @param areaName 지역 이름
     * @return 생성된 지역 정보
     */
    @Transactional
    public AreaDTO.Response createArea(String areaName) {
        Area area = Area.builder()
                .areaName(areaName)
                .build();
        Area savedArea = areaRepository.save(area);
        return AreaDTO.Response.fromEntity(savedArea);
    }

    /**
     * 특정 지역 정보 조회
     * 
     * @param areaId 지역 ID
     * @return 지역 정보
     */
    @Transactional(readOnly = true)
    public AreaDTO.Response getAreaById(Integer areaId) {
        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AREA_NOT_FOUND, areaId));
        return AreaDTO.Response.fromEntity(area);
    }

    /**
     * 모든 지역 정보 조회
     * 
     * @return 지역 정보 리스트
     */
    @Transactional(readOnly = true)
    public List<AreaDTO.Response> getAllAreas() {
        return areaRepository.findAll().stream()
                .map(AreaDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /** 특정 지역 정보 수정
     * 
     * @param areaId 지역 ID
     * @param newAreaName 새로운 지역 이름
     * @return 수정된 지역 정보
     */
    @Transactional
    public AreaDTO.Response updateArea(Integer areaId, String newAreaName) {
        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AREA_NOT_FOUND, areaId));
        area.changeAreaName(newAreaName);
        return AreaDTO.Response.fromEntity(area);
    }

    /** 특정 지역 정보 삭제
     * 
     * @param areaId 지역 ID
     */
    @Transactional
    public void deleteArea(Integer areaId) {
        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AREA_NOT_FOUND, areaId));
        areaRepository.delete(area);
    }
}