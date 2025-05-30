package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.controller.dto.DibsDTO;
import com.miniproject.rookiejangter.entity.Dibs;
import com.miniproject.rookiejangter.entity.Product;
import com.miniproject.rookiejangter.entity.User;
import com.miniproject.rookiejangter.repository.DibsRepository;
import com.miniproject.rookiejangter.repository.ProductRepository;
import com.miniproject.rookiejangter.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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
            throw new IllegalStateException("이미 찜한 상품입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + productId));

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
            throw new EntityNotFoundException("찜한 내역을 찾을 수 없습니다.");
        }
        dibsRepository.deleteByUser_UserIdAndProduct_ProductId(userId, productId);
    }

    @Transactional(readOnly = true)
    public DibsDTO.Response getDibsStatus(Long userId, Long productId) {
        boolean isLiked = dibsRepository.existsByUser_UserIdAndProduct_ProductId(userId, productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + productId));

        if (isLiked) {
            List<Dibs> dibsForProduct = dibsRepository.findByProduct_ProductId(productId);
            Optional<Dibs> userDibsOpt = dibsForProduct.stream()
                    .filter(d -> d.getUser().getUserId().equals(userId))
                    .findFirst();
            if (userDibsOpt.isPresent()) {
                return DibsDTO.Response.fromEntity(userDibsOpt.get(), true);
            }
            // 이론상 existsByUser_UserIdAndProduct_ProductId가 true면 여기서 찾아야 하지만, 동시성 문제 등 예외 케이스 방어
            Dibs tempDibs = Dibs.builder().product(product).addedAt(LocalDateTime.now()).build(); // 임시로 addedAt 설정
            return DibsDTO.Response.fromEntity(tempDibs, true); // isLiked는 true지만 정확한 dibs 객체를 못찾은 경우
        } else {
            Dibs tempDibs = Dibs.builder().product(product).build();
            return DibsDTO.Response.fromEntity(tempDibs, false);
        }
    }

    @Transactional(readOnly = true)
    public List<DibsDTO.Response.DibbedProduct> getUserDibsList(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        List<Dibs> dibsList = dibsRepository.findByUser_UserId(userId);

        return dibsList.stream()
                .map(DibsDTO.Response.DibbedProduct::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getDibsCountForProduct(Long productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + productId));
        return dibsRepository.findByProduct_ProductId(productId).size();
    }
}