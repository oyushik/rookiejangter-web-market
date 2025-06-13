package com.miniproject.rookiejangter.entity;

import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "areas")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
@EqualsAndHashCode
public class Area extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "area_id")
    private Integer areaId;

    @NotBlank(message = "지역 이름은 필수입니다.")
    @Size(max = 50, message = "지역 이름은 최대 50자까지 가능합니다.")
    @Column(name = "area_name", length = 50)
    private String areaName;

    @OneToMany(mappedBy = "area", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users = new ArrayList<>();

    /**
     * 지역 이름을 변경합니다.
     *
     * @param newAreaName 새 지역 이름
     * @throws BusinessException 지역 이름이 비어있거나 길이가 50자를 초과하는 경우
     */
    public void changeAreaName(String newAreaName) {
        if (newAreaName == null || newAreaName.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.AREA_NAME_EMPTY);
        }
        if (newAreaName.length() > 50) {
            throw new BusinessException(ErrorCode.AREA_NAME_TOO_LONG);
        }
        this.areaName = newAreaName;
    }
}