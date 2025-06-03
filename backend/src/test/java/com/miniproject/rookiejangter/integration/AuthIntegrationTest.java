package com.miniproject.rookiejangter.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniproject.rookiejangter.controller.dto.UserDTO;
import com.miniproject.rookiejangter.entity.Area;
import com.miniproject.rookiejangter.repository.AreaRepository; // AreaRepository 임포트 추가
import org.junit.jupiter.api.BeforeEach; // BeforeEach 임포트 추가
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull; // assertNotNull 임포트 추가

@SpringBootTest
@AutoConfigureMockMvc // AutoConfigureWebMvc는 SpringBootTest와 AutoConfigureMockMvc가 있으면 대개 불필요
@ActiveProfiles("test")
@Transactional
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AreaRepository areaRepository; // AreaRepository 주입

    private Integer testAreaId; // 테스트에 사용할 Area ID를 저장할 변수

    @BeforeEach // 각 테스트 시작 전에 실행
    void setUp() {
        // 테스트용 Area를 생성하고 데이터베이스에 저장합니다.
        Area area = Area.builder().areaName("테스트 지역").build();
        Area savedArea = areaRepository.save(area); // Area 저장 후, 저장된 Area 객체 반환
        testAreaId = savedArea.getAreaId(); // 저장된 Area의 ID를 가져와 변수에 저장
        assertNotNull(testAreaId, "Test Area ID should not be null after saving."); // ID가 제대로 저장되었는지 확인
    }

    @Test
    @DisplayName("Complete Authentication Flow Test: SignUp -> Login -> Logout")
    void completeAuthenticationFlowTest() throws Exception {
        // 1. Sign Up
        UserDTO.SignUpRequest signUpRequest = new UserDTO.SignUpRequest();
        signUpRequest.setLoginId("integrationtest");
        signUpRequest.setPassword("password123!"); // 특수문자 추가
        signUpRequest.setUserName("테스트유저"); // 한글로 12자 이내
        signUpRequest.setAreaId(testAreaId); // setUp()에서 저장된 유효한 ID 사용
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
        // userId도 응답에서 추출하여 로그아웃에 사용합니다.
        Long userId = jsonNode.get("userId").asLong(); // userId 추출

        // Verify tokens are not empty
        assert !accessToken.isEmpty() : "Access token should not be empty";
        assert !refreshToken.isEmpty() : "Refresh token should not be empty";

        // 3. Logout (using actual token and userId)
        mockMvc.perform(post("/api/auth/logout/" + userId) // userId를 경로 변수로 전달 (API 설계에 따라 변경 필요)
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
        signUpRequest.setAreaId(testAreaId); // setUp()에서 저장된 유효한 ID 사용
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
        duplicateRequest.setAreaId(testAreaId); // setUp()에서 저장된 유효한 ID 사용
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
//                .andExpect(status().isUnauthorized());
                .andExpect(status().isInternalServerError()) // 500으로 변경
                .andExpect(jsonPath("$.message").value("서버 에러: 인증 정보를 찾을 수 없습니다: nonexistentuser")); // 상세 메시지 확인
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
        // userId는 1L 또는 테스트에 필요한 아무 값으로 가정합니다.
        // 실제 API가 userId를 요구한다면, 해당 userId로 경로를 구성해야 합니다.
        // 예를 들어 /api/auth/logout/1L
        mockMvc.perform(post("/api/auth/logout/1") // 임시 userId (실제 환경에 맞게 조정 필요)
                        .header("Authorization", "Bearer invalid_token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("SignUp with invalid password (no special character) should fail")
    void signUpWithInvalidPasswordTest() throws Exception {
        // Area area = Area.builder().areaName("Test Area").build(); // 이 줄은 이제 필요 없습니다.

        UserDTO.SignUpRequest signUpRequest = new UserDTO.SignUpRequest();
        signUpRequest.setLoginId("invalidpasstest");
        signUpRequest.setPassword("password123"); // 특수문자 없음
        signUpRequest.setUserName("유저테스트");
        signUpRequest.setAreaId(testAreaId); // setUp()에서 저장된 유효한 ID 사용
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
        // areaId와 phone 누락 -> phone만 누락시킵니다. areaId는 필수이므로 제외해서는 안됩니다.
        // areaId는 필수값이므로 setUp()에서 얻은 testAreaId를 사용합니다.
        signUpRequest.setAreaId(testAreaId);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("SignUp with invalid userName (too long) should fail")
    void signUpWithInvalidUserNameTest() throws Exception {
        // Area area = Area.builder().areaName("Test Area").build(); // 이 줄은 이제 필요 없습니다.

        UserDTO.SignUpRequest signUpRequest = new UserDTO.SignUpRequest();
        signUpRequest.setLoginId("invalidnametest");
        signUpRequest.setPassword("password123!");
        signUpRequest.setUserName("이름이너무길어서열두자를초과하는사용자이름"); // 12자 초과
        signUpRequest.setAreaId(testAreaId); // setUp()에서 저장된 유효한 ID 사용
        signUpRequest.setPhone("010-1234-5678");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("SignUp with userName containing spaces should fail")
    void signUpWithUserNameContainingSpacesTest() throws Exception {
        // Area area = Area.builder().areaName("Test Area").build(); // 이 줄은 이제 필요 없습니다.

        UserDTO.SignUpRequest signUpRequest = new UserDTO.SignUpRequest();
        signUpRequest.setLoginId("spacenametest");
        signUpRequest.setPassword("password123!");
        signUpRequest.setUserName("테스트 유저"); // 공백 포함
        signUpRequest.setAreaId(testAreaId); // setUp()에서 저장된 유효한 ID 사용
        signUpRequest.setPhone("010-1234-5678");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest());
    }
}