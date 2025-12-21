package com.elearning.elearning_platform.user.infrastructure.adapter.in.web;

import com.elearning.elearning_platform.user.domain.port.out.UserRepositoryPort;
import com.elearning.elearning_platform.user.infrastructure.adapter.in.web.dto.LoginRequest;
import com.elearning.elearning_platform.user.infrastructure.adapter.in.web.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
class AuthControllerE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepositoryPort userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Clear database before each test
        userRepository.findAll().forEach(user -> userRepository.deleteById(user.getId().value()));
    }

    @Test
    void shouldRegisterAndLoginSuccessfully() throws Exception {
        // 1️⃣ Register
        RegisterRequest register = new RegisterRequest(
                "e2e@test.com",
                "Password1",
                "John",
                "Doe",
                "STUDENT"
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        // 2️⃣ Login
        LoginRequest login = new LoginRequest(
                "e2e@test.com",
                "Password1"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").isNumber());
    }

    @Test
    void shouldFailWhenRegisteringDuplicateEmail() throws Exception {
        RegisterRequest register = new RegisterRequest(
                "duplicate@test.com",
                "Password1",
                "John",
                "Doe",
                "STUDENT"
        );

        // First registration succeeds
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        // Second registration with same email fails
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailWhenLoginWithInvalidCredentials() throws Exception {
        LoginRequest login = new LoginRequest(
                "nonexistent@test.com",
                "WrongPassword"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isBadRequest());
    }
}
