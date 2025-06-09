package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.dto.BumpDTO;
import com.miniproject.rookiejangter.entity.Bump;
import com.miniproject.rookiejangter.entity.Product;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.repository.BumpRepository;
import com.miniproject.rookiejangter.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BumpService {

    private final BumpRepository bumpRepository;
    private final ProductRepository productRepository;
    private static final int MAX_BUMPS_PER_DAY = 3; // 일일 최대 끌어올리기 횟수

    /**
     * 특정 상품을 끌어올립니다.
     *
     * @param productId 끌어올릴 상품 ID
     * @return BumpDTO.Response 끌어올린 정보
     */
    @Transactional
    public BumpDTO.Response bumpProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, productId));

        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        long bumpsToday = bumpRepository.countByProduct_ProductIdAndBumpedAtBetween(productId, todayStart, todayEnd); //
        if (bumpsToday >= MAX_BUMPS_PER_DAY) {
            throw new BusinessException(ErrorCode.PRODUCT_CANNOT_BUMP, "일일 최대 끌어올리기 횟수(" + MAX_BUMPS_PER_DAY + "회)를 초과했습니다.");
        }

        Optional<Bump> latestBumpOpt = bumpRepository.findTopByProduct_ProductIdOrderByBumpedAtDesc(productId); //
        int nextBumpCount = latestBumpOpt.map(bump -> bump.getBumpCount() + 1).orElse(1);

        Bump newBump = Bump.builder()
                .product(product)
                .bumpedAt(LocalDateTime.now())
                .bumpCount(nextBumpCount)
                .build();
        Bump savedBump = bumpRepository.save(newBump);

        product.markAsBumped(true);
        productRepository.save(product);

        return BumpDTO.Response.fromEntity(savedBump);
    }

    /**
     * 특정 상품의 최신 끌어올리기 정보를 조회합니다.
     *
     * @param productId 상품 ID
     * @return BumpDTO.Response 최신 끌어올리기 정보
     */
    @Transactional(readOnly = true)
    public Optional<BumpDTO.Response> getLatestBumpForProduct(Long productId) {
        return bumpRepository.findTopByProduct_ProductIdOrderByBumpedAtDesc(productId) //
                .map(BumpDTO.Response::fromEntity);
    }

    /**
     * 특정 상품의 모든 끌어올리기 정보를 조회합니다.
     *
     * @param productId 상품 ID
     * @return List<BumpDTO.Response> 끌어올리기 정보 리스트
     */
    @Transactional(readOnly = true)
    public List<BumpDTO.Response> getBumpsForProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, productId);
        }
        return bumpRepository.findByProduct_ProductId(productId).stream() //
                .map(BumpDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 오늘 특정 상품에 대해 끌어올린 횟수를 조회합니다.
     *
     * @param productId 상품 ID
     * @return Long 오늘 끌어올린 횟수
     */
    @Transactional(readOnly = true)
    public Long getTodaysBumpCountForProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, productId);
        }
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return bumpRepository.countByProduct_ProductIdAndBumpedAtBetween(productId, todayStart, todayEnd); //
    }
}