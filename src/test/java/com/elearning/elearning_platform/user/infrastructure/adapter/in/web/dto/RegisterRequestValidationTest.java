package com.elearning.elearning_platform.user.infrastructure.adapter.in.web.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false) // Ignora seguridad para el test
class RegisterRequestValidationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldFailWhenEmailIsEmpty() throws Exception {
        String invalidRequest = """
        {
            "email": "",
            ...
        }
        """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailWhenEmailIsInvalid() throws Exception {
        String invalidRequest = """
            {
                "email": "invalidemail.com",
                "password": "Password1",
                "firstName": "John",
                "lastName": "Doe",
                "role": "STUDENT"
            }
            """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("email"));
    }

    @Test
    void shouldFailWhenPasswordIsTooShort() throws Exception {
        String invalidRequest = """
            {
                "email": "john@example.com",
                "password": "12345",
                "firstName": "John",
                "lastName": "Doe",
                "role": "STUDENT"
            }
            """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("password"));
    }

    @Test
    void shouldFailWhenRoleIsInvalid() throws Exception {
        String invalidRequest = """
            {
                "email": "john@example.com",
                "password": "Password1",
                "firstName": "John",
                "lastName": "Doe",
                "role": "INVALID"
            }
            """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("role"));
    }

    @Test
    void shouldFailWhenFirstNameIsEmpty() throws Exception {
        String invalidRequest = """
            {
                "email": "john@example.com",
                "password": "Password1",
                "firstName": "",
                "lastName": "Doe",
                "role": "STUDENT"
            }
            """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("firstName"));
    }
}
