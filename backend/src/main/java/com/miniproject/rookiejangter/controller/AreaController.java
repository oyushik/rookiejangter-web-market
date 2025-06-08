package com.miniproject.rookiejangter.controller;

import com.miniproject.rookiejangter.dto.AreaDTO;
import com.miniproject.rookiejangter.service.AreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/areas")
public class AreaController {

    private final AreaService areaService;

    // 전체 지역 조회
    @GetMapping
    public ResponseEntity<AreaDTO.ApiResponseWrapper<List<AreaDTO.Response>>> getAllAreas() {
        List<AreaDTO.Response> areas = areaService.getAllAreas();
        AreaDTO.ApiResponseWrapper<List<AreaDTO.Response>> response = new AreaDTO.ApiResponseWrapper<>();
        response.setSuccess(true);
        response.setData(areas);
        return ResponseEntity.ok(response);
    }
}