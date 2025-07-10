package com.example.jwks;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JwkSetController.class)
class JwkSetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetJwkSet() throws Exception {
        mockMvc.perform(get("/oauth2/jwks"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testGetJwkSetWithSpecificKey() throws Exception {
        mockMvc.perform(get("/oauth2/jwks")
                .param("kid", "user-service-rs256"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testGetJwkSetWithInvalidKey() throws Exception {
        mockMvc.perform(get("/oauth2/jwks")
                .param("kid", "invalid-key"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testGetJwkSetWithMultipleKeys() throws Exception {
        mockMvc.perform(get("/oauth2/jwks")
                .param("kid", "user-service-rs256,another-key"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testGetJwkSetWithFormat() throws Exception {
        mockMvc.perform(get("/oauth2/jwks")
                .param("format", "json"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error").exists());
    }
} 