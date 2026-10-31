package com.example.Backend.controller;

import com.example.Backend.dto.LoginRequestDTO;
import com.example.Backend.dto.SignupRequestDTO;
import com.example.Backend.entity.User;
import com.example.Backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class AuthControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/auth/signup - Successful registration creates user with BCrypt hash and returns JWT")
    void testSignupSuccess() throws Exception {
        SignupRequestDTO signupRequest = new SignupRequestDTO("John", "Doe", "john.doe@example.com", "Password123!");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("Account created successfully")))
                .andExpect(jsonPath("$.data.token", notNullValue()))
                .andExpect(jsonPath("$.data.type", is("Bearer")))
                .andExpect(jsonPath("$.data.user.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.data.user.firstName", is("John")))
                .andExpect(jsonPath("$.data.user.lastName", is("Doe")))
                .andExpect(jsonPath("$.data.user.password").doesNotExist());

        // Verify database persistence in app_users
        User savedUser = userRepository.findByEmail("john.doe@example.com").orElse(null);
        assertNotNull(savedUser);
        assertNotEquals("Password123!", savedUser.getPassword(), "Plain-text password must never be stored");
        assertTrue(passwordEncoder.matches("Password123!", savedUser.getPassword()), "BCrypt password should match");
    }

    @Test
    @DisplayName("POST /api/auth/signup - Duplicate email registration returns 409 Conflict")
    void testSignupDuplicateEmail() throws Exception {
        SignupRequestDTO firstSignup = new SignupRequestDTO("Alice", "Smith", "alice@example.com", "Secret123!");
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstSignup)))
                .andExpect(status().isCreated());

        // Attempt same email again
        SignupRequestDTO duplicateSignup = new SignupRequestDTO("Alice2", "Smith2", "alice@example.com", "Different123!");
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateSignup)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    @DisplayName("POST /api/auth/signup - Validation failure on empty fields")
    void testSignupValidationFailure() throws Exception {
        SignupRequestDTO invalidSignup = new SignupRequestDTO("", "", "invalid-email", "123");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidSignup)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.data.firstName", notNullValue()))
                .andExpect(jsonPath("$.data.email", notNullValue()))
                .andExpect(jsonPath("$.data.password", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/auth/login - Successful login returns JWT and user profile")
    void testLoginSuccess() throws Exception {
        // Register user first
        SignupRequestDTO signup = new SignupRequestDTO("Jane", "Doe", "jane.doe@example.com", "ValidPass123");
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isCreated());

        // Login
        LoginRequestDTO login = new LoginRequestDTO("jane.doe@example.com", "ValidPass123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.token", notNullValue()))
                .andExpect(jsonPath("$.data.user.email", is("jane.doe@example.com")))
                .andExpect(jsonPath("$.data.user.password").doesNotExist());
    }

    @Test
    @DisplayName("POST /api/auth/login - Invalid password returns 401 Unauthorized")
    void testLoginInvalidPassword() throws Exception {
        SignupRequestDTO signup = new SignupRequestDTO("Jane", "Doe", "jane2@example.com", "ValidPass123");
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isCreated());

        LoginRequestDTO login = new LoginRequestDTO("jane2@example.com", "WrongPassword");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Invalid email or password")));
    }

    @Test
    @DisplayName("Protected API - Accessing /api/employees without token returns 401 Unauthorized")
    void testProtectedApiWithoutToken() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Unauthorized")));
    }

    @Test
    @DisplayName("Protected API - Accessing /api/employees with valid token succeeds")
    void testProtectedApiWithValidToken() throws Exception {
        SignupRequestDTO signup = new SignupRequestDTO("Admin", "User", "admin.auth@example.com", "AdminPass123");
        String signupResponse = mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(signupResponse).get("data").get("token").asText();

        mockMvc.perform(get("/api/employees")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }
}
