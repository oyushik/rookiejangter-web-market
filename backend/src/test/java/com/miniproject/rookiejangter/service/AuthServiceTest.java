package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.controller.dto.UserDTO;
import com.miniproject.rookiejangter.entity.User;
import com.miniproject.rookiejangter.exception.AuthenticationException;
import com.miniproject.rookiejangter.exception.InvalidCredentialsException;
import com.miniproject.rookiejangter.provider.JwtProvider;
import com.miniproject.rookiejangter.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private User testUser;
    private UserDTO.LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // Prepare test user data
        testUser = User.builder()
                .userId(1L)
                .loginId("testuser")
                .password("encodedPassword")
                .userName("Test User")
                .build();

        loginRequest = new UserDTO.LoginRequest();
        loginRequest.setLoginId("testuser");
        loginRequest.setPassword("password123");

        // Redis Mock setup - lenient to prevent unnecessary stubbing warnings
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("Login success test")
    void loginSuccess() {
        // Given
        when(userRepository.findByLoginId("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtProvider.createAccessToken(testUser)).thenReturn("accessToken123");
        when(jwtProvider.createRefreshToken(testUser)).thenReturn("refreshToken123");
        when(jwtProvider.getRefreshTokenExpireTime()).thenReturn(604800000L); // 7 days

        // When
        AuthService.LoginResponse response = authService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("accessToken123", response.getAccessToken());
        assertEquals("refreshToken123", response.getRefreshToken());
        assertEquals("Test User", response.getUserName());

        // Verify RefreshToken is stored in Redis
        verify(valueOperations).set(eq("RT:1"), eq("refreshToken123"), eq(604800000L), any());
    }

    @Test
    @DisplayName("Login failure - user not found")
    void loginFailure_UserNotFound() {
        // Given
        when(userRepository.findByLoginId("testuser")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(AuthenticationException.class, () -> {
            authService.login(loginRequest);
        });
    }

    @Test
    @DisplayName("Login failure - password mismatch")
    void loginFailure_PasswordMismatch() {
        // Given
        when(userRepository.findByLoginId("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(false);

        // When & Then
        assertThrows(InvalidCredentialsException.class, () -> {
            authService.login(loginRequest);
        });
    }

    @Test
    @DisplayName("Logout success test")
    void logoutSuccess() {
        // Given
        String accessToken = "accessToken123";
        Long userId = 1L;

        // Create Date object 1 hour from current time
        Date expirationDate = new Date(System.currentTimeMillis() + 3600000L);

        when(jwtProvider.validateToken(accessToken)).thenReturn(true);
        when(jwtProvider.isTokenExpired(accessToken)).thenReturn(false);
        when(jwtProvider.getClaimFromToken(eq(accessToken), any())).thenReturn(expirationDate);

        // When
        assertDoesNotThrow(() -> {
            authService.logout(accessToken, userId);
        });

        // Then
        verify(redisTemplate).delete("RT:1"); // Verify RefreshToken deletion
    }

    @Test
    @DisplayName("Token refresh success test")
    void tokenRefreshSuccess() {
        // Given
        String refreshToken = "refreshToken123";
        when(jwtProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtProvider.isTokenExpired(refreshToken)).thenReturn(false);
        when(jwtProvider.getUserIdFromToken(refreshToken)).thenReturn("1");
        when(valueOperations.get("RT:1")).thenReturn("refreshToken123");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(jwtProvider.createAccessToken(testUser)).thenReturn("newAccessToken123");

        // When
        AuthService.TokenRefreshResponse response = authService.refreshToken(refreshToken);

        // Then
        assertNotNull(response);
        assertEquals("newAccessToken123", response.getAccessToken());
    }

    @Test
    @DisplayName("Token refresh failure - invalid RefreshToken")
    void tokenRefreshFailure_InvalidToken() {
        // Given
        String refreshToken = "invalidRefreshToken";
        when(jwtProvider.validateToken(refreshToken)).thenReturn(false);

        // When & Then
        assertThrows(BadCredentialsException.class, () -> {
            authService.refreshToken(refreshToken);
        });
    }
}