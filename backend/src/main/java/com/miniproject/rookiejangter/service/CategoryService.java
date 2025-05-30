package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.controller.dto.CategoryDTO;
import com.miniproject.rookiejangter.entity.Category;
import com.miniproject.rookiejangter.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryDTO.Response createCategory(String categoryName) {
        if (categoryRepository.findByCategoryName(categoryName).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 카테고리 이름입니다: " + categoryName);
        }
        Category category = Category.builder()
                .categoryName(categoryName)
                .build();
        Category savedCategory = categoryRepository.save(category);
        return CategoryDTO.Response.fromEntity(savedCategory);
    }

    @Transactional(readOnly = true)
    public CategoryDTO.Response getCategoryById(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("ID에 해당하는 카테고리를 찾을 수 없습니다: " + categoryId));
        return CategoryDTO.Response.fromEntity(category);
    }

    @Transactional(readOnly = true)
    public CategoryDTO.Response getCategoryByName(String categoryName) {
        Category category = categoryRepository.findByCategoryName(categoryName)
                .orElseThrow(() -> new EntityNotFoundException("이름에 해당하는 카테고리를 찾을 수 없습니다: " + categoryName));
        return CategoryDTO.Response.fromEntity(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO.Response> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO.Response> searchCategoriesByName(String categoryName) {
        return categoryRepository.findByCategoryNameContainingIgnoreCase(categoryName).stream()
                .map(CategoryDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryDTO.Response updateCategory(Integer categoryId, String newCategoryName) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("ID에 해당하는 카테고리를 찾을 수 없습니다: " + categoryId));

        if (!category.getCategoryName().equals(newCategoryName) &&
                categoryRepository.findByCategoryName(newCategoryName).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 카테고리 이름입니다: " + newCategoryName);
        }

        category.setCategoryName(newCategoryName);
        Category updatedCategory = categoryRepository.save(category);
        return CategoryDTO.Response.fromEntity(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("ID에 해당하는 카테고리를 찾을 수 없습니다: " + categoryId));
        categoryRepository.delete(category);
    }
}