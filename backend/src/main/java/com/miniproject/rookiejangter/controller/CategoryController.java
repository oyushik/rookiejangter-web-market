package com.miniproject.rookiejangter.controller;

import com.miniproject.rookiejangter.dto.CategoryDTO;
import com.miniproject.rookiejangter.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<CategoryDTO.ApiResponseWrapper<List<CategoryDTO.Response>>> getAllCategories() {
        List<CategoryDTO.Response> categories = categoryService.getAllCategories();
        CategoryDTO.ApiResponseWrapper<List<CategoryDTO.Response>> response = new CategoryDTO.ApiResponseWrapper<>();
        response.setSuccess(true);
        response.setData(categories);
        return ResponseEntity.ok(response);
    }
}
