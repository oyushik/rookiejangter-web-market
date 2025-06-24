package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.dto.CancelationDTO;
import com.miniproject.rookiejangter.entity.*;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CancelationService {

    private final CancelationRepository cancelationRepository;
    private final CancelationReasonRepository cancelationReasonRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * 예약 취소를 생성합니다.
     *
     * @param request 취소 요청 정보
     * @return 생성된 취소 정보
     */
    public CancelationDTO.Response createCancelation(CancelationDTO.Request request) {

        CancelationReason cancelationReason = cancelationReasonRepository.findById(request.getCancelationReasonId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "CancelationReason", "ID", request.getCancelationReasonId()));

        // Retrieve product, buyer, and seller from their respective IDs
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, request.getProductId()));
        User buyer = userRepository.findById(request.getBuyerId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, request.getBuyerId()));
        User seller = userRepository.findById(request.getSellerId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, request.getSellerId()));

        // Create the cancelation entity
        Cancelation cancelation = Cancelation.builder()
                .cancelationReason(cancelationReason)
                .product(product)
                .buyer(buyer)
                .seller(seller)
                .isCanceledByBuyer(request.getIsCanceledByBuyer())
                .cancelationDetail(request.getCancelationDetail())
                .createdAt(LocalDateTime.now())
                .build();

        Cancelation savedCancelation = cancelationRepository.save(cancelation);
        return CancelationDTO.Response.fromEntity(savedCancelation);
    }

    /**
     * 특정 취소 사유 ID에 대한 모든 취소 정보를 조회합니다.
     *
     * @param cancelationReasonId 취소 사유 ID
     * @return 취소 정보 리스트
     */
    public List<CancelationDTO.Response> getCancelationsByCancelationReasonId(Integer cancelationReasonId) {
        List<Cancelation> cancelations = cancelationRepository.findByCancelationReason_CancelationReasonId(cancelationReasonId);

        return cancelations.stream()
                .map(CancelationDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }
}