package service;

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
        complete.setCompleteId(product.getProductId()); // Complete.java의 정의에 따라 직접 설정

        Complete savedComplete = completeRepository.save(complete);
        return CompleteDTO.Response.fromEntity(savedComplete);
    }

    @Transactional(readOnly = true)
    public CompleteDTO.Response getCompleteByProductId(Long productId) {
        Complete complete = completeRepository.findByProduct_ProductId(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMPLETE_RECORD_NOT_FOUND_BY_PRODUCT_ID, productId));
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