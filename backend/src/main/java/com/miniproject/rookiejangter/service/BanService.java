package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.dto.BanDTO;
import com.miniproject.rookiejangter.entity.Ban;
import com.miniproject.rookiejangter.entity.Report;
import com.miniproject.rookiejangter.entity.User;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.repository.BanRepository;
import com.miniproject.rookiejangter.repository.ReportRepository;
import com.miniproject.rookiejangter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BanService {

    private final BanRepository banRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    /**
     * 특정 사용자를 밴 처리합니다.
     *
     * @param userId      밴할 사용자 ID
     * @param reportId    관련된 신고 ID
     * @param banReason   밴 사유
     * @return 밴 정보 DTO
     */
    public BanDTO.Response banUser(Long userId, Long reportId, String banReason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Report", reportId));

        Ban ban = Ban.builder()
                .user(user)
                .report(report)
                .banReason(banReason)
                .build();

        Ban savedBan = banRepository.save(ban);

        user.changeBanStatus(true); // User 엔티티의 isBanned 필드를 true로 설정

        return BanDTO.Response.fromEntity(savedBan);
    }

    /**
     * 특정 사용자의 밴 상태를 해제합니다.
     *
     * @param userId 밴 해제할 사용자 ID
     */
    public void unbanUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));

        user.changeBanStatus(false); // User 엔티티의 isBanned 필드를 false로 설정
    }

    /**
     * 특정 밴 ID에 대한 밴 정보를 조회합니다.
     *
     * @param banId 밴 ID
     * @return 밴 정보 DTO
     */
    public BanDTO.Response getBanById(Long banId) {
        Ban ban = banRepository.findById(banId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Ban", banId));

        return BanDTO.Response.fromEntity(ban);
    }

    /**
     * 특정 사용자의 모든 밴 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 밴 정보 리스트 DTO
     */
    public List<BanDTO.Response> getBansByUserId(Long userId) {
        List<Ban> bans = banRepository.findByUser_UserId(userId);

        return bans.stream()
                .map(BanDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 특정 신고 ID에 대한 밴 정보를 조회합니다.
     *
     * @param reportId 신고 ID
     * @return 밴 정보 DTO
     */
    public BanDTO.Response getBanByReportId(Long reportId) {
        Ban ban = banRepository.findByReport_ReportId(reportId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Report", reportId));

        return BanDTO.Response.fromEntity(ban);
    }

    /**
     * 특정 밴 사유에 대한 모든 밴 정보를 조회합니다.
     *
     * @param banReason 밴 사유
     * @return 밴 정보 리스트 DTO
     */
    public List<BanDTO.Response> getBansByBanReason(String banReason) {
        List<Ban> bans = banRepository.findByBanReason(banReason);

        return bans.stream()
                .map(BanDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 밴 정보를 삭제합니다.
     *
     * @param banId      수정할 밴 ID
     */
    public void deleteBan(Long banId) {
        Ban ban = banRepository.findById(banId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Ban", banId));

        banRepository.delete(ban);
    }
}