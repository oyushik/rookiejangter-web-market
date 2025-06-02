package com.miniproject.rookiejangter.config;

import com.miniproject.rookiejangter.provider.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.cors.CorsConfiguration;

import java.io.IOException;
import java.util.List;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (JWT 사용시 필요)
                .csrf((csrf) -> csrf.disable())

                // 세션 관리 정책을 STATELESS로 설정 (JWT 사용시)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 인증/인가 설정
                .authorizeHttpRequests((requests) -> requests
                        // H2 콘솔 접근 허용 (개발 환경)
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
                        // 회원가입, 로그인 API 접근 허용
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/auth/signup")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/auth/login")).permitAll()
                        // 토큰 갱신 API 접근 허용
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/auth/refresh")).permitAll()
                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )

                // 커스텀 AuthenticationEntryPoint 설정 (401 반환)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(customAuthenticationEntryPoint()))

                // H2 콘솔을 위한 프레임 옵션 비활성화 (새로운 방식)
                .headers((headers) -> headers
                        .frameOptions(frameOptions -> frameOptions.disable()))

                // 기본 폼 로그인 비활성화 (JWT 사용시)
                .formLogin(form -> form.disable())

                // HTTP Basic 인증 비활성화
                .httpBasic(basic -> basic.disable())

                // 기본 로그아웃 비활성화 (커스텀 로그아웃 API 사용)
                .logout(logout -> logout.disable())

                // JWT 인증 필터 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    // CORS 설정 빈 추가
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));  // 프론트엔드 주소
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);  // 인증 쿠키가 필요하다면 true로 설정

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            // 인증 실패 시 401 Unauthorized 반환
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"인증이 필요합니다.\"}");
        };
    }
}