package com.miniproject.rookiejangter.entity;

import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;

    @NotBlank(message = "카테고리 이름은 필수입니다.")
    @Size(max = 20, message = "카테고리 이름은 최대 20자까지 가능합니다.")
    @Column(name = "category_name", length = 20)
    private String categoryName;

    // 비즈니스 메서드: 카테고리 이름 변경
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

        public void changeCategoryName(String newCategoryName) {
        if (newCategoryName == null || newCategoryName.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.CATEGORY_NAME_EMPTY);
        }
        if (newCategoryName.length() > 20) {
            throw new BusinessException(ErrorCode.CATEGORY_NAME_TOO_LONG);
        }
        this.categoryName = newCategoryName;
    }
}
