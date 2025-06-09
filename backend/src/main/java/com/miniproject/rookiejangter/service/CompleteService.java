package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.dto.CompleteDTO;
import com.miniproject.rookiejangter.entity.Complete;
import com.miniproject.rookiejangter.entity.Product;
import com.miniproject.rookiejangter.entity.User;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.repository.CompleteRepository;
import com.miniproject.rookiejangter.repository.ProductRepository; // ProductRepository 가정
import com.miniproject.rookiejangter.repository.UserRepository; // UserRepository 가정
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompleteService {

    private final CompleteRepository completeRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * 상품 거래 완료를 생성합니다.
     *
     * @param productId 상품 ID
     * @param buyerId   구매자 ID
     * @param sellerId  판매자 ID
     * @return 생성된 거래 완료 정보
     */
    @Transactional
    public CompleteDTO.Response createComplete(Long productId, Long buyerId, Long sellerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, productId));

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, buyerId));

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, sellerId));

        if (completeRepository.findByProduct_ProductId(productId).isPresent()) {
            throw new BusinessException(ErrorCode.PRODUCT_ALREADY_COMPLETED, productId);
        }

        Complete complete = Complete.builder()
                .product(product)
                .buyer(buyer)
                .seller(seller)
                .completedAt(LocalDateTime.now())
                .build();

        Complete savedComplete = completeRepository.save(complete);
        return CompleteDTO.Response.fromEntity(savedComplete);
    }

    /**
     * 특정 상품의 거래 완료 정보를 조회합니다.
     *
     * @param productId 상품 ID
     * @return 거래 완료 정보
     */
    @Transactional(readOnly = true)
    public CompleteDTO.Response getCompleteByProductId(Long productId) {
        Complete complete = completeRepository.findByProduct_ProductId(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMPLETE_RECORD_NOT_FOUND_BY_PRODUCT_ID, productId));
        return CompleteDTO.Response.fromEntity(complete);
    }

    /**
     * 특정 거래 완료 ID에 대한 거래 완료 정보를 조회합니다.
     *
     * @param completeId 거래 완료 ID
     * @return 거래 완료 정보
     */
    @Transactional(readOnly = true)
    public List<CompleteDTO.Response> getCompletesByBuyerId(Long buyerId) {
        return completeRepository.findByBuyer_UserId(buyerId).stream()
                .map(CompleteDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 특정 판매자의 거래 완료 정보를 조회합니다.
     *
     * @param sellerId 판매자 ID
     * @return 거래 완료 정보 리스트
     */
    @Transactional(readOnly = true)
    public List<CompleteDTO.Response> getCompletesBySellerId(Long sellerId) {
        return completeRepository.findBySeller_UserId(sellerId).stream()
                .map(CompleteDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 모든 거래 완료 정보를 조회합니다.
     *
     * @return 거래 완료 정보 리스트
     */
    @Transactional(readOnly = true)
    public List<CompleteDTO.Response> getAllCompletes() {
        return completeRepository.findAll().stream()
                .map(CompleteDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }
}