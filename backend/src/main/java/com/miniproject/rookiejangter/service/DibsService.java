package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.dto.DibsDTO;
import com.miniproject.rookiejangter.entity.Dibs;
import com.miniproject.rookiejangter.entity.Product;
import com.miniproject.rookiejangter.entity.User;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.repository.DibsRepository;
import com.miniproject.rookiejangter.repository.ProductRepository;
import com.miniproject.rookiejangter.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DibsService {

    private final DibsRepository dibsRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    /**
     * 찜 상태를 토글합니다. 이미 찜한 상품이면 찜을 해제하고, 찜하지 않은 상품이면 찜을 추가합니다.
     *
     * @param userId    사용자 ID
     * @param productId 상품 ID
     * @return 찜 상태 응답 DTO
     */
    public DibsDTO.Response toggleDibs(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, productId));

        Optional<Dibs> existingDibs = dibsRepository.findByUser_UserIdAndProduct_ProductId(userId, productId);

        Boolean isNowLiked;
        if (existingDibs.isPresent()) {
            dibsRepository.delete(existingDibs.get());
            isNowLiked = false;
        } else {

            if (product.getUser() != null && product.getUser().getUserId().equals(userId)) {
                throw new BusinessException("자신이 등록한 상품은 찜할 수 없습니다.");
            }
            Dibs newDibs = Dibs.builder()
                    .user(user)
                    .product(product)
                    .createdAt(LocalDateTime.now())
                    .build();
            dibsRepository.save(newDibs);
            isNowLiked = true;
        }
        return DibsDTO.Response.builder()
                .productId(productId)
                .isDibbed(isNowLiked)
                .build();
    }

    /**
     * 특정 사용자가 특정 상품에 대해 찜 상태를 조회합니다.
     *
     * @param userId    사용자 ID
     * @param productId 상품 ID
     * @return 찜 상태 응답 DTO
     */
    @Transactional(readOnly = true)
    public DibsDTO.Response getDibsStatus(Long userId, Long productId) {
        // 먼저 Product 존재 여부를 확인합니다.
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, productId));

        // Product가 존재할 경우에만 찜 상태를 확인합니다.
        Boolean isLiked = dibsRepository.existsByUser_UserIdAndProduct_ProductId(userId, productId);

        if (isLiked) {
            List<Dibs> dibsForProduct = dibsRepository.findByProduct_ProductId(productId);
            Optional<Dibs> userDibsOpt = dibsForProduct.stream()
                    .filter(d -> d.getUser().getUserId().equals(userId))
                    .findFirst();
            if (userDibsOpt.isPresent()) {
                return DibsDTO.Response.fromEntity(userDibsOpt.get(), true);
            }
            Dibs tempDibs = Dibs.builder().product(product).createdAt(LocalDateTime.now()).build();
            return DibsDTO.Response.fromEntity(tempDibs, true);
        } else {
            Dibs tempDibs = Dibs.builder().product(product).build();
            return DibsDTO.Response.fromEntity(tempDibs, false);
        }
    }

    /**
     * 특정 사용자의 찜 목록을 페이지네이션하여 조회합니다.
     *
     * @param userId   사용자 ID
     * @param pageable 페이지네이션 정보
     * @return 찜 목록 응답 DTO
     */
    @Transactional(readOnly = true)
    public DibsDTO.DibsListResponse getUserDibsList(Long userId, Pageable pageable) {
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));

        Page<Dibs> dibsPage = dibsRepository.findByUser_UserId(userId, pageable);
        return DibsDTO.DibsListResponse.fromPage(dibsPage);
    }

    /**
     * 특정 상품에 대한 찜 개수를 조회합니다.
     *
     * @param productId 상품 ID
     * @return 찜 개수
     */
    @Transactional(readOnly = true)
    public long getDibsCountForProduct(Long productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, productId));
        return dibsRepository.findByProduct_ProductId(productId).size();
    }
}