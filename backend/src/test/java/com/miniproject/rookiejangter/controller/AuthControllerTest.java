package com.miniproject.rookiejangter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniproject.rookiejangter.dto.UserDTO;
import com.miniproject.rookiejangter.service.AuthService;
import com.miniproject.rookiejangter.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * AuthController Test Class
 * Using @Mock instead of @MockBean for Spring Boot 3.4.0+ compatibility
 * CSRF removed to avoid SecurityContextHolderStrategy initialization issues
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private UserDTO.SignUpRequest signUpRequest;
    private UserDTO.LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build();

        // Initialize test data
        signUpRequest = new UserDTO.SignUpRequest();
        signUpRequest.setLoginId("testuser");
        signUpRequest.setPassword("Password123!");
        signUpRequest.setUserName("TestUser");
        signUpRequest.setAreaId(1);
        signUpRequest.setPhone("010-1234-5678");

        loginRequest = new UserDTO.LoginRequest();
        loginRequest.setLoginId("testuser");
        loginRequest.setPassword("Password123!");
    }

    @Test
    @DisplayName("Sign up success test")
    void signUpSuccess() throws Exception {
        // Given
        UserDTO.Response expectedResponse = UserDTO.Response.builder()
                .id(1L)
                .loginId("testuser")
                .userName("TestUser")
                .build();

        when(userService.createUser(any(UserDTO.SignUpRequest.class)))
                .thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.loginId").value("testuser"))
                .andExpect(jsonPath("$.userName").value("TestUser"));
    }

    @Test
    @DisplayName("Login success test")
    void loginSuccess() throws Exception {
        // Given
        AuthService.LoginResponse expectedResponse = AuthService.LoginResponse.builder()
                .userId(1L)
                .accessToken("accessToken123")
                .refreshToken("refreshToken123")
                .userName("Test User")
                .build();

        when(authService.login(any(UserDTO.LoginRequest.class)))
                .thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken123"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken123"))
                .andExpect(jsonPath("$.userName").value("Test User"));
    }

    @Test
    @DisplayName("Logout success test")
    void logoutSuccess() throws Exception {
        // Given
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn("1");
        when(securityContext.getAuthentication()).thenReturn(authentication);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);

            doNothing().when(authService).logout(anyString(), anyLong());

            // When & Then
            mockMvc.perform(post("/api/auth/logout")
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .header("Authorization", "Bearer accessToken123"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("Logged out successfully"));
        }
    }

    @Test
    @DisplayName("Logout failure test - no token")
    void logoutFailureNoToken() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Token is required"));
    }

    @Test
    @DisplayName("Logout failure test - invalid user")
    void logoutFailureInvalidUser() throws Exception {
        // Given
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);

            // When & Then
            mockMvc.perform(post("/api/auth/logout")
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .header("Authorization", "Bearer accessToken123"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Invalid user"));
        }
    }

    @Test
    @DisplayName("Token refresh success test")
    void tokenRefreshSuccess() throws Exception {
        // Given
        AuthController.TokenRefreshRequest request = new AuthController.TokenRefreshRequest();
        request.setRefreshToken("refreshToken123");

        AuthService.TokenRefreshResponse expectedResponse = AuthService.TokenRefreshResponse.builder()
                .accessToken("newAccessToken123")
                .build();

        when(authService.refreshToken(anyString()))
                .thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("newAccessToken123"));
    }

    @Test
    @DisplayName("Token refresh failure test")
    void tokenRefreshFailure() throws Exception {
        // Given
        AuthController.TokenRefreshRequest request = new AuthController.TokenRefreshRequest();
        request.setRefreshToken("invalidRefreshToken");

        when(authService.refreshToken(anyString()))
                .thenThrow(new RuntimeException("Invalid refresh token"));

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Token refresh failed: Invalid refresh token"));
    }
}