package com.miniproject.rookiejangter.provider;

import com.miniproject.rookiejangter.provider.JwtProvider;
import com.miniproject.rookiejangter.service.AuthService; // 사용되지 않으면 제거 가능
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // 이제 직접 사용 안 함
import org.springframework.security.core.Authentication; // 추가
import org.springframework.security.core.authority.SimpleGrantedAuthority; // 이제 직접 사용 안 함
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections; // 이제 직접 사용 안 함
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final AuthService authService;

    // 인증을 건너뛸 경로들
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/signup",
            "/api/auth/refresh",
            "/h2-console", // H2 콘솔 접근을 허용하려면 추가
            "/ws/chat" // WebSocket 핸드셰이크는 별도 인터셉터에서 처리하므로 여기서 스킵 가능
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 필터를 건너뛸 경로인지 확인
        if (shouldSkipFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = getTokenFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt) && !authService.isTokenBlacklisted(jwt)) {
                // JwtProvider의 createAuthentication 메서드를 사용하여 Authentication 객체 생성
                Authentication authentication = jwtProvider.createAuthentication(jwt);

                // 요청 세부정보 설정 (선택 사항이지만 권장)
                ((UsernamePasswordAuthenticationToken) authentication).setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContext에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("사용자 인증 완료: userId={}", authentication.getName()); // authentication.getName()은 userId (String) 반환
            } else {
                log.debug("유효하지 않거나 블랙리스트에 있는 JWT 토큰입니다. 또는 토큰이 없습니다.");
                // 인증 실패 시 SecurityContext 초기화 (혹시 모를 잔여 인증 정보 제거)
                SecurityContextHolder.clearContext();
            }

        } catch (Exception e) {
            log.error("JWT 인증 처리 중 오류 발생: {}", e.getMessage(), e);
            SecurityContextHolder.clearContext();
            // 특정 예외에 대한 HTTP 응답 코드를 여기서 직접 설정할 수도 있습니다.
            // 예: response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청에서 JWT 토큰 추출
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 필터를 건너뛸지 결정
     */
    private boolean shouldSkipFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }
}