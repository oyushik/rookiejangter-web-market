package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.controller.dto.DibsDTO;
import com.miniproject.rookiejangter.entity.Dibs;
import com.miniproject.rookiejangter.entity.Product;
import com.miniproject.rookiejangter.entity.User;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.repository.DibsRepository;
import com.miniproject.rookiejangter.repository.ProductRepository;
import com.miniproject.rookiejangter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DibsService {

    private final DibsRepository dibsRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public DibsDTO.Response addDibs(Long userId, Long productId) {
        if (dibsRepository.existsByUser_UserIdAndProduct_ProductId(userId, productId)) {
            throw new BusinessException(ErrorCode.DIBS_ALREADY_EXISTS, userId, productId);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, productId));

        Dibs dibs = Dibs.builder()
                .user(user)
                .product(product)
                .addedAt(LocalDateTime.now())
                .build();
        Dibs savedDibs = dibsRepository.save(dibs);
        return DibsDTO.Response.fromEntity(savedDibs, true);
    }

    @Transactional
    public void removeDibs(Long userId, Long productId) {
        if (!dibsRepository.existsByUser_UserIdAndProduct_ProductId(userId, productId)) {
            throw new BusinessException(ErrorCode.DIBS_NOT_FOUND, userId, productId);
        }
        dibsRepository.deleteByUser_UserIdAndProduct_ProductId(userId, productId);
    }

    @Transactional(readOnly = true)
    public DibsDTO.Response getDibsStatus(Long userId, Long productId) {
        // 먼저 Product 존재 여부를 확인합니다.
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, productId));

        // Product가 존재할 경우에만 찜 상태를 확인합니다.
        boolean isLiked = dibsRepository.existsByUser_UserIdAndProduct_ProductId(userId, productId);

        if (isLiked) {
            List<Dibs> dibsForProduct = dibsRepository.findByProduct_ProductId(productId);
            Optional<Dibs> userDibsOpt = dibsForProduct.stream()
                    .filter(d -> d.getUser().getUserId().equals(userId))
                    .findFirst();
            if (userDibsOpt.isPresent()) {
                return DibsDTO.Response.fromEntity(userDibsOpt.get(), true);
            }
            Dibs tempDibs = Dibs.builder().product(product).addedAt(LocalDateTime.now()).build();
            return DibsDTO.Response.fromEntity(tempDibs, true);
        } else {
            Dibs tempDibs = Dibs.builder().product(product).build();
            return DibsDTO.Response.fromEntity(tempDibs, false);
        }
    }

    @Transactional(readOnly = true)
    public List<DibsDTO.Response.DibbedProduct> getUserDibsList(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));

        List<Dibs> dibsList = dibsRepository.findByUser_UserId(userId);

        return dibsList.stream()
                .map(DibsDTO.Response.DibbedProduct::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getDibsCountForProduct(Long productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, productId));
        return dibsRepository.findByProduct_ProductId(productId).size();
    }
}