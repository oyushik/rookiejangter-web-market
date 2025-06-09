package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.dto.CategoryDTO;
import com.miniproject.rookiejangter.entity.Category;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 카테고리를 생성합니다.
     *
     * @param categoryName 카테고리 이름
     * @return 생성된 카테고리 정보
     */
    @Transactional
    public CategoryDTO.Response createCategory(String categoryName) {
        if (categoryRepository.findByCategoryName(categoryName).isPresent()) {
            throw new BusinessException(ErrorCode.CATEGORY_NAME_ALREADY_EXISTS, categoryName);
        }
        Category category = Category.builder()
                .categoryName(categoryName)
                .build();
        Category savedCategory = categoryRepository.save(category);
        return CategoryDTO.Response.fromEntity(savedCategory);
    }

    /**
     * 카테고리를 ID로 조회합니다.
     *
     * @param categoryId 카테고리 ID
     * @return 카테고리 정보
     */
    @Transactional(readOnly = true)
    public CategoryDTO.Response getCategoryById(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND, categoryId));
        return CategoryDTO.Response.fromEntity(category);
    }

    /**
     * 카테고리를 이름으로 조회합니다.
     *
     * @param categoryName 카테고리 이름
     * @return 카테고리 정보
     */
    @Transactional(readOnly = true)
    public CategoryDTO.Response getCategoryByName(String categoryName) {
        Category category = categoryRepository.findByCategoryName(categoryName)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND, categoryName));
        return CategoryDTO.Response.fromEntity(category);
    }

    /**
     * 모든 카테고리를 조회합니다.
     *
     * @return 카테고리 리스트
     */
    @Transactional(readOnly = true)
    public List<CategoryDTO.Response> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리 이름으로 카테고리를 검색합니다.
     *
     * @param categoryName 카테고리 이름
     * @return 카테고리 리스트
     */
    @Transactional(readOnly = true)
    public List<CategoryDTO.Response> searchCategoriesByName(String categoryName) {
        return categoryRepository.findByCategoryNameContainingIgnoreCase(categoryName).stream()
                .map(CategoryDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리 이름을 업데이트합니다.
     *
     * @param categoryId 카테고리 ID
     * @param newCategoryName 새로운 카테고리 이름
     * @return 업데이트된 카테고리 정보
     */
    @Transactional
    public CategoryDTO.Response updateCategory(Integer categoryId, String newCategoryName) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND, categoryId));

        if (!category.getCategoryName().equals(newCategoryName) &&
                categoryRepository.findByCategoryName(newCategoryName).isPresent()) {
            throw new BusinessException(ErrorCode.CATEGORY_NAME_ALREADY_EXISTS, newCategoryName);
        }
        category.changeCategoryName(newCategoryName);

        return CategoryDTO.Response.fromEntity(category);
    }

    /**
     * 카테고리를 삭제합니다.
     *
     * @param categoryId 카테고리 ID
     */
    @Transactional
    public void deleteCategory(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND, categoryId));
        categoryRepository.delete(category);
    }
}