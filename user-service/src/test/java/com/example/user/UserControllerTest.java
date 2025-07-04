package com.example.user;

import com.example.user.application.dto.CreateUserRequest;
import com.example.user.domain.User;
import com.example.user.application.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/users/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("User Service is healthy"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateUser() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .email("test@example.com")
                .password("password123")
                .role(User.UserRole.USER)
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testCreateUserValidation() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .email("invalid-email")
                .password("123")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUserById() throws Exception {
        // First create a user
        CreateUserRequest request = CreateUserRequest.builder()
                .email("getbyid@example.com")
                .password("password123")
                .role(User.UserRole.USER)
                .build();

        User.UserResponse createdUser = userService.createUser(request);

        mockMvc.perform(get("/users/{id}", createdUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdUser.getId()))
                .andExpect(jsonPath("$.email").value("getbyid@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUserByEmail() throws Exception {
        // First create a user
        CreateUserRequest request = CreateUserRequest.builder()
                .email("getbyemail@example.com")
                .password("password123")
                .role(User.UserRole.USER)
                .build();

        userService.createUser(request);

        mockMvc.perform(get("/users/email/getbyemail@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("getbyemail@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateUserStatus() throws Exception {
        // First create a user
        CreateUserRequest request = CreateUserRequest.builder()
                .email("updatestatus@example.com")
                .password("password123")
                .role(User.UserRole.USER)
                .build();

        User.UserResponse createdUser = userService.createUser(request);

        mockMvc.perform(put("/users/{id}/status", createdUser.getId())
                        .param("status", "INACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetNonExistentUser() throws Exception {
        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }
} 