package service;

import com.miniproject.rookiejangter.dto.CancelationReasonDTO;
import com.miniproject.rookiejangter.entity.CancelationReason;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.repository.CancelationReasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CancelationReasonService {

    private final CancelationReasonRepository cancelationReasonRepository;

    public CancelationReasonDTO.Response createCancelationReason(CancelationReasonDTO.Request request) {
        CancelationReason cancelationReason = CancelationReason.builder()
                .cancelationReasonType(request.getCancelationReasonType())
                .build();

        CancelationReason savedCancelationReason = cancelationReasonRepository.save(cancelationReason);
        return CancelationReasonDTO.Response.fromEntity(savedCancelationReason);
    }

    public CancelationReasonDTO.Response getCancelationReasonById(Integer cancelationReasonId) {
        CancelationReason cancelationReason = cancelationReasonRepository.findById(cancelationReasonId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "CancelationReason", cancelationReasonId));

        return CancelationReasonDTO.Response.fromEntity(cancelationReason);
    }

    public List<CancelationReasonDTO.Response> getAllCancelationReasons() {
        List<CancelationReason> cancelationReasons = cancelationReasonRepository.findAll();

        return cancelationReasons.stream()
                .map(CancelationReasonDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    public CancelationReasonDTO.Response updateCancelationReason(Integer cancelationReasonId, CancelationReasonDTO.Request request) {
        CancelationReason cancelationReason = cancelationReasonRepository.findById(cancelationReasonId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "CancelationReason", cancelationReasonId));

        cancelationReason.setCancelationReasonType(request.getCancelationReasonType());

        CancelationReason updatedCancelationReason = cancelationReasonRepository.save(cancelationReason);
        return CancelationReasonDTO.Response.fromEntity(updatedCancelationReason);
    }

    public void deleteCancelationReason(Integer cancelationReasonId) {
        CancelationReason cancelationReason = cancelationReasonRepository.findById(cancelationReasonId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "CancelationReason", cancelationReasonId));

        cancelationReasonRepository.delete(cancelationReason);
    }
}