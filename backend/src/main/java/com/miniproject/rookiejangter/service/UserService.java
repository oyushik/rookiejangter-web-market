package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.dto.UserDTO;
import com.miniproject.rookiejangter.entity.Area;
import com.miniproject.rookiejangter.entity.Ban;
import com.miniproject.rookiejangter.entity.User;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.repository.AreaRepository;
import com.miniproject.rookiejangter.repository.BanRepository;
import com.miniproject.rookiejangter.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AreaRepository areaRepository;
    private final BanRepository banRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자 생성 메서드
     *
     * @param requestDto 사용자 생성 요청 DTO
     * @return 생성된 사용자 정보 DTO
     */
    @Transactional
    public UserDTO.Response createUser(UserDTO.SignUpRequest requestDto) {

        if (userRepository.existsByLoginId(requestDto.getLoginId())) {
            throw new BusinessException(ErrorCode.LOGIN_ID_ALREADY_EXISTS, requestDto.getLoginId());
        }

        if (userRepository.existsByPhone(requestDto.getPhone())) {
            throw new BusinessException(ErrorCode.PHONE_ALREADY_EXISTS, requestDto.getPhone());
        }

        Area area = areaRepository.findById(requestDto.getAreaId().intValue())
                .orElseThrow(() -> new EntityNotFoundException("해당 지역을 찾을 수 없습니다: " + requestDto.getAreaId()));

        User user = User.builder()
                .loginId(requestDto.getLoginId())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .userName(requestDto.getUserName())
                .phone(requestDto.getPhone())
                .area(area)
                .isBanned(false)
                .isAdmin(false)
                .build();

        User savedUser = userRepository.save(user);
        return UserDTO.Response.fromEntity(savedUser);
    }

    /**
     * 사용자 ID로 사용자 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 정보 DTO
     */
    @Transactional(readOnly = true)
    public UserDTO.Response getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, String.valueOf(userId)));
        return UserDTO.Response.fromEntity(user);
    }

    /**
     * 사용자 이름으로 사용자 정보를 조회합니다.
     *
     * @param userName 사용자 이름
     * @return 사용자 정보 DTO
     */
    @Transactional(readOnly = true)
    public UserDTO.Response getUserByUserName(String userName) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userName));
        return UserDTO.Response.fromEntity(user);
    }

    /**
     * 로그인 ID로 사용자 정보를 조회합니다.
     *
     * @param loginId 로그인 ID
     * @return 사용자 정보 DTO
     */
    @Transactional(readOnly = true)
    public UserDTO.Response getUserByLoginId(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, loginId));
        return UserDTO.Response.fromEntity(user);
    }

    /**
     * 사용자 정보를 업데이트합니다. (JWT 토큰용)
     *
     * @param userId 사용자 ID
     * @param requestDto 사용자 정보 업데이트 요청 DTO
     * @return 업데이트된 사용자 정보 DTO
     */
    @Transactional
    public UserDTO.Response updateUser(Long userId, UserDTO.UpdateRequest requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, String.valueOf(userId)));

        Area newArea = null;
        if (requestDto.getAreaId() != null) {
            newArea = areaRepository.findById(requestDto.getAreaId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.AREA_NOT_FOUND, requestDto.getAreaId()));
        } else {
            newArea = user.getArea(); // 지역 정보가 업데이트되지 않으면 기존 지역 유지
        }

        user.updateUserInfo(
                newArea,
                requestDto.getUserName() != null ? requestDto.getUserName() : user.getUserName(),
                requestDto.getPhone() != null ? requestDto.getPhone() : user.getPhone()
        );

        User updatedUser = userRepository.save(user);
        return UserDTO.Response.fromEntity(updatedUser);
    }

    /**
     * 사용자 정보를 업데이트합니다. (사용자 이름 기반, 기존 호환성을 위해 남겨둠)
     *
     * @param username 사용자 이름
     * @param requestDto 사용자 정보 업데이트 요청 DTO
     * @return 업데이트된 사용자 정보 DTO
     */
    @Transactional
    public UserDTO.Response updateUser(String username, UserDTO.UpdateRequest requestDto) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, username));

        Area newArea = null;
        if (requestDto.getAreaId() != null) {
            newArea = areaRepository.findById(requestDto.getAreaId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.AREA_NOT_FOUND, requestDto.getAreaId()));
        } else {
            newArea = user.getArea(); // 지역 정보가 업데이트되지 않으면 기존 지역 유지
        }

        user.updateUserInfo(
                newArea,
                requestDto.getUserName() != null ? requestDto.getUserName() : user.getUserName(),
                requestDto.getPhone() != null ? requestDto.getPhone() : user.getPhone()
                );

        User updatedUser = userRepository.save(user);
        return UserDTO.Response.fromEntity(updatedUser);
    }

    @Transactional
    public UserDTO.Response updateUserStatus(Long userId, UserDTO.StatusUpdateRequest requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, String.valueOf(userId)));

        user.changeBanStatus(requestDto.getIsBanned());

        if (requestDto.getIsBanned()) {
            Ban ban = Ban.builder()
                    .user(user)
                    .banReason(requestDto.getBanReason())
                    .build();
            banRepository.save(ban);
        }

        User updatedUser = userRepository.save(user);
        User finalUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId));
        return UserDTO.Response.fromEntityStatus(finalUser);
    }

    /**
     * 사용자 정보 삭제제
     *
     * @param userId 사용자 ID
     */
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, String.valueOf(userId)));
        userRepository.delete(user);
    }

    /**
     * 비밀번호 확인 후 사용자 정보 삭제
     *
     * @param userId 사용자 ID
     * @param password 사용자의 비밀번호
     */
    @Transactional
    public void deleteUserWithPassword(Long userId, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, String.valueOf(userId)));

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH, "비밀번호가 일치하지 않습니다.");
        }

        userRepository.delete(user);
    }

    /**
     * 모든 사용자 정보를 페이지네이션하여 조회합니다.
     *
     * @param pageable 페이지 정보
     * @return 페이지네이션된 사용자 정보 DTO
     */
    @Transactional(readOnly = true)
    public UserDTO.UserListData getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        List<UserDTO.Response> userResponses = userPage.getContent().stream()
                .map(UserDTO.Response::fromEntity)
                .collect(Collectors.toList());

        UserDTO.UserListPagination pagination = UserDTO.UserListPagination.builder()
                .page(userPage.getNumber())
                .size(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .first(userPage.isFirst())
                .last(userPage.isLast())
                .build();

        return UserDTO.UserListData.builder()
                .content(userResponses)
                .pagination(pagination)
                .build();
    }

    /**
     * 제재된 사용자 정보를 조회합니다.
     * 
     * @return 제재된 사용자 정보 리스트
     */
    @Transactional(readOnly = true)
    public List<UserDTO.Response> getBannedUsers() {
        List<User> bannedUsers = userRepository.findByIsBannedTrue();
        return bannedUsers.stream()
                .map(UserDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 관리자 권한을 가진 사용자 정보를 조회합니다.
     *
     * @return 관리자 사용자 정보 리스트
     */
    @Transactional(readOnly = true)
    public List<UserDTO.Response> getAdminUsers() {
        List<User> adminUsers = userRepository.findByIsAdminTrue();
        return adminUsers.stream()
                .map(UserDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 로그인 ID가 사용 가능한지 확인합니다.
     * 
     * @param loginId 로그인 ID
     * @return 사용 가능 여부
     */
    @Transactional(readOnly = true)
    public boolean isLoginIdAvailable(String loginId) {
        return !userRepository.existsByLoginId(loginId);
    }

    /**
     * 전화번호가 사용 가능한지 확인합니다.
     *
     * @param phone 전화번호
     * @return 사용 가능 여부
     */
    @Transactional(readOnly = true)
    public boolean isPhoneAvailable(String phone) {
        return !userRepository.existsByPhone(phone);
    }
}