package com.miniproject.rookiejangter.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniproject.rookiejangter.controller.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Complete Authentication Flow Test: SignUp -> Login -> Logout")
    void completeAuthenticationFlowTest() throws Exception {
        // 1. Sign Up
        UserDTO.SignUpRequest signUpRequest = new UserDTO.SignUpRequest();
        signUpRequest.setLoginId("integrationtest");
        signUpRequest.setPassword("password123!"); // 특수문자 추가
        signUpRequest.setUserName("테스트유저"); // 한글로 12자 이내
        signUpRequest.setAreaId(1L); // 테스트 데이터에서 삽입된 ID
        signUpRequest.setPhone("010-1234-5678"); // 필수값 추가

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isCreated());

        // 2. Login
        UserDTO.LoginRequest loginRequest = new UserDTO.LoginRequest();
        loginRequest.setLoginId("integrationtest");
        loginRequest.setPassword("password123!");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andReturn();

        // Extract tokens from response
        String responseContent = loginResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseContent);
        String accessToken = jsonNode.get("accessToken").asText();
        String refreshToken = jsonNode.get("refreshToken").asText();

        // Verify tokens are not empty
        assert !accessToken.isEmpty() : "Access token should not be empty";
        assert !refreshToken.isEmpty() : "Refresh token should not be empty";

        // 3. Logout (using actual token)
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("SignUp with duplicate loginId should fail")
    void signUpWithDuplicateLoginIdTest() throws Exception {
        // First signup
        UserDTO.SignUpRequest signUpRequest = new UserDTO.SignUpRequest();
        signUpRequest.setLoginId("duplicatetest");
        signUpRequest.setPassword("password123!"); // 특수문자 추가
        signUpRequest.setUserName("첫번째유저"); // 한글로 12자 이내
        signUpRequest.setAreaId(1L); // 테스트 데이터에서 삽입된 ID
        signUpRequest.setPhone("010-1111-2222"); // 필수값 추가

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isCreated());

        // Second signup with same loginId should fail
        UserDTO.SignUpRequest duplicateRequest = new UserDTO.SignUpRequest();
        duplicateRequest.setLoginId("duplicatetest");
        duplicateRequest.setPassword("password456!"); // 특수문자 추가
        duplicateRequest.setUserName("두번째유저"); // 한글로 12자 이내
        duplicateRequest.setAreaId(2L); // 테스트 데이터에서 삽입된 ID
        duplicateRequest.setPhone("010-3333-4444"); // 필수값 추가

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Login with invalid credentials should fail")
    void loginWithInvalidCredentialsTest() throws Exception {
        UserDTO.LoginRequest loginRequest = new UserDTO.LoginRequest();
        loginRequest.setLoginId("nonexistentuser");
        loginRequest.setPassword("wrongpassword!");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Logout without token should fail")
    void logoutWithoutTokenTest() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Logout with invalid token should fail")
    void logoutWithInvalidTokenTest() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer invalid_token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("SignUp with invalid password (no special character) should fail")
    void signUpWithInvalidPasswordTest() throws Exception {
        UserDTO.SignUpRequest signUpRequest = new UserDTO.SignUpRequest();
        signUpRequest.setLoginId("invalidpasstest");
        signUpRequest.setPassword("password123"); // 특수문자 없음
        signUpRequest.setUserName("유저테스트");
        signUpRequest.setAreaId(1L);
        signUpRequest.setPhone("010-1234-5678");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("SignUp with missing required fields should fail")
    void signUpWithMissingFieldsTest() throws Exception {
        UserDTO.SignUpRequest signUpRequest = new UserDTO.SignUpRequest();
        signUpRequest.setLoginId("missingfieldstest");
        signUpRequest.setPassword("password123!");
        signUpRequest.setUserName("유저테스트");
        // areaId와 phone 누락

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("SignUp with invalid userName (too long) should fail")
    void signUpWithInvalidUserNameTest() throws Exception {
        UserDTO.SignUpRequest signUpRequest = new UserDTO.SignUpRequest();
        signUpRequest.setLoginId("invalidnametest");
        signUpRequest.setPassword("password123!");
        signUpRequest.setUserName("이름이너무길어서열두자를초과하는사용자이름"); // 12자 초과
        signUpRequest.setAreaId(1L);
        signUpRequest.setPhone("010-1234-5678");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("SignUp with userName containing spaces should fail")
    void signUpWithUserNameContainingSpacesTest() throws Exception {
        UserDTO.SignUpRequest signUpRequest = new UserDTO.SignUpRequest();
        signUpRequest.setLoginId("spacenametest");
        signUpRequest.setPassword("password123!");
        signUpRequest.setUserName("테스트 유저"); // 공백 포함
        signUpRequest.setAreaId(1L);
        signUpRequest.setPhone("010-1234-5678");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest());
    }
}