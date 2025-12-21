package com.elearning.elearning_platform.shared.infrastructure.security;

import com.elearning.elearning_platform.shared.domain.valueobject.Email;
import com.elearning.elearning_platform.user.domain.model.Role;
import com.elearning.elearning_platform.user.domain.model.User;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepositoryPort userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String adminToken;
    private String instructorToken;
    private String studentToken;

    @BeforeEach
    void setUp() throws Exception {
        // Clean database
        userRepository.findAll()
                .forEach(user -> userRepository.deleteById(user.getId().value()));

        // ADMIN
        registerUser("admin@test.com", "Admin1234", "ADMIN");
        adminToken = loginAndGetToken("admin@test.com", "Admin1234");

        // INSTRUCTOR
        registerUser("instructor@test.com", "Instructor1", "INSTRUCTOR");
        instructorToken = loginAndGetToken("instructor@test.com", "Instructor1");

        // STUDENT
        registerUser("student@test.com", "Student123", "STUDENT");
        studentToken = loginAndGetToken("student@test.com", "Student123");
    }

    /* ---------------------------------------------------
       Helper methods
       --------------------------------------------------- */

    private void registerUser(String email, String password, String role) {
        Email emailVO = Email.of(email);
        String hashedPassword = passwordEncoder.encode(password);
        Role roleEnum = Role.valueOf(role);

        User user = User.create(emailVO, hashedPassword, "John", "Doe", roleEnum);
        userRepository.save(user);
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        LoginRequest request = new LoginRequest(email, password);

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }

    /* ---------------------------------------------------
       Security tests
       --------------------------------------------------- */

    @Test
    void shouldAllowPublicAccessToCourses() throws Exception {
        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectInstructorCoursesWithoutToken() throws Exception {
        mockMvc.perform(post("/instructor/courses"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectInstructorCoursesWithStudentToken() throws Exception {
        mockMvc.perform(post("/instructor/courses")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowInstructorCoursesWithInstructorToken() throws Exception {
        mockMvc.perform(post("/instructor/courses")
                        .header("Authorization", "Bearer " + instructorToken))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectAdminUsersWithInstructorToken() throws Exception {
        mockMvc.perform(post("/admin/users")
                        .header("Authorization", "Bearer " + instructorToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminUsersWithAdminToken() throws Exception {
        mockMvc.perform(post("/admin/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }
}

