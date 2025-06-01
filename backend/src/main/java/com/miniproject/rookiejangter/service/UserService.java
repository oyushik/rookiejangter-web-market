package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.controller.dto.UserDTO;
import com.miniproject.rookiejangter.entity.Area;
import com.miniproject.rookiejangter.entity.Ban;
import com.miniproject.rookiejangter.entity.User;
import com.miniproject.rookiejangter.repository.AreaRepository;
import com.miniproject.rookiejangter.repository.BanRepository;
import com.miniproject.rookiejangter.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AreaRepository areaRepository;
    private final BanRepository banRepository;

    @Transactional
    public UserDTO.Response createUser(UserDTO.SignUpRequest requestDto) {

        if (userRepository.existsByLoginId(requestDto.getLoginId())) {
            throw new IllegalArgumentException("이미 사용 중인 로그인 ID입니다: " + requestDto.getLoginId());
        }

        if (userRepository.existsByPhone(requestDto.getPhone())) {
            throw new IllegalArgumentException("이미 사용 중인 전화번호입니다: " + requestDto.getPhone());
        }


        Area area = areaRepository.findById(requestDto.getAreaId().intValue())        .orElseThrow(() -> new EntityNotFoundException("해당 지역을 찾을 수 없습니다: " + requestDto.getAreaId()));

        User user = User.builder()
                .loginId(requestDto.getLoginId())
                // .password(passwordEncoder.encode(requestDto.getPassword()))
                .password(requestDto.getPassword())
                .userName(requestDto.getUserName())
                .phone(requestDto.getPhone())
                .area(area)
                .isBanned(false)
                .isAdmin(false)
                .build();

        User savedUser = userRepository.save(user);
        return UserDTO.Response.fromEntity(savedUser);
    }

    @Transactional(readOnly = true)
    public UserDTO.Response getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId));
        return UserDTO.Response.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public UserDTO.Response getUserByLoginId(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + loginId));
        return UserDTO.Response.fromEntity(user);
    }


    @Transactional
    public UserDTO.Response updateUser(Long userId, UserDTO.UpdateRequest requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        if (requestDto.getUserName() != null && !requestDto.getUserName().isEmpty()) {
            user.setUserName(requestDto.getUserName());
        }

        if (requestDto.getPhone() != null && !requestDto.getPhone().isEmpty()) {

            if (!user.getPhone().equals(requestDto.getPhone()) && userRepository.existsByPhone(requestDto.getPhone())) {
                throw new IllegalArgumentException("이미 사용 중인 전화번호입니다: " + requestDto.getPhone());
            }
            user.setPhone(requestDto.getPhone());
        }

        if (requestDto.getAreaId() != null) {
            Area area = areaRepository.findById(requestDto.getAreaId().intValue())        .orElseThrow(() -> new EntityNotFoundException("해당 지역을 찾을 수 없습니다: " + requestDto.getAreaId()));
            user.setArea(area);
        }

        User updatedUser = userRepository.save(user);
        return UserDTO.Response.fromEntity(updatedUser);
    }

    @Transactional
    public UserDTO.Response updateUserStatus(Long userId, UserDTO.StatusUpdateRequest requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        user.setIsBanned(requestDto.getIsBanned());

        if (requestDto.getIsBanned()) {

            Ban ban = Ban.builder()
                    .user(user)
                    .banReason(requestDto.getBanReason())
                    .build();
            banRepository.save(ban);

        } else {
            }

        User updatedUser = userRepository.save(user);
        User finalUser = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId));
        return UserDTO.Response.fromEntityStatus(finalUser);
    }
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId));
        userRepository.delete(user);
    }


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


    @Transactional(readOnly = true)
    public List<UserDTO.Response> getBannedUsers() {
        List<User> bannedUsers = userRepository.findByIsBannedTrue();
        return bannedUsers.stream()
                .map(UserDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<UserDTO.Response> getAdminUsers() {
        List<User> adminUsers = userRepository.findByIsAdminTrue();
        return adminUsers.stream()
                .map(UserDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public boolean isLoginIdAvailable(String loginId) {
        return !userRepository.existsByLoginId(loginId);
    }


    @Transactional(readOnly = true)
    public boolean isPhoneAvailable(String phone) {
        return !userRepository.existsByPhone(phone);
    }
}