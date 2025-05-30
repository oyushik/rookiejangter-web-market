package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.controller.dto.CompleteDTO;
import com.miniproject.rookiejangter.entity.Complete;
import com.miniproject.rookiejangter.entity.Product;
import com.miniproject.rookiejangter.entity.User;
import com.miniproject.rookiejangter.repository.CompleteRepository;
import com.miniproject.rookiejangter.repository.ProductRepository; // ProductRepository 가정
import com.miniproject.rookiejangter.repository.UserRepository; // UserRepository 가정
import jakarta.persistence.EntityNotFoundException;
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
    private final ProductRepository productRepository; // ProductRepository 주입 (가정)
    private final UserRepository userRepository; // UserRepository 주입 (가정)

    @Transactional
    public CompleteDTO.Response createComplete(Long productId, Long buyerId, Long sellerId) {
        // 게시글(Product) 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("ID에 해당하는 게시글을 찾을 수 없습니다: " + productId));

        // 구매자(User) 조회
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new EntityNotFoundException("ID에 해당하는 구매자를 찾을 수 없습니다: " + buyerId));

        // 판매자(User) 조회
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new EntityNotFoundException("ID에 해당하는 판매자를 찾을 수 없습니다: " + sellerId));

        if (completeRepository.findByProduct_ProductId(productId).isPresent()) {
            throw new IllegalStateException("이미 거래 완료된 게시글입니다: " + productId);
        }

        Complete complete = Complete.builder()
                .product(product)
                .buyer(buyer)
                .seller(seller)
                .completedAt(LocalDateTime.now())
                .build();
        complete.setCompleteId(product.getProductId()); // Complete.java의 정의에 따라 직접 설정

        Complete savedComplete = completeRepository.save(complete);
        return CompleteDTO.Response.fromEntity(savedComplete);
    }

    @Transactional(readOnly = true)
    public CompleteDTO.Response getCompleteByProductId(Long productId) {
        Complete complete = completeRepository.findByProduct_ProductId(productId)
                .orElseThrow(() -> new EntityNotFoundException("게시글 ID에 해당하는 거래완료 정보를 찾을 수 없습니다: " + productId));
        return CompleteDTO.Response.fromEntity(complete);
    }

    @Transactional(readOnly = true)
    public List<CompleteDTO.Response> getCompletesByBuyerId(Long buyerId) {
        return completeRepository.findByBuyer_UserId(buyerId).stream()
                .map(CompleteDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CompleteDTO.Response> getCompletesBySellerId(Long sellerId) {
        return completeRepository.findBySeller_UserId(sellerId).stream()
                .map(CompleteDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CompleteDTO.Response> getAllCompletes() {
        return completeRepository.findAll().stream()
                .map(CompleteDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }
}