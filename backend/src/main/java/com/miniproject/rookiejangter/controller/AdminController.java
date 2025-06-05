package com.miniproject.rookiejangter.controller;

import com.miniproject.rookiejangter.controller.dto.UserDTO;
import com.miniproject.rookiejangter.entity.User;
import com.miniproject.rookiejangter.repository.UserRepository;
import com.miniproject.rookiejangter.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

// 관리자 전용 컨트롤러
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    @Component
    @RequiredArgsConstructor
    static public class AdminDataInitializer {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;

        @PostConstruct
        public void initializeAdminAccount() {
            // 이미 관리자 계정이 존재하는지 확인
            if (!userRepository.existsByLoginId("admin")) {
                User admin = User.builder()
                        .userName("관리자")
                        .userId(1L)
                        .loginId("admin")
                        .phone("undefined")
                        .isBanned(false)
                        .isAdmin(true)
                        .password(passwordEncoder.encode("admin123!"))
                        .build();
                userRepository.save(admin);
                System.out.println("관리자 계정이 생성되었습니다. ID: admin, PW: admin123!");
            }
        }
    }

    private final UserService userService;
    private final UserRepository userRepository;

    /**
     * 현재 로그인한 사용자가 관리자인지 확인
     */
    private boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // JWT 필터에서 principal에 userId를 저장했으므로 userId로 사용자 조회
        String userId = authentication.getName();
        try {
            Long userIdLong = Long.parseLong(userId);
            User currentUser = userRepository.findById(userIdLong).orElse(null);
            return currentUser != null && Boolean.TRUE.equals(currentUser.getIsAdmin());
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 관리자 권한 검증 및 에러 응답 생성
     */
    private ResponseEntity<?> checkAdminPermission() {
        if (!isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("관리자 권한이 필요합니다.");
        }
        return null;
    }

    /**
     * 디버깅을 위한 인증 정보 확인 메서드
     */
    @GetMapping("/debug/auth")
    public ResponseEntity<?> debugAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return ResponseEntity.ok("Authentication 객체가 null입니다.");
        }

        String debugInfo = String.format(
                "Authentication 정보:\n" +
                        "- isAuthenticated: %s\n" +
                        "- getName(): %s\n" +
                        "- getPrincipal(): %s\n" +
                        "- getAuthorities(): %s\n" +
                        "- getClass(): %s",
                authentication.isAuthenticated(),
                authentication.getName(),
                authentication.getPrincipal(),
                authentication.getAuthorities(),
                authentication.getClass().getSimpleName()
        );

        return ResponseEntity.ok(debugInfo);
    }

    /**
     * 관리자의 유저 목록 조회
     * 관리자만 접근 가능
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(Pageable pageable, HttpServletRequest request) {
        // 관리자 권한 검증
        ResponseEntity<?> permissionCheck = checkAdminPermission();
        if (permissionCheck != null) {
            return permissionCheck;
        }

        try {
            UserDTO.UserListData allUsers = userService.getAllUsers(pageable);

            // 관리자 접근 로그 기록
            logAdminAccess("사용자 목록 조회", request);

            return ResponseEntity.ok(allUsers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("사용자 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 관리자의 특정 유저 조회
     * 관리자만 접근 가능
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id, HttpServletRequest request) {
        // 관리자 권한 검증
        ResponseEntity<?> permissionCheck = checkAdminPermission();
        if (permissionCheck != null) {
            return permissionCheck;
        }

        try {
            UserDTO.Response user = userService.getUserById(id);

            // 관리자 접근 로그 기록
            logAdminAccess("사용자 상세 조회 (ID: " + id + ")", request);

            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("사용자를 찾을 수 없습니다: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("사용자 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 관리자의 특정 유저 상태 수정
     * 관리자만 접근 가능하며, 자기 자신의 관리자 권한은 해제할 수 없음
     */
    @PutMapping("/users/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long id,
                                              @RequestBody UserDTO.StatusUpdateRequest request,
                                              HttpServletRequest httpRequest) {
        // 관리자 권한 검증
        ResponseEntity<?> permissionCheck = checkAdminPermission();
        if (permissionCheck != null) {
            return permissionCheck;
        }

        try {
            // 현재 관리자 정보 가져오기 (userId로 조회)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            Long currentAdminId = Long.parseLong(userId);
            User currentAdmin = userRepository.findById(currentAdminId).orElse(null);

            // 수정하려는 사용자 정보 가져오기
            User targetUser = userRepository.findById(id).orElse(null);
            if (targetUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("수정하려는 사용자를 찾을 수 없습니다.");
            }

            // 자기 자신의 관리자 권한 해제 방지
            if (currentAdmin != null && currentAdmin.getUserId().equals(id) &&
                    request.getIsAdmin().equals(Boolean.TRUE)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("자신의 관리자 권한을 해제할 수 없습니다.");
            }

            UserDTO.Response response = userService.updateUserStatus(id, request);

            // 관리자 작업 로그 기록
            logAdminAccess("사용자 상태 수정 (ID: " + id + ", 대상: " + targetUser.getLoginId() + ")", httpRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("사용자 상태 수정 실패: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("사용자 상태 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 현재 관리자 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentAdmin() {
        // 관리자 권한 검증
        ResponseEntity<?> permissionCheck = checkAdminPermission();
        if (permissionCheck != null) {
            return permissionCheck;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        Long userIdLong = Long.parseLong(userId);
        User currentAdmin = userRepository.findById(userIdLong).orElse(null);

        if (currentAdmin != null) {
            UserDTO.Response adminInfo = UserDTO.Response.builder()
                    .loginId(currentAdmin.getLoginId())
                    .userName(currentAdmin.getUserName())
                    .phone(currentAdmin.getPhone())
                    .isBanned(currentAdmin.getIsBanned())
                    .isAdmin(currentAdmin.getIsAdmin())
                    .build();
            return ResponseEntity.ok(adminInfo);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("관리자 정보를 찾을 수 없습니다.");
    }

    /**
     * 관리자 접근 로그 기록
     */
    private void logAdminAccess(String action, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        // userId로 사용자 정보 조회해서 loginId 가져오기
        try {
            Long userIdLong = Long.parseLong(userId);
            User admin = userRepository.findById(userIdLong).orElse(null);
            String adminLoginId = admin != null ? admin.getLoginId() : "unknown";

            String clientIp = getClientIpAddress(request);

            System.out.println(String.format("[ADMIN ACCESS] 관리자: %s, 작업: %s, IP: %s, 시간: %s",
                    adminLoginId, action, clientIp, java.time.LocalDateTime.now()));
        } catch (NumberFormatException e) {
            System.out.println("관리자 로그 기록 중 오류: userId 파싱 실패");
        }

        // 실제 운영환경에서는 로그 파일이나 데이터베이스에 저장
    }

    /**
     * 클라이언트 IP 주소 추출
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };

        for (String headerName : headerNames) {
            String ip = request.getHeader(headerName);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }

    // 관리자의 상품 게시글 삭제
}